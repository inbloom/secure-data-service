package org.slc.sli.ingestion.transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.ingestion.NeutralRecord;

/**
 * Transformer for StudentTranscriptAssociation Entities
 *
 * @author jcole
 *
 */
public class StudentTranscriptAssociationCombiner extends AbstractTransformationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(StudentTranscriptAssociationCombiner.class);

    private Map<String, Map<Object, NeutralRecord>> collections;

    private Map<String, Map<Object, NeutralRecord>> transformedCollections;

    public StudentTranscriptAssociationCombiner() {
        this.collections = new HashMap<String, Map<Object, NeutralRecord>>();
        this.transformedCollections = new HashMap<String, Map<Object, NeutralRecord>>();
    }

    /**
     * The chaining of transformation steps. This implementation assumes that all data will be
     * processed in "one-go"
     *
     */
    @Override
    public void performTransformation() {
        loadData();
        transform();
        persist();
    }

    public void loadData() {
        LOG.info("Loading data for transformation.");

        this.loadCollectionFromDb("studentTranscriptAssociation");
        LOG.info("StudentTranscriptAssociation is loaded into local storage.  Total Count = "
                + collections.get("studentTranscriptAssociation").size());
    }

    public void transform() {
        LOG.debug("Transforming data: Injecting studentAcademicRecords into studentTranscriptAssociation");

        HashMap<Object, NeutralRecord> newCollection = new HashMap<Object, NeutralRecord>();

        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collections.get("studentTranscriptAssociation")
                .entrySet()) {
            NeutralRecord neutralRecord = neutralRecordEntry.getValue();

            Map<String, Object> attrs = neutralRecord.getAttributes();

            if (attrs.get("creditsAttempted") == null) {
                attrs.remove("creditsAttempted");
            }

            neutralRecord.setAttributes(attrs);
            newCollection.put(neutralRecord.getRecordId(), neutralRecord);
        }

        transformedCollections.put("studentTranscriptAssociation", newCollection);
    }

    @SuppressWarnings("unchecked")
    private String getStudentId(String key, HashMap<String, Map<String, Object>> deepFamilyMap) {

        Map<String, String> paths = new HashMap<String, String>();
        paths.put("body.studentAcademicRecordId", key);

        Iterable<NeutralRecord> data = getNeutralRecordMongoAccess().getRecordRepository().findByPathsForJob(
                "studentAcademicRecord", paths, getJob().getId());

        ArrayList<Map<String, Object>> tempIdentificationCodes;
        Map<String, Object> tempMap;
        String studentId = "";
        for (NeutralRecord tempNr : data) {
            Map<String, Object> attrs = tempNr.getAttributes();
            studentId = (String) attrs.get("studentId");
        }

        return studentId;
    }

    public void persist() {
        LOG.info("Persisting transformed data to storage.");

        // transformedCollections should have been populated in the transform() step.
        for (Map.Entry<String, Map<Object, NeutralRecord>> collectionEntry : transformedCollections.entrySet()) {

            for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collectionEntry.getValue().entrySet()) {

                NeutralRecord neutralRecord = neutralRecordEntry.getValue();
                neutralRecord.setRecordType(neutralRecord.getRecordType() + "_transformed");

                getNeutralRecordMongoAccess().getRecordRepository().createForJob(neutralRecord, getJob().getId());
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

        Iterable<NeutralRecord> data = getNeutralRecordMongoAccess().getRecordRepository().findByQueryForJob(
                collectionName, new Query(jobIdCriteria), getJob().getId(), 0, 0);

        Map<Object, NeutralRecord> collection = new HashMap<Object, NeutralRecord>();
        NeutralRecord tempNr;

        Iterator<NeutralRecord> iter = data.iterator();
        while (iter.hasNext()) {
            tempNr = iter.next();
            collection.put(tempNr.getRecordId(), tempNr);
        }

        collections.put(collectionName, collection);
    }

}
