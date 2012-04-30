package org.slc.sli.ingestion.dal;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.dal.repository.MongoRepository;
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

    private EntityEncryption entityEncryption;

    private MongoIndexManager mongoIndexManager;

    public MongoIndexManager getMongoIndexManager() {
        return mongoIndexManager;
    }

    public void setMongoIndexManager(MongoIndexManager mongoIndexManager) {
        this.mongoIndexManager = mongoIndexManager;
    }

    @Override
    public boolean update(String collection, NeutralRecord neutralRecord) {
        return update(neutralRecord.getRecordType(), neutralRecord, null);
    }

    @Override
    public NeutralRecord create(String type, Map<String, Object> body, Map<String, Object> metaData, String collectionName) {
        Assert.notNull(body, "The given entity must not be null!");
        NeutralRecord neutralRecord = new NeutralRecord();
        neutralRecord.setLocalId(metaData.get("externalId"));
        neutralRecord.setAttributes(body);
        return create(neutralRecord, collectionName);
    }

    public NeutralRecord create(NeutralRecord neutralRecord) {
        return create(neutralRecord, neutralRecord.getRecordType());
    }


    @Override
    protected String getRecordId(NeutralRecord neutralRecord) {
        return neutralRecord.getRecordId();
    }

    public Set<String> getCollectionNames() {
        if (isCollectionGrouping()) {
            Set<String> collectionSet = getTemplate().getCollectionNames();
            Iterator<String> iter = collectionSet.iterator();

            Set<String> currentCollections = new HashSet<String>();
            String currentCollection;

            while (iter.hasNext()) {
                currentCollection = iter.next();

                if (currentCollection.endsWith(getCollectionGroupingIdentifier())) {
                    currentCollections.add(currentCollection.replace("_" + getCollectionGroupingIdentifier(), ""));
                }
            }

            return currentCollections;
        }

        return getTemplate().getCollectionNames();
    }

    public void deleteGroupedCollections() {
        if (isCollectionGrouping()) {
            Set<String> collectionSet = getTemplate().getCollectionNames();
            Iterator<String> iter = collectionSet.iterator();

            String currentCollection;

            while (iter.hasNext()) {
                currentCollection = iter.next();

                if (currentCollection.endsWith(getCollectionGroupingIdentifier())) {
                    getTemplate().dropCollection(currentCollection);
                }
            }
        }
    }

    public void registerBatchId(String batchJobId) {
        setCollectionGrouping(true);
        setCollectionGroupingIdentifier(batchJobId);
    }

    public String getBatchJobId() {
        return getCollectionGroupingIdentifier();
    }

    @Override
    protected Class<NeutralRecord> getRecordClass() {
        return NeutralRecord.class;
    }

    public void setEntityEncryption(EntityEncryption entityEncryption) {
        this.entityEncryption = entityEncryption;
    }

}
