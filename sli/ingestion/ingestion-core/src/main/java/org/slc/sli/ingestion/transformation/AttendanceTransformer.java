package org.slc.sli.ingestion.transformation;

import org.joda.time.DateTime;
import org.slc.sli.ingestion.NeutralRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
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
    public static final String STUDENT_SCHOOL_ASSOCIATION = "studentSchoolAssociation";

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
        
        // iterate over each studentSchoolAssociation --> this is where the dailyAttendance Map will be added
        LOG.info("Iterating over {} student-school associations.", collections.get("studentSchoolAssociation").size());
        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collections.get("studentSchoolAssociation").entrySet()) {
            NeutralRecord neutralRecord = neutralRecordEntry.getValue();
            Map<String, Object> attributes = neutralRecord.getAttributes();
            String studentId = (String) attributes.get("studentId");
            String schoolId = (String) attributes.get("schoolId");
            
            Map<Object, NeutralRecord> studentAttendance = getAttendanceEvents(studentId);
            Map<Object, NeutralRecord> sessions = getSessions(studentId, schoolId);
            
            LOG.info("For student with id: {} in school: {}", studentId, schoolId);
            LOG.info("  Found {} attendance events for student {}:", studentAttendance.size(), studentId);
            LOG.info("  Found {} sessions associated with student:", sessions.size());
            
            Map<String, Object> schoolYears = mapAttendanceIntoSchoolYears(studentAttendance, sessions);
            
            if (schoolYears.entrySet().size() > 0) {
                List<Map<String, Object>> daily = new ArrayList<Map<String, Object>>();
                for (Map.Entry<String, Object> entry : schoolYears.entrySet()) {
                    String schoolYear = (String) entry.getKey();
                    List<Map<String, Object>> events = (List<Map<String, Object>>) entry.getValue();
                    Map<String, Object> attendance = new HashMap<String, Object>();
                    attendance.put("schoolYear", schoolYear);
                    attendance.put("attendanceEvent", events);
                    daily.add(attendance);
                }
                attributes.put("dailyAttendance", daily);
                neutralRecord.setAttributes(attributes);
                newCollection.put(neutralRecord.getRecordId(), neutralRecord);
            } else {
                LOG.warn("  No daily attendance for student: {}", studentId);
            }
            transformedCollections.put("studentSchoolAssociation", newCollection);
            LOG.debug("Finished transforming attendance data.");
        }
    }

    /**
     * Persists the transformed data into mongo.
     */
    public void persist() {
        LOG.info("Persisting transformed attendance data into mongo.");        
        for (Map.Entry<String, Map<Object, NeutralRecord>> collectionEntry : transformedCollections.entrySet()) {
            for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collectionEntry.getValue().entrySet()) {
                NeutralRecord neutralRecord = neutralRecordEntry.getValue();
                neutralRecord.setRecordType(neutralRecord.getRecordType() + "_transformed");
                getNeutralRecordMongoAccess().getRecordRepository().create(neutralRecord);
            }
        }
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

    /**
     * Parses a date presently stored in the format yyyy-MM-dd and returns the corresponding DateTime object.
     *
     * @param dateToBeParsed String to be parsed into a DateTime object.
     * @return DateTime object.
     */
    private DateTime parseDateTime(String dateToBeParsed) {
        String[] pieces = dateToBeParsed.split("-");
        int year = Integer.valueOf(pieces[0]);
        int month = Integer.valueOf(pieces[1]);
        int day = Integer.valueOf(pieces[2]);
        DateTime date = new DateTime().withDate(year, month, day);
        return date;
    }

    /**
     * Determines if the 1st date is before or equal to the 2nd date (comparing only year, month, day).
     *
     * @param date1 1st date object.
     * @param date2 2nd date object.
     * @return true if date1 is before or equal to date2, false if date1 is after date2.
     */
    private boolean isLeftDateBeforeRightDate(DateTime date1, DateTime date2) {
        boolean less = false;
        if (date1.getYear() < date2.getYear()) {
            less = true;
        } else if (date1.getYear() == date2.getYear()) {
            if (date1.getMonthOfYear() < date2.getMonthOfYear()) {
                less = true;
            } else if (date1.getMonthOfYear() == date2.getMonthOfYear()) {
                if (date1.getDayOfMonth() <= date2.getDayOfMonth()) {
                    less = true;
                }
            }
        }
        return less;
    }
    
    /**
     * Gets attendance events for the specified student.
     * @param studentId StudentUniqueStateId for student.
     * @return Map of Attendance Events for student.
     */
    private Map<Object, NeutralRecord> getAttendanceEvents(String studentId) {
        // TODO: don't iterate over collections.get("attendance") --> create query that only gets attendance
        // events for the current student from the staging collections
        Map<Object, NeutralRecord> studentAttendance = new HashMap<Object, NeutralRecord>();
        for (Map.Entry<Object, NeutralRecord> attendanceEntry : collections.get("attendance").entrySet()) {
            NeutralRecord record = attendanceEntry.getValue();
            Map<String, Object> attendanceAttributes = record.getAttributes();
            if (attendanceAttributes.get("studentId").equals(studentId)) {
                studentAttendance.put(attendanceEntry.getKey(), attendanceEntry.getValue());
            }
        }
        return studentAttendance;
    }
    
    /**
     * Gets all sessions associated with the specified student-school pair.
     * @param studentId StudentUniqueStateId for student.
     * @param schoolId StateOrganizationId for school.
     * @return Map of Sessions for student-school pair.
     */
    private Map<Object, NeutralRecord> getSessions(String studentId, String schoolId) {
        Map<Object, NeutralRecord> sessions = new HashMap<Object, NeutralRecord>();
        for (Map.Entry<Object, NeutralRecord> association : collections.get("studentSectionAssociation").entrySet()) {
            NeutralRecord record = association.getValue();
            Map<String, Object> associationAttributes = record.getAttributes();
            if (associationAttributes.get("studentId").equals(studentId)) {
                String sectionId = (String) associationAttributes.get("sectionId");
                
                for (Map.Entry<Object, NeutralRecord> section : collections.get("section").entrySet()) {
                    NeutralRecord sectionRecord = section.getValue();
                    Map<String, Object> sectionAttributes = sectionRecord.getAttributes();
                    String currentSchoolId = (String) sectionAttributes.get("schoolId");
                    
                    if (sectionAttributes.get("uniqueSectionCode").equals(sectionId) && currentSchoolId.equals(schoolId)) {
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
        return sessions;
    }

    private Map<String, Object> createAttendanceEvent(NeutralRecord attendance) {

        Map<String, Object> event = new HashMap<String, Object>();

        event.put("date", (String) attendance.getAttributes().get("eventDate"));
        event.put("event", (String) attendance.getAttributes().get("attendanceEventCategory"));

        if (attendance.getAttributes().containsKey("attendanceEventReason")) {
            event.put("reason", (String) attendance.getAttributes().get("attendanceEventReason"));
        }

        return event;
    }
    
    /**
     * Maps the set of student attendance events into a transformed map of form {school year : list of attendance events} based
     * on dates published in the sessions.
     * @param studentAttendance Set of student attendance events.
     * @param sessions Set of sessions that correspond to the school the student attends.
     * @return Map containing transformed attendance information.
     */
    private Map<String, Object> mapAttendanceIntoSchoolYears(Map<Object, NeutralRecord> studentAttendance, Map<Object, NeutralRecord> sessions) {
        Map<String, Object> schoolYears = new HashMap<String, Object>();
        for (Map.Entry<Object, NeutralRecord> session : sessions.entrySet()) {
            NeutralRecord sessionRecord = session.getValue();
            Map<String, Object> sessionAttributes = sessionRecord.getAttributes();
            String schoolYear = (String) sessionAttributes.get("schoolYear");
            DateTime sessionBegin = parseDateTime((String) sessionAttributes.get("beginDate"));
            DateTime sessionEnd = parseDateTime((String) sessionAttributes.get("endDate"));
            
            List<Map<String, Object>> events = new ArrayList<Map<String, Object>>();
            for (Iterator<Map.Entry<Object, NeutralRecord>> recordItr = studentAttendance.entrySet().iterator(); recordItr.hasNext(); ) {
                NeutralRecord eventRecord = recordItr.next().getValue();

                DateTime date = parseDateTime((String) eventRecord.getAttributes().get("eventDate"));
                if (isLeftDateBeforeRightDate(sessionBegin, date) && isLeftDateBeforeRightDate(date, sessionEnd)) {
                    events.add( createAttendanceEvent(eventRecord) );
                    recordItr.remove();
                }
            }
            if (events.size() > 0) {
                schoolYears.put(schoolYear, events);
            }
            LOG.info("  {} attendance events for session in school year: {}", events.size(), schoolYear);    
        }
        
        // if student attendance still has attendance events --> orphaned events
        Iterator<Map.Entry<Object, NeutralRecord>> recordItr = studentAttendance.entrySet().iterator();
        if (recordItr.hasNext()) {
            int orphanedEvents = 0;
            List<Map<String, Object>> events = new ArrayList<Map<String, Object>>();
            while (recordItr.hasNext()) {
                Map.Entry<Object, NeutralRecord> record = recordItr.next();
                orphanedEvents++;
            }
            LOG.warn("  {} attendance events still need to be mapped into a school year.", orphanedEvents);    
        }
        return schoolYears;
    }
}
