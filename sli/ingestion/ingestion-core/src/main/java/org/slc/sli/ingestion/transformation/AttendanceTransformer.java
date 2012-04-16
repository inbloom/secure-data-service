package org.slc.sli.ingestion.transformation;

import org.joda.time.DateTime;
import org.slc.sli.ingestion.NeutralRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.security.InvalidParameterException;
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
            String schoolId = (String) attributes.get("schoolId");

            LOG.info("For student with id: {} in school: {}", studentId, schoolId);

            Map<Object, NeutralRecord> studentAttendance = new HashMap<Object, NeutralRecord>();

            for (Map.Entry<Object, NeutralRecord> attendanceEntry : collections.get("attendance").entrySet()) {
                NeutralRecord record = attendanceEntry.getValue();
                Map<String, Object> attendanceAttributes = record.getAttributes();
                if (attendanceAttributes.get("studentId").equals(studentId)) {
                    studentAttendance.put(attendanceEntry.getKey(), attendanceEntry.getValue());
                }
            }
            LOG.info("  Found {} attendance events:", studentAttendance.size());
            // LOG.info(" {} ", studentAttendance);

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
            LOG.info("  Found {} sessions associated with student:", sessions.size());

            Map<String, Object> schoolYears = new HashMap<String, Object>();
            for (Map.Entry<Object, NeutralRecord> session : sessions.entrySet()) {
                NeutralRecord sessionRecord = session.getValue();
                Map<String, Object> sessionAttributes = sessionRecord.getAttributes();
                String schoolYear = (String) sessionAttributes.get("schoolYear");
                DateTime sessionBegin = parseDateTime((String) sessionAttributes.get("beginDate"));
                DateTime sessionEnd = parseDateTime((String) sessionAttributes.get("endDate"));

                List<Map<String, Object>> events = new ArrayList<Map<String, Object>>();
                for (Map.Entry<Object, NeutralRecord> record : studentAttendance.entrySet()) {
                    NeutralRecord eventRecord = record.getValue();
                    Map<String, Object> eventAttributes = eventRecord.getAttributes();

                    String eventDate = (String) eventAttributes.get("eventDate");
                    DateTime date = parseDateTime(eventDate);
                    if (isLeftDateBeforeRightDate(sessionBegin, date) && isLeftDateBeforeRightDate(date, sessionEnd)) {
                        // TODO: need to add educational environment to sli.xsd and transform it here
                        Map<String, Object> event = new HashMap<String, Object>();
                        String eventCategory = (String) eventAttributes.get("attendanceEventCategory");
                        event.put("date", eventDate);
                        event.put("event", eventCategory);
                        if (eventAttributes.containsKey("attendanceEventReason")) {
                            String eventReason = (String) eventAttributes.get("attendanceEventReason");
                            event.put("reason", eventReason);
                        }
                        events.add(event);
                    }
                }
                // remove each attendance event in 'events' from studentAttendance
                LOG.info("  {} attendance events for session in school year: {}", events.size(), schoolYear);
            }

            // if student attendance still has attendance events --> orphaned events

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
        
        for (Map.Entry<String, Map<Object, NeutralRecord>> collectionEntry : transformedCollections.entrySet()) {
            String collectionName = collectionEntry.getKey();
            for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collectionEntry.getValue().entrySet()) {
                NeutralRecord neutralRecord = neutralRecordEntry.getValue();
                neutralRecord.setRecordType(neutralRecord.getRecordType() + "_transformed");
                getNeutralRecordMongoAccess().getRecordRepository().update(collectionName, neutralRecord);
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
     * @param dateToBeParsed String to be parsed into a DateTime object.
     *
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
     * Finds school year for a given date.
     */
    private class SchoolYearCalculator {

        private Map<String, Map<Object, NeutralRecord>> collections;
        HashMap<String, DateTime> schoolYearBegin;

        SchoolYearCalculator(Map<String, Map<Object, NeutralRecord>> collections) {
            this.collections = collections;
            schoolYearBegin = new HashMap<String,DateTime>(); //schoolId+year -> startDate
            init();
        }

        private void init() {
            HashMap<String, String> sessionToSchool = new HashMap<String,String>();
            for( NeutralRecord record : collections.get("section").values() ) {
                String sessionId = (String) record.getAttributes().get("sessionId");
                String schoolId = (String) record.getAttributes().get("schoolId");
                sessionToSchool.put(sessionId,schoolId);
            }

            Map<Object, NeutralRecord> sessions = collections.get("session");
            for( Map.Entry<String, String> entry: sessionToSchool.entrySet() ) {
                String schoolId = entry.getValue();
                NeutralRecord session = sessions.get(entry.getKey());


                DateTime sessionBeginDate = parseDateTime((String) session.getAttributes().get("beginDate"));
                Integer sessionBeginYear = sessionBeginDate.getYear();

                String key = schoolId + sessionBeginYear;
                DateTime currentBeginDate = schoolYearBegin.get( key );
                if( currentBeginDate == null || isLeftDateBeforeRightDate(sessionBeginDate, currentBeginDate)) {
                    schoolYearBegin.put( key, sessionBeginDate );
                }
            }
        }

        /**
         * @param date
         * @param schoolId
         * @return school year enum string, e.g. "2011-2012"
         */
        public String getSchoolYear(DateTime date, String schoolId) {

            DateTime startDate = schoolYearBegin.get(schoolId+date.getYear());
            if ( startDate == null ) {
                throw new InvalidParameterException("School year not found in collections: " + schoolId + date.year());
            }
            if ( isLeftDateBeforeRightDate(startDate, date) ) {
                return Integer.toString(date.getYear()) + "-" + Integer.toString(date.getYear()+1);
            } else {
                return Integer.toString(date.getYear()-1) + "-" + Integer.toString(date.getYear());
            }

        }

    }

}
