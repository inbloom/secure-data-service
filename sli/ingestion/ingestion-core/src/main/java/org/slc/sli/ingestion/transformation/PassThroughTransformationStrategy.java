package org.slc.sli.ingestion.transformation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.NeutralRecord;

/**
 * Transformer for Program entities.
 *
 * @author vmcglaughlin
 */
@Scope("prototype")
@Component("PassThroughTransformationStrategy")
public class PassThroughTransformationStrategy extends AbstractTransformationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(PassThroughTransformationStrategy.class);

    private Map<String, Map<Object, NeutralRecord>> collections;

    @Autowired
    private List<String> passThroughCollectionList;

    public PassThroughTransformationStrategy() {
        this.collections = new HashMap<String, Map<Object, NeutralRecord>>();
    }

    /**
     * The chaining of transformation steps. This implementation assumes that all data will be
     * processed in "one-go"
     */
    @Override
    public void performTransformation() {
        loadData();
        transform();
        persist();
    }

    public void loadData() {
        LOG.info("Loading data for transformation.");

        if (passThroughCollectionList == null || passThroughCollectionList.isEmpty()) {
            LOG.warn("No collections specified to pass through, null or empty");
            return;
        }

        for (String collection : passThroughCollectionList) {
            loadCollectionFromDb(collection);
            LOG.info("{} is loaded into local storage.  Total Count = {}", collection, collections.get(collection).size());
        }
     }

    public void transform() {
        LOG.info("Transforming data: No transformation, straight pass-through");
    }

    public void persist() {
        for (Map.Entry<String, Map<Object, NeutralRecord>> collectionEntry : collections.entrySet()) {
            LOG.info("Persisting {} data to storage.", collectionEntry.getKey());

            for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collectionEntry.getValue().entrySet()) {

                NeutralRecord neutralRecord = neutralRecordEntry.getValue();
                neutralRecord.setRecordType(neutralRecord.getRecordType() + "_transformed");

                getNeutralRecordMongoAccess().getRecordRepository().create(neutralRecord);
            }
        }
    }

    /**
     * Load a collection from the database
     *
     * @param collectionName
     */
    private void loadCollectionFromDb(String collectionName) {

        Criteria jobIdCriteria = Criteria.where(BATCH_JOB_ID_KEY).is(getBatchJobId());

        Iterable<NeutralRecord> data = getNeutralRecordMongoAccess().getRecordRepository().findByQuery(collectionName,
                new Query(jobIdCriteria), 0, 0);

        Map<Object, NeutralRecord> collection = new HashMap<Object, NeutralRecord>();
        NeutralRecord tempNr;

        Iterator<NeutralRecord> iter = data.iterator();
        while (iter.hasNext()) {
            tempNr = iter.next();
            collection.put(tempNr.getRecordId(), tempNr);
        }

        collections.put(collectionName, collection);
    }

    /**
     * @return the passThroughCollectionList
     */
    public List<String> getPassThroughCollectionList() {
        return passThroughCollectionList;
    }

    /**
     * @param passThroughCollectionList the passThroughCollectionList to set
     */
    public void setPassThroughCollectionList(List<String> passThroughCollectionList) {
        this.passThroughCollectionList = passThroughCollectionList;
    }
}
