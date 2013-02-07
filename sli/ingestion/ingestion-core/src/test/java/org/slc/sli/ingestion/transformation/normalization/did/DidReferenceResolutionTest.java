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

package org.slc.sli.ingestion.transformation.normalization.did;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.impl.DummyMessageReport;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;

/**
 *
 * Integrated JUnit tests to check to check the schema parser and
 * DeterministicIdResolver in combination
 *
 * @author jtully
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/entity-mapping.xml" })
public class DidReferenceResolutionTest {

    @Autowired
    DeterministicIdResolver didResolver;

    private static final String TENANT_ID = "tenant_id";

    @Test
    public void resolvesAssessmentRefDidInAssessmentItemCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/assessmentItem.json");
        AbstractMessageReport errorReport = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport, reportStats);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("assessmentTitle", "Fraction Homework 123");
        naturalKeys.put("gradeLevelAssessed", "Fifth grade");
        naturalKeys.put("version", "1");
        naturalKeys.put("academicSubject", ""); // apparently, empty optional natural key field is
                                                // default to empty string

        checkId(entity, "assessmentId", naturalKeys, "assessment");
    }

    @Test
    public void resolvesAssessmentRefDidInStudentAssessmentCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentAssessment.json");
        AbstractMessageReport errorReport = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport, reportStats);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("AssessmentTitle", "Fraction Homework 123");
        naturalKeys.put("GradeLevelAssessed", "Fifth grade");
        naturalKeys.put("Version", "1");
        naturalKeys.put("AcademicSubject", ""); // apparently, empty optional natural key field is
                                                // default to empty string

        checkId(entity, "assessmentId", naturalKeys, "assessment");
    }

    @Test
    public void resolvesEdOrgRefDidInAttendanceEventCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/attendanceEvent.json");
        AbstractMessageReport errorReport = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport, reportStats);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        checkId(entity, "schoolId", naturalKeys, "educationOrganization");

    }

    @Test
    public void resolvesEdOrgRefDidInCohortCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/cohort.json");
        AbstractMessageReport errorReport = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport, reportStats);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        checkId(entity, "EducationOrgReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesEdOrgRefDidInCourseCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/course.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        checkId(entity, "EducationOrganizationReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesSchoolReferenceDidsInCourseOfferingCorrectly() throws JsonParseException, JsonMappingException,
            IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/courseOffering.json");
        resolveInternalId(entity);

        Map<String, String> edOrgNaturalKeys = new HashMap<String, String>();
        edOrgNaturalKeys.put("stateOrganizationId", "testSchoolId");
        checkId(entity, "SchoolReference", edOrgNaturalKeys, "educationOrganization");

    }

    @Test
    public void resolvesGraduationPlanDidInStudentSchoolAssociationCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentSchoolAssociation.json");
        resolveInternalId(entity);
        Map<String, String> edOrgNaturalKeys = new HashMap<String, String>();
        edOrgNaturalKeys.put("educationOrganizationId", "someEdOrg");
        String edOrgId = generateExpectedDid(edOrgNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("educationOrganizationId", edOrgId);
        naturalKeys.put("graduationPlanType", "this graduation plan type");

        checkId(entity, "GraduationPlanReference", naturalKeys, "graduationPlan");
    }

    @Test
    public void resolvesSessionRefDidsInCourseOfferingCorrectly() throws JsonParseException, JsonMappingException,
            IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/courseOffering.json");
        resolveInternalId(entity);

        Map<String, String> edOrgNaturalKeys = new HashMap<String, String>();
        edOrgNaturalKeys.put("stateOrganizationId", "testSchoolId");
        String edOrgId = generateExpectedDid(edOrgNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> sessionNaturalKeys = new HashMap<String, String>();
        sessionNaturalKeys.put("schoolId", edOrgId);
        sessionNaturalKeys.put("sessionId", "theSessionName");

        checkId(entity, "SessionReference", sessionNaturalKeys, "session");
    }

    @Test
    public void resolvesResponsibilitySchoolReferenceEdOrgRefDidInDisciplineActionCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/disciplineAction.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "someResponsibilitySchoolReference");
        checkId(entity, "ResponsibilitySchoolReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesAssignmentSchoolReferenceEdOrgRefDidInDisciplineActionCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/disciplineAction.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "someAssignmentSchoolReference");
        checkId(entity, "AssignmentSchoolReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesDisciplineIncidentReferenceDidInDisciplineActionCorrectly() throws JsonParseException,
            JsonMappingException, IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/disciplineAction.json");
        resolveInternalId(entity);
        Map<String, String> edOrgNaturalKeys = new HashMap<String, String>();
        edOrgNaturalKeys.put("stateOrganizationId", "testSchoolId");
        String edOrgId = generateExpectedDid(edOrgNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> disciplineIncidentNaturalKeys = new HashMap<String, String>();
        disciplineIncidentNaturalKeys.put("schoolId", edOrgId);
        disciplineIncidentNaturalKeys.put("disciplineActionIdentifier", "theIncident");

        checkId(entity, "DisciplineIncidentReference", disciplineIncidentNaturalKeys, "disciplineIncident");
    }

    @Test
    public void resolvesDisciplineIncidentReferenceDidInStudentDisciplineIncidentAssocCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentDisciplineIncidentAssociation.json");
        resolveInternalId(entity);

        Map<String, String> edOrgNaturalKeys = new HashMap<String, String>();
        edOrgNaturalKeys.put("stateOrganizationId", "testSchoolId");
        String edOrgId = generateExpectedDid(edOrgNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> disciplineIncidentNaturalKeys = new HashMap<String, String>();
        disciplineIncidentNaturalKeys.put("schoolId", edOrgId);
        disciplineIncidentNaturalKeys.put("disciplineActionIdentifier", "theIncident");

        checkId(entity, "DisciplineIncidentReference", disciplineIncidentNaturalKeys, "disciplineIncident");
    }

    @Test
    public void resolvesGradeDidInReportCard() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/reportCard.json");
        resolveInternalId(entity);

        Map<String, String> edOrgNaturalKeys = new HashMap<String, String>();
        edOrgNaturalKeys.put("stateOrganizationId", "testEdOrgId");
        String edOrgDid = generateExpectedDid(edOrgNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> studentNaturalKeys = new HashMap<String, String>();
        studentNaturalKeys.put("studentUniqueStateId", "testStudentId");
        String studentDid = generateExpectedDid(studentNaturalKeys, TENANT_ID, "student", null);

        Map<String, String> gradingPeriodNaturalKeys = new HashMap<String, String>();
        gradingPeriodNaturalKeys.put("schoolId", edOrgDid);
        gradingPeriodNaturalKeys.put("gradingPeriod", "testGradingPeriod");
        gradingPeriodNaturalKeys.put("beginDate", "01-01-2012");
        String gradingPeriodDid = generateExpectedDid(gradingPeriodNaturalKeys, TENANT_ID, "gradingPeriod", null);

        Map<String, String> sectionNaturalKeys = new HashMap<String, String>();
        sectionNaturalKeys.put("schoolId", edOrgDid);
        sectionNaturalKeys.put("uniqueSectionCode", "testSectionCode");
        String sectionDid = generateExpectedDid(sectionNaturalKeys, TENANT_ID, "section", null);

        Map<String, String> studentSectionAssociationNaturalKeys = new HashMap<String, String>();
        studentSectionAssociationNaturalKeys.put("studentId", studentDid);
        studentSectionAssociationNaturalKeys.put("sectionId", sectionDid);
        studentSectionAssociationNaturalKeys.put("beginDate", "02-02-2012");
        String studentSectionAssociationDid = generateExpectedDid(studentSectionAssociationNaturalKeys, TENANT_ID,
                "studentSectionAssociation", sectionDid);

        Map<String, String> gradeNaturalKeys = new HashMap<String, String>();
        gradeNaturalKeys.put("studentSectionAssociationId", studentSectionAssociationDid);
        gradeNaturalKeys.put("gradingPeriodId", gradingPeriodDid);

        checkId(entity, "GradeReference", gradeNaturalKeys, "grade");
    }

    @Test
    public void resolvesEdOrgRefDidInTeacherSchoolAssociationCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/teacherSchoolAssociation.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        checkId(entity, "SchoolReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesEdOrgRefDidInStudentSchoolAssociationCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentSchoolAssociation.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        checkId(entity, "SchoolReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesEdOrgRefDidInStudentProgramAssociationCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentProgramAssociation.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        checkId(entity, "EducationOrganizationReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesEdOrgRefDidInStudentCompetencyObjectiveCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentCompetencyObjective.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        checkId(entity, "EducationOrganizationReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesEdOrgRefDidInSessionCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/session.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        checkId(entity, "EducationOrganizationReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesSchoolRefDidInSectionCorrectly() throws JsonParseException, JsonMappingException, IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/section.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        checkId(entity, "SchoolReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesSessionRefDidInSectionCorrectly() throws JsonParseException, JsonMappingException, IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/section.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        String edOrgId = generateExpectedDid(naturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> sessionNaturalKeys = new HashMap<String, String>();
        sessionNaturalKeys.put("schoolId", edOrgId);
        sessionNaturalKeys.put("sessionId", "theSessionName");

        checkId(entity, "SessionReference", sessionNaturalKeys, "session");
    }

    @Test
    public void resolvesSessionRefDidInStudentAcademicRecordCorrectly() throws JsonParseException,
            JsonMappingException, IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentAcademicRecord.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        String edOrgId = generateExpectedDid(naturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> sessionNaturalKeys = new HashMap<String, String>();
        sessionNaturalKeys.put("schoolId", edOrgId);
        sessionNaturalKeys.put("sessionId", "theSessionName");

        checkId(entity, "SessionReference", sessionNaturalKeys, "session");
    }

    @Test
    public void resolvesEdOrgRefDidInSchoolCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/school.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "someLEAOrganizationID");
        checkId(entity, "LocalEducationAgencyReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesEdOrgRefDidInDisciplineIncidentCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/disciplineIncident.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        checkId(entity, "SchoolReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesEdOrgRefDidInGradingPeriodCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/gradingPeriod.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testEdOrgId");
        checkId(entity, "EducationOrganizationReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesEdOrgRefDidInGraduationPlanCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/graduationPlan.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        checkId(entity, "EducationOrganizationReference", naturalKeys, "educationOrganization");
    }

    @Test
    @Ignore
    public void resolvesLEAEdOrgRefDidInLocalEducationAgencyCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/localEducationAgency.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "someLEAOrganizationID");
        checkId(entity, "LocalEducationAgencyReference", naturalKeys, "educationOrganization");
    }

    @Test
    @Ignore
    public void resolvesEducationServiceCenterEdOrgRefDidInLocalEducationAgencyCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/localEducationAgency.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "someEducationServiceCenterID");
        checkId(entity, "EducationServiceCenterReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesStateEdOrgRefDidInLocalEducationAgencyCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/localEducationAgency.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "someSEAOrganizationID");
        checkId(entity, "StateEducationAgencyReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesEdOrgRefDidInStaffEducationOrgAssignmentAssociationCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/staffEducationOrganizationAssociation.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "someEdOrg");
        checkId(entity, "EducationOrganizationReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesCalendarDateReferenceWithinGradingPeriodCorrectly() throws IOException {

        NeutralRecordEntity entity = loadEntity("didTestEntities/gradingPeriod.json");
        resolveInternalId(entity);

        Map<String, String> edorgNaturalKeys = new HashMap<String, String>();
        edorgNaturalKeys.put("stateOrganizationId", "Illinois");
        String edOrgDID = generateExpectedDid(edorgNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("date", "2012-01-01");
        naturalKeys.put("educationOrganizationId", edOrgDID);

        checkId(entity, "CalendarDateReference", naturalKeys, "calendarDate");
    }

    @Test
    public void resolvesCohortDidInStaffCohortAssociationCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/staffCohortAssociation.json");
        resolveInternalId(entity);
        Map<String, String> edorgNaturalKeys = new HashMap<String, String>();
        edorgNaturalKeys.put("educationOrgId", "STANDARD-SEA");
        String edOrgDID = generateExpectedDid(edorgNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("cohortIdentifier", "ACC-TEST-COH-1");
        naturalKeys.put("educationOrgId", edOrgDID);

        checkId(entity, "CohortReference", naturalKeys, "cohort");
    }

    @Test
    public void resolvesCohortDidInStudentCohortAssociationCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentCohortAssociation.json");

        resolveInternalId(entity);

        Map<String, String> edorgNaturalKeys = new HashMap<String, String>();
        edorgNaturalKeys.put("educationOrgId", "STANDARD-SEA");
        String edOrgDID = generateExpectedDid(edorgNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("cohortIdentifier", "ACC-TEST-COH-1");
        naturalKeys.put("educationOrgId", edOrgDID);

        checkId(entity, "CohortReference", naturalKeys, "cohort");
    }

    @Test
    public void resolvesCourseDidInCourseTranscriptCorrectly() throws IOException {

        NeutralRecordEntity courseTranscriptEntity = loadEntity("didTestEntities/courseTranscript.json");
        resolveInternalId(courseTranscriptEntity);
        Map<String, Object> courseTranscriptBody = courseTranscriptEntity.getBody();
        Object courseTranscriptResolvedRef = courseTranscriptBody.get("CourseReference");

        Map<String, String> schoolNaturalKeys = new HashMap<String, String>();
        schoolNaturalKeys.put("stateOrganizationId", "testSchoolId");
        String schoolId = generateExpectedDid(schoolNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("schoolId", schoolId);
        naturalKeys.put("uniqueCourseId", "testCourseId");

        String courseReferenceDID = generateExpectedDid(naturalKeys, TENANT_ID, "course", null);
        Assert.assertEquals(courseReferenceDID, courseTranscriptResolvedRef);
    }

    @Test
    public void resolvesCourseDidInCourseOfferingCorrectly() throws IOException {

        NeutralRecordEntity courseOfferingEntity = loadEntity("didTestEntities/courseOffering.json");
        resolveInternalId(courseOfferingEntity);
        Map<String, Object> courseOfferingBody = courseOfferingEntity.getBody();
        Object courseOfferingResolvedRef = courseOfferingBody.get("CourseReference");

        Map<String, String> schoolNaturalKeys = new HashMap<String, String>();
        schoolNaturalKeys.put("stateOrganizationId", "testSchoolId");
        String schoolId = generateExpectedDid(schoolNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("schoolId", schoolId);
        naturalKeys.put("uniqueCourseId", "testCourseId");

        String courseReferenceDID = generateExpectedDid(naturalKeys, TENANT_ID, "course", null);
        Assert.assertEquals(courseReferenceDID, courseOfferingResolvedRef);
    }

    @Test
    public void resolvesCourseOfferingDidInSectionCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/section.json");
        resolveInternalId(entity);

        Map<String, String> edorgNaturalKeys = new HashMap<String, String>();
        edorgNaturalKeys.put("stateOrganizationId", "state organization id 1");
        String sessionEdOrgDid = generateExpectedDid(edorgNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> sessionNaturalKeys = new HashMap<String, String>();
        sessionNaturalKeys.put("schoolId", sessionEdOrgDid);
        sessionNaturalKeys.put("sessionName", "session name");
        String sessionDid = generateExpectedDid(sessionNaturalKeys, TENANT_ID, "session", null);

        edorgNaturalKeys = new HashMap<String, String>();
        edorgNaturalKeys.put("stateOrganizationId", "state organization id 2");
        String edOrgDid = generateExpectedDid(edorgNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("localCourseCode", "local course code");
        naturalKeys.put("schoolId", edOrgDid);
        naturalKeys.put("sessionId", sessionDid);

        checkId(entity, "CourseOfferingReference", naturalKeys, "courseOffering");
    }

    @Test
    public void resolvesGradingPeriodDidInGradeCorrectly() throws IOException {

        NeutralRecordEntity entity = loadEntity("didTestEntities/grade.json");
        resolveInternalId(entity);

        Map<String, String> edorgNaturalKeys = new HashMap<String, String>();
        edorgNaturalKeys.put("stateOrganizationId", "this school");
        String edOrgDid = generateExpectedDid(edorgNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("schoolId", edOrgDid);
        naturalKeys.put("gradingPeriod", "First Six Weeks");
        naturalKeys.put("beginDate", "2011-09-01");

        checkId(entity, "GradingPeriodReference", naturalKeys, "gradingPeriod");
    }

    @Test
    public void resolvesGradingPeriodDidInGradebookEntryCorrectly() throws IOException {

        NeutralRecordEntity entity = loadEntity("didTestEntities/gradebookEntry.json");
        resolveInternalId(entity);

        Map<String, String> edorgNaturalKeys = new HashMap<String, String>();
        edorgNaturalKeys.put("stateOrganizationId", "this school");
        String edOrgDid = generateExpectedDid(edorgNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("schoolId", edOrgDid);
        naturalKeys.put("gradingPeriod", "First Six Weeks");
        naturalKeys.put("beginDate", "2011-09-01");

        checkId(entity, "GradingPeriodReference", naturalKeys, "gradingPeriod");
    }

    @Test
    public void resolvesGradingPeriodDidInReportCardCorrectly() throws IOException {

        NeutralRecordEntity entity = loadEntity("didTestEntities/reportCard.json");
        resolveInternalId(entity);

        Map<String, String> edorgNaturalKeys = new HashMap<String, String>();
        edorgNaturalKeys.put("stateOrganizationId", "this school");
        String edOrgDid = generateExpectedDid(edorgNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("schoolId", edOrgDid);
        naturalKeys.put("gradingPeriod", "First Six Weeks");
        naturalKeys.put("beginDate", "2011-09-01");

        checkId(entity, "GradingPeriodReference", naturalKeys, "gradingPeriod");
    }

    @Test
    public void resolvesGradingPeriodDidInSessionCorrectly() throws IOException {

        NeutralRecordEntity entity = loadEntity("didTestEntities/session.json");
        resolveInternalId(entity);

        Map<String, String> edorgNaturalKeys = new HashMap<String, String>();
        edorgNaturalKeys.put("stateOrganizationId", "this school");
        String edOrgDid = generateExpectedDid(edorgNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("schoolId", edOrgDid);
        naturalKeys.put("gradingPeriod", "First Six Weeks");
        naturalKeys.put("beginDate", "2011-09-01");

        checkId(entity, "GradingPeriodReference", naturalKeys, "gradingPeriod");
    }

    @Test
    public void resolvesStudentCompetencyObjectiveDidInStudentCompetencyCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentCompetency.json");
        resolveInternalId(entity);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentCompetencyObjectiveId", "student competency objective id");

        checkId(entity, "StudentCompetencyObjectiveReference", naturalKeys, "studentCompetencyObjective");
    }

    @Test
    public void resolvesLearningObjectiveDidInStudentCompetencyCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentCompetency.json");
        resolveInternalId(entity);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("objective", "Writing: Informational Text");
        naturalKeys.put("academicSubject", "ELA");
        naturalKeys.put("objectiveGradeLevel", "Twelfth grade");

        checkId(entity, "LearningObjectiveReference", naturalKeys, "learningObjective");
    }

    @Test
    public void resolvesLearningObjectiveDidsInAssessmentCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/assessment.json");
        resolveInternalId(entity);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("objective", "Writing: Informational Text");
        naturalKeys.put("academicSubject", "ELA");
        naturalKeys.put("objectiveGradeLevel", "Twelfth grade");

        checkId(entity, "objectiveAssessment.[0].learningObjectives", naturalKeys, "learningObjective");
        checkId(entity, "objectiveAssessment.[0].objectiveAssessments.[0].learningObjectives", naturalKeys,
                "learningObjective");
    }

    @Test
    public void resolvesLearningObjectiveDidsInStudentAssessmentCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentAssessment.json");
        resolveInternalId(entity);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("objective", "Writing: Informational Text");
        naturalKeys.put("academicSubject", "ELA");
        naturalKeys.put("objectiveGradeLevel", "Twelfth grade");

        checkId(entity, "studentObjectiveAssessments.[0].objectiveAssessment.learningObjectives", naturalKeys,
                "learningObjective");
        checkId(entity,
                "studentObjectiveAssessments.[0].objectiveAssessment.objectiveAssessments.[0].learningObjectives",
                naturalKeys, "learningObjective");
    }

    @Test
    public void resolvesLearningStandardDidInLearningObjectiveCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/learningObjective.json");
        resolveInternalId(entity);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("learningStandardId.identificationCode", "0123456789");

        checkId(entity, "LearningStandardReference", naturalKeys, "learningStandard");
    }

    @Test
    public void resolvesLearningStandardDidInAssessmentItemCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/assessmentItem.json");
        resolveInternalId(entity);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("learningStandardId.identificationCode", "0123456789");

        checkId(entity, "learningStandards", naturalKeys, "learningStandard");
    }

    @Test
    public void resolvesLearningStandardDidInStudentAssessmentCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentAssessment.json");
        resolveInternalId(entity);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("learningStandardId.identificationCode", "0123456789");

        checkId(entity, "studentAssessmentItems.[0].assessmentItem.learningStandards", naturalKeys, "learningStandard");
    }

    @Test
    public void resolvesProgramDidInStudentProgramAssociationCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentProgramAssociation.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("programId", "testProgramId");
        checkId(entity, "ProgramReference", naturalKeys, "program");
    }

    @Test
    public void resolvesProgramDidInCohortCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/cohort.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("programId", "testProgramId");
        checkId(entity, "ProgramReference", naturalKeys, "program");
    }

    @Test
    public void resolvesProgramDidInSchoolCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/school.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("programId", "testProgramId");
        checkId(entity, "ProgramReference", naturalKeys, "program");
    }

    @Test
    public void resolvesProgramDidInLocalEducationAgencyCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/localEducationAgency.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("programId", "testProgramId");
        checkId(entity, "ProgramReference", naturalKeys, "program");
    }

    @Test
    public void resolvesProgramDidInStateEducationAgencyCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/stateEducationAgency.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("programId", "testProgramId");
        checkId(entity, "ProgramReference", naturalKeys, "program");
    }

    @Test
    public void resolvesProgramDidInSectionCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/section.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("programId", "testProgramId");
        checkId(entity, "ProgramReference", naturalKeys, "program");
    }

    @Test
    public void resolvesProgramDidInStaffProgramAssociationCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/staffProgramAssociation.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("programId", "testProgramId");
        checkId(entity, "ProgramReference", naturalKeys, "program");
    }

    @Test
    public void resolvesStudentSectionAssociationDidInGradeCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/grade.json");
        resolveInternalId(entity);

        Map<String, String> studentNaturalKeys = new HashMap<String, String>();
        studentNaturalKeys.put("studentUniqueStateId", "800000025");
        String studentDid = generateExpectedDid(studentNaturalKeys, TENANT_ID, "student", null);

        Map<String, String> schoolNaturalKeys = new HashMap<String, String>();
        schoolNaturalKeys.put("stateOrganizationId", "this school");
        String schoolId = generateExpectedDid(schoolNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> sectionNaturalKeys = new HashMap<String, String>();
        sectionNaturalKeys.put("schoolId", schoolId);
        sectionNaturalKeys.put("uniqueSectionCode", "this section");
        String sectionDid = generateExpectedDid(sectionNaturalKeys, TENANT_ID, "section", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentId", studentDid);
        naturalKeys.put("sectionId", sectionDid);
        naturalKeys.put("beginDate", "2011-09-01");

        // because we don't have a full entity structure it thinks section is the parent, so use
        // sectionDid
        String refId = generateExpectedDid(naturalKeys, TENANT_ID, "studentSectionAssociation", sectionDid);
        Map<String, Object> body = entity.getBody();
        Object resolvedRef = body.get("StudentSectionAssociationReference");
        Assert.assertEquals(refId, resolvedRef);
    }

    @Test
    public void resolvesStudentSectionAssociationDidInStudentCompetencyCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentCompetency.json");
        resolveInternalId(entity);

        Map<String, String> studentNaturalKeys = new HashMap<String, String>();
        studentNaturalKeys.put("studentUniqueStateId", "800000025");
        String studentDid = generateExpectedDid(studentNaturalKeys, TENANT_ID, "student", null);

        Map<String, String> schoolNaturalKeys = new HashMap<String, String>();
        schoolNaturalKeys.put("stateOrganizationId", "this school");
        String schoolId = generateExpectedDid(schoolNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> sectionNaturalKeys = new HashMap<String, String>();
        sectionNaturalKeys.put("schoolId", schoolId);
        sectionNaturalKeys.put("uniqueSectionCode", "this section");
        String sectionDid = generateExpectedDid(sectionNaturalKeys, TENANT_ID, "section", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentId", studentDid);
        naturalKeys.put("sectionId", sectionDid);
        naturalKeys.put("beginDate", "2011-09-01");

        // because we don't have a full entity structure it thinks section is the parent, so use
        // sectionDid
        String refId = generateExpectedDid(naturalKeys, TENANT_ID, "studentSectionAssociation", sectionDid);
        Map<String, Object> body = entity.getBody();
        Object resolvedRef = body.get("StudentSectionAssociationReference");
        Assert.assertEquals(refId, resolvedRef);
    }

    @Test
    public void resolvesStudentSectionAssociationDidInStudentGradebookEntryCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentGradebookEntry.json");
        resolveInternalId(entity);

        Map<String, String> studentNaturalKeys = new HashMap<String, String>();
        studentNaturalKeys.put("studentUniqueStateId", "800000025");
        String studentDid = generateExpectedDid(studentNaturalKeys, TENANT_ID, "student", null);

        Map<String, String> schoolNaturalKeys = new HashMap<String, String>();
        schoolNaturalKeys.put("stateOrganizationId", "this school");
        String schoolId = generateExpectedDid(schoolNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> sectionNaturalKeys = new HashMap<String, String>();
        sectionNaturalKeys.put("schoolId", schoolId);
        sectionNaturalKeys.put("uniqueSectionCode", "this section");
        String sectionDid = generateExpectedDid(sectionNaturalKeys, TENANT_ID, "section", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentId", studentDid);
        naturalKeys.put("sectionId", sectionDid);
        naturalKeys.put("beginDate", "2011-09-01");

        // section is the parent entity, so use sectionDid when generating expected DID
        String refId = generateExpectedDid(naturalKeys, TENANT_ID, "studentSectionAssociation", sectionDid);
        Map<String, Object> body = entity.getBody();
        Object resolvedRef = body.get("StudentSectionAssociationReference");
        Assert.assertEquals(refId, resolvedRef);
    }

    @Test
    public void resolvesSectionDidInGradebookEntryCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/gradebookEntry.json");
        resolveInternalId(entity);

        Map<String, String> schoolNaturalKeys = new HashMap<String, String>();
        schoolNaturalKeys.put("stateOrganizationId", "this school");
        String schoolId = generateExpectedDid(schoolNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("schoolId", schoolId);
        naturalKeys.put("uniqueSectionCode", "this section");

        checkId(entity, "SectionReference", naturalKeys, "section");
    }

    @Test
    public void resolvesSectionDidInStudentSectionAssociationCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentSectionAssociation.json");
        resolveInternalId(entity);

        Map<String, String> schoolNaturalKeys = new HashMap<String, String>();
        schoolNaturalKeys.put("stateOrganizationId", "this school");
        String schoolId = generateExpectedDid(schoolNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("schoolId", schoolId);
        naturalKeys.put("uniqueSectionCode", "this section");

        checkId(entity, "SectionReference", naturalKeys, "section");
    }

    @Test
    public void resolvesSectionDidInTeacherSectionAssociationCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/teacherSectionAssociation.json");
        resolveInternalId(entity);

        Map<String, String> schoolNaturalKeys = new HashMap<String, String>();
        schoolNaturalKeys.put("stateOrganizationId", "this school");
        String schoolId = generateExpectedDid(schoolNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("schoolId", schoolId);
        naturalKeys.put("uniqueSectionCode", "this section");

        checkId(entity, "SectionReference", naturalKeys, "section");
    }

    @Test
    public void resolvesSectionDidInStudentGradebookEntryCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentGradebookEntry.json");
        resolveInternalId(entity);

        Map<String, String> schoolNaturalKeys = new HashMap<String, String>();
        schoolNaturalKeys.put("stateOrganizationId", "this school");
        String schoolId = generateExpectedDid(schoolNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("schoolId", schoolId);
        naturalKeys.put("uniqueSectionCode", "this section");

        checkId(entity, "SectionReference", naturalKeys, "section");
    }

    @Test
    public void resolvesStudentAcademicRecordRefDidInCourseTranscriptCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/courseTranscript.json");
        resolveInternalId(entity);

        Map<String, String> edOrgNaturalKeys = new HashMap<String, String>();
        edOrgNaturalKeys.put("stateOrganizationId", "testSchoolId");
        String edOrgId = generateExpectedDid(edOrgNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> sessionNaturalKeys = new HashMap<String, String>();
        sessionNaturalKeys.put("schoolId", edOrgId);
        sessionNaturalKeys.put("sessionId", "theSessionName");
        String sessionId = generateExpectedDid(sessionNaturalKeys, TENANT_ID, "session", null);

        Map<String, String> studentNaturalKeys = new HashMap<String, String>();
        studentNaturalKeys.put("studentUniqueStateId", "100000000");
        String studentId = generateExpectedDid(studentNaturalKeys, TENANT_ID, "student", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentId", studentId);
        naturalKeys.put("sessionId", sessionId);

        checkId(entity, "StudentAcademicRecordReference", naturalKeys, "studentAcademicRecord");
    }

    @Test
    public void resolvesStudentRefDidInStudentCohortAssociationCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentCohortAssociation.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentUniqueStateId", "100000000");
        checkId(entity, "StudentReference", naturalKeys, "student");
    }

    @Test
    public void resolvesStudentRefDidInStudentDisciplineIncidentAssociationCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentDisciplineIncidentAssociation.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentUniqueStateId", "100000000");
        checkId(entity, "StudentReference", naturalKeys, "student");
    }

    @Test
    public void resolvesStudentRefDidInStudentParentAssociationCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentParentAssociation.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentUniqueStateId", "100000000");
        checkId(entity, "StudentReference", naturalKeys, "student");
    }

    @Test
    public void resolvesStudentRefDidInStudentProgramAssociationCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentProgramAssociation.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentUniqueStateId", "100000000");
        checkId(entity, "StudentReference", naturalKeys, "student");
    }

    @Test
    public void resolvesStudentRefDidInStudentSchoolAssociationCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentSchoolAssociation.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentUniqueStateId", "100000000");
        checkId(entity, "StudentReference", naturalKeys, "student");
    }

    @Test
    public void resolvesStudentRefDidInStudentSectionAssociationCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentSectionAssociation.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentUniqueStateId", "100000000");
        checkId(entity, "StudentReference", naturalKeys, "student");
    }

    @Test
    public void resolvesStudentRefDidInAttendanceCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/attendance.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentUniqueStateId", "100000000");
        checkId(entity, "StudentReference", naturalKeys, "student");
    }

    @Test
    public void resolvesStudentRefDidInDisciplineActionCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/disciplineAction.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentUniqueStateId", "100000000");
        checkId(entity, "StudentReference", naturalKeys, "student");
    }

    @Test
    public void resolvesStudentRefDidInReportCardCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/reportCard.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentUniqueStateId", "100000000");
        checkId(entity, "StudentReference", naturalKeys, "student");
    }

    @Test
    public void resolvesStudentRefDidInStudentAcademicRecordCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentAcademicRecord.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentUniqueStateId", "100000000");
        checkId(entity, "StudentReference", naturalKeys, "student");
    }

    @Test
    public void resolvesStudentRefDidInStudentAssessmentCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentAssessment.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentUniqueStateId", "100000000");
        checkId(entity, "studentId", naturalKeys, "student");
    }

    @Test
    public void resolvesStudentCompetencyRefDidInReportCardCorrectlyUsingLearningObjectiveReference()
            throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/reportCard.json");
        resolveInternalId(entity);

        Map<String, String> studentNaturalKeys = new HashMap<String, String>();
        studentNaturalKeys.put("studentUniqueStateId", "100000000");
        String studentDid = generateExpectedDid(studentNaturalKeys, TENANT_ID, "student", null);

        Map<String, String> schoolNaturalKeys = new HashMap<String, String>();
        schoolNaturalKeys.put("stateOrganizationId", "this school");
        String schoolId = generateExpectedDid(schoolNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> sectionNaturalKeys = new HashMap<String, String>();
        sectionNaturalKeys.put("schoolId", schoolId);
        sectionNaturalKeys.put("uniqueSectionCode", "this section");
        String sectionDid = generateExpectedDid(sectionNaturalKeys, TENANT_ID, "section", null);

        Map<String, String> studentSectionAssociationNaturalKeys = new HashMap<String, String>();
        studentSectionAssociationNaturalKeys.put("studentId", studentDid);
        studentSectionAssociationNaturalKeys.put("sectionId", sectionDid);
        studentSectionAssociationNaturalKeys.put("beginDate", "2011-09-01");
        String studentSectionAssociationId = generateExpectedDid(studentSectionAssociationNaturalKeys, TENANT_ID,
                "studentSectionAssociation", sectionDid);

        Map<String, String> learningObjectiveNaturalKeys = new HashMap<String, String>();
        learningObjectiveNaturalKeys.put("objective", "Writing: Informational Text");
        learningObjectiveNaturalKeys.put("objectiveGradeLevel", "Twelfth grade");
        learningObjectiveNaturalKeys.put("academicSubject", "ELA");
        String learningObjectiveId = generateExpectedDid(learningObjectiveNaturalKeys, TENANT_ID, "learningObjective",
                null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("objectiveId.learningObjectiveId", learningObjectiveId);
        naturalKeys.put("objectiveId.studentCompetencyObjectiveId", "");
        naturalKeys.put("studentSectionAssociationId", studentSectionAssociationId);
        naturalKeys.put("competencyLevel.codeValue", "code");

        checkId(entity, "StudentCompetencyReference", naturalKeys, "studentCompetency");
    }

    @Test
    public void resolvesStudentCompetencyRefDidInReportCardCorrectlyUsingStudentCompetencyObjectiveReference()
            throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/reportCard2.json");
        resolveInternalId(entity);

        Map<String, String> studentCompetencyObjectiveNaturalKeys = new HashMap<String, String>();
        studentCompetencyObjectiveNaturalKeys.put("studentCompetencyObjectiveId", "student competency objective id");
        String studentCompetencyObjectiveId = generateExpectedDid(studentCompetencyObjectiveNaturalKeys, TENANT_ID,
                "studentCompetencyObjective", null);

        Map<String, String> studentNaturalKeys = new HashMap<String, String>();
        studentNaturalKeys.put("studentUniqueStateId", "100000000");
        String studentDid = generateExpectedDid(studentNaturalKeys, TENANT_ID, "student", null);

        Map<String, String> schoolNaturalKeys = new HashMap<String, String>();
        schoolNaturalKeys.put("stateOrganizationId", "this school");
        String schoolId = generateExpectedDid(schoolNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> sectionNaturalKeys = new HashMap<String, String>();
        sectionNaturalKeys.put("schoolId", schoolId);
        sectionNaturalKeys.put("uniqueSectionCode", "this section");
        String sectionDid = generateExpectedDid(sectionNaturalKeys, TENANT_ID, "section", null);

        Map<String, String> studentSectionAssociationNaturalKeys = new HashMap<String, String>();
        studentSectionAssociationNaturalKeys.put("studentId", studentDid);
        studentSectionAssociationNaturalKeys.put("sectionId", sectionDid);
        studentSectionAssociationNaturalKeys.put("beginDate", "2011-09-01");
        String studentSectionAssociationId = generateExpectedDid(studentSectionAssociationNaturalKeys, TENANT_ID,
                "studentSectionAssociation", sectionDid);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("objectiveId.studentCompetencyObjectiveId", studentCompetencyObjectiveId);
        naturalKeys.put("objectiveId.learningObjectiveId", "");
        naturalKeys.put("studentSectionAssociationId", studentSectionAssociationId);
        naturalKeys.put("competencyLevel.codeValue", "code");

        checkId(entity, "StudentCompetencyReference", naturalKeys, "studentCompetency");
    }

    @Test
    public void resolvesStaffRefDidInDisciplineActionCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/disciplineAction.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("staffUniqueStateId", "jjackson");
        checkId(entity, "StaffReference", naturalKeys, "staff");
    }

    @Test
    public void resolvesStaffRefDidInDisciplineIncidentCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/disciplineIncident.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("staffUniqueStateId", "jjackson");
        checkId(entity, "StaffReference", naturalKeys, "staff");
    }

    @Test
    public void resolvesStaffRefDidInStaffCohortAssociationCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/staffCohortAssociation.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("staffUniqueStateId", "jjackson");
        checkId(entity, "StaffReference", naturalKeys, "staff");
    }

    @Test
    public void resolvesStaffRefDidInStaffEducationOrganizationAssociationCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/staffEducationOrganizationAssociation.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("staffUniqueStateId", "jjackson");
        checkId(entity, "StaffReference", naturalKeys, "staff");
    }

    @Test
    public void resolvesStaffRefDidInStaffProgramAssociationCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/staffProgramAssociation.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("staffUniqueStateId", "jjackson");
        checkId(entity, "StaffReference", naturalKeys, "staff");
    }

    @Test
    public void resolvesStaffRefDidInTeacherSchoolAssociationCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/teacherSchoolAssociation.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("staffUniqueStateId", "jjackson");
        checkId(entity, "TeacherReference", naturalKeys, "staff");
    }

    @Test
    public void resolvesStaffRefDidInTeacherSectionAssociationCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/teacherSectionAssociation.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("staffUniqueStateId", "jjackson");
        checkId(entity, "TeacherReference", naturalKeys, "staff");
    }

    @Test
    public void resolvesGradebookEntryRefDidInStudentGradebookEntryCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentGradebookEntry.json");
        resolveInternalId(entity);
        Map<String, String> schoolNaturalKeys = new HashMap<String, String>();
        schoolNaturalKeys.put("stateOrganizationId", "this school");
        String schoolId = generateExpectedDid(schoolNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> sectionNaturalKeys = new HashMap<String, String>();
        sectionNaturalKeys.put("schoolId", schoolId);
        sectionNaturalKeys.put("uniqueSectionCode", "this section");
        String sectionDid = generateExpectedDid(sectionNaturalKeys, TENANT_ID, "section", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("gradebookEntryId", "Unit test");
        naturalKeys.put("dateAssigned", "2011-09-15");
        naturalKeys.put("sectionId", sectionDid);

        // section is the parent entity, so use sectionDid when generating expected DID
        String refId = generateExpectedDid(naturalKeys, TENANT_ID, "gradebookEntry", sectionDid);
        Map<String, Object> body = entity.getBody();
        Object resolvedRef = body.get("GradebookEntryReference");
        Assert.assertEquals(refId, resolvedRef);
    }

    @Test
    public void resolvesReportCardRefDidInStudentAcademicRecordCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentAcademicRecord.json");
        resolveInternalId(entity);

        Map<String, String> studentNaturalKeys = new HashMap<String, String>();
        studentNaturalKeys.put("studentUniqueStateId", "100000000");
        String studentId = generateExpectedDid(studentNaturalKeys, TENANT_ID, "student", null);

        Map<String, String> edOrgNaturalKeys = new HashMap<String, String>();
        edOrgNaturalKeys.put("stateOrganizationId", "testSchoolId");
        String edOrgId = generateExpectedDid(edOrgNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> gradingPeriodNaturalKeys = new HashMap<String, String>();
        gradingPeriodNaturalKeys.put("beginDate", "2011-09-01");
        gradingPeriodNaturalKeys.put("gradingPeriod", "First Six Weeks");
        gradingPeriodNaturalKeys.put("stateOrganizationId", edOrgId);
        String gradingPeriodId = generateExpectedDid(gradingPeriodNaturalKeys, TENANT_ID, "gradingPeriod", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("gradingPeriodId", gradingPeriodId);
        naturalKeys.put("studentId", studentId);
        checkId(entity, "ReportCardReference", naturalKeys, "reportCard");
    }

    @Test
    public void resolvesParentDidInStudentParentAssociationCorrectly() throws IOException {
        NeutralRecordEntity entity = loadEntity("didTestEntities/studentParentAssociation.json");
        resolveInternalId(entity);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("parentUniqueStateId", "testParentId");

        checkId(entity, "ParentReference", naturalKeys, "parent");
    }

    // generate the expected deterministic ids to validate against
    private String generateExpectedDid(Map<String, String> naturalKeys, String tenantId, String entityType,
            String parentId) throws IOException {
        NaturalKeyDescriptor nkd = new NaturalKeyDescriptor(naturalKeys, tenantId, entityType, parentId);
        return new DeterministicUUIDGeneratorStrategy().generateId(nkd);
    }

    // validate reference resolution
    @SuppressWarnings("unchecked")
    private void checkId(NeutralRecordEntity entity, String referenceField, Map<String, String> naturalKeys,
            String collectionName) throws IOException {
        String expectedDid = generateExpectedDid(naturalKeys, TENANT_ID, collectionName, null);

        Map<String, Object> body = entity.getBody();
        Object resolvedRef = null;

        try {
            resolvedRef = PropertyUtils.getProperty(body, referenceField);
        } catch (Exception e) {
            Assert.fail("Exception thrown accessing resolved reference: " + e);
        }

        Assert.assertNotNull("Expected non-null reference", resolvedRef);

        if (resolvedRef instanceof List) {
            List<Object> refs = (List<Object>) resolvedRef;
            Assert.assertEquals(1, refs.size());
            Assert.assertEquals(expectedDid, refs.get(0));
        } else {
            Assert.assertEquals(expectedDid, resolvedRef);
        }

    }

    // load a sample NeutralRecordEntity from a json file
    private NeutralRecordEntity loadEntity(String fname) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Resource jsonFile = new ClassPathResource(fname);
        NeutralRecord nr = mapper.readValue(jsonFile.getInputStream(), NeutralRecord.class);
        return new NeutralRecordEntity(nr);
    }

    private void resolveInternalId(NeutralRecordEntity entity) {
        AbstractMessageReport errorReport = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport, reportStats);
    }
}
