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
import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.landingzone.validation.TestErrorReport;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 *
 * Integrated JUnit tests to check to check the schema parser and
 * DeterministicIdResolver in combination
 *
 * @author jtully
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class DidReferenceResolutionTest {

    @Autowired
    DeterministicIdResolver didResolver;

    private static final String TENANT_ID = "tenant_id";

    @Test
    public void resolvesAssessmentRefDidInAssessmentItemCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/assessmentItem.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("assessmentTitle", "Fraction Homework 123");
        naturalKeys.put("gradeLevelAssessed", "Fifth grade");
        naturalKeys.put("version", "1");
        naturalKeys.put("academicSubject", ""); // apparently, empty optional natural key field is default to empty string

        checkId(entity, "AssessmentReference", naturalKeys, "assessment");
    }

    @Test
    public void resolvesAssessmentRefDidInStudentAssessmentCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/studentAssessment.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("AssessmentTitle", "Fraction Homework 123");
        naturalKeys.put("GradeLevelAssessed", "Fifth grade");
        naturalKeys.put("Version", "1");
        naturalKeys.put("AcademicSubject", ""); // apparently, empty optional natural key field is default to empty string

        checkId(entity, "assessmentId", naturalKeys, "assessment");
    }

    @Test
    public void resolvesEdOrgRefDidInAttendanceEventCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/attendanceEvent.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        checkId(entity, "schoolId", naturalKeys, "educationOrganization");

    }

    @Test
    public void resolvesEdOrgRefDidInCohortCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/cohort.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        checkId(entity, "EducationOrgReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesEdOrgRefDidInCourseCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/course.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        checkId(entity, "EducationOrganizationReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesSchoolReferenceDidsInCourseOfferingCorrectly() throws JsonParseException,
            JsonMappingException,
            IOException {
        Entity entity = loadEntity("didTestEntities/courseOffering.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);

        Map<String, String> edOrgNaturalKeys = new HashMap<String, String>();
        edOrgNaturalKeys.put("stateOrganizationId", "testSchoolId");
        checkId(entity, "SchoolReference", edOrgNaturalKeys, "educationOrganization");

    }

    @Test
    public void resolvesSessionRefDidsInCourseOfferingCorrectly() throws JsonParseException, JsonMappingException,
            IOException {
        Entity entity = loadEntity("didTestEntities/courseOffering.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);

        Map<String, String> edOrgNaturalKeys = new HashMap<String, String>();
        edOrgNaturalKeys.put("stateOrganizationId", "testSchoolId");
        String edOrgId = generateExpectedDid(edOrgNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> sessionNaturalKeys = new HashMap<String, String>();
        sessionNaturalKeys.put("schoolId", edOrgId);
        sessionNaturalKeys.put("sessionId", "theSessionName");

        checkId(entity, "SessionReference", sessionNaturalKeys, "session");
    }

    @Test
    public void resolvesResponsibilitySchoolReferenceEdOrgRefDidInDisciplineActionCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/disciplineAction.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "someResponsibilitySchoolReference");
        checkId(entity, "ResponsibilitySchoolReference", naturalKeys, "educationOrganization");
    }


    @Test
    public void resolvesAssignmentSchoolReferenceEdOrgRefDidInDisciplineActionCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/disciplineAction.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "someAssignmentSchoolReference");
        checkId(entity, "AssignmentSchoolReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesDisciplineIncidentReferenceDidInDisciplineActionCorrectly() throws JsonParseException,
            JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/disciplineAction.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);

        Map<String, String> edOrgNaturalKeys = new HashMap<String, String>();
        edOrgNaturalKeys.put("stateOrganizationId", "testSchoolId");
        String edOrgId = generateExpectedDid(edOrgNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> disciplineIncidentNaturalKeys = new HashMap<String, String>();
        disciplineIncidentNaturalKeys.put("schoolId", edOrgId);
        disciplineIncidentNaturalKeys.put("disciplineActionIdentifier", "theIncident");

        checkId(entity, "DisciplineIncidentReference", disciplineIncidentNaturalKeys, "disciplineIncident");
    }

    @Test
    public void resolvesDisciplineIncidentReferenceDidInStudentDisciplineIncidentAssocCorrectly()
            throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/studentDisciplineIncidentAssociation.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);

        Map<String, String> edOrgNaturalKeys = new HashMap<String, String>();
        edOrgNaturalKeys.put("stateOrganizationId", "testSchoolId");
        String edOrgId = generateExpectedDid(edOrgNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> disciplineIncidentNaturalKeys = new HashMap<String, String>();
        disciplineIncidentNaturalKeys.put("schoolId", edOrgId);
        disciplineIncidentNaturalKeys.put("disciplineActionIdentifier", "theIncident");

        checkId(entity, "DisciplineIncidentReference", disciplineIncidentNaturalKeys,
 "disciplineIncident");
    }

    @Test
    public void resolvesEdOrgRefDidInTeacherSchoolAssociationCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/teacherSchoolAssociation.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        checkId(entity, "SchoolReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesEdOrgRefDidInStudentSchoolAssociationCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/studentSchoolAssociation.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        checkId(entity, "SchoolReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesEdOrgRefDidInStudentProgramAssociationCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/studentProgramAssociation.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        checkId(entity, "EducationOrganizationReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesEdOrgRefDidInStudentCompetencyObjectiveCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/studentCompetencyObjective.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        checkId(entity, "EducationOrganizationReference", naturalKeys, "educationOrganization");
    }


    @Test
    public void resolvesEdOrgRefDidInSessionCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/session.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        checkId(entity, "EducationOrganizationReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesSchoolRefDidInSectionCorrectly() throws JsonParseException, JsonMappingException,
            IOException {
        Entity entity = loadEntity("didTestEntities/section.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        checkId(entity, "SchoolReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesSessionRefDidInSectionCorrectly() throws JsonParseException, JsonMappingException,
            IOException {
        Entity entity = loadEntity("didTestEntities/section.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
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
        Entity entity = loadEntity("didTestEntities/studentAcademicRecord.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        String edOrgId = generateExpectedDid(naturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> sessionNaturalKeys = new HashMap<String, String>();
        sessionNaturalKeys.put("schoolId", edOrgId);
        sessionNaturalKeys.put("sessionId", "theSessionName");

        checkId(entity, "SessionReference", sessionNaturalKeys, "session");
    }

    @Test
    public void resolvesEdOrgRefDidInSchoolCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/school.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "someLEAOrganizationID");
        checkId(entity, "LocalEducationAgencyReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesEdOrgRefDidInGraduationPlanCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/graduationPlan.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        checkId(entity, "EducationOrganizationReference", naturalKeys, "educationOrganization");
    }

    @Test
    @Ignore
    public void resolvesLEAEdOrgRefDidInLocalEducationAgencyCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/localEducationAgency.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "someLEAOrganizationID");
        checkId(entity, "LocalEducationAgencyReference", naturalKeys, "educationOrganization");
    }

    @Test
    @Ignore
    public void resolvesEducationServiceCenterEdOrgRefDidInLocalEducationAgencyCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/localEducationAgency.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "someEducationServiceCenterID");
        checkId(entity, "EducationServiceCenterReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesStateEdOrgRefDidInLocalEducationAgencyCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/localEducationAgency.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "someSEAOrganizationID");
        checkId(entity, "StateEducationAgencyReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolvesEdOrgRefDidInStaffEducationOrgAssignmentAssociationCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/staffEducationOrganizationAssociation.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "someEdOrg");
        checkId(entity, "EducationOrganizationReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void resolveStaffDidInStaffEducationOrganizationAssociationCorrectly() throws JsonParseException, JsonMappingException, IOException {
            Entity entity = loadEntity("didTestEntities/staffEducationOrganizationAssociation.json");
            ErrorReport errorReport = new TestErrorReport();
            didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
            Map<String, String> naturalKeys = new HashMap<String, String>();
            naturalKeys.put("staffUniqueStateId", "jjackson");
            checkId(entity, "StaffReference", naturalKeys, "staff");
    }

    @Test
    public void resolveStaffDidInDisciplineActionCorrectly() throws JsonParseException, JsonMappingException, IOException {
            Entity entity = loadEntity("didTestEntities/disciplineAction.json");
            ErrorReport errorReport = new TestErrorReport();
            didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
            Map<String, String> naturalKeys = new HashMap<String, String>();
            naturalKeys.put("staffUniqueStateId", "jjackson");
            checkId(entity, "StaffReference", naturalKeys, "staff");
    }

    @Test
    public void resolveStaffDidInDisciplineIncidentCorrectly() throws JsonParseException, JsonMappingException, IOException {
            Entity entity = loadEntity("didTestEntities/disciplineIncident.json");
            ErrorReport errorReport = new TestErrorReport();
            didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
            Map<String, String> naturalKeys = new HashMap<String, String>();
            naturalKeys.put("staffUniqueStateId", "jjackson");
            checkId(entity, "StaffReference", naturalKeys, "staff");
    }

    @Test
    public void resolveStaffDidInStaffCohortAssociationCorrectly() throws JsonParseException, JsonMappingException, IOException {
            Entity entity = loadEntity("didTestEntities/staffCohortAssociation.json");
            ErrorReport errorReport = new TestErrorReport();
            didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
            Map<String, String> naturalKeys = new HashMap<String, String>();
            naturalKeys.put("staffUniqueStateId", "jjackson");
            checkId(entity, "StaffReference", naturalKeys, "staff");
    }

    @Test
    public void resolveStaffDidInStaffProgramAssociationCorrectly() throws JsonParseException, JsonMappingException, IOException {
            Entity entity = loadEntity("didTestEntities/staffProgramAssociation.json");
            ErrorReport errorReport = new TestErrorReport();
            didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
            Map<String, String> naturalKeys = new HashMap<String, String>();
            naturalKeys.put("staffUniqueStateId", "jjackson");
            checkId(entity, "StaffReference", naturalKeys, "staff");
    }

    @Test
    public void resolveStaffDidInTeacherSchoolAssociationCorrectly() throws JsonParseException, JsonMappingException, IOException {
            Entity entity = loadEntity("didTestEntities/teacherSchoolAssociation.json");
            ErrorReport errorReport = new TestErrorReport();
            didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
            Map<String, String> naturalKeys = new HashMap<String, String>();
            naturalKeys.put("staffUniqueStateId", "jjackson");
            checkId(entity, "TeacherReference", naturalKeys, "staff");
    }

    @Test
    public void resolveStaffDidInTeacherSectionAssociationCorrectly() throws JsonParseException, JsonMappingException, IOException {
            Entity entity = loadEntity("didTestEntities/teacherSectionAssociation.json");
            ErrorReport errorReport = new TestErrorReport();
            didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
            Map<String, String> naturalKeys = new HashMap<String, String>();
            naturalKeys.put("staffUniqueStateId", "jjackson");
            checkId(entity, "TeacherReference", naturalKeys, "staff");
    }

    @Test
    public void resolvesCohortDidInStaffCohortAssociationCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/staffCohortAssociation.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> edorgNaturalKeys = new HashMap<String, String>();
        edorgNaturalKeys.put("educationOrgId", "STANDARD-SEA");
        String edOrgDID = generateExpectedDid (edorgNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("cohortIdentifier", "ACC-TEST-COH-1");
        naturalKeys.put("educationOrgId", edOrgDID);

        checkId(entity, "CohortReference", naturalKeys, "cohort");
    }

    @Test
    public void resolvesCohortDidInStudentCohortAssociationCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/studentCohortAssociation.json");

        ErrorReport errorReport = new TestErrorReport();

        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);

        Map<String, String> edorgNaturalKeys = new HashMap<String, String>();
        edorgNaturalKeys.put("educationOrgId", "STANDARD-SEA");
        String edOrgDID = generateExpectedDid (edorgNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("cohortIdentifier", "ACC-TEST-COH-1");
        naturalKeys.put("educationOrgId", edOrgDID);

        checkId(entity, "CohortReference", naturalKeys, "cohort");
    }

    @Test
    public void resolvesCourseDidInCourseTranscriptCorrectly() throws JsonParseException, JsonMappingException, IOException {

        ErrorReport errorReport = new TestErrorReport();

        Entity courseTranscriptEntity = loadEntity("didTestEntities/courseTranscript.json");
        didResolver.resolveInternalIds(courseTranscriptEntity, TENANT_ID, errorReport);
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
    public void resolvesCourseDidInCourseOfferingCorrectly() throws JsonParseException, JsonMappingException, IOException {

        ErrorReport errorReport = new TestErrorReport();

        Entity courseOfferingEntity = loadEntity("didTestEntities/courseOffering.json");
        didResolver.resolveInternalIds(courseOfferingEntity, TENANT_ID, errorReport);
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
    public void resolvesCourseOfferingDidInSectionCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/section.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);

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
    public void resolvesStudentCompetencyObjectiveDidInStudentCompetencyCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/studentCompetency.json");
        ErrorReport errorReport = new TestErrorReport();

        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentCompetencyObjectiveId", "student competency objective id");

        checkId(entity, "StudentCompetencyObjectiveReference", naturalKeys, "studentCompetencyObjective");
    }

    @Test
    public void resolvesLearningStandardDidInLearningObjectiveCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/learningObjective.json");
        ErrorReport errorReport = new TestErrorReport();

        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("learningStandardId.identificationCode", "0123456789");

        checkId(entity, "LearningStandardReference", naturalKeys, "learningStandard");
    }

    @Test
    public void resolvesStudentSectionAssociationDidInGradeCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/grade.json");
        ErrorReport errorReport = new TestErrorReport();

        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);

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

        // because we don't have a full entity structure it thinks section is the parent, so use sectionDid
        String refId = generateExpectedDid(naturalKeys, TENANT_ID, "studentSectionAssociation", sectionDid);
        Map<String, Object> body = entity.getBody();
        Object resolvedRef = body.get("StudentSectionAssociationReference");
        Assert.assertEquals(refId, resolvedRef);
    }

    @Test
    public void resolvesStudentSectionAssociationDidInStudentCompetencyCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/studentCompetency.json");
        ErrorReport errorReport = new TestErrorReport();

        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);

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

        // because we don't have a full entity structure it thinks section is the parent, so use sectionDid
        String refId = generateExpectedDid(naturalKeys, TENANT_ID, "studentSectionAssociation", sectionDid);
        Map<String, Object> body = entity.getBody();
        Object resolvedRef = body.get("StudentSectionAssociationReference");
        Assert.assertEquals(refId, resolvedRef);
    }

    @Test
    public void resolvesStudentSectionAssociationDidInStudentGradebookEntryCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/studentGradebookEntry.json");
        ErrorReport errorReport = new TestErrorReport();

        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);

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
    public void resolvesSectionDidInGradebookEntryCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/gradebookEntry.json");
        ErrorReport errorReport = new TestErrorReport();

        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);

        Map<String, String> schoolNaturalKeys = new HashMap<String, String>();
        schoolNaturalKeys.put("stateOrganizationId", "this school");
        String schoolId = generateExpectedDid(schoolNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("schoolId", schoolId);
        naturalKeys.put("uniqueSectionCode", "this section");

        checkId(entity, "SectionReference", naturalKeys, "section");
    }

    @Test
    public void resolvesSectionDidInStudentSectionAssociationCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/studentSectionAssociation.json");
        ErrorReport errorReport = new TestErrorReport();

        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);

        Map<String, String> schoolNaturalKeys = new HashMap<String, String>();
        schoolNaturalKeys.put("stateOrganizationId", "this school");
        String schoolId = generateExpectedDid(schoolNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("schoolId", schoolId);
        naturalKeys.put("uniqueSectionCode", "this section");

        checkId(entity, "SectionReference", naturalKeys, "section");
    }

    @Test
    public void resolvesSectionDidInTeacherSectionAssociationCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/teacherSectionAssociation.json");
        ErrorReport errorReport = new TestErrorReport();

        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);

        Map<String, String> schoolNaturalKeys = new HashMap<String, String>();
        schoolNaturalKeys.put("stateOrganizationId", "this school");
        String schoolId = generateExpectedDid(schoolNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("schoolId", schoolId);
        naturalKeys.put("uniqueSectionCode", "this section");

        checkId(entity, "SectionReference", naturalKeys, "section");
    }

    @Test
    public void resolvesSectionDidInStudentGradebookEntryCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/studentGradebookEntry.json");
        ErrorReport errorReport = new TestErrorReport();

        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);

        Map<String, String> schoolNaturalKeys = new HashMap<String, String>();
        schoolNaturalKeys.put("stateOrganizationId", "this school");
        String schoolId = generateExpectedDid(schoolNaturalKeys, TENANT_ID, "educationOrganization", null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("schoolId", schoolId);
        naturalKeys.put("uniqueSectionCode", "this section");

        checkId(entity, "SectionReference", naturalKeys, "section");
    }

    @Test
    public void resolvesStudentRefDidInStudentCohortAssociationCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/studentCohortAssociation.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentUniqueStateId", "100000000");
        checkId(entity, "StudentReference", naturalKeys, "student");
    }

    @Test
    public void resolvesStudentRefDidInStudentDisciplineIncidentAssociationCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/studentDisciplineIncidentAssociation.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentUniqueStateId", "100000000");
        checkId(entity, "StudentReference", naturalKeys, "student");
    }

    @Test
    public void resolvesStudentRefDidInStudentParentAssociationCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/studentParentAssociation.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentUniqueStateId", "100000000");
        checkId(entity, "StudentReference", naturalKeys, "student");
    }

    @Test
    public void resolvesStudentRefDidInStudentProgramAssociationCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/studentProgramAssociation.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentUniqueStateId", "100000000");
        checkId(entity, "StudentReference", naturalKeys, "student");
    }

    @Test
    public void resolvesStudentRefDidInStudentSchoolAssociationCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/studentSchoolAssociation.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentUniqueStateId", "100000000");
        checkId(entity, "StudentReference", naturalKeys, "student");
    }

    @Test
    public void resolvesStudentRefDidInStudentSectionAssociationCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/studentSectionAssociation.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentUniqueStateId", "100000000");
        checkId(entity, "StudentReference", naturalKeys, "student");
    }

    @Test
    public void resolvesStudentRefDidInAttendanceCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/attendance.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentUniqueStateId", "100000000");
        checkId(entity, "StudentReference", naturalKeys, "student");
    }

    @Test
    public void resolvesStudentRefDidInDisciplineActionCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/disciplineAction.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentUniqueStateId", "100000000");
        checkId(entity, "StudentReference", naturalKeys, "student");
    }

    @Test
    public void resolvesStudentRefDidInReportCardCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/reportCard.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentUniqueStateId", "100000000");
        checkId(entity, "StudentReference", naturalKeys, "student");
    }

    @Test
    public void resolvesStudentRefDidInStudentAcademicRecordCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/studentAcademicRecord.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentUniqueStateId", "100000000");
        checkId(entity, "StudentReference", naturalKeys, "student");
    }

    @Test
    public void resolvesStudentRefDidInStudentAssessmentCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/studentAssessment.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("studentUniqueStateId", "100000000");
//        checkId(entity, "StudentReference", naturalKeys, "student");
    }

    @Test
    public void resolvesStaffRefDidInDisciplineActionCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/disciplineAction.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("staffUniqueStateId", "jjackson");
        checkId(entity, "StaffReference", naturalKeys, "staff");
    }

    @Test
    public void resolvesStaffRefDidInDisciplineIncidentCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/disciplineIncident.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("staffUniqueStateId", "jjackson");
        checkId(entity, "StaffReference", naturalKeys, "staff");
    }

    @Test
    public void resolvesStaffRefDidInStaffCohortAssociationCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/staffCohortAssociation.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("staffUniqueStateId", "jjackson");
        checkId(entity, "StaffReference", naturalKeys, "staff");
    }

    @Test
    public void resolvesStaffRefDidInStaffEducationOrganizationAssociationCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/staffEducationOrganizationAssociation.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("staffUniqueStateId", "jjackson");
        checkId(entity, "StaffReference", naturalKeys, "staff");
    }

    @Test
    public void resolvesStaffRefDidInStaffProgramAssociationCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/staffProgramAssociation.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("staffUniqueStateId", "jjackson");
        checkId(entity, "StaffReference", naturalKeys, "staff");
    }

    @Test
    public void resolvesStaffRefDidInTeacherSchoolAssociationCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/teacherSchoolAssociation.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("staffUniqueStateId", "jjackson");
        checkId(entity, "TeacherReference", naturalKeys, "staff");
    }

    @Test
    public void resolvesStaffRefDidInTeacherSectionAssociationCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/teacherSectionAssociation.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("staffUniqueStateId", "jjackson");
        checkId(entity, "TeacherReference", naturalKeys, "staff");
    }

    @Test
    public void resolvesGradebookEntryRefDidInStudentGradebookEntryCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/studentGradebookEntry.json");
        ErrorReport errorReport = new TestErrorReport();

        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
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
    public void resolvesParentDidInStudentParentAssociationCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/studentParentAssociation.json");
        ErrorReport errorReport = new TestErrorReport();

        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("parentUniqueStateId", "testParentId");

        checkId(entity, "ParentReference", naturalKeys, "parent");
    }

    // generate the expected deterministic ids to validate against
    private String generateExpectedDid(Map<String, String> naturalKeys, String tenantId, String entityType, String parentId) throws JsonParseException, JsonMappingException, IOException {
        NaturalKeyDescriptor nkd = new NaturalKeyDescriptor(naturalKeys, tenantId, entityType, parentId);
        return new DeterministicUUIDGeneratorStrategy().generateId(nkd);
    }

    // validate reference resolution
    @SuppressWarnings("unchecked")
    private void checkId(Entity entity, String referenceField, Map<String, String> naturalKeys, String collectionName)
            throws JsonParseException, JsonMappingException, IOException {
        String expectedDid =  generateExpectedDid(naturalKeys, TENANT_ID, collectionName, null);
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
    private Entity loadEntity(String fname) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Resource jsonFile = new ClassPathResource(fname);
        NeutralRecord nr = mapper.readValue(jsonFile.getInputStream(), NeutralRecord.class);
        return new NeutralRecordEntity(nr);
    }
}
