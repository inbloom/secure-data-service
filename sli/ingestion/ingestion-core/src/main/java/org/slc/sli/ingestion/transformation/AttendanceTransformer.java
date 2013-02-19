/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
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
import org.slc.sli.ingestion.delta.SliDeltaManager;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.AggregatedSource;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;
import org.slc.sli.ingestion.reporting.impl.ElementSourceImpl;

/**
 * Transforms disjoint set of attendance events into cleaner set of {school year : list of
 * attendance events} mappings and stores in the appropriate student-school or student-section
 * associations.
 *
 * @author shalka
 */
@Scope("prototype")
@Component("attendanceEventTransformationStrategy")
public class AttendanceTransformer extends AbstractTransformationStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(AttendanceTransformer.class);

    private static final String VALUE = "_value";

    private static final String STATE_ORGANIZATION_ID = "StateOrganizationId";
    private static final String EDUCATIONAL_ORG_ID = "EducationalOrgIdentity";
    private static final String EDORG_ID = EDUCATIONAL_ORG_ID + "." + STATE_ORGANIZATION_ID;

    private static final String ATTENDANCE_EVENT = "attendanceEvent";
    private static final String ATTENDANCE_EVENT_REASON = "AttendanceEventReason" + VALUE;
    private static final String ATTENDANCE_TRANSFORMED = ATTENDANCE_EVENT + "_transformed";
    private static final String ATTENDANCE_EVENT_DATE = "EventDate." + VALUE;
    private static final String ATTENDANCE_EVENT_CATEGORY = "AttendanceEventCategory." + VALUE;
    private static final String ATTENDANCE_SCHOOL_ID = "SchoolReference." + EDORG_ID + "." + VALUE;

    private static final String SCHOOL = "school";
    private static final String SCHOOL_ID = "schoolId";
    private static final String SCHOOL_YEAR = "SchoolYear";
    private static final String STUDENT_ID = "studentId";
    private static final String SESSION = "session";
    private static final String SESSION_SHOOL_ID = "EducationOrganizationReference." + EDORG_ID + "." + VALUE;
    private static final String SESSION_BEGINDATE = "BeginDate." + VALUE;
    private static final String SESSION_ENDDATE = "EndDate." + VALUE;
    private static final String DATE = "date";
    private static final String EVENT = "event";
    private static final String REASON = "reason";
    private static final String STUDENT_UNIQUE_STATE_ID = "StudentReference.StudentIdentity.StudentUniqueStateId." + VALUE;
    private static final String RECORDHASHDATA = SliDeltaManager.RECORDHASH_DATA;

    private int numAttendancesIngested = 0;

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
        attendances = getCollectionFromDb(ATTENDANCE_EVENT);
        LOG.info("{} is loaded into local storage.  Total Count = {}", ATTENDANCE_EVENT, attendances.size());
    }

    /**
     * Transforms attendance events from Ed-Fi model into SLI model.
     */
    public void transform() {
        LOG.info("Transforming attendance data");

        Map<Pair<String, String>, List<Map<String, Object>>> studentSchoolAttendanceEvents = new HashMap<Pair<String, String>, List<Map<String, Object>>>();

        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : attendances.entrySet()) {
            NeutralRecord neutralRecord = neutralRecordEntry.getValue();
            Map<String, Object> attributes = neutralRecord.getAttributes();
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> recordHashData = (List<Map<String, Object>>) neutralRecord.getMetaData().get(
                    SliDeltaManager.RECORDHASH_DATA);

            String studentId = null;
            try {
                studentId = (String) PropertyUtils.getNestedProperty(attributes, STUDENT_UNIQUE_STATE_ID);
            } catch (IllegalAccessException e) {
                LOG.error("Failed to extract StudentUniqueStateId from attendance entity", e);
                super.reportError(attendances.values().iterator().next().getSourceFile(), getAggregatedSource(),
                        CoreMessageCode.CORE_0048, e.toString());
            } catch (InvocationTargetException e) {
                LOG.error("Failed to extract StudentUniqueStateId from attendance entity", e);
                super.reportError(attendances.values().iterator().next().getSourceFile(), getAggregatedSource(),
                        CoreMessageCode.CORE_0056, e.toString());
            } catch (NoSuchMethodException e) {
                LOG.error("Failed to extract StudentUniqueStateId from attendance entity", e);
                super.reportError(attendances.values().iterator().next().getSourceFile(), getAggregatedSource(),
                        CoreMessageCode.CORE_0057, e.toString());
            }

            String stateOrganizationId = (String) getProperty(attributes, ATTENDANCE_SCHOOL_ID);

            if (stateOrganizationId != null) {
                String schoolId = stateOrganizationId;
                List<Map<String, Object>> events = new ArrayList<Map<String, Object>>();

                if (studentSchoolAttendanceEvents.containsKey(Pair.of(studentId, schoolId))) {
                    events = studentSchoolAttendanceEvents.get(Pair.of(studentId, schoolId));
                }

                Map<String, Object> event = new HashMap<String, Object>();
                String eventDate = (String) getProperty(attributes, ATTENDANCE_EVENT_DATE);
                String eventCategory = (String) getProperty(attributes, ATTENDANCE_EVENT_CATEGORY);
                event.put(DATE, eventDate);
                event.put(EVENT, eventCategory);
                if (attributes.containsKey(ATTENDANCE_EVENT_REASON)) {
                    String eventReason = (String) getProperty(attributes, ATTENDANCE_EVENT_REASON);
                    event.put(REASON, eventReason);
                }
                event.put(RECORDHASHDATA, recordHashData);  // include data for record level
                                                           // delta hash processing
                events.add(event);
                studentSchoolAttendanceEvents.put(Pair.of(studentId, schoolId), events);
            }
        }


            if (studentSchoolAttendanceEvents.size() > 0) {
                LOG.info("Discovered {} student-school associations from attendance events",
                        studentSchoolAttendanceEvents.size());
                for (Map.Entry<Pair<String, String>, List<Map<String, Object>>> entry : studentSchoolAttendanceEvents
                        .entrySet()) {
                    Pair<String, String> studentSchoolPair = entry.getKey();
                    List<Map<String, Object>> attendance = entry.getValue();
                    String attendanceStudentId = studentSchoolPair.getLeft();
                    String schoolId = studentSchoolPair.getRight();
                    transformAndPersistAttendanceEvents(attendanceStudentId, schoolId, attendance);
                }
            }

            long numAttendance = attendances.size();
            if (numAttendance != numAttendancesIngested) {
                long remainingAttendances = numAttendance - numAttendancesIngested;
                super.reportWarnings(attendances.values().iterator().next().getSourceFile(), getAggregatedSource(),
                        CoreMessageCode.CORE_0028, Long.toString(remainingAttendances));
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
    private void transformAndPersistAttendanceEvents(String studentId, String schoolId,
            List<Map<String, Object>> attendance) {
        Map<Object, NeutralRecord> sessions = getSessions(schoolId, new HashSet<String>());

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

        /*
         * metaData: {
         * ...
         * "rhData": [ {"rhId": <blahId0>, "rhHash": <blahHash0>}, {"rhId": <blahId1>, "rhHash":
         * <blahHash1>}, ... ],
         * "rhTenantId": <tenantId>
         * }
         */
        List<Map<String, Object>> attendanceRhData = new ArrayList<Map<String, Object>>();

        Map<String, List<Map<String, Object>>> schoolYears = mapAttendanceIntoSchoolYears(attendance, sessions,
                studentId, schoolId);

        if (schoolYears.entrySet().size() > 0) {
            for (Map.Entry<String, List<Map<String, Object>>> attendanceEntry : schoolYears.entrySet()) {
                String schoolYear = attendanceEntry.getKey();
                List<Map<String, Object>> events = attendanceEntry.getValue();

                NeutralQuery query = new NeutralQuery(1);
                query.addCriteria(new NeutralCriteria(BATCH_JOB_ID_KEY, NeutralCriteria.OPERATOR_EQUAL,
                        getBatchJobId(), false));
                query.addCriteria(new NeutralCriteria(STUDENT_ID, NeutralCriteria.OPERATOR_EQUAL, studentId));
                query.addCriteria(new NeutralCriteria("SchoolReference." + EDUCATIONAL_ORG_ID + "." + STATE_ORGANIZATION_ID + "." + VALUE,
                        NeutralCriteria.OPERATOR_EQUAL, schoolId));
                query.addCriteria(new NeutralCriteria("schoolYearAttendance.SchoolYear",
                        NeutralCriteria.OPERATOR_EQUAL, schoolYear));

                attendanceRhData.addAll(extractRecordHashDataFromAttendanceEvents(events));

                Map<String, Object> attendanceEventsToPush = new HashMap<String, Object>();
                attendanceEventsToPush.put("body.schoolYearAttendance.$.attendanceEvent", events.toArray());
                Map<String, Object> update = new HashMap<String, Object>();
                update.put("pushAll", attendanceEventsToPush);
                getNeutralRecordMongoAccess().getRecordRepository().updateFirstForJob(query, update,
                        ATTENDANCE_TRANSFORMED);

                LOG.debug("Added {} attendance events for school year: {}", events.size(), schoolYear);
            }
        } else {
            LOG.warn("No daily attendance for student: {} in school: {}", studentId, schoolId);
        }

        // Add record delta hash data to the transformed attendance metaData
        updateAttendanceRecordHashMetaData(studentId, schoolId, attendanceRhData);
    }

    /**
     * updateAttendanceRecordHashMetaData
     *
     * Update the identified attendance_transformed entity by adding record delta hash data to its
     * metaData
     *
     * @param studentId
     *            - identifier for the student whose attendance_transformed entity will be updating
     * @param schoolId
     *            - identifier for the school to which the attendance_transformed entity to be
     *            updated is associated
     * @param attendanceRhData
     *            - attendance record hash data to update the attendance_transformed entity with
     */
    private void updateAttendanceRecordHashMetaData(String studentId, String schoolId,
            List<Map<String, Object>> attendanceRhData) {
        NeutralQuery query = new NeutralQuery(1);
        query.addCriteria(new NeutralCriteria(BATCH_JOB_ID_KEY, NeutralCriteria.OPERATOR_EQUAL, getBatchJobId(), false));
        query.addCriteria(new NeutralCriteria(STUDENT_ID, NeutralCriteria.OPERATOR_EQUAL, studentId));
        query.addCriteria(new NeutralCriteria("schoolId." + EDUCATIONAL_ORG_ID + "." + STATE_ORGANIZATION_ID  + "." + VALUE,
                NeutralCriteria.OPERATOR_EQUAL, schoolId));

        Map<String, Object> attendanceEventDeltaHashValuesToPush = new HashMap<String, Object>();
        attendanceEventDeltaHashValuesToPush.put("metaData.rhData", attendanceRhData.toArray());
        Map<String, Object> updateOfDeltaHashValues = new HashMap<String, Object>();
        updateOfDeltaHashValues.put("pushAll", attendanceEventDeltaHashValuesToPush);

        getNeutralRecordMongoAccess().getRecordRepository().updateFirstForJob(query, updateOfDeltaHashValues,
                ATTENDANCE_TRANSFORMED);

    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractRecordHashDataFromAttendanceEvents(List<Map<String, Object>> events) {
        List<Map<String, Object>> rhData = new ArrayList<Map<String, Object>>();

        for (Map<String, Object> event : events) {
            // note that events from SLI will not have RECORDHASHDATA so will NOT be added to the
            // recordHash
            List<Map<String, Object>> eventRhData = (List<Map<String, Object>>) event.remove(RECORDHASHDATA);
            if (eventRhData != null) {
                rhData.addAll(eventRhData);
            }
        }
        return rhData;
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
            schoolYearAttendanceEvents.put(SCHOOL_YEAR, schoolYear);
            schoolYearAttendanceEvents.put(ATTENDANCE_EVENT, events);
            daily.add(schoolYearAttendanceEvents);
        }

        Map<String, Object> attendanceAttributes = new HashMap<String, Object>();

        // rebuild StudentReference, since that is what reference resolution will expect
        Map<String, Object> studentReference = new HashMap<String, Object>();
        Map<String, Object> studentIdentity = new HashMap<String, Object>();
        Map<String, Object> student_value = new HashMap<String, Object>();
        student_value.put("_value", studentId);
        studentIdentity.put("StudentUniqueStateId", student_value);
        studentReference.put("StudentIdentity", studentIdentity);
        

        attendanceAttributes.put("StudentReference", studentReference);
        attendanceAttributes.put(STUDENT_ID, studentId);
        attendanceAttributes.put("SchoolReference", createEdfiSchoolReference(schoolId));
        attendanceAttributes.put("schoolYearAttendance", daily);

        record.setAttributes(attendanceAttributes);

        record.setSourceFile(attendances.values().iterator().next().getSourceFile());

        return record;
    }

    /**
     * Gets all sessions associated with the specified student-school pair.
     *
     * @param studentId
     *            StudentUniqueStateId for student.
     * @param edOrgId
     *            StateOrganizationId for school.
     * @return Map of Sessions for student-school pair.
     */
    private Map<Object, NeutralRecord> getSessions(String edOrgId, Set<String> edOrgsSoFar) {
        edOrgsSoFar.add(edOrgId);
        Map<Object, NeutralRecord> sessions = new HashMap<Object, NeutralRecord>();
        Query query = new Query().limit(0);
        query.addCriteria(Criteria.where(BATCH_JOB_ID_KEY).is(getBatchJobId()));
        query.addCriteria(Criteria.where("body." + SESSION_SHOOL_ID).is(edOrgId));

        Iterable<NeutralRecord> queriedSessions = getNeutralRecordMongoAccess().getRecordRepository().findAllByQuery(
                SESSION, query);

        // get sessions of the school from SLI db
        Iterable<NeutralRecord> sliSessions = getSliSessions(edOrgId);

        queriedSessions = concat((List<NeutralRecord>) queriedSessions, (List<NeutralRecord>) sliSessions);

        if (queriedSessions != null) {
            Iterator<NeutralRecord> itr = queriedSessions.iterator();
            NeutralRecord record = null;
            while (itr.hasNext()) {
                record = itr.next();
                sessions.put(record.getRecordId(), record);
            }

        }

        String parentEdOrg = getParentEdOrg(edOrgId);
        if (parentEdOrg != null && !edOrgsSoFar.contains(parentEdOrg)) {
            sessions.putAll(getSessions(parentEdOrg, edOrgsSoFar));
        }

        return sessions;
    }

    /**
     * Gets the Parent Education Organization associated with a specific school
     *
     * @param edOrgId
     *            The StateOrganizationid for the school
     * @return The Id of the Parent Education Organization
     */
    @SuppressWarnings("unchecked")
    private String getParentEdOrg(String edOrgId) {
        Query schoolQuery = new Query().limit(1);
        schoolQuery.addCriteria(Criteria.where(BATCH_JOB_ID_KEY).is(getBatchJobId()));
        schoolQuery.addCriteria(Criteria.where("body." + STATE_ORGANIZATION_ID + "." + VALUE).is(edOrgId));

        Iterable<NeutralRecord> queriedSchool = getNeutralRecordMongoAccess().getRecordRepository().findAllByQuery(
                SCHOOL, schoolQuery);

        String parentEducationAgency = null;
        if (queriedSchool.iterator().hasNext()) {
            NeutralRecord record = queriedSchool.iterator().next();
            Map<String, Object> edorgReference = (Map<String, Object>) record.getAttributes().get(
                    "LocalEducationAgencyReference");
            if (edorgReference != null) {
                Map<String, Object> edorgIdentity = (Map<String, Object>) edorgReference.get(EDUCATIONAL_ORG_ID);
                if (edorgIdentity != null) {
                    Map<String, Object> stateOrgId = (Map<String, Object>) (edorgIdentity.get(STATE_ORGANIZATION_ID));
                    if (stateOrgId != null) {
                        parentEducationAgency = (String) stateOrgId.get(VALUE);
                    }
                }
            }
        } else {
            Entity school = getSliSchool(edOrgId);
            if (school != null) {
                String parentEducationAgencyID = (String) school.getBody().get("parentEducationAgencyReference");
                if (parentEducationAgencyID != null) {
                    // look up the state edorg id of parent from SLI using its _id. Since the child
                    // is in sli, the parent must be as well.
                    NeutralQuery parentQuery = new NeutralQuery(0);
                    parentQuery.addCriteria(new NeutralCriteria("_id", NeutralCriteria.OPERATOR_EQUAL,
                            parentEducationAgencyID));
                    Entity parent = getMongoEntityRepository().findOne(EntityNames.EDUCATION_ORGANIZATION, parentQuery);
                    parentEducationAgency = (String) parent.getBody().get("stateOrganizationId");
                }
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
            String schoolYear = (String) getProperty( sessionAttributes,SCHOOL_YEAR + "." + VALUE);
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
        Set<Integer> processedAttendances = new HashSet<Integer>();

        for (Map.Entry<Object, NeutralRecord> session : sessions.entrySet()) {
            NeutralRecord sessionRecord = session.getValue();
            Map<String, Object> sessionAttributes = sessionRecord.getAttributes();
            String schoolYear = (String) getProperty( sessionAttributes,SCHOOL_YEAR + "." + VALUE);
            DateTime sessionBegin = DateTimeUtil.parseDateTime((String) getProperty(sessionAttributes,SESSION_BEGINDATE));
            DateTime sessionEnd = DateTimeUtil.parseDateTime((String) getProperty(sessionAttributes, SESSION_ENDDATE));

            List<Map<String, Object>> events = new ArrayList<Map<String, Object>>();

            for (int i = 0; i < attendance.size(); i++) {
                Map<String, Object> event = attendance.get(i);
                String eventDate = (String) event.get(DATE);
                DateTime date = DateTimeUtil.parseDateTime(eventDate);
                if (DateTimeUtil.isLeftDateBeforeRightDate(sessionBegin, date)
                        && DateTimeUtil.isLeftDateBeforeRightDate(date, sessionEnd)) {
                    events.add(event);

                    // Only count one attendance event once.
                    processedAttendances.add(i);
                }
            }

            int eventSize = events.size();
            if (eventSize > 0) {
                stageSchoolYearAttendances.add(new SchoolYearAttendance(schoolYear, events));
            }
        }

        numAttendancesIngested += processedAttendances.size();

        if (sessions.entrySet().size() == 0 && attendance.size() > 0) {
            super.reportWarnings(attendances.values().iterator().next().getSourceFile(), getAggregatedSource(),
                    CoreMessageCode.CORE_0029, studentId, schoolId);
        }

        // Step 2: retrieve sli SchoolYearAttendances
        Set<SchoolYearAttendance> sliSchoolYearAttendances = getSliSchoolYearAttendances(studentId, schoolId);
        // Step 3: merge sli and staging SchoolYearAttendances
        Set<SchoolYearAttendance> mergedAttendances = mergeSchoolYearAttendance(sliSchoolYearAttendances,
                stageSchoolYearAttendances);
        Map<String, List<Map<String, Object>>> schoolYears = new HashMap<String, List<Map<String, Object>>>();
        for (SchoolYearAttendance schoolYearAttendance : mergedAttendances) {
            schoolYears.put(schoolYearAttendance.getSchoolYear(), schoolYearAttendance.getAttendanceEvent());
        }
        return schoolYears;
    }

    private Source getAggregatedSource() {
        String sourceFile = attendances.values().iterator().next().getSourceFile();
        AggregatedSource source = new AggregatedSource(sourceFile);
        for (NeutralRecord nr : attendances.values()) {
            source.addSource(new ElementSourceImpl(nr));
        }
        return source;
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
        if (sliSchoolYearAttendances.size() == 0) {
            return stageSchoolYearAttendances;
        }
        if (stageSchoolYearAttendances.size() == 0) {
            return sliSchoolYearAttendances;
        }

        Set<SchoolYearAttendance> newSchoolYearAttendances = new HashSet<SchoolYearAttendance>();
        for (SchoolYearAttendance stageSchoolYearAttendance : stageSchoolYearAttendances) {
            boolean merged = false;
            for (SchoolYearAttendance sliSchoolYearAttendance : sliSchoolYearAttendances) {
                // check and merge
                if (sliSchoolYearAttendance.sameSchoolYear(stageSchoolYearAttendance)) {
                    // find same school year
                    // merge stageSchoolYearAttendance into sliSchoolYearAttendance
                    sliSchoolYearAttendance.mergeAndUpdateAttendanceEvent(stageSchoolYearAttendance);
                    merged = true;
                    break;
                }
            }
            if (!merged) {
                // Add new school year
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
    private Set<SchoolYearAttendance> getSliSchoolYearAttendances(String studentUniqueStateId,
            String stateOrganizationId) {
        Set<SchoolYearAttendance> attendanceSet = new HashSet<SchoolYearAttendance>();

        NeutralQuery studentQuery = new NeutralQuery(0);
        studentQuery.addCriteria(new NeutralCriteria("studentUniqueStateId", NeutralCriteria.OPERATOR_EQUAL,
                studentUniqueStateId));
        Entity studentEntity = getMongoEntityRepository().findOne(EntityNames.STUDENT, studentQuery);
        String studentEntityId = null;
        if (studentEntity != null) {
            studentEntityId = studentEntity.getEntityId();
        } else {
            return attendanceSet;
        }

        NeutralQuery schoolQuery = new NeutralQuery(0);
        schoolQuery.addCriteria(new NeutralCriteria("stateOrganizationId", NeutralCriteria.OPERATOR_EQUAL,
                stateOrganizationId));
        Entity schoolEntity = getMongoEntityRepository().findOne(EntityNames.EDUCATION_ORGANIZATION, schoolQuery);
        String schoolEntityId = null;
        if (schoolEntity != null) {
            schoolEntityId = schoolEntity.getEntityId();
        } else {
            return attendanceSet;
        }

        NeutralQuery attendanceQuery = new NeutralQuery(0);
        attendanceQuery.addCriteria(new NeutralCriteria(STUDENT_ID, NeutralCriteria.OPERATOR_EQUAL, studentEntityId));
        attendanceQuery.addCriteria(new NeutralCriteria(SCHOOL_ID, NeutralCriteria.OPERATOR_EQUAL, schoolEntityId));
        Entity attendanceEntity = getMongoEntityRepository().findOne(EntityNames.ATTENDANCE, attendanceQuery);
        if (attendanceEntity == null) {
            return attendanceSet;
        }
        List<Map<String, Object>> schoolYearAttendance = (List<Map<String, Object>>) attendanceEntity.getBody().get(
                "schoolYearAttendance");
        if (schoolYearAttendance == null || schoolYearAttendance.size() == 0) {
            return attendanceSet;
        }

        for (Map<String, Object> yearAttendance : schoolYearAttendance) {
            String schoolYear = (String) yearAttendance.get("SchoolYear");
            List<Map<String, Object>> attendanceEvent = (List<Map<String, Object>>) yearAttendance
                    .get(ATTENDANCE_EVENT);
            if (attendanceEvent.size() > 0) {
                attendanceSet.add(new SchoolYearAttendance(schoolYear, attendanceEvent));
            }
        }
        return attendanceSet;
    }

    private Entity getSliSchool(String stateOrganizationId) {
        NeutralQuery schoolQuery = new NeutralQuery(0);
        schoolQuery.addCriteria(new NeutralCriteria("stateOrganizationId", NeutralCriteria.OPERATOR_EQUAL,
                stateOrganizationId));
        return getMongoEntityRepository().findOne(EntityNames.EDUCATION_ORGANIZATION, schoolQuery);
    }

    /**
     * Get the sessions from SLI db
     *
     * @param schoolName
     *            :
     * @return: List of sessions of the school from SLI
     */
    private Iterable<NeutralRecord> getSliSessions(String stateOrganizationId) {
        // Get schoolId within SLI db
        // TODO: we may not need this query when deterministic ID is implemented.
        Entity school = getSliSchool(stateOrganizationId);
        String sliSchoolId = "";

        if (school != null) {
            sliSchoolId = school.getEntityId();
        } else {
            return new ArrayList<NeutralRecord>();
        }

        NeutralQuery sessionQuery = new NeutralQuery(0);
        sessionQuery.addCriteria(new NeutralCriteria(SCHOOL_ID, NeutralCriteria.OPERATOR_EQUAL, sliSchoolId));
        Iterable<NeutralRecord> sliSessions = transformIntoNeutralRecord(getMongoEntityRepository().findAll(
                EntityNames.SESSION, sessionQuery));

        return sliSessions;
    }

    private Iterable<NeutralRecord> transformIntoNeutralRecord(Iterable<Entity> entities) {
        Iterator<Entity> entityItr = entities.iterator();
        List<NeutralRecord> sessionRecords = new ArrayList<NeutralRecord>();

        // Trasnforming SLI entity back to neutralRecord
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

    /*
     * This function only concatenate 2 lists sessions of the same school. Since the keys for
     * sessions are schoolId
     * and session name, this functions only needs to distinguish sessions via sessionName.
     */
    private Iterable<NeutralRecord> concat(List<NeutralRecord> first, List<NeutralRecord> second) {
        List<NeutralRecord> res = new ArrayList<NeutralRecord>();
        res.addAll(first);
        Set<String> sessions = new HashSet<String>();
        for (NeutralRecord record : first) {
            sessions.add((String) record.getAttributes().get("SessionName._value"));
        }
        for (NeutralRecord record : second) {
            String sKey = (String) record.getAttributes().get("SessionName._value");
            if (!sessions.contains(sKey)) {
                sessions.add(sKey);
                res.add(record);
            }
        }
        return res;
    }

    /**
     * create a school reference that can be resolved by the deterministic ID resolver
     */
    private static Map<String, Object> createEdfiSchoolReference(String schoolId) {
        Map<String, Object> schoolReferenceObj = new HashMap<String, Object>();
        Map<String, Object> idObj = new HashMap<String, Object>();
        Map<String, Object> _value = new HashMap<String, Object>();
        _value.put(VALUE, schoolId);
        idObj.put(STATE_ORGANIZATION_ID, _value);
        schoolReferenceObj.put(EDUCATIONAL_ORG_ID, idObj);
        return schoolReferenceObj;
    }

    /**
     * TODO: add javadoc
     *
     */
    static class SchoolYearAttendance {
        private String schoolYear;
        private List<Map<String, Object>> attendanceEvent;

        public SchoolYearAttendance(String schoolYear, List<Map<String, Object>> attendanceEvent) {
            this.schoolYear = schoolYear;
            this.attendanceEvent = attendanceEvent;
        }

        public void mergeAndUpdateAttendanceEvent(SchoolYearAttendance obj) {
            if (!sameSchoolYear(obj)) {
                return;
            }
            for (Map<String, Object> stageAttendance : obj.attendanceEvent) {
                for (Map<String, Object> sliAttendance : this.attendanceEvent) {
                    Object sliEvent = sliAttendance.get(EVENT);
                    Object stageEvent = stageAttendance.get(EVENT);
                    Object sliDate = sliAttendance.get(DATE);
                    Object stageDate = stageAttendance.get(DATE);
                    boolean eventMatch = sliEvent != null && stageEvent != null && sliEvent.equals(stageEvent);
                    boolean dateMatch = sliDate != null && stageDate != null && sliDate.equals(stageDate);
                    if (eventMatch && dateMatch) {
                        // remove matched to prevent duplicated
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
            return obj.schoolYear != null && obj.schoolYear.equals(this.schoolYear);
        }

        public String getSchoolYear() {
            return this.schoolYear;
        }

        public List<Map<String, Object>> getAttendanceEvent() {
            return this.attendanceEvent;
        }

        @Override
        public String toString() {
            return "schoolYear:" + schoolYear + ",attendanceEvent:" + attendanceEvent;
        }

    }

}
