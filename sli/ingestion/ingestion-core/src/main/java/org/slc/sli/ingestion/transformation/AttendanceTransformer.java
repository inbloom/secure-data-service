package org.slc.sli.ingestion.transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.client.constants.EntityNames;
import org.slc.sli.common.util.datetime.DateTimeUtil;
import org.slc.sli.common.util.uuid.Type1UUIDGeneratorStrategy;
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
@Component("attendanceTransformationStrategy")
public class AttendanceTransformer extends AbstractTransformationStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(AttendanceTransformer.class);

    private Map<String, Map<Object, NeutralRecord>> collections;
    private Map<String, Map<Object, NeutralRecord>> transformedCollections;

    @Autowired
    private Type1UUIDGeneratorStrategy uuidGenerator;

    /**
     * Default constructor.
     */
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
     * Pre-requisite interchanges for daily attendance data to be successfully transformed:
     * student, education organization, education organization calendar, master schedule,
     * student enrollment
     */
    public void loadData() {
        LOG.info("Loading data for attendance transformation.");

        List<String> collectionsToLoad = Arrays.asList(EntityNames.STUDENT_SCHOOL_ASSOCIATION);
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
        LOG.debug("Transforming attendance data");
        HashMap<Object, NeutralRecord> newCollection = new HashMap<Object, NeutralRecord>();
        Set<Pair<String, String>> studentSchoolPairs = new HashSet<Pair<String, String>>();

        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collections.get(
                EntityNames.STUDENT_SCHOOL_ASSOCIATION).entrySet()) {
            NeutralRecord neutralRecord = neutralRecordEntry.getValue();
            Map<String, Object> attributes = neutralRecord.getAttributes();

            String studentId = (String) attributes.get("studentId");
            String schoolId = (String) attributes.get("schoolId");

            if (studentSchoolPairs.contains(Pair.of(studentId, schoolId))) {
                LOG.warn("Already assembled attendance data for student: {} at school: {}", studentId, schoolId);
            } else {
                studentSchoolPairs.add(Pair.of(studentId, schoolId));

                NeutralRecord attendanceRecord = createTransformedAttendanceRecord();
                Map<String, Object> attendanceAttributes = attendanceRecord.getAttributes();

                attendanceAttributes.put("studentId", studentId);
                attendanceAttributes.put("schoolId", schoolId);

                Map<Object, NeutralRecord> studentAttendance = getAttendanceEvents(studentId);
                Map<Object, NeutralRecord> sessions = getSessions(studentId, schoolId);

                LOG.info("For student with id: {} in school: {}", studentId, schoolId);
                LOG.info("  Found {} associated sessions.", sessions.size());
                LOG.info("  Found {} attendance events.", studentAttendance.size());

                Map<String, Object> schoolYears = mapAttendanceIntoSchoolYears(studentAttendance, sessions);

                if (schoolYears.entrySet().size() > 0) {
                    List<Map<String, Object>> daily = new ArrayList<Map<String, Object>>();
                    for (Map.Entry<String, Object> entry : schoolYears.entrySet()) {
                        String schoolYear = entry.getKey();

                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> events = (List<Map<String, Object>>) entry.getValue();
                        Map<String, Object> attendance = new HashMap<String, Object>();
                        attendance.put("schoolYear", schoolYear);
                        attendance.put("attendanceEvent", events);
                        daily.add(attendance);
                    }
                    attendanceAttributes.put("schoolYearAttendance", daily);
                    attendanceRecord.setAttributes(attendanceAttributes);
                    newCollection.put(attendanceRecord.getRecordId(), attendanceRecord);
                } else {
                    LOG.warn("  No daily attendance for student: {} in school: {}", studentId, schoolId);
                }
            }
        }
        transformedCollections.put(EntityNames.ATTENDANCE, newCollection);
        LOG.info("Finished transforming attendance data for {} student-school associations.", newCollection.entrySet()
                .size());
    }

    /**
     * Creates a Neutral Record of type 'dailyAttendance'.
     *
     * @return newly created 'dailyAttendance' Neutral Record.
     */
    private NeutralRecord createTransformedAttendanceRecord() {
        NeutralRecord record = new NeutralRecord();
        record.setRecordId(uuidGenerator.randomUUID().toString());
        record.setRecordType(EntityNames.ATTENDANCE);
        return record;
    }

    /**
     * Persists the transformed data into mongo.
     */
    public void persist() {
        LOG.info("Persisting transformed data into attendance_transformed staging collection.");
        for (Map.Entry<String, Map<Object, NeutralRecord>> collectionEntry : transformedCollections.entrySet()) {
            for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collectionEntry.getValue().entrySet()) {
                NeutralRecord neutralRecord = neutralRecordEntry.getValue();
                neutralRecord.setRecordType(neutralRecord.getRecordType() + "_transformed");
                getNeutralRecordMongoAccess().getRecordRepository().createForJob(neutralRecord, getJob().getId());
            }
        }
        LOG.info("Finished persisting transformed data into attendance_transformed staging collection.");
    }

    /**
     * Returns all collection entities found in temp ingestion database
     *
     * @param collectionName
     */
    private Map<Object, NeutralRecord> getCollectionFromDb(String collectionName) {
        Criteria jobIdCriteria = Criteria.where(BATCH_JOB_ID_KEY).is(getBatchJobId());

        @SuppressWarnings("deprecation")
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

    /**
     * Gets attendance events for the specified student.
     *
     * @param studentId
     *            StudentUniqueStateId for student.
     * @return Map of Attendance Events for student.
     */
    private Map<Object, NeutralRecord> getAttendanceEvents(String studentId) {
        NeutralQuery query = new NeutralQuery();
        query.setLimit(0);
        query.addCriteria(new NeutralCriteria("studentId", "=", studentId));

        Iterable<NeutralRecord> records = getNeutralRecordMongoAccess().getRecordRepository().findAllForJob(
                EntityNames.ATTENDANCE, getJob().getId(), query);

        Map<Object, NeutralRecord> studentAttendance = new HashMap<Object, NeutralRecord>();
        if (records != null) {
            for (NeutralRecord record : records) {
                studentAttendance.put(record.getRecordId(), record);
            }
        }
        return studentAttendance;
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
    private Map<String, Object> mapAttendanceIntoSchoolYears(Map<Object, NeutralRecord> studentAttendance,
            Map<Object, NeutralRecord> sessions) {
        Map<String, Object> schoolYears = new HashMap<String, Object>();
        for (Map.Entry<Object, NeutralRecord> session : sessions.entrySet()) {
            NeutralRecord sessionRecord = session.getValue();
            Map<String, Object> sessionAttributes = sessionRecord.getAttributes();
            String schoolYear = (String) sessionAttributes.get("schoolYear");
            DateTime sessionBegin = DateTimeUtil.parseDateTime((String) sessionAttributes.get("beginDate"));
            DateTime sessionEnd = DateTimeUtil.parseDateTime((String) sessionAttributes.get("endDate"));

            List<Map<String, Object>> events = new ArrayList<Map<String, Object>>();
            for (Iterator<Map.Entry<Object, NeutralRecord>> recordItr = studentAttendance.entrySet().iterator(); recordItr
                    .hasNext();) {
                Map.Entry<Object, NeutralRecord> record = recordItr.next();
                NeutralRecord eventRecord = record.getValue();
                Map<String, Object> eventAttributes = eventRecord.getAttributes();

                String eventDate = (String) eventAttributes.get("eventDate");
                DateTime date = DateTimeUtil.parseDateTime(eventDate);
                if (DateTimeUtil.isLeftDateBeforeRightDate(sessionBegin, date)
                        && DateTimeUtil.isLeftDateBeforeRightDate(date, sessionEnd)) {
                    Map<String, Object> event = new HashMap<String, Object>();
                    String eventCategory = (String) eventAttributes.get("attendanceEventCategory");
                    event.put("date", eventDate);
                    event.put("event", eventCategory);
                    if (eventAttributes.containsKey("attendanceEventReason")) {
                        String eventReason = (String) eventAttributes.get("attendanceEventReason");
                        event.put("reason", eventReason);
                    }
                    events.add(event);
                    recordItr.remove();
                }
            }
            if (events.size() > 0) {
                schoolYears.put(schoolYear, events);
            }
        }

        // if student attendance still has attendance events --> orphaned events

        // Iterator<Map.Entry<Object, NeutralRecord>> recordItr =
        // studentAttendance.entrySet().iterator();
        // if (recordItr.hasNext()) {
        // int orphanedEvents = 0;
        // while (recordItr.hasNext()) {
        // recordItr.next();
        // orphanedEvents++;
        // }
        // LOG.warn("  {} attendance events still need to be mapped into a school year.",
        // orphanedEvents);
        // }
        return schoolYears;
    }
}
