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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;
import junitx.util.PrivateAccessor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.RangedWorkNote;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.dal.NeutralRecordRepository;

/**
 * Unit Test for AttendanceTransformer
 *
 * @author slee
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })

public class AttendanceTransformerTest {
    @Autowired
    private AttendanceTransformer transformer;

    @Mock
    private MongoEntityRepository entityRepository = Mockito.mock(MongoEntityRepository.class);

    @Mock
    private NeutralRecordMongoAccess neutralRecordMongoAccess = Mockito.mock(NeutralRecordMongoAccess.class);

    @Mock
    private NeutralRecordRepository repository = Mockito.mock(NeutralRecordRepository.class);

    private String batchJobId = "10001";
    private static final String SESSION_SCHOOL_ID = "EducationOrganizationReference.EducationalOrgIdentity.StateOrganizationId";
    private Job job = mock(Job.class);

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);

        when(neutralRecordMongoAccess.getRecordRepository()).thenReturn(repository);
        neutralRecordMongoAccess.setNeutralRecordRepository(repository);
        transformer.setNeutralRecordMongoAccess(neutralRecordMongoAccess);
        transformer.setMongoEntityRepository(entityRepository);
        transformer.setBatchJobId(batchJobId);
        transformer.setJob(job);
        transformer.setWorkNote(RangedWorkNote.createSimpleWorkNote(batchJobId));

    }


    @SuppressWarnings("unchecked")
    @Test
    public void testGetSchoolsForStudentFromSLI() throws Throwable {

        //query SLI DB for student
        Entity studentEntity = buildStudentEntity("studentId2");
        String studentUniqueStateId = (String) studentEntity.getBody().get("studentUniqueStateId");
        NeutralQuery studentQuery = new NeutralQuery(0);
        studentQuery.addCriteria(new NeutralCriteria("studentUniqueStateId", NeutralCriteria.OPERATOR_EQUAL, studentUniqueStateId));
        when(entityRepository.findOne(Mockito.eq("student"), Mockito.argThat(new IsCorrectNeutralQuery(studentQuery))))
            .thenReturn(studentEntity);
        String studentEntityId = studentEntity.getEntityId();

        //query SLI DB for studentSchoolAssociation
        List<Entity> associations = buildStudentSchoolAssocEntities();
        NeutralQuery query = new NeutralQuery(0);
        query.addCriteria(new NeutralCriteria("studentId", NeutralCriteria.OPERATOR_EQUAL, studentEntityId));
        when(entityRepository.findAll(Mockito.eq("studentSchoolAssociation"), Mockito.argThat(new IsCorrectNeutralQuery(query))))
            .thenReturn(associations);

        //query SLI DB for school from studentSchoolAssociation
        List<String> schoolIds = new ArrayList<String>(associations.size());
        for (Entity association : associations) {
            schoolIds.add((String) association.getBody().get("schoolId"));
        }
        NeutralQuery schoolQuery = new NeutralQuery(0);
        schoolQuery.addCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, schoolIds));
        when(entityRepository.findAll(Mockito.eq("educationOrganization"), Mockito.argThat(new IsCorrectNeutralQuery(schoolQuery))))
            .thenReturn(buildSchoolEntities());


        List<NeutralRecord> result = (List<NeutralRecord>) PrivateAccessor.invoke(transformer, "getSchoolsForStudentFromSLI",
                new Class[]{String.class},  new Object[]{studentUniqueStateId});

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        NeutralRecord nr = result.get(0);
        Assert.assertEquals("schoolId2", nr.getAttributes().get("stateOrganizationId"));
        Assert.assertNull(nr.getRecordId());

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetSession() throws Throwable {

        String stateOrganizationId = "schoolId1";
        //Mock the query in staging for session
        Query q1 = new Query().limit(0);
        q1.addCriteria(Criteria.where("batchJobId").is(batchJobId));
        q1.addCriteria(Criteria.where("body."+ SESSION_SCHOOL_ID).is(stateOrganizationId));
        when(repository.findAllByQuery(Mockito.eq("session"), Mockito.argThat(new IsCorrectQuery(q1))))
            .thenReturn(new ArrayList<NeutralRecord>());

        //Mock the query in staging for parentEducationAgency
        q1 = new Query().limit(0);
        q1.addCriteria(Criteria.where("batchJobId").is(batchJobId));
        q1.addCriteria(Criteria.where("body.StateOrganizationId").is("schoolId1"));
        when(repository.findAllByQuery(Mockito.eq("school"), Mockito.argThat(new IsCorrectQuery(q1))))
            .thenReturn(new ArrayList<NeutralRecord>());

        //Mock the query in SLI for session
        Entity schoolEntity = buildSchoolEntity(stateOrganizationId);
        NeutralQuery schoolQuery = new NeutralQuery(0);
        schoolQuery.addCriteria(new NeutralCriteria("stateOrganizationId", NeutralCriteria.OPERATOR_EQUAL, stateOrganizationId));
        when(entityRepository.findOne(Mockito.eq("educationOrganization"), Mockito.argThat(new IsCorrectNeutralQuery(schoolQuery))))
            .thenReturn(schoolEntity);
        String schoolEntityId = schoolEntity.getEntityId();

        //Mock the query in sli for sessions
        List<Entity> sessions = buildSessionEntity();
        NeutralQuery sessionQuery = new NeutralQuery(0);
        sessionQuery.addCriteria(new NeutralCriteria("schoolId", NeutralCriteria.OPERATOR_EQUAL, schoolEntityId));
        when(entityRepository.findAll(Mockito.eq("session"), Mockito.argThat(new IsCorrectNeutralQuery(sessionQuery))))
            .thenReturn(sessions);

        String[] args = new String[1];
        args[0] = "schoolId1";
        Map<Object, NeutralRecord> res = (Map<Object, NeutralRecord>) PrivateAccessor.invoke(transformer, "getSessions", new Class[]{String.class, Set.class}, new Object[]{stateOrganizationId, new HashSet<String>()});

        Assert.assertEquals(res.get("2012mf-ed8c0a46-fc4b-11e1-97f4-ec9a74fc9dff").getAttributes().get("body.schoolId"), "schoolId2");
    }

    @Test
    public void testMapAttendanceIntoSchoolYears() throws IOException {
        String studentUniqueStateId = "studentId1";
        String stateOrganizationId = "schoolId1";
        //prepare for Sli SchoolYearAttendances
        Entity studentEntity = buildStudentEntity(studentUniqueStateId);
        NeutralQuery studentQuery = new NeutralQuery(0);
        studentQuery.addCriteria(new NeutralCriteria("studentUniqueStateId", NeutralCriteria.OPERATOR_EQUAL, studentUniqueStateId));
        when(entityRepository.findOne(Mockito.eq("student"), Mockito.argThat(new IsCorrectNeutralQuery(studentQuery))))
            .thenReturn(studentEntity);
        String studentEntityId = studentEntity.getEntityId();

        Entity schoolEntity = buildSchoolEntity(stateOrganizationId);
        NeutralQuery schoolQuery = new NeutralQuery(0);
        schoolQuery.addCriteria(new NeutralCriteria("stateOrganizationId", NeutralCriteria.OPERATOR_EQUAL, stateOrganizationId));
        when(entityRepository.findOne(Mockito.eq("educationOrganization"), Mockito.argThat(new IsCorrectNeutralQuery(schoolQuery))))
            .thenReturn(schoolEntity);
        String schoolEntityId = schoolEntity.getEntityId();

        Entity attendanceEntity = buildAttendanceEntity(studentEntityId, schoolEntityId);
        NeutralQuery attendanceQuery = new NeutralQuery(0);
        attendanceQuery.addCriteria(new NeutralCriteria("studentId", NeutralCriteria.OPERATOR_EQUAL, studentEntityId));
        attendanceQuery.addCriteria(new NeutralCriteria("schoolId", NeutralCriteria.OPERATOR_EQUAL, schoolEntityId));
        when(entityRepository.findOne(Mockito.eq("attendance"), Mockito.argThat(new IsCorrectNeutralQuery(attendanceQuery))))
            .thenReturn(attendanceEntity);

        //prepare for satging SchoolYearAttendances
        List<Map<String, Object>> attendance = buildAttendanceEvents();
        Map<Object, NeutralRecord> sessions = buildSchoolSessions(schoolEntityId);

        Map<String, List<Map<String, Object>>> schoolYears = transformer.mapAttendanceIntoSchoolYears(attendance, sessions, studentUniqueStateId, stateOrganizationId);

        Assert.assertNotNull(schoolYears);
        Assert.assertNotNull(schoolYears.entrySet());
        Assert.assertEquals(3, schoolYears.entrySet().size());
        for (Map.Entry<String, List<Map<String, Object>>> attendanceEntry : schoolYears.entrySet()) {
            String schoolYear = attendanceEntry.getKey();
            List<Map<String, Object>> events = attendanceEntry.getValue();
            if (schoolYear.equals("2006-2007")) {
                Assert.assertEquals(1, events.size());
            }
            if (schoolYear.equals("2007-2008")) {
                Assert.assertEquals(4, events.size());
            }
            if (schoolYear.equals("2008-2009")) {
                Assert.assertEquals(1, events.size());
            }
        }
    }


    @SuppressWarnings("unchecked")
    @Test
    public void testPerformTransformation() throws IOException {
        //generate attendance
        when(repository.findAllByQuery(Mockito.eq("attendance"), Mockito.any(Query.class)))
            .thenReturn(buildAttendanceRecords());

        //getParentEdOrg for schoolId1
        Query q1 = new Query().limit(0);
        q1.addCriteria(Criteria.where("batchJobId").is(batchJobId));
        q1.addCriteria(Criteria.where("body.StateOrganizationId").is("schoolId1"));
        when(repository.findAllByQuery(Mockito.eq("school"), Mockito.argThat(new IsCorrectQuery(q1))))
            .thenReturn(new ArrayList<NeutralRecord>());

        //getParentEdOrg for schoolId2
        Query q2 = new Query().limit(0);
        q2.addCriteria(Criteria.where("batchJobId").is(batchJobId));
        q2.addCriteria(Criteria.where("body.StateOrganizationId").is("schoolId2"));
        when(repository.findAllByQuery(Mockito.eq("school"), Mockito.argThat(new IsCorrectQuery(q2))))
            .thenReturn(new ArrayList<NeutralRecord>());

        //query for studentSchoolAssociation
        Query q = new Query().limit(0);
        q.addCriteria(Criteria.where("batchJobId").is(batchJobId));
        q.addCriteria(Criteria.where("body.StudentReference.StudentIdentity.StudentUniqueStateId" ).is("studentId2"));
        when(repository.findAllByQuery(Mockito.eq("studentSchoolAssociation"), Mockito.argThat(new IsCorrectQuery(q))))
            .thenReturn(buildStudentSchoolAssocRecords());

        //query for school from studentSchoolAssociation
        List<NeutralRecord> schools = buildStudentSchoolAssocRecords();
        List<String> schoolIds = new ArrayList<String>(schools.size());
        for (NeutralRecord r : schools) {
            schoolIds.add((String) r.getAttributes().get("schoolId"));
        }
        Query schoolQuery = new Query().limit(0);
        schoolQuery.addCriteria(Criteria.where("batchJobId").is(batchJobId));
        schoolQuery.addCriteria(Criteria.where("body.StateOrganizationId").in(schoolIds));
        when(repository.findAllByQuery(Mockito.eq("school"), Mockito.argThat(new IsCorrectQuery(schoolQuery))))
            .thenReturn(buildSchoolRecords());

        //session query for schoolId1
        Query sessionQuery1 = new Query().limit(0);
        sessionQuery1.addCriteria(Criteria.where("batchJobId").is(batchJobId));
        sessionQuery1.addCriteria(Criteria.where("body." + SESSION_SCHOOL_ID).is("schoolId1"));
        when(repository.findAllByQuery(Mockito.eq("session"), Mockito.argThat(new IsCorrectQuery(sessionQuery1))))
            .thenReturn(buildSessionRecords());

        //session query for schoolId2
        Query sessionQuery2 = new Query().limit(0);
        sessionQuery2.addCriteria(Criteria.where("batchJobId").is(batchJobId));
        sessionQuery2.addCriteria(Criteria.where("body." + SESSION_SCHOOL_ID).is("schoolId2"));
        when(repository.findAllByQuery(Mockito.eq("session"), Mockito.argThat(new IsCorrectQuery(sessionQuery2))))
            .thenReturn(buildSessionRecords());

        transformer.performTransformation();

        //verify attendance for studentId1
        NeutralQuery query1 = new NeutralQuery(1);
        query1.addCriteria(new NeutralCriteria("batchJobId", NeutralCriteria.OPERATOR_EQUAL, batchJobId, false));
        query1.addCriteria(new NeutralCriteria("studentId", NeutralCriteria.OPERATOR_EQUAL, "studentId1"));
        query1.addCriteria(new NeutralCriteria("schoolId.EducationalOrgIdentity.StateOrganizationId", NeutralCriteria.OPERATOR_EQUAL, "schoolId1"));
        query1.addCriteria(new NeutralCriteria("schoolYearAttendance.schoolYear",
                NeutralCriteria.OPERATOR_EQUAL, "2012"));

        NeutralQuery queryRh1 = new NeutralQuery(1);
        queryRh1.addCriteria(new NeutralCriteria("batchJobId", NeutralCriteria.OPERATOR_EQUAL, batchJobId, false));
        queryRh1.addCriteria(new NeutralCriteria("studentId", NeutralCriteria.OPERATOR_EQUAL, "studentId1"));
        queryRh1.addCriteria(new NeutralCriteria("schoolId.EducationalOrgIdentity.StateOrganizationId", NeutralCriteria.OPERATOR_EQUAL, "schoolId1"));

        Mockito.verify(repository, Mockito.times(1))
            .updateFirstForJob(
                Mockito.argThat(new IsCorrectNeutralQuery(query1)),
                Mockito.anyMap(),
                Mockito.eq("attendance_transformed")
             );

        Mockito.verify(repository, Mockito.times(1))
        .updateFirstForJob(
            Mockito.argThat(new IsCorrectNeutralQuery(queryRh1)),
            Mockito.anyMap(),
            Mockito.eq("attendance_transformed")
         );

                        //verify attendance for studentId2
        NeutralQuery query2 = new NeutralQuery(1);
        query2.addCriteria(new NeutralCriteria("batchJobId", NeutralCriteria.OPERATOR_EQUAL, batchJobId, false));
        query2.addCriteria(new NeutralCriteria("studentId", NeutralCriteria.OPERATOR_EQUAL, "studentId2"));
        query2.addCriteria(new NeutralCriteria("schoolId.EducationalOrgIdentity.StateOrganizationId", NeutralCriteria.OPERATOR_EQUAL, "schoolId2"));
        query2.addCriteria(new NeutralCriteria("schoolYearAttendance.schoolYear",
                NeutralCriteria.OPERATOR_EQUAL, "2012"));

        NeutralQuery queryRh2 = new NeutralQuery(1);
        queryRh2.addCriteria(new NeutralCriteria("batchJobId", NeutralCriteria.OPERATOR_EQUAL, batchJobId, false));
        queryRh2.addCriteria(new NeutralCriteria("studentId", NeutralCriteria.OPERATOR_EQUAL, "studentId1"));
        queryRh2.addCriteria(new NeutralCriteria("schoolId.EducationalOrgIdentity.StateOrganizationId", NeutralCriteria.OPERATOR_EQUAL, "schoolId1"));

        Mockito.verify(repository, Mockito.times(1))
            .updateFirstForJob(
                Mockito.argThat(new IsCorrectNeutralQuery(query2)),
                Mockito.anyMap(),
                Mockito.eq("attendance_transformed")
             );

        Mockito.verify(repository, Mockito.times(1))
        .updateFirstForJob(
            Mockito.argThat(new IsCorrectNeutralQuery(queryRh2)),
            Mockito.anyMap(),
            Mockito.eq("attendance_transformed")
         );
      }


    private List<Entity> buildSessionEntity() {
        Map<String, Object> b = new HashMap<String, Object>();
        SimpleEntity r = new SimpleEntity();
        r.setType("session");
        r.setEntityId("2012mf-ed8c0a46-fc4b-11e1-97f4-ec9a74fc9dff");
        r.setBody(b);
        b.put("body." + "schoolId", "schoolId2");
        Entity e = r;
        return Arrays.asList(e);
    }

    private List<NeutralRecord> buildSchoolRecords() {
        NeutralRecord r = new NeutralRecord();
        r.setRecordType("school");
        r.setRecordId("recordId");
        r.setAttributeField("stateOrganizationId", "schoolId2");
        return Arrays.asList(r);
    }

    private List<Entity> buildSchoolEntities() {
        Map<String, Object> b = new HashMap<String, Object>();
        SimpleEntity r = new SimpleEntity();
        r.setType("school");
        r.setEntityId("2012mf-ed8c0a46-fc4b-11e1-97f4-ec9a74fc9dff");
        r.setBody(b);
        b.put("stateOrganizationId", "schoolId2");
        b.put("nameOfInstitution", "schoolId2");
        Entity e = r;
        return Arrays.asList(e);
    }

    private Entity buildSchoolEntity(String schoolId) {
        Map<String, Object> b = new HashMap<String, Object>();
        SimpleEntity r = new SimpleEntity();
        r.setType("school");
        r.setEntityId("2012mf-ed8c0a46-fc4b-11e1-97f4-ec9a74fc9dff");
        r.setBody(b);
        b.put("stateOrganizationId", schoolId);
        b.put("nameOfInstitution", schoolId);
        Entity e = r;
        return e;
    }

    private Entity buildAttendanceEntity(String studentEntityId, String schoolEntityId) {
        List<Map<String, Object>> schoolYearAttendance = new ArrayList<Map<String, Object>>();
        Map<String, Object> b = new HashMap<String, Object>();
        SimpleEntity r = new SimpleEntity();
        r.setType("attendance");
        r.setEntityId("2012mf-ed8c0a46-fc4b-11e1-97f4-ec9a74fc8dff");
        r.setBody(b);
        b.put("StudentReference", buildStudentReference(studentEntityId));
        b.put("schoolId", schoolEntityId);
        b.put("schoolYearAttendance", schoolYearAttendance);
        Map<String, Object> year1 = new HashMap<String, Object>();
        Map<String, Object> year2 = new HashMap<String, Object>();
        List<Map<String, Object>> attend1 = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> attend2 = new ArrayList<Map<String, Object>>();
        schoolYearAttendance.add(year1);
        schoolYearAttendance.add(year2);
        year1.put("schoolYear", "2006-2007");
        year1.put("attendanceEvent", attend1);
        year2.put("schoolYear", "2007-2008");
        year2.put("attendanceEvent", attend2);
        Map<String, Object> attendanceEvent11 = new HashMap<String, Object>();
        Map<String, Object> attendanceEvent21 = new HashMap<String, Object>();
        Map<String, Object> attendanceEvent22 = new HashMap<String, Object>();
        Map<String, Object> attendanceEvent23 = new HashMap<String, Object>();
        attend1.add(attendanceEvent11);
        attend2.add(attendanceEvent21);
        attend2.add(attendanceEvent22);
        attend2.add(attendanceEvent23);
        attendanceEvent11.put("event", "In Attendance");
        attendanceEvent11.put("date", "2006-12-01");
        attendanceEvent21.put("event", "In Attendance");
        attendanceEvent21.put("date", "2007-12-01");
        attendanceEvent22.put("event", "In Attendance");
        attendanceEvent22.put("date", "2007-12-02");
        attendanceEvent22.put("reason", "absent");
        attendanceEvent23.put("event", "In Attendance");
        attendanceEvent23.put("date", "2007-12-03");
        Entity e = r;
        return e;
    }

    private List<Map<String, Object>> buildAttendanceEvents() {
        List<Map<String, Object>> attendEvents = new ArrayList<Map<String, Object>>();
        Map<String, Object> attendanceEvent11 = new HashMap<String, Object>();
        Map<String, Object> attendanceEvent21 = new HashMap<String, Object>();
        Map<String, Object> attendanceEvent22 = new HashMap<String, Object>();
        Map<String, Object> attendanceEvent23 = new HashMap<String, Object>();
        attendanceEvent11.put("event", "In Attendance");
        attendanceEvent11.put("date", "2008-12-01");
        attendanceEvent21.put("event", "In Attendance");
        attendanceEvent21.put("date", "2007-12-04");
        attendanceEvent22.put("event", "In Attendance");
        attendanceEvent22.put("date", "2007-12-02");
        attendanceEvent22.put("reason", "Sick");
        attendanceEvent23.put("event", "In Attendance");
        attendanceEvent23.put("date", "2007-12-03");
        attendEvents.add(attendanceEvent11);
        attendEvents.add(attendanceEvent21);
        attendEvents.add(attendanceEvent22);
        attendEvents.add(attendanceEvent23);
        return attendEvents;
    }

    private Map<Object, NeutralRecord> buildSchoolSessions(String schoolEntityId) {
        Map<Object, NeutralRecord> sessions = new HashMap<Object, NeutralRecord>();
        NeutralRecord s1 = new NeutralRecord();
        s1.setRecordType("session");
        s1.setRecordId("recordId1");
        s1.setAttributeField("schoolId", schoolEntityId);
        s1.setAttributeField("SchoolYear", "2006-2007");
        s1.setAttributeField("BeginDate", "2006-08-06");
        s1.setAttributeField("EndDate", "2007-06-30");
        NeutralRecord s2 = new NeutralRecord();
        s2.setRecordType("session");
        s2.setRecordId("recordId2");
        s2.setAttributeField("SchoolYear", "2007-2008");
        s2.setAttributeField("BeginDate", "2007-08-06");
        s2.setAttributeField("EndDate", "2008-06-30");
        NeutralRecord s3 = new NeutralRecord();
        s3.setRecordType("session");
        s3.setRecordId("recordId2");
        s3.setAttributeField("SchoolYear", "2008-2009");
        s3.setAttributeField("BeginDate", "2008-08-06");
        s3.setAttributeField("EndDate", "2009-06-30");
        sessions.put("year1", s1);
        sessions.put("year2", s2);
        sessions.put("year3", s3);
        return sessions;
    }

    private Entity buildStudentEntity(String studentId) {
        Map<String, Object> b = new HashMap<String, Object>();
        SimpleEntity r2 = new SimpleEntity();
        r2.setType("student");
        r2.setEntityId("2012th-26af1dd8-fc4c-11e1-97f4-ec9a74fc9dff");
        r2.setBody(b);
        b.put("studentUniqueStateId", studentId);
        return r2;
    }

    private List<Entity> buildStudentSchoolAssocEntities() {
        Map<String, Object> b = new HashMap<String, Object>();
        SimpleEntity r2 = new SimpleEntity();
        r2.setType("studentSchoolAssociation");
        r2.setEntityId("2012cu-e31d23f0-fc4b-11e1-97f4-ec9a74fc9dff");
        r2.setBody(b);
        b.put("studentId", "2012th-26af1dd8-fc4c-11e1-97f4-ec9a74fc9dff");
        b.put("schoolId", "2012mf-ed8c0a46-fc4b-11e1-97f4-ec9a74fc9dff");
        Entity e = r2;
        return Arrays.asList(e);
    }

    private Object buildStudentReference(String studentId) {
        Map<String, Object> studentReference = new HashMap<String, Object>();
        Map<String, Object> studentIdentity = new HashMap<String, Object>();
        studentIdentity.put("StudentUniqueStateId", studentId);
        studentReference.put("StudentIdentity", studentIdentity);
        return studentReference;
    }

    private List<NeutralRecord> buildStudentSchoolAssocRecords() {
        NeutralRecord r2 = new NeutralRecord();
        r2.setRecordType("studentSchoolAssociation");
        r2.setRecordId("recordId");
        r2.setAttributeField("StudentId", "studentId2");
        r2.setAttributeField("SchoolId", "schoolId2");
        return Arrays.asList(r2);
    }

    private List<NeutralRecord> buildSessionRecords() {
        NeutralRecord r = new NeutralRecord();
        r.setRecordType("session");
        r.setRecordId("recordId");
        r.setAttributeField("SchoolYear", "2012");
        r.setAttributeField("BeginDate", "2012-09-01");
        r.setAttributeField("EndDate", "2012-12-31");
        return Arrays.asList(r);
    }

    private Map<String, Map<String, String>> buildEdorgIdentity(String stateId){
        Map<String, String> stateEdorgId = new HashMap<String, String>();
        stateEdorgId.put("StateOrganizationId", stateId);
        Map<String, Map<String, String>> res = new HashMap<String, Map<String, String>>();
        res.put("EducationalOrgIdentity", stateEdorgId);

        return res;
    }
    private List<NeutralRecord> buildAttendanceRecords() {
        NeutralRecord r1 = new NeutralRecord();
        r1.setRecordType("attendance");
        r1.setRecordId("recordId1");
        r1.setAttributeField("StudentReference", buildStudentReference("studentId1"));
        r1.setAttributeField("schoolId", "schoolId1");
        r1.setAttributeField("EventDate", "2012-09-09");
        r1.setAttributeField("AttendanceEventCategory", "attendanceEventCategory1");
        r1.setAttributeField("AttendanceEventReason", "attendanceEventReason1");
        r1.setAttributeField("SchoolReference", buildEdorgIdentity("schoolId1"));
        HashMap<String, Object> relement1 = new HashMap<String, Object>();
        relement1.put("rhId", "rhId1");
        relement1.put("rhHash", "rhHash1");
        List<HashMap<String, Object>> rList1 = new ArrayList<HashMap<String, Object>>();
        rList1.add(relement1);
        r1.addMetaData("rhData", rList1);

        NeutralRecord r2 = new NeutralRecord();
        r2.setRecordType("attendance");
        r2.setRecordId("recordId2");
        r2.setAttributeField("StudentReference", buildStudentReference("studentId2"));
        r2.setAttributeField("EventDate", "2012-09-09");
        r2.setAttributeField("AttendanceEventCategory", "attendanceEventCategory2");
        r2.setAttributeField("AttendanceEventReason", "attendanceEventReason2");
        r2.setAttributeField("SchoolReference", buildEdorgIdentity("schoolId2"));
        HashMap<String, Object> relement2 = new HashMap<String, Object>();
        relement2.put("rhId", "rhId1");
        relement2.put("rhHash", "rhHash1");
        List<HashMap<String, Object>> rList2 = new ArrayList<HashMap<String, Object>>();
        rList2.add(relement2);
        r2.addMetaData("rhData", rList2);

        return Arrays.asList(r1, r2);
    }

}
