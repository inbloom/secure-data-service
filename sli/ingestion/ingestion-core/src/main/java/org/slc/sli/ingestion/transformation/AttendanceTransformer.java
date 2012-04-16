package org.slc.sli.ingestion.transformation;

import org.slc.sli.ingestion.NeutralRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    }
    
    /**
     * Pre-requisite interchanges for attendance data to be successfully transformed: 
     * section, session, studentSchoolAssociation, studentSectionAssociation
     */
    public void loadData() {
        LOG.info("Loading data for attendance transformation.");

        List<String> collectionsToLoad = Arrays.asList("section", "session", "studentSchoolAssociation", "studentSectionAssociation", "attendance");

        for (String collectionName : collectionsToLoad) {
            Map<Object, NeutralRecord> collection = getCollectionFromDb(collectionName);
            collections.put(collectionName, collection);
            LOG.info("{} is loaded into local storage.  Total Count = {}", collectionName, collection.size());
        }

        LOG.info("Attendance data is loaded into local storage.  Total Count = " + collections.get("attendance").size());
    }


    public void transform() {
        LOG.debug("Transforming attendance data");

        HashMap<Object, NeutralRecord> newCollection = new HashMap<Object, NeutralRecord>();
        
        // iterate over attendance events instead?
        // Map<Object, NeutralRecord> attendance = collections.get("attendance");
        
        // iterate over each studentSchoolAssociation --> this is where the dailyAttendance Map will be added
        LOG.info("Iterating over {} student-school associations.", collections.get("studentSectionAssociation").size());
        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collections.get("studentSchoolAssociation").entrySet()) {
            NeutralRecord neutralRecord = neutralRecordEntry.getValue();
            Map<String, Object> attributes = neutralRecord.getAttributes();
            String studentId = (String) attributes.get("studentId");
            
            LOG.info("For student with id: {}", studentId);
            
            Map<Object, NeutralRecord> studentAttendance = new HashMap<Object, NeutralRecord>();
            
            for (Map.Entry<Object, NeutralRecord> attendanceEntry : collections.get("attendance").entrySet()) {
                NeutralRecord record = attendanceEntry.getValue();
                Map<String, Object> attendanceAttributes = record.getAttributes();
                if (attendanceAttributes.get("studentId").equals(studentId)) {
                    studentAttendance.put(attendanceEntry.getKey(), attendanceEntry.getValue());
                }
            }
            LOG.info(" - Found {} attendance events.", studentAttendance.size());
            
            Map<Object, NeutralRecord> sessions = new HashMap<Object, NeutralRecord>();
            
            for (Map.Entry<Object, NeutralRecord> association : collections.get("studentSectionAssociation").entrySet()) {
                NeutralRecord record = association.getValue();
                Map<String, Object> associationAttributes = record.getAttributes();
                if (associationAttributes.get("studentId").equals(studentId)) {
                    String sectionId = (String) associationAttributes.get("sectionId");
                    
                    for (Map.Entry<Object, NeutralRecord> section : collections.get("section").entrySet()) {
                        NeutralRecord sectionRecord = section.getValue();
                        Map<String, Object> sectionAttributes = sectionRecord.getAttributes();
                        if (sectionAttributes.get("uniqueSectionCode").equals(sectionId)) {
                            String sessionId = (String) sectionAttributes.get("sessionId");
                            
                            for (Map.Entry<Object, NeutralRecord> session : collections.get("session").entrySet()) {
                                NeutralRecord sessionRecord = session.getValue();
                                Map<String, Object> sessionAttributes = sessionRecord.getAttributes();
                                if (sessionAttributes.get("sessionName").equals(sessionId)) {
                                    sessions.put(session.getKey(), sessionRecord);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
            }
            LOG.info(" - Found {} sessions associated with student.", sessions.size());
            
//            if (attributes.containsKey("dailyAttendance")) {
//                Map<String, Object> daily = (Map<String, Object>) attributes.get("dailyAttendance");
//                // work will eventually be done here
//                attributes.put("dailyAttendance", daily);
//            } else {
//                Map<String, Object> daily = new HashMap<String, Object>;
//                List<Pair<String, List<Object>>> schoolYearAttendance = new ArrayList<Pair<String, List<Object>>>();
//                
//                attributes.put("dailyAttendance", daily);
//            }
            
            neutralRecord.setAttributes(attributes);
            newCollection.put(neutralRecord.getRecordId(), neutralRecord);
        }
        
        transformedCollections.put("studentSchoolAssociation", newCollection);
        
        LOG.debug("Finished transforming attendance data.");
    }

    /**
     * Persists the transformed data into mongo.
     */
    public void persist() {
        LOG.info("Persisting transformed attendance data into mongo.");
        // uncomment this when ready to persist transformed associations into mongo
        
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
     * Returns all collection entities found in temp ingestion database
     *
     * @param collectionName
     */
    private Map<Object, NeutralRecord> getCollectionFromDb(String collectionName) {
        Criteria jobIdCriteria = Criteria.where(BATCH_JOB_ID_KEY).is(getBatchJobId());

        Iterable<NeutralRecord> data = getNeutralRecordMongoAccess().getRecordRepository().findByQuery(collectionName,
                new Query(jobIdCriteria), 0, 0);

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
