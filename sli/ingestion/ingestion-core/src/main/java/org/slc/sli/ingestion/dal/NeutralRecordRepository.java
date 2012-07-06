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

package org.slc.sli.ingestion.dal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slc.sli.dal.repository.MongoRepository;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.NeutralRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

import com.mongodb.DBCollection;

/**
 * Specialized class providing basic CRUD and field query methods for neutral records
 * using a Mongo Staging DB, for use for staging data for intermediate operations.
 * 
 * @author Thomas Shewchuk tshewchuk@wgen.net 2/23/2012 (PI3 US1226)
 * 
 */
public class NeutralRecordRepository extends MongoRepository<NeutralRecord> {
    
    protected static final Logger LOG = LoggerFactory.getLogger(NeutralRecordRepository.class);
    
    private static final String BATCH_JOB_ID = "batchJobId";
    private static final String CREATION_TIME = "creationTime";
    
    @Override
    public boolean update(String collection, NeutralRecord neutralRecord) {
        return update(neutralRecord.getRecordType(), neutralRecord, null);
    }
    
    @Override
    protected Query getUpdateQuery(NeutralRecord entity) {
        throw new UnsupportedOperationException("NeutralReordRepository.getUpdateQuery not implemented");
    }
    
    @Override
    protected NeutralRecord getEncryptedRecord(NeutralRecord entity) {
        throw new UnsupportedOperationException("NeutralReordRepository.getEncryptedRecord not implemented");
    }
    
    @Override
    protected Update getUpdateCommand(NeutralRecord entity) {
        throw new UnsupportedOperationException("NeutralReordRepository.getUpdateCommand not implemented");
    }
    
    @Override
    public NeutralRecord create(String type, Map<String, Object> body, Map<String, Object> metaData,
            String collectionName) {
        Assert.notNull(body, "The given entity must not be null!");
        NeutralRecord neutralRecord = new NeutralRecord();
        neutralRecord.setLocalId(metaData.get("externalId"));
        neutralRecord.setAttributes(body);
        return create(neutralRecord, collectionName);
    }
    
    public NeutralRecord createForJob(NeutralRecord neutralRecord, String jobId) {
        Map<String, Object> body = neutralRecord.getAttributes();
        if (body == null) {
            body = new HashMap<String, Object>();
        }
        neutralRecord.setAttributes(body);
        return create(neutralRecord, neutralRecord.getRecordType());
    }
    
    public NeutralRecord insert(String type, Map<String, Object> body, Map<String, Object> metaData,
            String collectionName) {
        Assert.notNull(body, "The given entity must not be null!");
        NeutralRecord neutralRecord = new NeutralRecord();
        neutralRecord.setLocalId(metaData.get("externalId"));
        neutralRecord.setAttributes(body);
        return insert(neutralRecord, collectionName);
    }
    
    public NeutralRecord insertForJob(NeutralRecord neutralRecord, String jobId) {
        return insert(neutralRecord, neutralRecord.getRecordType());
    }
    
    public List<NeutralRecord> insertAll(List<NeutralRecord> entities, String collectionName, String jobId) {
        return insert(entities, collectionName);
    }
    
    public List<NeutralRecord> insertAllForJob(List<NeutralRecord> entities, String collectionName, String jobId) {
        return insertAll(entities, collectionName, jobId);
    }
    
    @SuppressWarnings("deprecation")
    public Iterable<NeutralRecord> findAllByQuery(String collectionName, Query query) {
        return findByQuery(collectionName, query);
    }
    
    public Iterable<NeutralRecord> findAllForJob(String collectionName, String jobId, NeutralQuery neutralQuery) {
        return findAll(collectionName, prependBatchJobIdOntoQuery(neutralQuery, jobId));
    }
    
    @SuppressWarnings("deprecation")
    public Iterable<NeutralRecord> findByPathsForJob(String collectionName, Map<String, String> paths, String jobId) {
        paths.put(BATCH_JOB_ID, jobId);
        return findByPaths(collectionName, paths);
    }
    
    public NeutralRecord findOneForJob(String collectionName, NeutralQuery neutralQuery, String jobId) {
        return findOne(collectionName, prependBatchJobIdOntoQuery(neutralQuery, jobId));
    }
    
    public DBCollection getCollectionForJob(String collectionName) {
        return getCollection(collectionName);
    }
    
    public long countForJob(String collectionName, NeutralQuery neutralQuery, String jobId) {
        return count(collectionName, prependBatchJobIdOntoQuery(neutralQuery, jobId));
    }
    
    public long countByQuery(String collectionName, Query query) {
        return count(collectionName, query);
    }
    
    public Set<String> getStagedCollectionsForJob(String batchJobId) {
        Set<String> collectionNamesForJob = new HashSet<String>();
        if (batchJobId != null) {
            LOG.info("Checking staged collection counts for batch job id: {}", batchJobId);
            Query query = new Query().limit(0);
            query.addCriteria(Criteria.where(BATCH_JOB_ID).is(batchJobId));
            query.addCriteria(Criteria.where(CREATION_TIME).gt(0));
            
            for (String currentCollection : getTemplate().getCollectionNames()) {
                long count = countByQuery(currentCollection, query);
                if (count > 0) {
                    collectionNamesForJob.add(currentCollection);
                }
                LOG.info("Count for collection: {} ==> {} [query: {}]",
                        new Object[] { currentCollection, count, query });
            }
        }
        return collectionNamesForJob;
    }
    
    public void deleteStagedRecordsForJob(String batchJobId) {
        if (batchJobId != null) {
            Query query = new Query(Criteria.where(BATCH_JOB_ID).is(batchJobId));
            for (String currentCollection : getTemplate().getCollectionNames()) {
                if (!currentCollection.startsWith("system.")) {
                    LOG.info("Removing staged entities in collection: {} for batch job: {}", currentCollection,
                            batchJobId);
                    getTemplate().remove(query, currentCollection);
                }
            }
        }
    }
    
    public void updateFirstForJob(NeutralQuery query, Map<String, Object> update, String collectionName, String jobId) {
        update(prependBatchJobIdOntoQuery(query, jobId), update, collectionName);
    }
    
    @Override
    protected String getRecordId(NeutralRecord neutralRecord) {
        return neutralRecord.getRecordId();
    }
    
    @Override
    protected Class<NeutralRecord> getRecordClass() {
        return NeutralRecord.class;
    }
    
    @Override
    public void setReferenceCheck(String referenceCheck) {
        
    }
    
    public NeutralQuery prependBatchJobIdOntoQuery(NeutralQuery query, String jobId) {
        NeutralCriteria criteria = new NeutralCriteria(BATCH_JOB_ID, NeutralCriteria.OPERATOR_EQUAL, jobId, false);
        if (!query.getCriteria().contains(criteria)) {
            query.prependCriteria(criteria);
        }
        return query;
    }
    
    // TODO FIXME hack for alpha release 6/18/12 - need to properly implement unsupported methods
    // above.
    public NeutralRecord create(NeutralRecord record, String collectionName) {
        template.save(record, collectionName);
        LOG.debug(" create a record in collection {} with id {}", new Object[] { collectionName, getRecordId(record) });
        return record;
    }
}
