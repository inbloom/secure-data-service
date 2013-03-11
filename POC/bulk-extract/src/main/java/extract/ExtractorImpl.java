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
package extract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.mongodb.util.JSON;
import com.mongodb.util.ThreadUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

import util.OutstreamZipFile;

/**
 * Extractor pulls data from mongo and writes it to file.
 *
 * @author tshewchuk
 *
 */
public class ExtractorImpl implements Extractor {

    private static final Logger LOG = LoggerFactory.getLogger(ExtractorImpl.class);

    private static final int DEFAULT_EXECUTOR_THREADS = 3;
    private static final int DEFAULT_EXTRACTOR_JOB_TIME = 600;
    private static final String ID_STRING = "id";
    private static final String TYPE_STRING = "entityType";

    private List<String> collections;

    private String extractDir;

    private ExecutorService executor;

    private int executorThreads = DEFAULT_EXECUTOR_THREADS;

    private boolean runOnStartup = false;

    private List<String> tenants;

    private Repository<Entity> entityRepository;

    public void destroy() {
        executor.shutdown();
    }

    public void init() throws FileNotFoundException {
        createExtractDir();
        // create thread pool to process files
        executor = Executors.newFixedThreadPool(executorThreads);
        if (runOnStartup) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    execute();
                }
            });
        }
    }

    public void createExtractDir() {
        new File(extractDir).mkdirs();
    }

    @Override
    public void execute() {
        Future<File> call;
        List<Future<File>> futures = new LinkedList<Future<File>>();
        OutstreamZipFile zipFile = null;
        for (String tenant : tenants) {
            try {
                call = executor.submit(new ExtractWorker(tenant, zipFile));
                futures.add(call);
            } catch (FileNotFoundException e) {
                LOG.error("Error while extracting data for tenant " + tenant, e);
            }
        }

        // Wait for job to be finished.
        for (Future<File> future : futures) {
            processFuture(future);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.search.process.Extractor#execute()
     */
    @Override
    public void execute(String tenant) {
        // TODO: implement isRunning flag to make sure only one extract is
        // running at a time
        OutstreamZipFile zipFile = null;
        try {
            zipFile = new OutstreamZipFile(extractDir, tenant);
        } catch (IOException e) {
            LOG.error("Error while extracting data for tenant " + tenant, e);
        }
        for (String collection : collections) {
           extractCollection(tenant, zipFile, collection);
        }

        // Rename temp zip file to permanent.
        try {
            zipFile.renameTempZipFile();
        } catch (IOException e) {
            LOG.error("Error attempting to create zipfile " + zipFile.getZipFile().getPath(), e);
        }
    }

    @Override
    public String getHealth() {
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) executor;
        return getClass() + ": {" + extractDir + " size:" + new File(extractDir).list().length
                + ", active count:" + tpe.getActiveCount() + ", completed count:"
                + tpe.getCompletedTaskCount() + "}";
    }

    protected void processFuture(Future<File> future) {
        try {
            future.get(DEFAULT_EXTRACTOR_JOB_TIME, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOG.error("Error while waiting for extractor job to be finished", e);
        }
    }

    public File extractCollection(String tenant, OutstreamZipFile zipFile, String collectionName) {
        try {
            zipFile.createArchiveEntry(collectionName);
            extractCollection(tenant, zipFile, collectionName, 0);
        } catch (IOException e) {
            LOG.error("Error while extracting " + collectionName, e);
        }
        return zipFile.getZipFile();
    }

    private File extractCollection(String tenant, OutstreamZipFile zipFile, String collectionName,
            int retryCount) {

        LOG.info("Extracting " + collectionName);
        Iterable<Entity> records = null;
        try {
            TenantContext.setTenantId(tenant);
            records = entityRepository.findAll(collectionName, null);
            // write each record to file
            for (Entity record : records) {
                addAPIFields(record);
                zipFile.writeData(toJSON(record));
            }
            LOG.info("Finished extracting " + collectionName);
        } catch (IOException e) {
            LOG.error("Error while extracting " + collectionName, e);
            if (retryCount <= 1) {
                LOG.error("Retrying extract for " + collectionName);
                ThreadUtil.sleep(1000);
                extractCollection(tenant, zipFile, collectionName, retryCount + 1);
            }
        } finally {
            TenantContext.setTenantId(null);
        }
        return zipFile.getZipFile();
    }

    private String toJSON(Entity record) {
        // MongoEntity apiRecord = new MongoEntity(record.getType(),
        // record.getBody());
        // return JSON.serialize(apiRecord.toDBObject(null, null));
        return JSON.serialize(record.getBody());
    }

    public void addAPIFields(Entity entity) {
        entity.getBody().put(ID_STRING, entity.getEntityId());
        entity.getBody().put(TYPE_STRING, entity.getType());
    }

    public void setCollections(List<String> collections) {
        this.collections = collections;
    }

    public void setExtractDir(String extractDir) {
        this.extractDir = extractDir;
    }

    public void setExecutorThreads(int executorThreads) {
        this.executorThreads = executorThreads;
    }

    public void setRunOnStartup(boolean runOnStartup) {
        this.runOnStartup = runOnStartup;
    }

    public void setEntityRepository(Repository<Entity> entityRepository) {
        this.entityRepository = entityRepository;
    }

    public void setTenants(List<String> tenants) {
        this.tenants = tenants;
    }

    /**
     * Runnable Thread class to write into file read from Mongo.
     *
     * @author tosako
     *
     */
    private class ExtractWorker implements Callable<File> {

        private final String tenant;

        private OutstreamZipFile zipFile;

        public ExtractWorker(String tenant, OutstreamZipFile zipFile) throws FileNotFoundException {
            this.tenant = tenant;
            this.zipFile = zipFile;
        }

        @Override
        public File call() throws Exception {
            execute(tenant);
            return zipFile.getZipFile();
        }
    }

}
