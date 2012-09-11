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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.dal.NeutralRecordRepository;
import org.slc.sli.ingestion.handler.EntityPersistHandler;

/**
 * Unit Test for AttendanceTransformer
 *
 * @author slee
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })

public class AttendanceTransformerTest
{
    @Autowired
    private AttendanceTransformer transformer;

    @Autowired
    private EntityPersistHandler entityPersistHandler;

    @Mock
    private MongoEntityRepository entityRepository = Mockito.mock(MongoEntityRepository.class);

    @Mock
    private NeutralRecordMongoAccess neutralRecordMongoAccess = Mockito.mock(NeutralRecordMongoAccess.class);

    @Mock
    private NeutralRecordRepository repository = Mockito.mock(NeutralRecordRepository.class);

    private String batchJobId = "10001";
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
        transformer.setWorkNote(WorkNote.createSimpleWorkNote(batchJobId));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAttendanceWithStagingSchoolInfo() throws IOException {
        when(repository.findAllByQuery(Mockito.eq("attendance"), Mockito.any(Query.class)))
            .thenReturn(buildAttendanceRecords());

        //getParentEdOrg for schoolId1
        Query q1 = new Query().limit(0);
        q1.addCriteria(Criteria.where("batchJobId").is(batchJobId));
        q1.addCriteria(Criteria.where("body.stateOrganizationId").is("schoolId1"));
        when(repository.findAllByQuery(Mockito.eq("school"), Mockito.argThat(new IsCorrectQuery(q1))))
            .thenReturn(new ArrayList<NeutralRecord>());

        //getParentEdOrg for schoolId2
        Query q2 = new Query().limit(0);
        q2.addCriteria(Criteria.where("batchJobId").is(batchJobId));
        q2.addCriteria(Criteria.where("body.stateOrganizationId").is("schoolId2"));
        when(repository.findAllByQuery(Mockito.eq("school"), Mockito.argThat(new IsCorrectQuery(q2))))
            .thenReturn(new ArrayList<NeutralRecord>());

        //query for studentSchoolAssociation
        Query q = new Query().limit(0);
        q.addCriteria(Criteria.where("batchJobId").is(batchJobId));
        q.addCriteria(Criteria.where("body.studentId").is("studentId2"));
        when(repository.findAllByQuery(Mockito.eq("studentSchoolAssociation"), Mockito.argThat(new IsCorrectQuery(q))))
            .thenReturn(buildStudentSchoolAssocRecords());

        //query for school from studentSchoolAssociation
        List<NeutralRecord> schools = buildStudentSchoolAssocRecords();
        List<String> schoolIds = new ArrayList<String>(schools.size());
        for (NeutralRecord r : schools) {
            schoolIds.add((String)r.getAttributes().get("schoolId"));
        }
        Query schoolQuery = new Query().limit(0);
        schoolQuery.addCriteria(Criteria.where("batchJobId").is(batchJobId));
        schoolQuery.addCriteria(Criteria.where("body.stateOrganizationId").in(schoolIds));
        when(repository.findAllByQuery(Mockito.eq("school"), Mockito.argThat(new IsCorrectQuery(schoolQuery))))
            .thenReturn(buildSchoolRecords());

        //session query for schoolId1
        Query sessionQuery1 = new Query().limit(0);
        sessionQuery1.addCriteria(Criteria.where("batchJobId").is(batchJobId));
        sessionQuery1.addCriteria(Criteria.where("body.schoolId").is("schoolId1"));
        when(repository.findAllByQuery(Mockito.eq("session"), Mockito.argThat(new IsCorrectQuery(sessionQuery1))))
            .thenReturn(buildSessionRecords());

        //session query for schoolId2
        Query sessionQuery2 = new Query().limit(0);
        sessionQuery2.addCriteria(Criteria.where("batchJobId").is(batchJobId));
        sessionQuery2.addCriteria(Criteria.where("body.schoolId").is("schoolId2"));
        when(repository.findAllByQuery(Mockito.eq("session"), Mockito.argThat(new IsCorrectQuery(sessionQuery2))))
            .thenReturn(buildSessionRecords());

        transformer.performTransformation();

        //verify attendance for studentId1
        NeutralQuery query1 = new NeutralQuery(1);
        query1.addCriteria(new NeutralCriteria("batchJobId", NeutralCriteria.OPERATOR_EQUAL, batchJobId, false));
        query1.addCriteria(new NeutralCriteria("studentId", NeutralCriteria.OPERATOR_EQUAL, "studentId1"));
        query1.addCriteria(new NeutralCriteria("schoolId", NeutralCriteria.OPERATOR_EQUAL, "schoolId1"));
        query1.addCriteria(new NeutralCriteria("schoolYearAttendance.schoolYear",
                NeutralCriteria.OPERATOR_EQUAL, "2012"));
        Mockito.verify(repository, Mockito.times(1))
            .updateFirstForJob(
                Mockito.argThat(new IsCorrectNeutralQuery(query1)),
                Mockito.anyMap(),
                Mockito.eq("attendance_transformed"),
                Mockito.eq(batchJobId)
             );

        //verify attendance for studentId2
        NeutralQuery query2 = new NeutralQuery(1);
        query2.addCriteria(new NeutralCriteria("batchJobId", NeutralCriteria.OPERATOR_EQUAL, batchJobId, false));
        query2.addCriteria(new NeutralCriteria("studentId", NeutralCriteria.OPERATOR_EQUAL, "studentId2"));
        query2.addCriteria(new NeutralCriteria("schoolId", NeutralCriteria.OPERATOR_EQUAL, "schoolId2"));
        query2.addCriteria(new NeutralCriteria("schoolYearAttendance.schoolYear",
                NeutralCriteria.OPERATOR_EQUAL, "2012"));
        Mockito.verify(repository, Mockito.times(1))
            .updateFirstForJob(
                Mockito.argThat(new IsCorrectNeutralQuery(query2)),
                Mockito.anyMap(),
                Mockito.eq("attendance_transformed"),
                Mockito.eq(batchJobId)
             );

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAttendanceWithSliSchoolInfo() throws IOException {
        when(repository.findAllByQuery(Mockito.eq("attendance"), Mockito.any(Query.class)))
            .thenReturn(buildAttendanceRecords());

        //getParentEdOrg for schoolId1
        Query q1 = new Query().limit(0);
        q1.addCriteria(Criteria.where("batchJobId").is(batchJobId));
        q1.addCriteria(Criteria.where("body.stateOrganizationId").is("schoolId1"));
        when(repository.findAllByQuery(Mockito.eq("school"), Mockito.argThat(new IsCorrectQuery(q1))))
            .thenReturn(new ArrayList<NeutralRecord>());

        //getParentEdOrg for schoolId2
        Query q2 = new Query().limit(0);
        q2.addCriteria(Criteria.where("batchJobId").is(batchJobId));
        q2.addCriteria(Criteria.where("body.stateOrganizationId").is("schoolId2"));
        when(repository.findAllByQuery(Mockito.eq("school"), Mockito.argThat(new IsCorrectQuery(q2))))
            .thenReturn(new ArrayList<NeutralRecord>());

        //query staging DB for studentSchoolAssociation
        Query q = new Query().limit(0);
        q.addCriteria(Criteria.where("batchJobId").is(batchJobId));
        q.addCriteria(Criteria.where("body.studentId").is("studentId2"));
        when(repository.findAllByQuery(Mockito.eq("studentSchoolAssociation"), Mockito.argThat(new IsCorrectQuery(q))))
            .thenReturn(new ArrayList<NeutralRecord>());

        //query SLI DB for studentSchoolAssociation
        NeutralQuery query = new NeutralQuery(0);
        query.addCriteria(new NeutralCriteria("studentId", NeutralCriteria.OPERATOR_EQUAL, "studentId2"));
        when(entityRepository.findAll(Mockito.eq(EntityNames.STUDENT_SCHOOL_ASSOCIATION), Mockito.argThat(new IsCorrectNeutralQuery(query))))
            .thenReturn(buildStudentSchoolAssocEntities());

        //query SLI DB for school from studentSchoolAssociation
        List<Entity> schools = buildStudentSchoolAssocEntities();
        List<String> schoolIds = new ArrayList<String>(schools.size());
        for (Entity r : schools) {
            schoolIds.add((String)r.getBody().get("schoolId"));
        }
        NeutralQuery schoolQuery = new NeutralQuery(0);
        schoolQuery.addCriteria(new NeutralCriteria("stateOrganizationId", NeutralCriteria.CRITERIA_IN, schoolIds));
        when(entityRepository.findAll(Mockito.eq(EntityNames.EDUCATION_ORGANIZATION), Mockito.argThat(new IsCorrectNeutralQuery(schoolQuery))))
            .thenReturn(buildSchoolEntities());

        //session query for schoolId1
        Query sessionQuery1 = new Query().limit(0);
        sessionQuery1.addCriteria(Criteria.where("batchJobId").is(batchJobId));
        sessionQuery1.addCriteria(Criteria.where("body.schoolId").is("schoolId1"));
        when(repository.findAllByQuery(Mockito.eq("session"), Mockito.argThat(new IsCorrectQuery(sessionQuery1))))
            .thenReturn(buildSessionRecords());

        //session query for schoolId2
        Query sessionQuery2 = new Query().limit(0);
        sessionQuery2.addCriteria(Criteria.where("batchJobId").is(batchJobId));
        sessionQuery2.addCriteria(Criteria.where("body.schoolId").is("schoolId2"));
        when(repository.findAllByQuery(Mockito.eq("session"), Mockito.argThat(new IsCorrectQuery(sessionQuery2))))
            .thenReturn(buildSessionRecords());

        transformer.performTransformation();

        //verify attendance for studentId1
        NeutralQuery query1 = new NeutralQuery(1);
        query1.addCriteria(new NeutralCriteria("batchJobId", NeutralCriteria.OPERATOR_EQUAL, batchJobId, false));
        query1.addCriteria(new NeutralCriteria("studentId", NeutralCriteria.OPERATOR_EQUAL, "studentId1"));
        query1.addCriteria(new NeutralCriteria("schoolId", NeutralCriteria.OPERATOR_EQUAL, "schoolId1"));
        query1.addCriteria(new NeutralCriteria("schoolYearAttendance.schoolYear",
                NeutralCriteria.OPERATOR_EQUAL, "2012"));
        Mockito.verify(repository, Mockito.times(1))
            .updateFirstForJob(
                Mockito.argThat(new IsCorrectNeutralQuery(query1)),
                Mockito.anyMap(),
                Mockito.eq("attendance_transformed"),
                Mockito.eq(batchJobId)
             );

        //verify attendance for studentId2
        NeutralQuery query2 = new NeutralQuery(1);
        query2.addCriteria(new NeutralCriteria("batchJobId", NeutralCriteria.OPERATOR_EQUAL, batchJobId, false));
        query2.addCriteria(new NeutralCriteria("studentId", NeutralCriteria.OPERATOR_EQUAL, "studentId2"));
        query2.addCriteria(new NeutralCriteria("schoolId", NeutralCriteria.OPERATOR_EQUAL, "schoolId2"));
        query2.addCriteria(new NeutralCriteria("schoolYearAttendance.schoolYear",
                NeutralCriteria.OPERATOR_EQUAL, "2012"));
        Mockito.verify(repository, Mockito.times(1))
            .updateFirstForJob(
                Mockito.argThat(new IsCorrectNeutralQuery(query2)),
                Mockito.anyMap(),
                Mockito.eq("attendance_transformed"),
                Mockito.eq(batchJobId)
             );

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
        r.setEntityId("entityId");
        r.setBody(b);
        b.put("stateOrganizationId", "schoolId2");
        Entity e = r;
        return Arrays.asList(e);
    }

    private List<Entity> buildStudentSchoolAssocEntities() {
        Map<String, Object> b = new HashMap<String, Object>();
        SimpleEntity r2 = new SimpleEntity();
        r2.setType("studentSchoolAssociation");
        r2.setEntityId("entityId");
        r2.setBody(b);
        b.put("studentId", "studentId2");
        b.put("schoolId", "schoolId2");
        Entity e = r2;
        return Arrays.asList(e);
    }

    private List<NeutralRecord> buildStudentSchoolAssocRecords() {
        NeutralRecord r2 = new NeutralRecord();
        r2.setRecordType("studentSchoolAssociation");
        r2.setRecordId("recordId");
        r2.setAttributeField("studentId", "studentId2");
        r2.setAttributeField("schoolId", "schoolId2");
        return Arrays.asList(r2);
    }

    private List<NeutralRecord> buildSessionRecords() {
        NeutralRecord r = new NeutralRecord();
        r.setRecordType("session");
        r.setRecordId("recordId");
        r.setAttributeField("schoolYear", "2012");
        r.setAttributeField("beginDate", "2012-09-01");
        r.setAttributeField("endDate", "2012-12-31");
        return Arrays.asList(r);
    }

    private List<NeutralRecord> buildAttendanceRecords() {
        NeutralRecord r1 = new NeutralRecord();
        r1.setRecordType("attendance");
        r1.setRecordId("recordId1");
        r1.setAttributeField("studentId", "studentId1");
        r1.setAttributeField("schoolId", "schoolId1");
        r1.setAttributeField("eventDate", "2012-09-09");
        r1.setAttributeField("attendanceEventCategory", "attendanceEventCategory1");
        r1.setAttributeField("attendanceEventReason", "attendanceEventReason1");
        NeutralRecord r2 = new NeutralRecord();
        r2.setRecordType("attendance");
        r2.setRecordId("recordId2");
        r2.setAttributeField("studentId", "studentId2");
        r2.setAttributeField("eventDate", "2012-09-09");
        r2.setAttributeField("attendanceEventCategory", "attendanceEventCategory2");
        r2.setAttributeField("attendanceEventReason", "attendanceEventReason2");
        return Arrays.asList(r1,r2);
    }

}
