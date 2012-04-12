package org.slc.sli.ingestion.transformation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.ingestion.NeutralRecord;

/**
 * Transforms disjoint set of attendance events into cleaner set of {school year : list of attendance events} mappings and
 * stores in the appopriate student-school or student-section associations.
 *  
 * @author shalka
 */
public class AttendanceTransformer extends AbstractTransformationStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(AttendanceTransformer.class);

    private Map<String, Map<Object, NeutralRecord>> collections;
    private Map<String, Map<Object, NeutralRecord>> transformedCollections;

    public AttendanceTransformer() {
        collections = new HashMap<String, Map<Object, NeutralRecord>>();
        transformedCollections = new HashMap<String, Map<Object, NeutralRecord>>();
    }

    /**
     * The chaining of transformation steps. This implementation assumes that all data will be
     * processed in "one-go."
     */
    @Override
    public void performTransformation() {
        LOG.info("\n\n\n\n\n--------------------------------------------------------------");
        loadData();
        transform();
        persist();
        LOG.info("--------------------------------------------------------------\n\n\n\n\n");
    }

    public void loadData() {
        LOG.info("Loading data for attendance transformation.");

        loadCollectionFromDb("attendance");
        LOG.info("Attendance data is loaded into local storage.  Total Count = " + collections.get("attendance").size());

        // loadCollection needs to be augmented to return a boolean value indicating whether or not the
        // requested collection was added to the 'collections' local entity
        // -> if not, need to check actual mongo for entities
        
        // loadCollectionFromDb("school");
        // loadCollectionFromDb("student");
        
        // need to get interchanges for this information --> definitely
        // loadCollectionFromDb("schoolSessionAssociation"); --> get list of sessionIds from schoolId
        // loadCollectionFromDb("session"); --> get list of sessions (as Map<String, Object>)
        // iterate over sessions and add them into studentSchoolAssociation.body.dailyAttendance as Maps (with _year equal to schoolYear from session)
        
        // TODO: will (eventually) need a filter here to determine what type of attendance data is being loaded
        // -> can be daily attendance (added to studentSchoolAssociation)
        // -> can be section attendance (added to studentSectionAssociation)
        
        loadCollectionFromDb("studentSchoolAssociation");
        LOG.info("Student School Associations is loaded into local storage.  Total Count = " + collections.get("studentSchoolAssociation").size());
    }

    public void transform() {
        LOG.debug("Transforming data: Injecting direct references to students and schools/sections into attendance");

//        HashMap<Object, NeutralRecord> newCollection = new HashMap<Object, NeutralRecord>();
//        String key;
//
//        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collections.get("attendance").entrySet()) {
//            NeutralRecord neutralRecord = neutralRecordEntry.getValue();

            
            // get the key of parent
//            Map<String, Object> attrs = neutralRecord.getAttributes();
//            key = (String) attrs.get("parentAssessmentFamilyId");
//            String familyHierarchyName = "";
//            familyHierarchyName = getAssocationFamilyMap(key, new HashMap<String, Map<String, Object>>(),
//                    familyHierarchyName);
//
//            attrs.put("assessmentFamilyHierarchyName", familyHierarchyName);
//
//            @SuppressWarnings("unchecked")
//            List<String> objectiveAssessmentRefs = (List<String>) attrs.get("objectiveAssessmentRefs");
//            List<Map<String, Object>> objectiveAssessments = new ArrayList<Map<String, Object>>();
//            if (objectiveAssessmentRefs != null && !(objectiveAssessmentRefs.isEmpty())) {
//
//                for (String objectiveAssessmentRef : objectiveAssessmentRefs) {
//
//                    objectiveAssessments.add(getObjectiveAssessment(objectiveAssessmentRef));
//                }
//                attrs.put("objectiveAssessment", objectiveAssessments);
//            }
//
//            String assessmentPeriodDescriptorRef = (String) attrs.get("periodDescriptorRef");
//            if (assessmentPeriodDescriptorRef != null) {
//
//                attrs.put("assessmentPeriodDescriptor", getAssessmentPeriodDescriptor(assessmentPeriodDescriptorRef));
//
//            }
//            neutralRecord.setAttributes(attrs);
//            newCollection.put(neutralRecord.getRecordId(), neutralRecord);
//        }
//
//        transformedCollections.put("studentSchoolAssociation", newCollection);
        
        LOG.debug("Finished transforming data.");
    }

    private Map<String, Object> getObjectiveAssessment(String objectiveAssessmentRef) {
        Map<String, String> paths = new HashMap<String, String>();

        paths.put("body.id", objectiveAssessmentRef);

        Iterable<NeutralRecord> data = getNeutralRecordMongoAccess().getRecordRepository().findByPaths(
                "objectiveAssessment", paths);

        Map<String, Object> objectiveAssessment = data.iterator().next().getAttributes();
        objectiveAssessment.remove("id");

        return objectiveAssessment;

        // return null;
    }

    /**
     * Persists the transformed data into mongo.
     */
    public void persist() {
        LOG.info("Persisting transformed attendance data into mongo.");

//        for (Map.Entry<String, Map<Object, NeutralRecord>> collectionEntry : transformedCollections.entrySet()) {
//            for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collectionEntry.getValue().entrySet()) {
//                NeutralRecord neutralRecord = neutralRecordEntry.getValue();
//                neutralRecord.setRecordType(neutralRecord.getRecordType() + "_transformed");
//                getNeutralRecordMongoAccess().getRecordRepository().create(neutralRecord);
//            }
//        }
        
        LOG.info("Finished persisting transformed attendance data into mongo.");
    }

    /**
     * Stores all items in collection found in database to local storage (HashMap)
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
}
