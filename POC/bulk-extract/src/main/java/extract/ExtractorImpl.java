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
import java.util.Map;
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

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

    private List<String> entities;

    private Map<String, String> queriedEntities;

    private Map<String, List<String>> combinedEntities;

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
        Future<String> call;
        List<Future<String>> futures = new LinkedList<Future<String>>();
        for (String tenant : tenants) {
            try {
                call = executor.submit(new ExtractWorker(tenant));
                futures.add(call);
            } catch (FileNotFoundException e) {
                LOG.error("Error while extracting data for tenant " + tenant, e);
            }
        }

        // Wait for job to be finished.
        for (Future<String> future : futures) {
            processFuture(future);
        }

        // Shutdown.
        destroy();
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
        LOG.info("Extracting data from tenant " + tenant);
        OutstreamZipFile zipFile = null;
        try {
            zipFile = new OutstreamZipFile(extractDir, tenant);
        } catch (IOException e) {
            LOG.error("Error while extracting data from tenant " + tenant, e);
        }
        TenantContext.setTenantId(tenant);

        for (String entity : entities) {
            extractEntity(tenant, zipFile, entity);
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

    protected void processFuture(Future<String> future) {
        try {
            future.get(DEFAULT_EXTRACTOR_JOB_TIME, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOG.error("Error while waiting for extractor job to be finished", e);
        }
    }

    public File extractEntity(String tenant, OutstreamZipFile zipFile, String entityName) {
        try {
            zipFile.createArchiveEntry(entityName + ".json");
            extractEntity(tenant, zipFile, entityName, 0);
        } catch (IOException e) {
            LOG.error("Error while extracting " + entityName + " from " + tenant, e);
        }
        return zipFile.getZipFile();
    }

    private File extractEntity(String tenant, OutstreamZipFile zipFile, String entityName,
            int retryCount) {

        LOG.debug("Extracting " + entityName + " from " + tenant);
        Iterable<Entity> records = null;
        String collectionName = entityName;
        Query query = new Query();
        if (queriedEntities.containsKey(entityName)) {
            collectionName = queriedEntities.get(entityName);
            query.addCriteria(Criteria.where("type").is(entityName));
        }
        if (combinedEntities.containsKey(entityName)) {
            query = new Query();
            query.addCriteria(Criteria.where("type").in(combinedEntities.get(entityName)));
        }
        try {
            TenantContext.setTenantId(tenant);
            records = entityRepository.findByQuery(collectionName, query, 0, 0);
            // write each record to file
            for (Entity record : records) {
                addAPIFields(entityName, record);
                zipFile.writeData(toJSON(record));
            }
            LOG.debug("Finished extracting " + entityName + " from " + tenant);
        } catch (IOException e) {
            LOG.error("Error while extracting " + entityName + " from " + tenant, e);
            if (retryCount <= 1) {
                LOG.error("Retrying extract for " + entityName + " from " + tenant);
                ThreadUtil.sleep(1000);
                extractEntity(tenant, zipFile, entityName, retryCount + 1);
            }
        } finally {
            TenantContext.setTenantId(null);
        }
        return zipFile.getZipFile();
    }

    private String toJSON(Entity record) {
        return JSON.serialize(record.getBody());
    }

    private void addAPIFields(String archiveName, Entity entity) {
        entity.getBody().put(TYPE_STRING, entity.getType());
        if (combinedEntities.containsKey(archiveName)) {
            entity.getBody().put(ID_STRING, archiveName);
        } else {
            entity.getBody().put(ID_STRING, entity.getEntityId());
        }
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

    public void setEntities(List<String> entities) {
        this.entities = entities;
    }

    public void setQueriedEntities(Map<String, String> queriedEntities) {
        this.queriedEntities = queriedEntities;
    }

    public void setCombinedEntities(Map<String, List<String>> combinedEntities) {
        this.combinedEntities = combinedEntities;
    }

    /**
     * Runnable Thread class to write into file read from Mongo.
     *
     * @author tosako
     *
     */
    private class ExtractWorker implements Callable<String> {

        private final String tenant;

        public ExtractWorker(String tenant) throws FileNotFoundException {
            this.tenant = tenant;
        }

        @Override
        public String call() throws Exception {
            execute(tenant);
            return tenant;
        }
    }

}
