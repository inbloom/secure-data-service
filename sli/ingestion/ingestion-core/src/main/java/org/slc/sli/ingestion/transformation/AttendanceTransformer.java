package org.slc.sli.ingestion.transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.util.datetime.DateTimeUtil;
import org.slc.sli.common.util.uuid.Type1UUIDGeneratorStrategy;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.NeutralRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
        Map<Object, NeutralRecord> newCollection = new HashMap<Object, NeutralRecord>();
        
        // roll attendance events into a map where { studentId --> list of associated attendance
        // events}
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
        
        // now studentAttendanceEvents contains a Map of the form { studentId --> list of attendance
        // events }
        for (Map.Entry<String, List<Map<String, Object>>> entry : studentAttendanceEvents.entrySet()) {
            String studentId = entry.getKey();
            List<Map<String, Object>> attendance = entry.getValue();
            List<NeutralRecord> schools = getSchoolsForStudent(studentId);
            if (schools.size() == 0) {
                LOG.error("Student with id: {} is not associated to any schools.");
            } else if (schools.size() > 1) {
                LOG.error("Student with id: {} is associated to more than one school.. impossible to associate.");
            } else {
                NeutralRecord school = schools.get(0);
                String schoolId = (String) school.getAttributes().get("stateOrganizationId");
                Map<Object, NeutralRecord> sessions = getSessions(studentId, schoolId);
                
                LOG.info("For student with id: {} in school: {}", studentId, schoolId);
                LOG.info("  Found {} associated sessions.", sessions.size());
                LOG.info("  Found {} attendance events.", attendance.size());
                
                Map<String, List<Map<String, Object>>> schoolYears = mapAttendanceIntoSchoolYears(attendance, sessions);
                
                if (schoolYears.entrySet().size() > 0) {
                    
                    // check if attendance for student - school pair exists in staging
                    NeutralQuery query = new NeutralQuery(1);
                    query.addCriteria(new NeutralCriteria("studentId", NeutralCriteria.OPERATOR_EQUAL, studentId));
                    query.addCriteria(new NeutralCriteria("schoolId", NeutralCriteria.OPERATOR_EQUAL, schoolId));
                    
                    boolean foundStudentAttendance = false;
                    
                    // check here to see if neutral record already exists in (transformed) staging
                    // mongo
                    Iterable<NeutralRecord> records = getNeutralRecordMongoAccess().getRecordRepository()
                            .findAllForJob(ATTENDANCE_TRANSFORMED, getJob().getId(), query);
                    if (records != null && records.iterator().hasNext()) {
                        foundStudentAttendance = true;
                        NeutralRecord record = records.iterator().next();
                        Map<String, Object> stagedAttributes = record.getAttributes();
                        
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> schoolYearAttendances = (List<Map<String, Object>>) stagedAttributes
                                .get("schoolYearAttendance");
                        
                        for (int i = 0; i < schoolYearAttendances.size(); i++) {
                            Map<String, Object> schoolYearAttendance = schoolYearAttendances.get(i);
                            String schoolYear = (String) schoolYearAttendance.get("schoolYear");
                            @SuppressWarnings("unchecked")
                            List<Map<String, Object>> events = (List<Map<String, Object>>) schoolYearAttendance
                                    .get("attendanceEvent");
                            
                            if (schoolYears.containsKey(schoolYear)) {
                                List<Map<String, Object>> localEvents = schoolYears.get(schoolYear);
                                events.addAll(localEvents);
                            }
                        }
                        
                        record.setAttributes(stagedAttributes);
                        
                        // TODO: This sets attendance record's source file to FIRST attendance
                        // record found
                        // --> augment transformer so that the file of each attendance event is
                        // carried through to this stage?
                        record.setSourceFile(((NeutralRecord) collections.get(EntityNames.ATTENDANCE).values()
                                .iterator().next()).getSourceFile());
                        newCollection.put(record.getRecordId(), record);
                    }
                    
                    // if attendance for student - school pair doens't exist in staging --> check
                    // persistent mongo
                    // if (!foundStudentAttendance) {
                    //
                    // }
                    
                    // if attendance for student - school pair doesn't exist in either location -->
                    // create new record
                    if (!foundStudentAttendance) {
                        List<Map<String, Object>> daily = new ArrayList<Map<String, Object>>();
                        for (Map.Entry<String, List<Map<String, Object>>> year : schoolYears.entrySet()) {
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
                        
                        NeutralRecord attendanceRecord = createAttendanceRecord();
                        attendanceRecord.setAttributes(attendanceAttributes);
                        
                        // TODO: This sets attendance record's source file to FIRST attendance
                        // record found
                        // --> augment transformer so that the file of each attendance event is
                        // carried through to this stage?
                        attendanceRecord.setSourceFile(((NeutralRecord) collections.get(EntityNames.ATTENDANCE)
                                .values().iterator().next()).getSourceFile());
                        newCollection.put(attendanceRecord.getRecordId(), attendanceRecord);
                    }
                    
                } else {
                    LOG.warn("No daily attendance for student: {} in school: {}", studentId, schoolId);
                }
            }
        }
        transformedCollections.put(EntityNames.ATTENDANCE, newCollection);
        LOG.info("Finished transforming attendance data");
    }
    
    /**
     * Persists the transformed data into staging mongo database.
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
     * Creates a Neutral Record of type 'dailyAttendance'.
     * 
     * @return newly created 'dailyAttendance' Neutral Record.
     */
    private NeutralRecord createAttendanceRecord() {
        NeutralRecord record = new NeutralRecord();
        record.setRecordId(uuidGenerator.randomUUID().toString());
        record.setRecordType(EntityNames.ATTENDANCE);
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
        query.addCriteria(new NeutralCriteria("studentId", NeutralCriteria.OPERATOR_EQUAL, studentId));
        
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
                    EntityNames.EDUCATION_ORGANIZATION, getJob().getId(), schoolQuery);
            
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
                String eventDate = (String) event.get("eventDate");
                DateTime date = DateTimeUtil.parseDateTime(eventDate);
                if (DateTimeUtil.isLeftDateBeforeRightDate(sessionBegin, date)
                        && DateTimeUtil.isLeftDateBeforeRightDate(date, sessionEnd)) {
                    events.add(event);
                }
            }
            
            if (events.size() > 0) {
                schoolYears.put(schoolYear, events);
            }
        }
        return schoolYears;
    }
}
