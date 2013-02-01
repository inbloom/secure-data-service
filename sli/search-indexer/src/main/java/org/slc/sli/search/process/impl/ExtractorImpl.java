/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slc.sli.search.process.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.config.IndexConfigStore;
import org.slc.sli.search.connector.SourceDatastoreConnector;
import org.slc.sli.search.connector.SourceDatastoreConnector.Tenant;
import org.slc.sli.search.entity.IndexEntity.Action;
import org.slc.sli.search.process.Extractor;
import org.slc.sli.search.process.Loader;
import org.slc.sli.search.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.mongodb.util.ThreadUtil;

/**
 * Extractor pulls data from mongo and writes it to file.
 * 
 * @author dwu
 * 
 */
public class ExtractorImpl implements Extractor {

	private static final Logger LOG = LoggerFactory
			.getLogger(ExtractorImpl.class);

	private final static int DEFAULT_LINE_PER_FILE = 100000;
	private final static int DEFAULT_EXECUTOR_THREADS = 3;
	private final static int DEFAULT_JOB_WAIT_TIMEOUT_MINS = 180;
	private final static int DEFAULT_EXTRACTOR_JOB_TIME = 600;

	private int maxLinePerFile = DEFAULT_LINE_PER_FILE;

	private IndexConfigStore indexConfigStore;

	private Loader loader;

	private String extractDir = Constants.DEFAULT_TMP_DIR;

	private ExecutorService executor;

	private int executorThreads = DEFAULT_EXECUTOR_THREADS;

	private int jobWaitTimeoutInMins = DEFAULT_JOB_WAIT_TIMEOUT_MINS;

	private boolean runOnStartup = false;

	private SourceDatastoreConnector sourceDatastoreConnector;

	public void destroy() {
		executor.shutdown();
	}

	public void init() {
		createExtractDir();
		// create thread pool to process files
		executor = Executors.newFixedThreadPool(executorThreads);
		if (runOnStartup) {
			executor.execute(new Runnable() {
				public void run() {
					execute(Action.INDEX);
				}
			});
		}
	}

	public void createExtractDir() {
		new File(extractDir).mkdirs();
	}

	public void execute() {
		execute(Action.UPDATE);
	}

	public void execute(Action action) {
		for (Tenant tenant : sourceDatastoreConnector.getTenants()) {
			execute(tenant, action);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.slc.sli.search.process.Extractor#execute()
	 */
	public void execute(Tenant tenant, Action action) {
		// TODO: implement isRunning flag to make sure only one extract is
		// running at a time
		IndexConfig config;
		Collection<String> collections = indexConfigStore.getCollections();
		Future<List<File>> call;
		List<Future<List<File>>> futures = new LinkedList<Future<List<File>>>();
		for (String collection : collections) {
			config = indexConfigStore.getConfig(collection);
			// child docs will be processed as dependents
			if (config.isChildDoc()) {
				continue;
			}
			call = executor.submit(new ExtractWorker(config, action, tenant));
			futures.add(call);
			if (config.hasDependents()) {
				for (String dependent : config.getDependents()) {
					futures.add(executor.submit(new DependentExtractWorker(
							indexConfigStore.getConfig(dependent), call, tenant)));
				}
			}
		}
		// wait job to be finished.
		for (Future<List<File>> future : futures) {
			processFuture(future);
		}
	}

	protected void processFuture(Future<List<File>> future) {
		try {
			future.get(DEFAULT_EXTRACTOR_JOB_TIME, TimeUnit.SECONDS);
		} catch (Exception e) {
			LOG.error("Error while waiting extractor job to be finished", e);
		}
	}

	public List<File> extractCollection(IndexConfig config, Action action,
			Tenant tenant) {
		return extractCollection(config, action, tenant, 0);
	}

	private List<File> extractCollection(IndexConfig config, Action action,
			Tenant tenant, int retryCount) {

		LOG.info("Extracting " + config);
		String collectionName = config.getCollectionName();
		BufferedWriter bw = null;
		DBObject obj;
		int numberOfLineWritten = 0;
		File outFile = null;
		DBCursor cursor = null;
		List<File> producedFiles = new ArrayList<File>();
		int fileCount = 0;
		try {
			TenantContext.setTenantId(tenant.getTenantId());
			cursor = sourceDatastoreConnector.getDBCursor(collectionName,
					config.getFields());
			// write each record to file
			while (cursor.hasNext()) {
				if (numberOfLineWritten % maxLinePerFile == 0) {
					numberOfLineWritten = 0;
					IOUtils.closeQuietly(bw);
					finishProcessing(tenant.getDbName(), outFile, action,
							producedFiles);
					fileCount++;

					// open file to write
					outFile = createTempFile(collectionName, tenant, fileCount);
					bw = new BufferedWriter(new FileWriter(outFile));
					LOG.info("File [" + outFile.getName() + "] was created");
				}
				obj = cursor.next();
				bw.write(JSON.serialize(obj));
				bw.newLine();
				numberOfLineWritten++;
			}
			IOUtils.closeQuietly(bw);

			finishProcessing(tenant.getDbName(), outFile, action, producedFiles);
			LOG.info("Finished extracting " + collectionName);
		} catch (FileNotFoundException e) {
			LOG.error("Error writing entities file", e);
		} catch (Exception e) {
			LOG.error("Error while extracting " + collectionName, e);
			if (retryCount <= 1) {
				LOG.error("Retrying extract for " + collectionName);
				ThreadUtil.sleep(1000);
				extractCollection(config, action, tenant, retryCount + 1);
			}
		} finally {
			TenantContext.setTenantId(null);
			// close file
			IOUtils.closeQuietly(bw);
			// close cursor
			if (cursor != null) {
				cursor.close();
			}
		}
		return producedFiles;
	}

	protected void finishProcessing(String index, File outFile, Action action,
			List<File> producedFiles) {
		// finish up
		if (outFile != null) {
			loader.processFile(index, action, outFile);
			producedFiles.add(outFile);
		}
	}

	private File createTempFile(String collectionName, Tenant tenant, int increment) {
		return new File(extractDir, tenant.getDbName() + "_" + collectionName + "_" + increment + ".json");
	}

	public void setIndexConfigStore(IndexConfigStore config) {
		this.indexConfigStore = config;
	}

	public void setExtractDir(String extractDir) {
		this.extractDir = extractDir;
	}

	public void setMaxLinePerFile(int maxLinePerFile) {
		this.maxLinePerFile = maxLinePerFile;
	}

	public void setExecutorThreads(int executorThreads) {
		this.executorThreads = executorThreads;
	}

	public void setRunOnStartup(boolean runOnStartup) {
		this.runOnStartup = runOnStartup;
	}

	public void setJobWaitTimeoutInMins(int jobWaitTimeoutInMins) {
		this.jobWaitTimeoutInMins = jobWaitTimeoutInMins;
	}

	public void setLoader(Loader loader) {
		this.loader = loader;
	}

	public void setSourceDatastoreConnector(
			SourceDatastoreConnector sourceDatastoreConnector) {
		this.sourceDatastoreConnector = sourceDatastoreConnector;
	}

	/**
	 * Runnable Thread class to write into file read from Mongo.
	 * 
	 * @author tosako
	 * 
	 */
	private class ExtractWorker implements Callable<List<File>> {

		final IndexConfig config;
		final Action action;
		final Tenant tenant;

		public ExtractWorker(IndexConfig config, Action action, Tenant tenant) {
			this.config = config;
			this.action = action;
			this.tenant = tenant;
		}

		public List<File> call() throws Exception {
			return extractCollection(config, action, tenant, 0);
		}
	}

	/**
	 * Runnable Thread class to write into file read from Mongo.
	 * 
	 * @author tosako
	 * 
	 */
	private class DependentExtractWorker extends ExtractWorker {
		private final Future<List<File>> parentJob;

		public DependentExtractWorker(IndexConfig config,
				Future<List<File>> parentJob, Tenant tenant) {
			super(config, Action.UPDATE, tenant);
			this.parentJob = parentJob;
		}

		@Override
		public List<File> call() throws Exception {
			try {
				final List<File> files = parentJob.get(20, TimeUnit.MINUTES);
				long timeToStopWaiting = System.currentTimeMillis()
						+ TimeUnit.MINUTES.toMillis(jobWaitTimeoutInMins);
				List<File> remaining = new ArrayList<File>(files);
				File f;
				while (!remaining.isEmpty()) {
					if (System.currentTimeMillis() <= timeToStopWaiting) {
						f = remaining.get(0);
						if (f.exists()) {
							ThreadUtil.sleep(3000);
						} else {
							remaining.remove(f);
						}
					}

				}
			} catch (Exception e) {
				LOG.error("Error while waiting for parent job to finish for "
						+ config, e);
			}
			return super.call();
		}
	}

	public String getHealth() {
		ThreadPoolExecutor tpe = (ThreadPoolExecutor) executor;
		return getClass() + ": {" + extractDir + " size:"
				+ new File(extractDir).list().length + ", active count:"
				+ tpe.getActiveCount() + ", completed count:"
				+ tpe.getCompletedTaskCount() + "}";
	}

}
