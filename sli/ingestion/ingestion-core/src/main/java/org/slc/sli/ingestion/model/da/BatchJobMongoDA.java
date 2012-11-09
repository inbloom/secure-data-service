/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.ingestion.model.da;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.dao.DataAccessResourceFailureException;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.dal.RetryMongoCommand;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.IngestionStagedEntity;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.RecordHash;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.queues.MessageType;

/**
 *
 * @author ldalgado
 *
 */
@Component
public class BatchJobMongoDA implements BatchJobDAO {

    private static final String TENANT_JOB_LOCK_COLLECTION = "tenantJobLock";

    private static final Logger LOG = LoggerFactory.getLogger(BatchJobMongoDA.class);

    private static final String BATCHJOB_ERROR_COLLECTION = "error";
    private static final String BATCHJOB_STAGE_SEPARATE_COLLECTION = "batchJobStage";

    private static final String ERROR = "error";
    private static final String WARNING = "warning";
    private static final String BATCHJOBID_FIELDNAME = "batchJobId";
    private static final String FILE_NAME_FIELD = "resourceId";
    private static final String SEVERITY_FIELD = "severity";
    private static final String TRANSFORMATION_LATCH = "transformationLatch";
    private static final String PERSISTENCE_LATCH = "persistenceLatch";
    private static final String STAGED_ENTITIES = "stagedEntities";
    private static final String RECORD_HASH = "recordHash";
    private static final int DUP_KEY_CODE = 11000;

    @Value("${sli.ingestion.totalRetries}")
    private int numberOfRetries;

    private MongoTemplate batchJobMongoTemplate;

    private MongoTemplate batchJobHashCacheMongoTemplate;

    private MongoTemplate sliMongo;

    @Value("${sli.ingestion.errors.tracking}")
    private String trackIngestionErrors;

    @Value("${sli.ingestion.warnings.tracking}")
    private String trackIngestionWarnings;

    @Override
    public void saveBatchJob(final NewBatchJob job) {
        if (job != null) {

            RetryMongoCommand retry = new RetryMongoCommand() {

                @Override
                public Object execute() {
                    batchJobMongoTemplate.save(job);
                    return null;
                }

            };
            retry.executeOperation(numberOfRetries);
        }
    }

    @Override
    public void saveBatchJobStage(String batchJobId, Stage stage) {
        stage.setJobId(batchJobId);
        batchJobMongoTemplate.save(stage, BATCHJOB_STAGE_SEPARATE_COLLECTION);
    }

    @Override
    public List<Stage> getBatchJobStages(String batchJobId) {
        return batchJobMongoTemplate.find(query(where("jobId").is(batchJobId)), Stage.class,
                BATCHJOB_STAGE_SEPARATE_COLLECTION);
    }

    @Override
    public NewBatchJob findBatchJobById(String batchJobId) {
        return batchJobMongoTemplate.findOne(query(where("_id").is(batchJobId)), NewBatchJob.class);
    }

    @Override
    public List<Error> findBatchJobErrors(String jobId) {
        List<Error> errors = batchJobMongoTemplate.find(query(where(BATCHJOBID_FIELDNAME).is(jobId)), Error.class,
                BATCHJOB_ERROR_COLLECTION);
        return errors;
    }

    public NewBatchJob findLatestBatchJob() {
        Query query = new Query();
        query.sort().on("jobStartTimestamp", Order.DESCENDING);
        query.limit(1);
        List<NewBatchJob> sortedBatchJobs = batchJobMongoTemplate.find(query, NewBatchJob.class);
        if (sortedBatchJobs == null || sortedBatchJobs.size() == 0) {
            return null;
        }
        return batchJobMongoTemplate.find(query, NewBatchJob.class).get(0);
    }

    @Override
    public void saveError(Error error) {
        if (error != null && "true".equals(trackIngestionErrors) && ERROR.equalsIgnoreCase(error.getSeverity())) {
            batchJobMongoTemplate.save(error);
        }

        if (error != null && "true".equals(trackIngestionWarnings) && WARNING.equalsIgnoreCase(error.getSeverity())) {
            batchJobMongoTemplate.save(error);
        }
    }

    @Override
    public Iterable<Error> getBatchJobErrors(String jobId, String fileName, FaultType type, int limit) {
        return batchJobMongoTemplate.find(
                Query.query(
                        Criteria.where(BATCHJOBID_FIELDNAME).is(jobId).and(FILE_NAME_FIELD).is(fileName)
                                .and(SEVERITY_FIELD).is(type.getName())).limit(limit), Error.class,
                BATCHJOB_ERROR_COLLECTION);
    }

    @Override
    public boolean createTransformationLatch(String jobId, String recordType, int count) {
        try {

            final BasicDBObject latchObject = new BasicDBObject();
            latchObject.put("syncStage", MessageType.DATA_TRANSFORMATION.name());
            latchObject.put("jobId", jobId);
            latchObject.put("recordType", recordType);
            latchObject.put("count", count);

            RetryMongoCommand retry = new RetryMongoCommand() {

                @Override
                public Object execute() {
                    batchJobMongoTemplate.getCollection(TRANSFORMATION_LATCH).insert(latchObject, WriteConcern.SAFE);
                    return null;
                }

            };
            retry.executeOperation(numberOfRetries);

        } catch (MongoException me) {
            if (me.getCode() == DUP_KEY_CODE) {
                LOG.debug(me.getMessage());
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean createPersistanceLatch(List<Map<String, Object>> defaultPersistenceLatch, String jobId) {
        try {
            final BasicDBObject latchObject = new BasicDBObject();
            latchObject.put("syncStage", MessageType.PERSIST_REQUEST.name());
            latchObject.put("jobId", jobId);
            latchObject.put("entities", defaultPersistenceLatch);

            RetryMongoCommand retry = new RetryMongoCommand() {

                @Override
                public Object execute() {
                    batchJobMongoTemplate.getCollection(PERSISTENCE_LATCH).insert(latchObject, WriteConcern.SAFE);
                    return null;
                }

            };

            retry.executeOperation(numberOfRetries);

        } catch (MongoException me) {
            if (me.getCode() == DUP_KEY_CODE) {
                LOG.debug(me.getMessage());
            }
            return false;
        }
        return true;

    }

    @Override
    public boolean countDownLatch(String syncStage, String jobId, String recordType) {
        if (syncStage.equals(MessageType.DATA_TRANSFORMATION.name())) {
            return countDownTransformationLatch(jobId, recordType);
        } else {
            return countDownPersistenceLatches(jobId, recordType);
        }
    }

    private boolean countDownTransformationLatch(String jobId, String recordType) {

        final BasicDBObject query = new BasicDBObject();
        query.put("syncStage", MessageType.DATA_TRANSFORMATION.name());
        query.put("jobId", jobId);
        query.put("recordType", recordType);

        BasicDBObject decrementCount = new BasicDBObject("count", -1);
        final BasicDBObject update = new BasicDBObject("$inc", decrementCount);

        RetryMongoCommand retry = new RetryMongoCommand() {

            @Override
            public Object execute() {

                return batchJobMongoTemplate.getCollection(TRANSFORMATION_LATCH).findAndModify(query, null, null,
                        false, update, true, false);
            }

        };
        DBObject latchObject = (DBObject) retry.executeOperation(numberOfRetries);

        return (Integer) latchObject.get("count") <= 0;
    }

    @SuppressWarnings("unchecked")
    private boolean countDownPersistenceLatches(String jobId, String recordType) {

        final BasicDBObject query = new BasicDBObject();
        query.put("syncStage", MessageType.PERSIST_REQUEST.name());
        query.put("jobId", jobId);
        query.put("entities.type", recordType);

        BasicDBObject decrementCount = new BasicDBObject("entities.$.count", -1);
        final BasicDBObject update = new BasicDBObject("$inc", decrementCount);

        RetryMongoCommand retry = new RetryMongoCommand() {

            @Override
            public Object execute() {

                return batchJobMongoTemplate.getCollection(PERSISTENCE_LATCH).findAndModify(query, null, null, false,
                        update, true, false);
            }

        };
        DBObject latchObject = (DBObject) retry.executeOperation(numberOfRetries);

        List<Map<String, Object>> entities = (List<Map<String, Object>>) latchObject.get("entities");

        boolean isEmpty = true;

        for (Map<String, Object> entityMap : entities) {
            int count = (Integer) entityMap.get("count");
            if (count > 0) {
                isEmpty = false;
            }
        }
        return isEmpty;
    }

    @Override
    public void setPersistenceLatchCount(String jobId, String collectionNameAsStaged, int size) {
        final BasicDBObject query = new BasicDBObject();
        query.put("syncStage", MessageType.PERSIST_REQUEST.name());
        query.put("jobId", jobId);
        query.put("entities.type", collectionNameAsStaged);

        BasicDBObject decrementCount = new BasicDBObject("entities.$.count", size);
        final BasicDBObject update = new BasicDBObject("$set", decrementCount);

        RetryMongoCommand retry = new RetryMongoCommand() {

            @Override
            public Object execute() {

                return batchJobMongoTemplate.getCollection(PERSISTENCE_LATCH).findAndModify(query, null, null, false,
                        update, true, false);
            }

        };
        retry.executeOperation(numberOfRetries);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<IngestionStagedEntity> getStagedEntitiesForJob(String jobId) {
        BasicDBObject query = new BasicDBObject();
        query.put("jobId", jobId);

        DBObject entities = batchJobMongoTemplate.getCollection(STAGED_ENTITIES).findOne(query);
        Map<String, Boolean> entitiesMap = (Map<String, Boolean>) entities.get("entities");

        Set<IngestionStagedEntity> stagedEntities = new HashSet<IngestionStagedEntity>();
        for (Map.Entry<String, Boolean> entityEntry : entitiesMap.entrySet()) {
            // only return entitites that are not complete
            if (!entityEntry.getValue()) {
                stagedEntities.add(IngestionStagedEntity.createFromRecordType(entityEntry.getKey()));
            }
        }
        return stagedEntities;
    }

    @Override
    public void setStagedEntitiesForJob(Set<IngestionStagedEntity> stagedEntities, String jobId) {

        Map<String, Boolean> entitiesMap = new HashMap<String, Boolean>(stagedEntities.size());
        for (IngestionStagedEntity recordType : stagedEntities) {
            entitiesMap.put(recordType.getCollectionNameAsStaged(), Boolean.FALSE);
        }

        try {
            final BasicDBObject entities = new BasicDBObject();
            entities.put("jobId", jobId);
            entities.put("entities", entitiesMap);

            RetryMongoCommand retry = new RetryMongoCommand() {

                @Override
                public Object execute() {
                    batchJobMongoTemplate.getCollection(STAGED_ENTITIES).insert(entities, WriteConcern.SAFE);
                    return null;
                }

            };
            retry.executeOperation(numberOfRetries);
        } catch (MongoException me) {
            if (me.getCode() == DUP_KEY_CODE) {
                LOG.error("Error inserting entry for job to stageEntities collection. ", me);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean removeAllPersistedStagedEntitiesFromJob(String jobId) {
        DBCursor cursor = getWorkNoteLatchesForStage(jobId, MessageType.PERSIST_REQUEST.name());
        boolean isEmpty = false;

        while (cursor.hasNext()) {
            DBObject latch = cursor.next();
            List<Map<String, Object>> entities = (List<Map<String, Object>>) latch.get("entities");
            for (Map<String, Object> entityMap : entities) {
                isEmpty = markStagedEntityComplete((String) entityMap.get("type"), jobId);
            }
        }

        return isEmpty;
    }

    /**
     * Mark the staged entity for the provided record type as complete for this job.
     *
     * @param recordType
     * @param jobId
     * @return True, if all staged entities are complete for this job as a result of this operation.
     */
    @SuppressWarnings("unchecked")
    protected boolean markStagedEntityComplete(String recordType, String jobId) {

        final BasicDBObject query = new BasicDBObject();
        query.put("jobId", jobId);

        BasicDBObject setEntityComplete = new BasicDBObject("entities." + recordType, Boolean.TRUE);
        final BasicDBObject update = new BasicDBObject("$set", setEntityComplete);
        RetryMongoCommand retry = new RetryMongoCommand() {

            @Override
            public Object execute() {

                return batchJobMongoTemplate.getCollection(STAGED_ENTITIES).findAndModify(query, null, null, false,
                        update, true, false);
            }

        };

        DBObject dbStagedEntities = (DBObject) retry.executeOperation(numberOfRetries);

        // return whether all staged entities now complete
        Map<String, Boolean> entitiesMap = (Map<String, Boolean>) dbStagedEntities.get("entities");
        for (Map.Entry<String, Boolean> entityEntry : entitiesMap.entrySet()) {
            if (!entityEntry.getValue()) {
                return false;
            }
        }
        return true;
    }

    private DBCursor getWorkNoteLatchesForStage(String jobId, String syncStage) {

        BasicDBObject ref = new BasicDBObject();
        ref.put("syncStage", syncStage);
        ref.put("jobId", jobId);

        DBCursor cursor;

        if (syncStage.equals(MessageType.DATA_TRANSFORMATION.name())) {
            cursor = batchJobMongoTemplate.getCollection(TRANSFORMATION_LATCH).find(ref);
        } else {
            cursor = batchJobMongoTemplate.getCollection(PERSISTENCE_LATCH).find(ref);
        }

        return cursor;

    }

    @Override
    public void cleanUpWorkNoteLatchAndStagedEntites(String jobId) {
        BasicDBObject dbObj = new BasicDBObject();
        dbObj.put("jobId", jobId);
        batchJobMongoTemplate.getCollection(TRANSFORMATION_LATCH).remove(dbObj);
        batchJobMongoTemplate.getCollection(PERSISTENCE_LATCH).remove(dbObj);
        batchJobMongoTemplate.getCollection(STAGED_ENTITIES).remove(dbObj);
    }

    public void setBatchJobMongoTemplate(MongoTemplate mongoTemplate) {
        this.batchJobMongoTemplate = mongoTemplate;
    }

    public void setBatchJobHashCacheMongoTemplate(MongoTemplate batchJobHashCacheMongoTemplate) {
        this.batchJobHashCacheMongoTemplate = batchJobHashCacheMongoTemplate;
    }

    public void setNumberOfRetries(int numberOfRetries) {
        this.numberOfRetries = numberOfRetries;
    }

    public MongoTemplate getSliMongo() {
        return sliMongo;
    }

    public void setSliMongo(MongoTemplate sliMongo) {
        this.sliMongo = sliMongo;
    }

    @Override
    public void insertRecordHash(String tenantId, String recordId, String newHashValues) throws DataAccessResourceFailureException {
    
    	// record was not found
        RecordHash rh = new RecordHash();
        rh._id = recordId;
        rh.hash = newHashValues;
        rh.tenantId = tenantId;
        rh.created = "" + System.currentTimeMillis();
        rh.updated = rh.created;
        this.batchJobHashCacheMongoTemplate.save(rh, RECORD_HASH);
    }
    
    public void updateRecordHash(String tenantId, RecordHash rh, String newHashValues) throws DataAccessResourceFailureException {
        rh.hash = newHashValues;
        rh.updated = "" + System.currentTimeMillis();
        rh.version += 1;
        // Detect tenant collision - should never occur since tenantId is in the hash
        if ( ! rh.tenantId.equals(tenantId) )
        	throw new DataAccessResourceFailureException("Tenant mismatch: recordHash cache has '" + rh.tenantId + "', input data has '" + tenantId + "' for entity ID '" + rh._id + "'");
        this.batchJobHashCacheMongoTemplate.save(rh, RECORD_HASH);
    }

    @Override
    public RecordHash findRecordHash(String tenantId, String recordId) {
        Query query = new Query().limit(1);
        // query.addCriteria(Criteria.where("tenantId").is(tenantId));
        query.addCriteria(Criteria.where("_id").is(recordId));
        return this.batchJobHashCacheMongoTemplate.findOne(query, RecordHash.class, RECORD_HASH);
    }

    @Override
    public void removeRecordHashByTenant(String tenantId) {
        // batchJobMongoTemplate.remove(new
        // Query(Criteria.where("_id").regex("^"+TenantContext.getTenantId()+"-"+"[a-z|A-Z|0-9|-]*")),
        // RECORD_HASH);
        Query searchTenantId = new Query();
        searchTenantId.addCriteria(Criteria.where(EntityMetadataKey.TENANT_ID.getKey()).is(tenantId));
        batchJobHashCacheMongoTemplate.remove(searchTenantId, RECORD_HASH);
    }

}
