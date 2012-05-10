package org.slc.sli.ingestion.transformation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.ingestion.NeutralRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * Transformer for StudentTranscriptAssociation Entities
 *
 * @author jcole
 * @author shalka
 */
@Component("studentTranscriptAssociationTransformationStrategy")
public class StudentTranscriptAssociationCombiner extends AbstractTransformationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(StudentTranscriptAssociationCombiner.class);
    
    private Map<String, Map<Object, NeutralRecord>> collections;
    private Map<String, Map<Object, NeutralRecord>> transformedCollections;

    /**
     * Default constructor.
     */
    public StudentTranscriptAssociationCombiner() {
        this.collections = new HashMap<String, Map<Object, NeutralRecord>>();
        this.transformedCollections = new HashMap<String, Map<Object, NeutralRecord>>();
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

    /**
     * Pre-requisite interchanges for student transcript data to be successfully transformed:
     * student
     */
    public void loadData() {
        LOG.info("Loading data for studentTranscriptAssociation transformation.");
        List<String> collectionsToLoad = Arrays.asList(EntityNames.STUDENT_TRANSCRIPT_ASSOCIATION);
        for (String collectionName : collectionsToLoad) {
            Map<Object, NeutralRecord> collection = getCollectionFromDb(collectionName);
            collections.put(collectionName, collection);
            LOG.info("{} is loaded into local storage.  Total Count = {}", collectionName, collection.size());
        }
        LOG.info("Finished loading data for studentTranscriptAssociation transformation.");
    }

    /**
     * Transforms student transcript association data to pass SLI data validation.
     */
    public void transform() {
        LOG.info("Transforming student transcript association data");
        Map<Object, NeutralRecord> newCollection = new HashMap<Object, NeutralRecord>();

        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collections.
                get(EntityNames.STUDENT_TRANSCRIPT_ASSOCIATION).entrySet()) {
            NeutralRecord neutralRecord = neutralRecordEntry.getValue();
            Map<String, Object> attributes = neutralRecord.getAttributes();
            if (attributes.get("creditsAttempted") == null) {
                attributes.remove("creditsAttempted");
            }
            neutralRecord.setAttributes(attributes);
            newCollection.put(neutralRecord.getRecordId(), neutralRecord);
        }
        transformedCollections.put(EntityNames.STUDENT_TRANSCRIPT_ASSOCIATION, newCollection);
        LOG.info("Finished transforming student transcript association data");
    }

    /**
     * Persists the transformed data into staging mongo database.
     */
    public void persist() {
        LOG.info("Persisting transformed data into studentTranscriptAssociation_transformed staging collection.");
        for (Map.Entry<String, Map<Object, NeutralRecord>> collectionEntry : transformedCollections.entrySet()) {
            for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collectionEntry.getValue().entrySet()) {
                NeutralRecord neutralRecord = neutralRecordEntry.getValue();
                neutralRecord.setRecordType(neutralRecord.getRecordType() + "_transformed");
                getNeutralRecordMongoAccess().getRecordRepository().createForJob(neutralRecord, getJob().getId());
            }
        }
        LOG.info("Finished persisting transformed data into studentTranscriptAssociation_transformed staging collection.");
    }

    /**
     * Returns all collection entities found in temp ingestion database
     *
     * @param collectionName
     */
    private Map<Object, NeutralRecord> getCollectionFromDb(String collectionName) {
        Criteria jobIdCriteria = Criteria.where(BATCH_JOB_ID_KEY).is(getBatchJobId());

        Iterable<NeutralRecord> data = getNeutralRecordMongoAccess().getRecordRepository().findByQueryForJob(
                collectionName, new Query(jobIdCriteria), getJob().getId(), 0, 0);

        Map<Object, NeutralRecord> collection = new HashMap<Object, NeutralRecord>();
        NeutralRecord tempNr;

        Iterator<NeutralRecord> neutralRecordIterator = data.iterator();
        while (neutralRecordIterator.hasNext()) {
            tempNr = neutralRecordIterator.next();
            collection.put(tempNr.getRecordId(), tempNr);
        }
        return collection;
    }

}
