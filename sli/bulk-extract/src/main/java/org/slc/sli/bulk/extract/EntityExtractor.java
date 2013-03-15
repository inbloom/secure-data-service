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
package org.slc.sli.bulk.extract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slc.sli.bulk.extract.zip.OutstreamZipFile;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.dal.repository.connection.TenantAwareMongoDbFactory;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.util.JSON;
import com.mongodb.util.ThreadUtil;


/**
 * Extractor pulls data from mongo and writes it to file.
 *
 * @author tshewchuk
 *
 */
public class EntityExtractor implements Extractor {

    private static final Logger LOG = LoggerFactory.getLogger(EntityExtractor.class);

    private static final int DEFAULT_EXECUTOR_THREADS = 3;
    private static final int DEFAULT_EXTRACTOR_JOB_TIME = 600;
    private static final String ID_STRING = "id";
    private static final String TYPE_STRING = "entityType";
    
    private String baseDirectory;

    private List<String> entities;

    private Map<String, String> queriedEntities;

    private Map<String, List<String>> combinedEntities;

    private ExecutorService executor;

    private int executorThreads = DEFAULT_EXECUTOR_THREADS;

    private boolean runOnStartup = false;

    private List<String> tenants;

    private Repository<Entity> entityRepository;

    public void destroy() {
        executor.shutdown();
    }

    public void init(List<String> tenants) throws FileNotFoundException {
        setTenants(tenants);
        init();
    }
    
    public void init() throws FileNotFoundException {
        createBaseDir();
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

    public void createBaseDir() {
        new File(baseDirectory).mkdirs();
    }

    @Override
    public void execute() {
        Future<File> call;
        List<Future<File>> futures = new LinkedList<Future<File>>();
        for (String tenant : tenants) {
            try {
                OutstreamZipFile zipFile =new OutstreamZipFile(getTenantDirectory(tenant), tenant);
                call = executor.submit(new ExtractWorker(tenant, zipFile));
                futures.add(call);
            } catch (FileNotFoundException e) {
                LOG.error("Error while extracting data for tenant " + tenant, e);
            } catch (IOException e) {
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
     * @see org.slc.sli.bulk.extract.Extractor#execute()
     */
    @Override
    public void execute(String tenant) {
        // TODO: implement isRunning flag to make sure only one extract is
        // running at a time
        OutstreamZipFile zipFile = null;
        try {
            zipFile = new OutstreamZipFile(getTenantDirectory(tenant), tenant);
        } catch (IOException e) {
            LOG.error("Error while extracting data for tenant " + tenant, e);
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

    protected void processFuture(Future<File> future) {
        try {
            future.get(DEFAULT_EXTRACTOR_JOB_TIME, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOG.error("Error while waiting for extractor job to be finished", e);
        }
    }

    public File extractEntity(String tenant, OutstreamZipFile zipFile, String entityName) {
            extractEntity(tenant, zipFile, entityName, 0);
            return zipFile.getZipFile();
    }

    private File extractEntity(String tenant, OutstreamZipFile zipFile, String entityName,
            int retryCount) {

        LOG.info("Extracting " + entityName);
        Iterable<Entity> records = null;
        String collectionName = entityName;
        Query query = new Query();
        if (queriedEntities.containsKey(entityName)) {
            collectionName = queriedEntities.get(entityName);
            query.addCriteria(Criteria.where("type").is(entityName));
        }
        if (combinedEntities.containsKey(entityName)) {
            query = new Query(Criteria.where("type").in(combinedEntities.get(entityName)));
        }
        try {
            TenantContext.setTenantId(tenant);
            records = entityRepository.findByQuery(collectionName, query, 0, 0);
            
            if(records.iterator().hasNext())
            {   
                zipFile.createArchiveEntry(entityName + ".json");
                // write each record to file
                JSONArray jsonRecords = new JSONArray();
                for (Entity record : records) {
                    addAPIFields(entityName, record);
                    jsonRecords.put(new JSONObject(record.getBody()));
                }
                writeToZip(zipFile,jsonRecords);
            }
            
            LOG.info("Finished extracting " + entityName);
        } catch (IOException e) {
            LOG.error("Error while extracting " + entityName, e);
            if (retryCount <= 1) {
                LOG.error("Retrying extract for " + entityName);
                ThreadUtil.sleep(1000);
                extractEntity(tenant, zipFile, entityName, retryCount + 1);
            }
        } finally {
            TenantContext.setTenantId(null);
        }
        return zipFile.getZipFile();
    }

    private void writeToZip(OutstreamZipFile zipFile, JSONArray records) throws NoSuchElementException, IOException  {
        zipFile.writeData(records.toString());
    }
    
    private void addAPIFields(String archiveName, Entity entity) {
        entity.getBody().put(TYPE_STRING, entity.getType());
        if (combinedEntities.containsKey(archiveName)) {
            entity.getBody().put(ID_STRING, archiveName);
        } else {
            entity.getBody().put(ID_STRING, entity.getEntityId());
        }
    }
    
    private String getTenantDirectory(String tenant) {
        
        File tenantDirectory = new File(baseDirectory, TenantAwareMongoDbFactory.getTenantDatabaseName(tenant));
        tenantDirectory.mkdirs();
        return tenantDirectory.getPath();
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

    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    /**
     * Runnable Thread class to write into file read from Mongo.
     *
     * @author tosako
     *
     */
    private class ExtractWorker implements Callable<File> {

        private final String tenant;

        private final OutstreamZipFile zipFile;

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
