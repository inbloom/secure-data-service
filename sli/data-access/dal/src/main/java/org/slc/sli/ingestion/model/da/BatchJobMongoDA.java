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

package org.slc.sli.ingestion.model.da;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.dal.RetryMongoCommand;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.landingzone.AttributeType;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.RecordHash;
import org.slc.sli.ingestion.model.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;

/**
 *
 * @author ldalgado
 *
 */

/*
 * @author rcook
 * This class was moved from the ingestion-core project to the dal project, along with ~20 other supporting classes.
 * This was done in support of a request to make ingestion job data available via the API; until this, the only
 * project in the inBloom system that accessed ingestion job data was the ingestion project.  In order to avoid
 * making the API project dependent on the ingestion project, the necessary classes were moved to the dal project,
 * which contains library classes and was already a dependency for both ingestion-core and api.  In the course of the
 * resulting work, ingestion-base was also made dependent on dal.
 */
@Component
public class BatchJobMongoDA implements BatchJobDAO {

    private static final Logger LOG = LoggerFactory.getLogger(BatchJobMongoDA.class);

    private static final String BATCHJOB_ERROR_COLLECTION = "error";
    private static final String BATCHJOB_STAGE_SEPARATE_COLLECTION = "batchJobStage";

    private static final String ERROR = "error";
    private static final String WARNING = "warning";
    private static final String BATCHJOBID_FIELDNAME = "batchJobId";
    private static final String RESOURCE_ID_FIELD = "resourceId";
    private static final String SEVERITY_FIELD = "severity";
    private static final String RECORD_HASH = "recordHash";
    private static final String JOB_ID = "jobId";
    private static final String STAGE_NAME = "stageName";
    private static final int DUP_KEY_CODE = 11000;
    private static final String FILE_ENTRY_LATCH = "fileEntryLatch";
    private static final String FILES = "files";

    @Value("${sli.ingestion.totalRetries}")
    private int numberOfRetries;

    private MongoTemplate batchJobMongoTemplate;

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
        return batchJobMongoTemplate.find(query(where(JOB_ID).is(batchJobId)), Stage.class,
                BATCHJOB_STAGE_SEPARATE_COLLECTION);
    }

    @Override
    public List<Stage> getBatchJobStages(String batchJobId, BatchJobStageType stageType) {
        return batchJobMongoTemplate.find(query(where(JOB_ID).is(batchJobId).and(STAGE_NAME).is(stageType.getName())), Stage.class,
                BATCHJOB_STAGE_SEPARATE_COLLECTION);
    }

    @Override
    public NewBatchJob findBatchJobById(String batchJobId) {
        return batchJobMongoTemplate.findOne(query(where("_id").is(batchJobId)), NewBatchJob.class);
    }
    
    public List<NewBatchJob> findLatestBatchJobs(int limit)
    {
        Query query = new Query();
        query.sort().on("jobStartTimestamp", Order.DESCENDING);
        query.limit(limit);
        List<NewBatchJob> sortedBatchJobs = batchJobMongoTemplate.find(query, NewBatchJob.class);
        if (sortedBatchJobs.size() == 0) { sortedBatchJobs = null; } 
        return sortedBatchJobs;
    }

    public NewBatchJob findLatestBatchJob() 
    {
    	NewBatchJob result = null;
    	List<NewBatchJob> list = findLatestBatchJobs(1);
    	if (list != null && list.size() > 0) { result = list.get(0); }
    	return result;
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
    public Iterable<Error> getBatchJobErrors(String jobId, String resourceId, FaultType type, int limit) {
        return batchJobMongoTemplate.find(
                Query.query(
                        Criteria.where(BATCHJOBID_FIELDNAME).is(jobId).and(RESOURCE_ID_FIELD).is(resourceId)
                                .and(SEVERITY_FIELD).is(type.getName())).limit(limit), Error.class,
                BATCHJOB_ERROR_COLLECTION);
    }

    public void setBatchJobMongoTemplate(MongoTemplate mongoTemplate) {
    	LOG.debug("setting batchJobMongoTemplate");
        this.batchJobMongoTemplate = mongoTemplate;
    }

    public MongoTemplate getSliMongo() {
        return sliMongo;
    }

    public void setSliMongo(MongoTemplate sliMongo) {
    	LOG.debug("setting sliMongo");
        this.sliMongo = sliMongo;
    }

    /*
     * @param tenantId
     *         The tenant Id
     * @param recordId
     *         A 40-char hex string suffixed with "_id" identifying the object hashed
     * @param newHashValues
     *         The (initial) value of the record hash, a 40-character hex string
     */
    @Override
    public void insertRecordHash(String recordId, String newHashValues) throws DataAccessResourceFailureException {

        // record was not found
        RecordHash rh = new RecordHash();
        rh.setId(recordId);
        rh.setHash(newHashValues);
        rh.setCreated(System.currentTimeMillis());
        rh.setUpdated(rh.getCreated());
        TenantContext.setIsSystemCall(false);
        this.sliMongo.getCollection(RECORD_HASH).insert(new BasicDBObject(rh.toKVMap()));
    }

    /*
     *
     * @param rh
     *         The RecordHash object to be removed from the database
     *
     */
    @Override
    public void removeRecordHash( RecordHash rh) throws DataAccessResourceFailureException {

        // Detect tenant collision - should never occur since tenantId is in the hash
        TenantContext.setIsSystemCall(false);
        this.sliMongo.getCollection(RECORD_HASH).remove(recordHashQuery(rh.getId()).getQueryObject());

    }
    /*
     * @param tenantId
     *         The tenant Id
     * @param rh
     *         The RecordHash object to be updated in the database
     * @param newHashValues
     *         The (updated) value of the record hash, a 40-character hex string
     */
    @Override
    public void updateRecordHash(RecordHash rh, String newHashValues) throws DataAccessResourceFailureException {
        rh.setHash(newHashValues);
        rh.setUpdated(System.currentTimeMillis());
        rh.setVersion(rh.getVersion() + 1);
        // Detect tenant collision - should never occur since tenantId is in the hash
        TenantContext.setIsSystemCall(false);
        this.sliMongo.getCollection(RECORD_HASH).update(recordHashQuery(rh.getId()).getQueryObject(), new BasicDBObject(rh.toKVMap()));
    }

    /*
     * @param tenantId
     *         The tenant Id
     * @param recordId
     *         A 40-char hex string suffixed with "_id" identifying the object hashed
     *
     */
    @Override
    public RecordHash findRecordHash(String tenantId, String recordId) {
        TenantContext.setIsSystemCall(false);
        Map<String, Object> map = this.sliMongo.findOne(recordHashQuery(recordId), Map.class, RECORD_HASH);
        if (null == map) {
            return null;
        }
        return new RecordHash(map);
    }

    /*
     * Get SpringData Query object that locates a recordHash item by its recordId
     *
     * @param recordId
     *         A 40-char hex string suffixed with "_id" identifying the object hashed
     * @return
     *         The SpringDadta Query object that looks the record up in the recordHash collection.
     */
    public Query recordHashQuery(String recordId) {
        Query query = new Query().limit(1);
        query.addCriteria(Criteria.where("_id").is(RecordHash.hex2Binary(recordId)));
        return query;
    }

    @Override
    public void removeRecordHashByTenant(String tenantId) {
        TenantContext.setIsSystemCall(false);
        sliMongo.remove(new Query(), RECORD_HASH);
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return sliMongo;
    }

    @Override
    public boolean updateFileEntryLatch(String batchJobId, String filename) {
        final BasicDBObject query = new BasicDBObject();
        query.put(BATCHJOBID_FIELDNAME, batchJobId);

        BasicDBObject files = new BasicDBObject("files", filename);
        final BasicDBObject update = new BasicDBObject("$pull", files);
        RetryMongoCommand retry = new RetryMongoCommand() {

            @Override
            public Object execute() {

                return batchJobMongoTemplate.getCollection(FILE_ENTRY_LATCH).findAndModify(query, null, null, false,
                        update, true, false);
            }

        };
        DBObject fileEntryLatch = (DBObject) retry.executeOperation(numberOfRetries);

        List<String> file = (List<String>) fileEntryLatch.get("files");

        if (file == null || file.isEmpty() ) {
            return true;
        }

        return false;
    }

    @Override
    public boolean createFileLatch(String jobId, List<String> fileEntries) {
        try {
            final BasicDBObject latchObject = new BasicDBObject();
            latchObject.put(BATCHJOBID_FIELDNAME, jobId);
            latchObject.put(FILES, fileEntries);

            RetryMongoCommand retry = new RetryMongoCommand() {

                @Override
                public Object execute() {
                    batchJobMongoTemplate.getCollection(FILE_ENTRY_LATCH).insert(latchObject, WriteConcern.SAFE);
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
    public boolean isDryRun(String jobId) {
        Map<String, String> batchProperties = getBatchProperties(jobId);
        for (Entry<String, String> property : batchProperties.entrySet()) {
            if(property.getKey().equals(AttributeType.DRYRUN.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDuplicateDetectionMode(String jobId) {
        Map<String, String> batchProperties = getBatchProperties(jobId);
        for (Entry<String, String> property : batchProperties.entrySet()) {
            if(property.getKey().equals(AttributeType.DUPLICATE_DETECTION.getName())) {
                return property.getValue();
            }
        }
        return null;
    }

    public Map<String, String> getBatchProperties(String jobId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(jobId));
        NewBatchJob job = batchJobMongoTemplate.findOne(query, NewBatchJob.class);
        return job.getBatchProperties();
    }

    public void audit(SecurityEvent event) {
        // TODO Auto-generated method stub

    }

}
