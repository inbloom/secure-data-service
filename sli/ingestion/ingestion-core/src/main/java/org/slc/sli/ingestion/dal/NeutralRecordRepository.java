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

package org.slc.sli.ingestion.dal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mongodb.DBCollection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.dal.RetryMongoCommand;
import org.slc.sli.dal.repository.MongoQueryConverter;
import org.slc.sli.dal.repository.MongoRepository;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.NeutralRecord;

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

    @Autowired
    private MongoQueryConverter queryConverter;

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

    public NeutralRecord createForJob(NeutralRecord neutralRecord) {
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

    public NeutralRecord insertWithRetries(final NeutralRecord neutralRecord, int numberOfRetries) {
        RetryMongoCommand rc = new RetryMongoCommand() {

            @Override
            public Object execute() {
                return insert(neutralRecord);
            }
        };
        return (NeutralRecord) rc.executeOperation(numberOfRetries);
    }

    public NeutralRecord insert(NeutralRecord neutralRecord) {
        return insert(neutralRecord, neutralRecord.getRecordType());
    }

    @SuppressWarnings("unchecked")
    public List<NeutralRecord> insertAllWithRetries(final List<NeutralRecord> entities, final String collectionName,
            int noOfRetries) {
        RetryMongoCommand rc = new RetryMongoCommand() {

            @Override
            public Object execute() {
                return insertAll(entities, collectionName);
            }
        };
        return (List<NeutralRecord>) rc.executeOperation(noOfRetries);
    }

    public List<NeutralRecord> insertAll(List<NeutralRecord> entities, String collectionName) {
        return insert(entities, collectionName);
    }

    public List<NeutralRecord> insertAllForJob(List<NeutralRecord> entities, String collectionName) {
        return insertAll(entities, collectionName);
    }

    @SuppressWarnings("deprecation")
    public Iterable<NeutralRecord> findAllByQuery(String collectionName, Query query) {
        return findByQuery(collectionName, query);
    }

    public Iterable<NeutralRecord> findAllForJob(String collectionName, NeutralQuery neutralQuery) {
        neutralQuery.setIncludeFieldString("*");
        return findAll(collectionName, neutralQuery);
    }

    @SuppressWarnings("deprecation")
    public Iterable<NeutralRecord> findByPathsForJob(String collectionName, Map<String, String> paths, String jobId) {
        paths.put(BATCH_JOB_ID, jobId);
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setIncludeFieldString("*");
        Query query = this.queryConverter.convert(collectionName, neutralQuery);
        return findByQuery(collectionName, addSearchPathsToQuery(query, paths));
    }

    private Query addSearchPathsToQuery(Query query, Map<String, String> searchPaths) {
        for (Map.Entry<String, String> field : searchPaths.entrySet()) {
            Criteria criteria = Criteria.where(field.getKey()).is(field.getValue());
            query.addCriteria(criteria);
        }

        return query;
    }

    public NeutralRecord findOneForJob(String collectionName, NeutralQuery neutralQuery) {
        neutralQuery.setIncludeFieldString("*");
        return findOne(collectionName, neutralQuery);
    }

    public DBCollection getCollectionForJob(String collectionName) {
        return getCollection(collectionName);
    }

    public long countForJob(String collectionName, NeutralQuery neutralQuery) {
        neutralQuery.setIncludeFieldString("*");
        return count(collectionName, neutralQuery);
    }

    public long countByQuery(String collectionName, Query query) {
        return count(collectionName, query);
    }

    public Set<String> getStagedCollectionsForJob() {
        Set<String> collectionNamesForJob = new HashSet<String>();
            LOG.info("Checking staged collection counts");
            Query query = new Query().limit(0);
            query.addCriteria(Criteria.where(CREATION_TIME).gt(0));

            for (String currentCollection : getCollectionNames()) {
                long count = countByQuery(currentCollection, query);
                if (count > 0) {
                    collectionNamesForJob.add(currentCollection);
                }
                LOG.debug("Count for collection: {} ==> {} [query: {}]",
                        new Object[] { currentCollection, count, query });
            }
        return collectionNamesForJob;
    }

    public void deleteStagedRecordsForJob(String batchJobId) {
        LOG.info("Dropping db for job: {}", batchJobId);

        TenantContext.setJobId(batchJobId);
        getTemplate().getDb().dropDatabase();
    }

    public void updateFirstForJob(NeutralQuery query, Map<String, Object> update, String collectionName) {
        query.setIncludeFieldString("*");
        updateFirst(query, update, collectionName);
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
        // Does nothing
    }

    // TODO FIXME hack for alpha release 6/18/12 - need to properly implement unsupported methods
    // above.
    NeutralRecord create(NeutralRecord record, String collectionName) {
        template.save(record, collectionName);
        LOG.debug(" create a record in collection {} with id {}", new Object[] { collectionName, getRecordId(record) });
        return record;
    }

    @Override
    public boolean updateWithRetries(String collection, NeutralRecord object, int noOfRetries) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public NeutralRecord createWithRetries(String type, String id, Map<String, Object> body,
            Map<String, Object> metaData, String collectionName, int noOfRetries) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Iterable<NeutralRecord> findAllAcrossTenants(String collectionName, Query mongoQuery) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NeutralRecord findAndUpdate(String collectionName, NeutralQuery neutralQuery, Update update) {
        return null;
    }
}
