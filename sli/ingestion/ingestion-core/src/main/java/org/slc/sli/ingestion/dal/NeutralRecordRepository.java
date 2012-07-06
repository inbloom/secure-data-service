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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slc.sli.dal.repository.MongoRepository;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.index.IndexDefinition;
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
    
    private MongoIndexManager mongoIndexManager;
    
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
        return create(neutralRecord, toStagingCollectionName(neutralRecord.getRecordType(), jobId));
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
        return insert(neutralRecord, toStagingCollectionName(neutralRecord.getRecordType(), jobId));
    }
    
    public List<NeutralRecord> insertAll(List<NeutralRecord> entities, String collectionName, String jobId) {
        return insert(entities, toStagingCollectionName(collectionName, jobId));
    }
    
    public List<NeutralRecord> insertAllForJob(List<NeutralRecord> entities, String collectionName, String jobId) {
        return insertAll(entities, collectionName, jobId);
    }
    
    public Iterable<NeutralRecord> findAllForJob(String collectionName, String jobId, NeutralQuery neutralQuery) {
        return findAll(toStagingCollectionName(collectionName, jobId), neutralQuery);
    }
    
    @SuppressWarnings("deprecation")
    public Iterable<NeutralRecord> findByQueryForJob(String collectionName, Query query, String jobId, int skip, int max) {
        return findByQuery(toStagingCollectionName(collectionName, jobId), query, skip, max);
    }
    
    @SuppressWarnings("deprecation")
    public Iterable<NeutralRecord> findByQueryForJob(String collectionName, Query query, String jobId) {
        return findByQuery(toStagingCollectionName(collectionName, jobId), query);
    }
    
    @SuppressWarnings("deprecation")
    public Iterable<NeutralRecord> findByPathsForJob(String collectionName, Map<String, String> paths, String jobId) {
        return findByPaths(toStagingCollectionName(collectionName, jobId), paths);
    }
    
    public NeutralRecord findOneForJob(String collectionName, NeutralQuery neutralQuery, String jobId) {
        return findOne(toStagingCollectionName(collectionName, jobId), neutralQuery);
    }
    
    public NeutralRecord findOneForJob(String collectionName, Query query, String jobId) {
        return findOne(toStagingCollectionName(collectionName, jobId), query);
    }
    
    public DBCollection getCollectionForJob(String collectionName, String jobId) {
        return getCollection(toStagingCollectionName(collectionName, jobId));
    }
    
    public boolean collectionExistsForJob(String collectionName, String jobId) {
        return collectionExists(toStagingCollectionName(collectionName, jobId));
    }
    
    public void createCollectionForJob(String collectionName, String jobId) {
        createCollection(toStagingCollectionName(collectionName, jobId));
    }
    
    public long countForJob(String collectionName, NeutralQuery neutralQuery, String jobId) {
        return count(toStagingCollectionName(collectionName, jobId), neutralQuery);
    }
    
    public long countForJob(String collectionName, Query query, String jobId) {
        return count(toStagingCollectionName(collectionName, jobId), query);
    }
    
    public Set<String> getCollectionNamesForJob(String batchJobId) {
        Set<String> collectionNamesForJob = new HashSet<String>();
        
        if (batchJobId != null) {
            String jobIdPattern = "_" + toMongoCleanId(batchJobId);
            
            Set<String> allCollectionNames = getTemplate().getCollectionNames();
            for (String currentCollection : allCollectionNames) {
                
                int jobPatternIndex = currentCollection.indexOf(jobIdPattern);
                if (jobPatternIndex != -1) {
                    // creating indexes seems to create the collections.
                    // only add collections with count > 0
                    if (this.count(currentCollection, new NeutralQuery()) > 0) {
                        collectionNamesForJob.add(currentCollection.substring(0, jobPatternIndex));
                    }
                }
            }
        }
        return collectionNamesForJob;
    }
    
    public Set<String> getCollectionFullNamesForJob(String batchJobId) {
        Set<String> collectionNamesForJob = new HashSet<String>();
        
        if (batchJobId != null) {
            String jobIdPattern = "_" + toMongoCleanId(batchJobId);
            
            Set<String> allCollectionNames = getTemplate().getCollectionNames();
            for (String currentCollection : allCollectionNames) {
                
                int jobPatternIndex = currentCollection.indexOf(jobIdPattern);
                if (jobPatternIndex != -1) {
                    collectionNamesForJob.add(currentCollection);
                }
            }
        }
        return collectionNamesForJob;
    }
    
    public void deleteCollectionsForJob(String batchJobId) {
        if (batchJobId != null) {
            String jobIdPattern = "_" + toMongoCleanId(batchJobId);
            deleteTypedCollectionForJob(batchJobId, jobIdPattern);
        }
    }
    
    public void deleteTransformedCollectionsForJob(String batchJobId) {
        if (batchJobId != null) {
            String jobIdPattern = "_transformed_" + toMongoCleanId(batchJobId);
            deleteTypedCollectionForJob(batchJobId, jobIdPattern);
        }
    }
    
    private void deleteTypedCollectionForJob(String batchJobId, String jobIdPattern) {
        Set<String> allCollectionNames = getTemplate().getCollectionNames();
        for (String currentCollection : allCollectionNames) {
            
            int jobPatternIndex = currentCollection.indexOf(jobIdPattern);
            if (jobPatternIndex != -1) {
                getTemplate().dropCollection(currentCollection);
            }
        }
    }
    
    public void ensureIndexesForJob(String batchJobId) {
        LOG.info("ENSURING ALL INDEXES FOR A DB {} ", batchJobId);
        
        Set<String> collectionNames = mongoIndexManager.getCollectionIndexes().keySet();
        Iterator<String> it = collectionNames.iterator();
        String collectionName;
        
        while (it.hasNext()) {
            collectionName = it.next();
            LOG.info("INDEXING COLLECTION: {} ==> staged as {} ", collectionName,
                    toStagingCollectionName(collectionName, batchJobId));
            
            if (!collectionExistsForJob(collectionName, batchJobId)) {
                createCollectionForJob(collectionName, batchJobId);
            }
            
            try {
                for (IndexDefinition definition : mongoIndexManager.getCollectionIndexes().get(collectionName)) {
                    LOG.debug("Adding Index: {}", definition);
                    ensureIndex(definition, toStagingCollectionName(collectionName, batchJobId));
                }
            } catch (Exception e) {
                LogUtil.error(LOG, "Failed to create mongo indexes for collection " + collectionName, e);
            }
        }
        
        LOG.info("DONE ENSURING INDEXES FOR JOB {} ", batchJobId);
    }
    
    public void updateFirstForJob(NeutralQuery query, Map<String, Object> update, String collectionName, String jobId) {
        update(query, update, toStagingCollectionName(collectionName, jobId));
    }
    
    @Override
    protected String getRecordId(NeutralRecord neutralRecord) {
        return neutralRecord.getRecordId();
    }
    
    @Override
    protected Class<NeutralRecord> getRecordClass() {
        return NeutralRecord.class;
    }
    
    private static String toStagingCollectionName(String collectionName, String jobId) {
        return collectionName + "_" + toMongoCleanId(jobId);
    }
    
    private static String toMongoCleanId(String id) {
        return id.substring(id.length() - 37, id.length()).replace("-", "");
    }
    
    public void setMongoIndexManager(MongoIndexManager mongoIndexManager) {
        this.mongoIndexManager = mongoIndexManager;
    }
    
    @Override
    public void setReferenceCheck(String referenceCheck) {
        
    }
    
    // TODO FIXME hack for alpha release 6/18/12 - need to properly implement unsupported methods
    // above.
    public NeutralRecord create(NeutralRecord record, String collectionName) {
        template.save(record, collectionName);
        LOG.debug(" create a record in collection {} with id {}", new Object[] { collectionName, getRecordId(record) });
        return record;
    }
}
