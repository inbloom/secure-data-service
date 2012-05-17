package org.slc.sli.ingestion.transformation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.NeutralRecord;

/**
 * Transformation strategy to persist data as-is, no actual transformation
 *
 * @author vmcglaughlin
 */
@Scope("prototype")
@Component("PassThroughTransformationStrategy")
public class PassThroughTransformationStrategy extends AbstractTransformationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(PassThroughTransformationStrategy.class);

    private Map<Object, NeutralRecord> collection;

    private String passThroughCollectionName;

    public PassThroughTransformationStrategy() {
        this.collection = new HashMap<Object, NeutralRecord>();
    }

    /**
     * The chaining of transformation steps. This implementation forces that all data will be
     * processed in "one-go"
     */
    @Override
    public void performTransformation() {
        loadData();
        transform();
        persist();
    }

    private void loadData() {
        LOG.info("Loading data for transformation.");

        if (passThroughCollectionName == null || passThroughCollectionName.isEmpty()) {
            LOG.warn("Collection to pass through not specified, null or empty");
            return;
        }

        loadCollectionFromDb(passThroughCollectionName);

        LOG.info("{} is loaded into local storage.  Total Count = {}", passThroughCollectionName, collection.size());
    }

    private void transform() {
        LOG.info("Transforming data: No transformation, straight pass-through");
    }

    private void persist() {
        LOG.info("Persisting {} data to storage.", passThroughCollectionName);

        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collection.entrySet()) {

            NeutralRecord neutralRecord = neutralRecordEntry.getValue();
            neutralRecord.setRecordType(neutralRecord.getRecordType() + "_transformed");

            getNeutralRecordMongoAccess().getRecordRepository().createForJob(neutralRecord, getJob().getId());
        }
    }

    /**
     * Load a collection from the database
     *
     * @param collectionName
     */
    private void loadCollectionFromDb(String collectionName) {
        Criteria jobIdCriteria = Criteria.where(BATCH_JOB_ID_KEY).is(getBatchJobId());

        @SuppressWarnings("deprecation")
        Iterable<NeutralRecord> data = getNeutralRecordMongoAccess().getRecordRepository().findByQueryForJob(
                collectionName, new Query(jobIdCriteria), getJob().getId(), 0, 0);

        NeutralRecord tempNr;

        Iterator<NeutralRecord> iter = data.iterator();
        while (iter.hasNext()) {
            tempNr = iter.next();
            collection.put(tempNr.getRecordId(), tempNr);
        }
    }

    /**
     * @return the passThroughCollectionName
     */
    public String getPassThroughCollectionName() {
        return passThroughCollectionName;
    }

    /**
     * @param passThroughCollectionName
     *            the passThroughCollectionName to set
     */
    public void setPassThroughCollectionName(String passThroughCollectionName) {
        this.passThroughCollectionName = passThroughCollectionName;
    }
}
