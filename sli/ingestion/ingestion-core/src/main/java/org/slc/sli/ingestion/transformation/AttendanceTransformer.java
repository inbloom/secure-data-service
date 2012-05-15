package org.slc.sli.ingestion.transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.util.datetime.DateTimeUtil;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.NeutralRecord;

/**
 * Transforms disjoint set of attendance events into cleaner set of {school year : list of
 * attendance events} mappings and stores in the appropriate student-school or student-section
 * associations.
 *
 * @author shalka
 */
@Scope("prototype")
@Component("attendanceTransformationStrategy")
public class AttendanceTransformer extends AbstractTransformationStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(AttendanceTransformer.class);

    private static final String ATTENDANCE_TRANSFORMED = EntityNames.ATTENDANCE + "_transformed";

    private Map<String, Map<Object, NeutralRecord>> collections;

    @Autowired
    private UUIDGeneratorStrategy uuidGenerator;

    /**
     * Default constructor.
     */
    public AttendanceTransformer() {
        collections = new HashMap<String, Map<Object, NeutralRecord>>();
    }

    /**
     * The chaining of transformation steps. This implementation assumes that all data will be
     * processed in "one-go."
     */
    @Override
    public void performTransformation() {
        loadData();
        transform();
    }

    /**
     * Pre-requisite interchanges for daily attendance data to be successfully transformed:
     * student, education organization, education organization calendar, master schedule,
     * student enrollment
     */
    public void loadData() {
        LOG.info("Loading data for attendance transformation.");
        List<String> collectionsToLoad = Arrays.asList(EntityNames.ATTENDANCE);
        for (String collectionName : collectionsToLoad) {
            Map<Object, NeutralRecord> collection = getCollectionFromDb(collectionName);
            collections.put(collectionName, collection);
            LOG.info("{} is loaded into local storage.  Total Count = {}", collectionName, collection.size());
        }
        LOG.info("Finished loading data for attendance transformation.");
    }

    /**
     * Transforms attendance events from Ed-Fi model into SLI model.
     */
    public void transform() {
        LOG.info("Transforming attendance data");

        Map<String, List<Map<String, Object>>> studentAttendanceEvents = new HashMap<String, List<Map<String, Object>>>();
        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collections.get(EntityNames.ATTENDANCE).entrySet()) {
            NeutralRecord neutralRecord = neutralRecordEntry.getValue();
            Map<String, Object> attributes = neutralRecord.getAttributes();
            String studentId = (String) attributes.get("studentId");

            List<Map<String, Object>> events = new ArrayList<Map<String, Object>>();

            if (studentAttendanceEvents.containsKey(studentId)) {
                events = studentAttendanceEvents.get(studentId);
            }

            Map<String, Object> event = new HashMap<String, Object>();
            String eventDate = (String) attributes.get("eventDate");
            String eventCategory = (String) attributes.get("attendanceEventCategory");
            event.put("date", eventDate);
            event.put("event", eventCategory);
            if (attributes.containsKey("attendanceEventReason")) {
                String eventReason = (String) attributes.get("attendanceEventReason");
                event.put("reason", eventReason);
            }
            events.add(event);
            studentAttendanceEvents.put(studentId, events);
        }

        for (Map.Entry<String, List<Map<String, Object>>> entry : studentAttendanceEvents.entrySet()) {
            String studentId = entry.getKey();
            List<Map<String, Object>> attendance = entry.getValue();
            List<NeutralRecord> schools = getSchoolsForStudent(studentId);
            if (schools.size() == 0) {
                LOG.error("Student with id: {} is not associated to any schools.", studentId);
                // TODO: log number of failing attendance events
            } else if (schools.size() > 1) {
                LOG.error("Student with id: {} is associated to more than one school.. impossible to associate.",
                        studentId);
                // TODO: log number of failing attendance events
            } else {
                NeutralRecord school = schools.get(0);
                String schoolId = (String) school.getAttributes().get("stateOrganizationId");
                Map<Object, NeutralRecord> sessions = getSessions(studentId, schoolId);

                LOG.info("For student with id: {} in school: {}", studentId, schoolId);
                LOG.info("  Found {} associated sessions.", sessions.size());
                LOG.info("  Found {} attendance events.", attendance.size());

                // create a placeholder for the student-school pair and immediately write to staging
                // mongo db
                NeutralRecord placeholder = createAttendanceRecordPlaceholder(studentId, schoolId, sessions);
                getNeutralRecordMongoAccess().getRecordRepository().createForJob(placeholder, getJob().getId());

                Map<String, List<Map<String, Object>>> schoolYears = mapAttendanceIntoSchoolYears(attendance, sessions);

                if (schoolYears.entrySet().size() > 0) {
                    // it is now known that a document exists for the student-school pair (whether
                    // or not created by this pit node)

                    for (Map.Entry<String, List<Map<String, Object>>> attendanceEntry : schoolYears.entrySet()) {
                        String schoolYear = attendanceEntry.getKey();
                        List<Map<String, Object>> events = attendanceEntry.getValue();

                        NeutralQuery query = new NeutralQuery(1);
                        query.addCriteria(new NeutralCriteria("studentId", NeutralCriteria.OPERATOR_EQUAL, studentId));
                        query.addCriteria(new NeutralCriteria("schoolId", NeutralCriteria.OPERATOR_EQUAL, schoolId));
                        query.addCriteria(new NeutralCriteria("schoolYearAttendance.schoolYear", NeutralCriteria.OPERATOR_EQUAL, schoolYear));

                        Map<String, Object> attendanceEventsToPush = new HashMap<String, Object>();
                        attendanceEventsToPush.put("body.schoolYearAttendance.$.attendanceEvent", events.toArray());
                        Map<String, Object> update = new HashMap<String, Object>();
                        update.put("pushAll", attendanceEventsToPush);
                        getNeutralRecordMongoAccess().getRecordRepository().updateFirstForJob(query, update, ATTENDANCE_TRANSFORMED, getBatchJobId());
                        LOG.info("Added {} attendance events for student: {} at school: {}", events.size(), new Object[] {studentId, schoolId});
                    }
                } else {
                    LOG.warn("No daily attendance for student: {} in school: {}", studentId, schoolId);
                }
            }
        }
        LOG.info("Finished transforming attendance data");
    }

    /**
     * Creates a Neutral Record of type 'attendance'.
     *
     * @return newly created 'attendance' Neutral Record.
     */
    private NeutralRecord createAttendanceRecordPlaceholder(String studentId, String schoolId,
            Map<Object, NeutralRecord> sessions) {
        NeutralRecord record = new NeutralRecord();
        record.setRecordId(uuidGenerator.randomUUID().toString());
        record.setRecordType(ATTENDANCE_TRANSFORMED);

        Map<String, List<Map<String, Object>>> placeholders = createAttendancePlaceholdersFromSessions(sessions);

        List<Map<String, Object>> daily = new ArrayList<Map<String, Object>>();
        for (Map.Entry<String, List<Map<String, Object>>> year : placeholders.entrySet()) {
            String schoolYear = year.getKey();
            List<Map<String, Object>> events = year.getValue();
            Map<String, Object> schoolYearAttendanceEvents = new HashMap<String, Object>();
            schoolYearAttendanceEvents.put("schoolYear", schoolYear);
            schoolYearAttendanceEvents.put("attendanceEvent", events);
            daily.add(schoolYearAttendanceEvents);
        }

        Map<String, Object> attendanceAttributes = new HashMap<String, Object>();
        attendanceAttributes.put("studentId", studentId);
        attendanceAttributes.put("schoolId", schoolId);
        attendanceAttributes.put("schoolYearAttendance", daily);

        record.setAttributes(attendanceAttributes);

        // TODO: This sets attendance record's source file to FIRST attendance record
        // --> augment transformer so that the file of each attendance event is
        // carried through to this stage?
        record.setSourceFile(collections.get(EntityNames.ATTENDANCE).values().iterator().next()
                .getSourceFile());
        return record;
    }

    /**
     * Gets all schools associated with the specified student.
     *
     * @param studentId
     *            StudentUniqueStateId for student.
     * @return List of Neutral Records representing schools.
     */
    private List<NeutralRecord> getSchoolsForStudent(String studentId) {
        List<NeutralRecord> schools = new ArrayList<NeutralRecord>();

        NeutralQuery query = new NeutralQuery(0);
        query.addCriteria(new NeutralCriteria("studentId", "=", studentId));

        Iterable<NeutralRecord> associations = getNeutralRecordMongoAccess().getRecordRepository().findAllForJob(
                EntityNames.STUDENT_SCHOOL_ASSOCIATION, getJob().getId(), query);

        if (associations != null) {
            List<String> schoolIds = new ArrayList<String>();
            for (NeutralRecord association : associations) {
                Map<String, Object> associationAttributes = association.getAttributes();
                String schoolId = (String) associationAttributes.get("schoolId");
                schoolIds.add(schoolId);
            }

            NeutralQuery schoolQuery = new NeutralQuery(0);
            schoolQuery.addCriteria(new NeutralCriteria("stateOrganizationId", "=", schoolIds));

            Iterable<NeutralRecord> queriedSchools = getNeutralRecordMongoAccess().getRecordRepository().findAllForJob(
                    EntityNames.SCHOOL, getJob().getId(), schoolQuery);

            if (queriedSchools != null) {
                Iterator<NeutralRecord> itr = queriedSchools.iterator();
                NeutralRecord record = null;
                while (itr.hasNext()) {
                    record = itr.next();
                    schools.add(record);
                }
            }
        }
        return schools;
    }

    /**
     * Gets all sessions associated with the specified student-school pair.
     *
     * @param studentId
     *            StudentUniqueStateId for student.
     * @param schoolId
     *            StateOrganizationId for school.
     * @return Map of Sessions for student-school pair.
     */
    private Map<Object, NeutralRecord> getSessions(String studentId, String schoolId) {
        NeutralQuery query = new NeutralQuery();
        query.setLimit(0);
        query.addCriteria(new NeutralCriteria("studentId", "=", studentId));

        Iterable<NeutralRecord> associations = getNeutralRecordMongoAccess().getRecordRepository().findAllForJob(
                EntityNames.STUDENT_SECTION_ASSOCIATION, getJob().getId(), query);

        Map<Object, NeutralRecord> studentSchoolSessions = new HashMap<Object, NeutralRecord>();
        if (associations != null) {
            List<String> sectionIds = new ArrayList<String>();
            for (NeutralRecord association : associations) {
                Map<String, Object> associationAttributes = association.getAttributes();
                String sectionId = (String) associationAttributes.get("sectionId");
                sectionIds.add(sectionId);
            }

            NeutralQuery sectionQuery = new NeutralQuery();
            sectionQuery.setLimit(0);
            sectionQuery.addCriteria(new NeutralCriteria("schoolId", "=", schoolId));
            sectionQuery.addCriteria(new NeutralCriteria("uniqueSectionCode", "=", sectionIds));

            Iterable<NeutralRecord> sections = getNeutralRecordMongoAccess().getRecordRepository().findAllForJob(
                    EntityNames.SECTION, getJob().getId(), sectionQuery);

            if (sections != null) {
                List<String> sessionIds = new ArrayList<String>();
                for (NeutralRecord section : sections) {
                    Map<String, Object> sectionAttributes = section.getAttributes();
                    String sessionId = (String) sectionAttributes.get("sessionId");
                    sessionIds.add(sessionId);
                }

                NeutralQuery sessionQuery = new NeutralQuery();
                sessionQuery.addCriteria(new NeutralCriteria("sessionName", "=", sessionIds));

                Iterable<NeutralRecord> sessions = getNeutralRecordMongoAccess().getRecordRepository().findAllForJob(
                        EntityNames.SESSION, getJob().getId(), sessionQuery);

                if (sessions != null) {
                    for (NeutralRecord session : sessions) {
                        studentSchoolSessions.put(session.getRecordId(), session);
                    }
                }
            }
        }
        return studentSchoolSessions;
    }

    /**
     * Creates placeholders for attendance events based on provided sessions.
     *
     * @param sessions
     *            Sessions enumerating school years to key off of for attendance events.
     * @return Map containing { schoolYear --> empty list }
     */
    private Map<String, List<Map<String, Object>>> createAttendancePlaceholdersFromSessions(
            Map<Object, NeutralRecord> sessions) {
        Map<String, List<Map<String, Object>>> placeholders = new HashMap<String, List<Map<String, Object>>>();
        for (Map.Entry<Object, NeutralRecord> session : sessions.entrySet()) {
            NeutralRecord sessionRecord = session.getValue();
            Map<String, Object> sessionAttributes = sessionRecord.getAttributes();
            String schoolYear = (String) sessionAttributes.get("schoolYear");
            placeholders.put(schoolYear, new ArrayList<Map<String, Object>>());
        }
        return placeholders;
    }

    /**
     * Maps the set of student attendance events into a transformed map of form {school year : list
     * of attendance events} based
     * on dates published in the sessions.
     *
     * @param studentAttendance
     *            Set of student attendance events.
     * @param sessions
     *            Set of sessions that correspond to the school the student attends.
     * @return Map containing transformed attendance information.
     */
    private Map<String, List<Map<String, Object>>> mapAttendanceIntoSchoolYears(List<Map<String, Object>> attendance,
            Map<Object, NeutralRecord> sessions) {
        Map<String, List<Map<String, Object>>> schoolYears = new HashMap<String, List<Map<String, Object>>>();
        for (Map.Entry<Object, NeutralRecord> session : sessions.entrySet()) {
            NeutralRecord sessionRecord = session.getValue();
            Map<String, Object> sessionAttributes = sessionRecord.getAttributes();
            String schoolYear = (String) sessionAttributes.get("schoolYear");
            DateTime sessionBegin = DateTimeUtil.parseDateTime((String) sessionAttributes.get("beginDate"));
            DateTime sessionEnd = DateTimeUtil.parseDateTime((String) sessionAttributes.get("endDate"));

            List<Map<String, Object>> events = new ArrayList<Map<String, Object>>();

            for (int i = 0; i < attendance.size(); i++) {
                Map<String, Object> event = attendance.get(i);
                String eventDate = (String) event.get("date");
                DateTime date = DateTimeUtil.parseDateTime(eventDate);
                if (DateTimeUtil.isLeftDateBeforeRightDate(sessionBegin, date)
                        && DateTimeUtil.isLeftDateBeforeRightDate(date, sessionEnd)) {
                    events.add(event);
                }
            }
            schoolYears.put(schoolYear, events);
        }
        return schoolYears;
    }
}
