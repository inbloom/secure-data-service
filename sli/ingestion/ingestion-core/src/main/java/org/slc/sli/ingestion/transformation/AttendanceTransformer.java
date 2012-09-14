/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.ingestion.transformation;

import java.util.ArrayList;
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
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.common.util.datetime.DateTimeUtil;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
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

    private static final String ATTENDANCE = "attendance";
    private static final String SCHOOL = "school";
    private static final String SESSION = "session";
    private static final String STUDENT_SCHOOL_ASSOCIATION = "studentSchoolAssociation";
    private static final String ATTENDANCE_TRANSFORMED = ATTENDANCE + "_transformed";
    private static final String EDUCATIONORGANIZATION = "educationOrganization";

    private Map<Object, NeutralRecord> attendances;

    @Autowired
    private UUIDGeneratorStrategy type1UUIDGeneratorStrategy;

    /**
     * Default constructor.
     */
    public AttendanceTransformer() {
        attendances = new HashMap<Object, NeutralRecord>();
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
        attendances = getCollectionFromDb(ATTENDANCE);
        LOG.info("{} is loaded into local storage.  Total Count = {}", ATTENDANCE, attendances.size());
    }

    /**
     * Transforms attendance events from Ed-Fi model into SLI model.
     */
    public void transform() {
        LOG.info("Transforming attendance data");

        Map<String, List<Map<String, Object>>> studentAttendanceEvents = new HashMap<String, List<Map<String, Object>>>();
        Map<Pair<String, String>, List<Map<String, Object>>> studentSchoolAttendanceEvents = new HashMap<Pair<String, String>, List<Map<String, Object>>>();

        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : attendances.entrySet()) {
            NeutralRecord neutralRecord = neutralRecordEntry.getValue();
            Map<String, Object> attributes = neutralRecord.getAttributes();
            String studentId = (String) attributes.get("studentId");

            if (attributes.containsKey("schoolId")) {
                Object stateOrganizationId = attributes.get("schoolId");
                if (stateOrganizationId != null && stateOrganizationId instanceof String) {
                    String schoolId = (String) stateOrganizationId;
                    List<Map<String, Object>> events = new ArrayList<Map<String, Object>>();

                    if (studentSchoolAttendanceEvents.containsKey(Pair.of(studentId, schoolId))) {
                        events = studentSchoolAttendanceEvents.get(Pair.of(studentId, schoolId));
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
                    studentSchoolAttendanceEvents.put(Pair.of(studentId, schoolId), events);
                }
            } else {
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
        }

        int numAttendanceIngested = 0;
        if (studentSchoolAttendanceEvents.size() > 0) {
            LOG.info("Discovered {} student-school associations from attendance events",
                    studentSchoolAttendanceEvents.size());
            for (Map.Entry<Pair<String, String>, List<Map<String, Object>>> entry : studentSchoolAttendanceEvents
                    .entrySet()) {
                Pair<String, String> studentSchoolPair = entry.getKey();
                List<Map<String, Object>> attendance = entry.getValue();
                String studentId = studentSchoolPair.getLeft();
                String schoolId = studentSchoolPair.getRight();
                numAttendanceIngested += transformAndPersistAttendanceEvents(studentId, schoolId, attendance);
            }
        }

        if (studentAttendanceEvents.size() > 0) {
            LOG.info("Discovered {} students from attendance events that need school mappings",
                    studentAttendanceEvents.size());
            for (Map.Entry<String, List<Map<String, Object>>> entry : studentAttendanceEvents.entrySet()) {
                String studentId = entry.getKey();
                List<Map<String, Object>> attendance = entry.getValue();
                List<NeutralRecord> schools = getSchoolsForStudent(studentId);
                if (schools.size() == 0) {
                    LOG.error("Student with id: {} is not associated to any schools.", studentId);
                } else if (schools.size() > 1) {
                    LOG.error("Student with id: {} is associated to more than one school.", studentId);
                } else {
                    NeutralRecord school = schools.get(0);
                    String schoolId = (String) school.getAttributes().get("stateOrganizationId");
                    numAttendanceIngested += transformAndPersistAttendanceEvents(studentId, schoolId, attendance);
                }
            }

        }

        long numAttendance = attendances.size();
        if (numAttendance != numAttendanceIngested) {
            long remainingAttendances = numAttendance - numAttendanceIngested;
            super.getErrorReport(attendances.values().iterator().next().getSourceFile())
                .warning(Long.toString(remainingAttendances) + " attendance events are not processed, because they are not within any school year", this);
        }

        LOG.info("Finished transforming attendance data");
    }

    /**
     * Transforms attendance data for the given student-school pair and persists to staging mongo
     * db.
     *
     * @param studentId
     *            Student Unique State Id.
     * @param schoolId
     *            State Organization Id.
     * @param attendance
     *            List of transformed attendance events.
     */
    private int transformAndPersistAttendanceEvents(String studentId, String schoolId,
            List<Map<String, Object>> attendance) {
        Map<Object, NeutralRecord> sessions = getSessions(schoolId);

        LOG.debug("For student with id: {} in school: {}", studentId, schoolId);
        LOG.debug("  Found {} associated sessions.", sessions.size());
        LOG.debug("  Found {} attendance events.", attendance.size());

        try {
            // create a placeholder for the student-school pair and write to staging mongo db
            NeutralRecord placeholder = createAttendanceRecordPlaceholder(studentId, schoolId, sessions);
            placeholder.setCreationTime(getWorkNote().getRangeMinimum());
            insertRecord(placeholder);
        } catch (DuplicateKeyException dke) {
            LOG.warn("Duplicate key exception when creating attendance placeholder. This is expected for the majority of such calls as there can only be one placeholder.");
        }

        Map<String, List<Map<String, Object>>> schoolYears = mapAttendanceIntoSchoolYears(attendance, sessions, studentId, schoolId);

        int numAttendances = 0;
        if (schoolYears.entrySet().size() > 0) {
            for (Map.Entry<String, List<Map<String, Object>>> attendanceEntry : schoolYears.entrySet()) {
                String schoolYear = attendanceEntry.getKey();
                List<Map<String, Object>> events = attendanceEntry.getValue();

                numAttendances += events.size();

                NeutralQuery query = new NeutralQuery(1);
                query.addCriteria(new NeutralCriteria(BATCH_JOB_ID_KEY, NeutralCriteria.OPERATOR_EQUAL, getBatchJobId(), false));
                query.addCriteria(new NeutralCriteria("studentId", NeutralCriteria.OPERATOR_EQUAL, studentId));
                query.addCriteria(new NeutralCriteria("schoolId", NeutralCriteria.OPERATOR_EQUAL, schoolId));
                query.addCriteria(new NeutralCriteria("schoolYearAttendance.schoolYear",
                        NeutralCriteria.OPERATOR_EQUAL, schoolYear));

                Map<String, Object> attendanceEventsToPush = new HashMap<String, Object>();
                attendanceEventsToPush.put("body.schoolYearAttendance.$.attendanceEvent", events.toArray());
                Map<String, Object> update = new HashMap<String, Object>();
                update.put("pushAll", attendanceEventsToPush);
                getNeutralRecordMongoAccess().getRecordRepository().updateFirstForJob(query, update,
                        ATTENDANCE_TRANSFORMED, getBatchJobId());
                LOG.debug("Added {} attendance events for school year: {}", events.size(), schoolYear);
            }
            return numAttendances;
        } else {
            LOG.warn("No daily attendance for student: {} in school: {}", studentId, schoolId);
            return 0;
        }
    }

    /**
     * Creates a Neutral Record of type 'attendance'.
     *
     * @return newly created 'attendance' Neutral Record.
     */
    private NeutralRecord createAttendanceRecordPlaceholder(String studentId, String schoolId,
            Map<Object, NeutralRecord> sessions) {
        NeutralRecord record = new NeutralRecord();
        record.setRecordId(type1UUIDGeneratorStrategy.generateId().toString());
        record.setRecordType(ATTENDANCE_TRANSFORMED);
        record.setBatchJobId(getBatchJobId());

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

        record.setSourceFile(attendances.values().iterator().next().getSourceFile());
        record.setLocationInSourceFile(attendances.values().iterator().next().getLocationInSourceFile());

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

        Query query = new Query().limit(0);
        query.addCriteria(Criteria.where(BATCH_JOB_ID_KEY).is(getBatchJobId()));
        query.addCriteria(Criteria.where("body.studentId").is(studentId));

        Iterable<NeutralRecord> associations = getNeutralRecordMongoAccess().getRecordRepository().findAllByQuery(
                STUDENT_SCHOOL_ASSOCIATION, query);

        if (associations != null) {
            List<String> schoolIds = new ArrayList<String>();
            for (NeutralRecord association : associations) {
                Map<String, Object> associationAttributes = association.getAttributes();
                String schoolId = (String) associationAttributes.get("schoolId");
                schoolIds.add(schoolId);
            }

            Query schoolQuery = new Query().limit(0);
            schoolQuery.addCriteria(Criteria.where(BATCH_JOB_ID_KEY).is(getBatchJobId()));
            schoolQuery.addCriteria(Criteria.where("body.stateOrganizationId").in(schoolIds));

            Iterable<NeutralRecord> queriedSchools = getNeutralRecordMongoAccess().getRecordRepository().findAllByQuery(
                    SCHOOL, schoolQuery);

            if (queriedSchools != null) {
                Iterator<NeutralRecord> itr = queriedSchools.iterator();
                NeutralRecord record = null;
                while (itr.hasNext()) {
                    record = itr.next();
                    schools.add(record);
                }
            }
        }
        if (schools.size() == 0) {
            schools.addAll(getSchoolsForStudentFromSLI(studentId));
        }
        return schools;
    }

    private List<NeutralRecord> getSchoolsForStudentFromSLI(String studentUniqueStateId) {
        List<NeutralRecord> schools = new ArrayList<NeutralRecord>();

        NeutralQuery studentQuery = new NeutralQuery(0);
        studentQuery.addCriteria(new NeutralCriteria("studentUniqueStateId", NeutralCriteria.OPERATOR_EQUAL, studentUniqueStateId));
        Entity studentEntity = getMongoEntityRepository().findOne(EntityNames.STUDENT, studentQuery);
        String studentEntityId = "";
        if(studentEntity != null) {
            studentEntityId = studentEntity.getEntityId();
        }

        NeutralQuery query = new NeutralQuery(0);
        query.addCriteria(new NeutralCriteria("studentId", NeutralCriteria.OPERATOR_EQUAL, studentEntityId));
        Iterable<Entity> associations = getMongoEntityRepository().findAll(EntityNames.STUDENT_SCHOOL_ASSOCIATION, query);

        if (associations != null) {
            List<String> schoolIds = new ArrayList<String>();
            for (Entity association : associations) {
                Map<String, Object> associationAttributes = association.getBody();
                String schoolId = (String) associationAttributes.get("schoolId");
                schoolIds.add(schoolId);
            }

            NeutralQuery schoolQuery = new NeutralQuery(0);
            schoolQuery.addCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, schoolIds));
            Iterable<Entity> queriedSchools = getMongoEntityRepository().findAll(EntityNames.EDUCATION_ORGANIZATION, schoolQuery);

            if (queriedSchools != null) {
                Iterator<Entity> itr = queriedSchools.iterator();
                NeutralRecord record = null;
                Entity entity = null;
                while (itr.hasNext()) {
                    entity = itr.next();
                    record = new NeutralRecord();
                    record.setAttributes(entity.getBody());
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
    private Map<Object, NeutralRecord> getSessions(String schoolId) {
        Map<Object, NeutralRecord> sessions = new HashMap<Object, NeutralRecord>();

        Query query = new Query().limit(0);
        query.addCriteria(Criteria.where(BATCH_JOB_ID_KEY).is(getBatchJobId()));
        query.addCriteria(Criteria.where("body.schoolId").is(schoolId));

        Iterable<NeutralRecord> queriedSessions = getNeutralRecordMongoAccess().getRecordRepository().findAllByQuery(
                SESSION, query);

        //get sessions of the school from SLI db
        Iterable<NeutralRecord> sliSessions = getSliSessions(schoolId);

        queriedSessions = concat((List<NeutralRecord>)queriedSessions, (List<NeutralRecord>)sliSessions);

        if (queriedSessions != null) {
            Iterator<NeutralRecord> itr = queriedSessions.iterator();
            NeutralRecord record = null;
            while (itr.hasNext()) {
                record = itr.next();
                sessions.put(record.getRecordId(), record);
            }
        }

        String parentEducationAgency = getParentEdOrg(schoolId);
        if (parentEducationAgency != null) {
            sessions.putAll(getSessions(parentEducationAgency));
        }

        return sessions;
    }

    /**
     * Gets the Parent Education Organization associated with a specific school
     *
     * @param schoolId
     *            The StateOrganizationid for the school
     * @return The Id of the Parent Education Organization
     */
    private String getParentEdOrg(String schoolId) {
        Query schoolQuery = new Query().limit(1);
        schoolQuery.addCriteria(Criteria.where(BATCH_JOB_ID_KEY).is(getBatchJobId()));
        schoolQuery.addCriteria(Criteria.where("body.stateOrganizationId").is(schoolId));

        Iterable<NeutralRecord> queriedSchool = getNeutralRecordMongoAccess().getRecordRepository().findAllByQuery(
                SCHOOL, schoolQuery);

        String parentEducationAgency = null;
        if (queriedSchool.iterator().hasNext()) {
            NeutralRecord record = queriedSchool.iterator().next();
            parentEducationAgency = (String) record.getAttributes().get("parentEducationAgencyReference");
        } else {
            Entity school = getSliSchool(schoolId);
            if(school != null) {
                parentEducationAgency = (String) school.getBody().get("parentEducationAgencyReference");
            }
        }

        return parentEducationAgency;
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
            if (schoolYear != null) {
                placeholders.put(schoolYear, new ArrayList<Map<String, Object>>());
            }
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
     * @param studentId
     *            studentUniqueStateId of the student.
     * @param schoolId
     *            stateOrganizationId of the school the student attends.
     * @return Map containing transformed attendance information.
     */
    protected Map<String, List<Map<String, Object>>> mapAttendanceIntoSchoolYears(List<Map<String, Object>> attendance,
            Map<Object, NeutralRecord> sessions, String studentId, String schoolId) {
        // Step 1: prepare stageing SchoolYearAttendances
        Set<SchoolYearAttendance> stageSchoolYearAttendances = new HashSet<SchoolYearAttendance>();
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
            
            if (events.size() > 0 ) {
                stageSchoolYearAttendances.add(new SchoolYearAttendance(schoolYear, events));
            }
        }
        // Step 2: retrieve sli SchoolYearAttendances
        Set<SchoolYearAttendance> sliSchoolYearAttendances = getSliSchoolYearAttendances(studentId, schoolId);        
        // Step 3: merge sli and staging SchoolYearAttendances
        Set<SchoolYearAttendance> mergedAttendances = mergeSchoolYearAttendance(sliSchoolYearAttendances, stageSchoolYearAttendances);
        Map<String, List<Map<String, Object>>> schoolYears = new HashMap<String, List<Map<String, Object>>>();
        for (SchoolYearAttendance schoolYearAttendance : mergedAttendances) {
            schoolYears.put(schoolYearAttendance.getSchoolYear(), schoolYearAttendance.getAttendanceEvent());
        }
        return schoolYears;
    }
    
    /**
     * Merge the sets of SchoolYearAttendance from SLI and stage.
     *
     * @param sliSchoolYearAttendances
     *            Set containing SchoolYearAttendance from sli.
     * @param stageSchoolYearAttendances
     *            Set containing SchoolYearAttendance from stage.
     * @return Set containing SchoolYearAttendance merged from both.
     */
    private Set<SchoolYearAttendance> mergeSchoolYearAttendance(Set<SchoolYearAttendance> sliSchoolYearAttendances, 
                                                                Set<SchoolYearAttendance> stageSchoolYearAttendances) {
        if (sliSchoolYearAttendances.size() == 0) 
            return stageSchoolYearAttendances;
        if (stageSchoolYearAttendances.size() == 0) 
            return sliSchoolYearAttendances;
        
        Set<SchoolYearAttendance> newSchoolYearAttendances = new HashSet<SchoolYearAttendance>();
        for (SchoolYearAttendance stageSchoolYearAttendance : stageSchoolYearAttendances) {
            boolean merged = false;
            for (SchoolYearAttendance sliSchoolYearAttendance : sliSchoolYearAttendances) {
                //check and merge
                if (sliSchoolYearAttendance.sameSchoolYear(stageSchoolYearAttendance)) {
                    //find same school year
                    //merge stageSchoolYearAttendance into sliSchoolYearAttendance
                    sliSchoolYearAttendance.mergeAndUpdateAttendanceEvent(stageSchoolYearAttendance);
                    merged = true;
                    break;
                }
            }
            if (!merged) {
                //Add new school year
                newSchoolYearAttendances.add(stageSchoolYearAttendance);
            }
        }
        newSchoolYearAttendances.addAll(sliSchoolYearAttendances);
        return newSchoolYearAttendances;
    }
    /**
     * Retrieve the set of student attendance events from SLI.
     *
     * @param studentUniqueStateId
     *            studentUniqueStateId of the student.
     * @param stateOrganizationId
     *            stateOrganizationId of the school the student attends.
     * @return Set containing SchoolYearAttendance retrieved from SLI.
     */
    @SuppressWarnings("unchecked")
    private Set<SchoolYearAttendance> getSliSchoolYearAttendances(String studentUniqueStateId, String stateOrganizationId) { 
        Set<SchoolYearAttendance> attendances = new HashSet<SchoolYearAttendance>();
        
        NeutralQuery studentQuery = new NeutralQuery(0);
        studentQuery.addCriteria(new NeutralCriteria("studentUniqueStateId", NeutralCriteria.OPERATOR_EQUAL, studentUniqueStateId));
        Entity studentEntity = getMongoEntityRepository().findOne(EntityNames.STUDENT, studentQuery);
        String studentEntityId = null;
        if(studentEntity != null) {
            studentEntityId = studentEntity.getEntityId();
        } else {
            return attendances;            
        }

        NeutralQuery schoolQuery = new NeutralQuery(0);
        schoolQuery.addCriteria(new NeutralCriteria("stateOrganizationId", NeutralCriteria.OPERATOR_EQUAL, stateOrganizationId));
        Entity schoolEntity = getMongoEntityRepository().findOne(EntityNames.EDUCATION_ORGANIZATION, schoolQuery);
        String schoolEntityId = null;
        if(schoolEntity != null) {
            schoolEntityId = schoolEntity.getEntityId();
        } else {
            return attendances;            
        }

        NeutralQuery attendanceQuery = new NeutralQuery(0);
        attendanceQuery.addCriteria(new NeutralCriteria("studentId", NeutralCriteria.OPERATOR_EQUAL, studentEntityId));
        attendanceQuery.addCriteria(new NeutralCriteria("schoolId", NeutralCriteria.OPERATOR_EQUAL, schoolEntityId));
        Entity attendanceEntity = getMongoEntityRepository().findOne(EntityNames.ATTENDANCE, attendanceQuery);
        if(attendanceEntity == null) {
            return attendances;            
        }        
        List<Map<String,Object>> schoolYearAttendance = (List<Map<String,Object>>)attendanceEntity.getBody().get("schoolYearAttendance");       
        if (schoolYearAttendance == null || schoolYearAttendance.size() == 0) {
            return attendances;            
        }
        
        for (Map<String,Object> yearAttendance : schoolYearAttendance) {
            String schoolYear = (String)yearAttendance.get("schoolYear");
            List<Map<String, Object>> attendanceEvent = (List<Map<String, Object>>)yearAttendance.get("attendanceEvent");
            if (attendanceEvent.size() > 0) {
                attendances.add(new SchoolYearAttendance(schoolYear, attendanceEvent));
            }
        }        
        return attendances;            
    }

    @SuppressWarnings("deprecation")
    private Entity getSliSchool(String schoolName) {
        Query sliEdorgQuery = new Query().limit(0);
        sliEdorgQuery.addCriteria(Criteria.where("body.stateOrganizationId").is(schoolName));

        Iterable<Entity> sliSchool = getMongoEntityRepository().findByQuery(EDUCATIONORGANIZATION, sliEdorgQuery, 0, 0);
        Iterator<Entity> it = sliSchool.iterator();
        //At most one school is returned from the previous query
        if(it.hasNext()) {
            return it.next();
        }
        return null;
    }

    /**
     * Get the sessions from SLI db
     * @param schoolName:
     * @return: List of sessions of the school from SLI
     */
    @SuppressWarnings("deprecation")
    private Iterable<NeutralRecord> getSliSessions(String schoolName) {
        //Get schoolId within SLI db
        //TODO: we may not need this query when deterministic ID is implemented.
        Entity school = getSliSchool(schoolName);
        String sliSchoolId = "";

        if(school != null){
            sliSchoolId = school.getEntityId();
        }

        Query sliSessionQuery = new Query().limit(0);
        sliSessionQuery.addCriteria(Criteria.where("body.schoolId").is(sliSchoolId));

        Iterable<NeutralRecord> sliSessions = transformIntoNeutralRecord(getMongoEntityRepository().findByQuery(SESSION, sliSessionQuery, 0, 0));

        return sliSessions;
    }

    private Iterable<NeutralRecord> transformIntoNeutralRecord(Iterable<Entity> entities) {
        Iterator<Entity> entityItr = entities.iterator();
        List<NeutralRecord> sessionRecords = new ArrayList<NeutralRecord>();

        //Trasnforming SLI entity back to neutralRecord
        Entity sliSession = null;
        while (entityItr.hasNext()) {
            sliSession = entityItr.next();
            NeutralRecord session = new NeutralRecord();
            session.setRecordId(sliSession.getEntityId());
            session.setRecordType(sliSession.getType());
            session.setBatchJobId(getBatchJobId());
            session.setAttributes(sliSession.getBody());

            sessionRecords.add(session);
        }
        return sessionRecords;
    }

    private Iterable<NeutralRecord> concat(List<NeutralRecord> first, List<NeutralRecord> second) {
        List<NeutralRecord> res = new ArrayList<NeutralRecord>();
        res.addAll(first);
        res.addAll(second);
        return res;
    }

    class SchoolYearAttendance {
        private String schoolYear;
        private List<Map<String, Object>> attendanceEvent;
        
        public SchoolYearAttendance(String schoolYear, List<Map<String, Object>> attendanceEvent) {
            this.schoolYear = schoolYear;
            this.attendanceEvent = attendanceEvent;
        }
        
        public void mergeAndUpdateAttendanceEvent(SchoolYearAttendance obj) {
            if (!sameSchoolYear(obj)) 
                return;
            for (Map<String, Object> stageAttendance : obj.attendanceEvent) {
                for (Map<String, Object> sliAttendance : this.attendanceEvent) {
                    Object sliEvent =sliAttendance.get("event");
                    Object stageEvent =stageAttendance.get("event");
                    Object sliDate =sliAttendance.get("date");
                    Object stageDate =stageAttendance.get("date");
                    boolean eventMatch = sliEvent!=null && stageEvent!=null && sliEvent.equals(stageEvent);
                    boolean dateMatch = sliDate!=null && stageDate!=null && sliDate.equals(stageDate);
                    if (eventMatch && dateMatch) {
                        //remove matched to prevent duplicated
                        this.attendanceEvent.remove(sliAttendance);
                        break;
                    }                    
                }
            }
            List<Map<String, Object>> mergedAttendanceEvent = new ArrayList<Map<String, Object>>();
            mergedAttendanceEvent.addAll(this.attendanceEvent);
            mergedAttendanceEvent.addAll(obj.attendanceEvent);
            this.attendanceEvent = mergedAttendanceEvent;
        }
        
        public boolean sameSchoolYear(SchoolYearAttendance obj) {
            return obj.schoolYear!=null && obj.schoolYear.equals(this.schoolYear);            
        }
        
        public String getSchoolYear() {
            return this.schoolYear;
        }
        public List<Map<String, Object>> getAttendanceEvent() {
            return this.attendanceEvent;
        }
        public String toString() {
            return "schoolYear:"+schoolYear+",attendanceEvent:"+attendanceEvent;
        }
        
     }
}
