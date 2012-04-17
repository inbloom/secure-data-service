package org.slc.sli.ingestion.transformation;

import org.joda.time.DateTime;
import org.slc.sli.ingestion.NeutralRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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
    private SchoolYearCalculator schoolYearCalculator;

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

        schoolYearCalculator = new SchoolYearCalculator(collections);
    }


    public void transform() {

        LOG.debug("Transforming attendance data");
        HashMap<Object, NeutralRecord> newCollection = new HashMap<Object, NeutralRecord>();

        Map<String, Map<String, NeutralRecord>> studentToSchoolToAssociation = getStudentToSchoolToAssociation();

        for (NeutralRecord attendance : collections.get("attendance").values()) {
            String schoolId = (String) attendance.getAttributes().get("schoolId");
            String studentId = (String) attendance.getAttributes().get("studentId");

            NeutralRecord association = studentToSchoolToAssociation.get(studentId).get(schoolId);
            if (association == null) { //TODO
            }

            Map<String, Object> attendanceEvent = createAttendanceEvent(attendance);

            addAttendanceToAssociation(attendanceEvent, association);
            newCollection.put(association.getRecordId(), association);
        }

        transformedCollections.put("studentSchoolAssociation", newCollection);

        LOG.debug("Finished transforming attendance data.");
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

    private void addAttendanceToAssociation(Map<String, Object> attendance, NeutralRecord association) {

        String calculatedSchoolYear = schoolYearCalculator.getSchoolYear(
                parseDateTime((String) attendance.get("date")),
                (String) association.getAttributes().get("schoolId"));

        Map<String, List<Object>> dailyAttendance = (Map<String, List<Object>>) association.getAttributes().get("dailyAttendance");
        if (dailyAttendance == null) {
            dailyAttendance = new HashMap<String, List<Object>>();
            association.getAttributes().put("dailyAttendance", dailyAttendance);
        }

        List<Object> schoolYearAttendance = dailyAttendance.get("schoolYearAttendance");
        if (schoolYearAttendance == null) {
            schoolYearAttendance = new LinkedList<Object>();
            dailyAttendance.put("schoolYearAttendance", schoolYearAttendance);
        }

        for (Object o : schoolYearAttendance) {
            Map<String, Object> schoolYear = (Map<String, Object>) o;
            if (((String) schoolYear.get("schoolYear")).equals(calculatedSchoolYear)) {

            }
        }

        List<Object> attendances = dailyAttendance.get(calculatedSchoolYear);
        if (attendances == null) {
            attendances = new LinkedList<Object>();
            dailyAttendance.put(calculatedSchoolYear, attendances);
        }

        attendances.add(attendance);
    }


    private Map<String, Map<String, NeutralRecord>> getStudentToSchoolToAssociation() {
        Map<String, Map<String, NeutralRecord>> studentToSchoolToAssociation =
                new HashMap<String, Map<String, NeutralRecord>>();

        for (NeutralRecord record : collections.get(STUDENT_SCHOOL_ASSOCIATION).values()) {
            String studentId = (String) record.getAttributes().get("studentId");
            String schoolId = (String) record.getAttributes().get("schoolId");

            Map<String, NeutralRecord> records = studentToSchoolToAssociation.get(studentId);
            if (records == null) {
                records = new HashMap<String, NeutralRecord>();
                studentToSchoolToAssociation.put(studentId, records);
            }
            records.put(schoolId, record);
        }
        return studentToSchoolToAssociation;
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
     * Finds school year for a given date.
     */
    private class SchoolYearCalculator {

        private Map<String, Map<Object, NeutralRecord>> collections;
        HashMap<String, DateTime> schoolYearBegin;

        SchoolYearCalculator(Map<String, Map<Object, NeutralRecord>> collections) {
            this.collections = collections;
            schoolYearBegin = new HashMap<String, DateTime>(); //schoolId+year -> startDate
            init();
        }

        private void init() {
            HashMap<String, String> sessionToSchool = new HashMap<String, String>();
            for (NeutralRecord record : collections.get("section").values()) {
                String sessionId = (String) record.getAttributes().get("sessionId");
                String schoolId = (String) record.getAttributes().get("schoolId");
                sessionToSchool.put(sessionId, schoolId);
            }

            Map<Object, NeutralRecord> sessions = collections.get("session");
            for (Map.Entry<String, String> entry : sessionToSchool.entrySet()) {
                String schoolId = entry.getValue();
                NeutralRecord session = sessions.get(entry.getKey());


                DateTime sessionBeginDate = parseDateTime((String) session.getAttributes().get("beginDate"));
                Integer sessionBeginYear = sessionBeginDate.getYear();

                String key = schoolId + sessionBeginYear;
                DateTime currentBeginDate = schoolYearBegin.get(key);
                if (currentBeginDate == null || isLeftDateBeforeRightDate(sessionBeginDate, currentBeginDate)) {
                    schoolYearBegin.put(key, sessionBeginDate);
                }
            }
        }

        /**
         * @param date
         * @param schoolId
         * @return school year enum string, e.g. "2011-2012"
         */
        public String getSchoolYear(DateTime date, String schoolId) {

            DateTime startDate = schoolYearBegin.get(schoolId + date.getYear());
            if (startDate == null) {
                throw new InvalidParameterException("School year not found in collections: " + schoolId + date.year());
            }
            if (isLeftDateBeforeRightDate(startDate, date)) {
                return Integer.toString(date.getYear()) + "-" + Integer.toString(date.getYear() + 1);
            } else {
                return Integer.toString(date.getYear() - 1) + "-" + Integer.toString(date.getYear());
            }

        }

    }

}
