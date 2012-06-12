package org.slc.sli.ingestion.dal;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.mongodb.DBCollection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

import org.slc.sli.dal.repository.MongoRepository;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.util.LogUtil;

/**
 * Specialized class providing basic CRUD and field query methods for neutral records
 * using a Mongo Staging DB, for use for staging data for intermediate operations.
 *
 * @author Thomas Shewchuk tshewchuk@wgen.net 2/23/2012 (PI3 US1226)
 *
 */
public class NeutralRecordRepository extends MongoRepository<NeutralRecord> {
    // public class NeutralRecordRepository {

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
        return create(neutralRecord, toStagingCollectionName(neutralRecord.getRecordType(), jobId));
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
        LOG.info("ENSURING ALL INDEXES FOR A DB");

        Set<String> collectionNames = mongoIndexManager.getCollectionIndexes().keySet();
        Iterator<String> it = collectionNames.iterator();
        String collectionName;

        while (it.hasNext()) {
            collectionName = it.next();
            LOG.info("INDEXING COLLECTION: {} ==> staged as {} ", collectionName, toStagingCollectionName(collectionName, batchJobId));

            if (!collectionExistsForJob(collectionName, batchJobId)) {
                createCollectionForJob(collectionName, batchJobId);
            }

            try {
                for (IndexDefinition definition : mongoIndexManager.getCollectionIndexes().get(collectionName)) {
                    LOG.info("Adding Index: {}", definition);
                    ensureIndex(definition, toStagingCollectionName(collectionName, batchJobId));
                }
            } catch (Exception e) {
                LogUtil.error(LOG, "Failed to create mongo indexes for collection " + collectionName, e);
            }
        }
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

}
