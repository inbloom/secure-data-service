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
    public void resolvesEdOrgRefDidInCourseOfferingCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/courseOffering.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        checkId(entity, "SchoolReference", naturalKeys, "educationOrganization");
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
    public void resolvesEdOrgRefDidInSectionCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/section.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "testSchoolId");
        checkId(entity, "SchoolReference", naturalKeys, "educationOrganization");
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
        Entity entity = loadEntity("didTestEntities/staffEducationOrgAssignmentAssociation.json");
        ErrorReport errorReport = new TestErrorReport();
        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("stateOrganizationId", "someEdOrg");
        checkId(entity, "EducationOrganizationReference", naturalKeys, "educationOrganization");
    }

    @Test
    public void shouldResolveStaffDidCorrectly() throws JsonParseException, JsonMappingException, IOException {
            Entity entity = loadEntity("didTestEntities/staffReference.json");
            ErrorReport errorReport = new TestErrorReport();
            didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);
            Map<String, String> naturalKeys = new HashMap<String, String>();
            naturalKeys.put("staffUniqueStateId", "jjackson");
            checkId(entity, "StaffReference", naturalKeys, "staff");
    }
    @Test
    public void shouldResolveCohortDidStaffCorrectly() throws JsonParseException, JsonMappingException, IOException {
            Entity entity = loadEntity("didTestEntities/cohortReference_staff.json");
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
	public void shouldResolveCohortDidStudentCorrectly() throws JsonParseException, JsonMappingException, IOException {
		Entity entity = loadEntity("didTestEntities/cohortReference_student.json");

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
	public void shouldResolveCourseOfferingDidCorrectly() throws JsonParseException, JsonMappingException, IOException {
	    Entity entity = loadEntity("didTestEntities/courseOfferingReference.json");
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
	public void shouldResolveStudentCompetencyObjectiveDidCorrectly() throws JsonParseException, JsonMappingException, IOException {
		Entity entity = loadEntity("didTestEntities/studentCompetencyObjectiveReference.json");
        ErrorReport errorReport = new TestErrorReport();

        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);

        Map<String, String> naturalKeys = new HashMap<String, String>();
		naturalKeys.put("studentCompetencyObjectiveId", "student competency objective id");

		checkId(entity, "StudentCompetencyObjectiveReference", naturalKeys, "studentCompetencyObjective");
    }


    @Test
    public void shouldResolveLearningStandardDidCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/learningStandardReference.json");
        ErrorReport errorReport = new TestErrorReport();

        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("learningStandardId.identificationCode", "0123456789");

        checkId(entity, "LearningStandardReference", naturalKeys, "learningStandard");
    }

    @Test
    public void shouldResolveLearningObjectiveDidCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/learningObjectiveReference.json");
        ErrorReport errorReport = new TestErrorReport();

        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("objective", "Writing: Informational Text");
        naturalKeys.put("academicSubject", "ELA");
        naturalKeys.put("objectiveGradeLevel", "Twelfth grade");

        checkId(entity, "objectiveAssessment.[0].learningObjectives", naturalKeys, "learningObjective");
    }

    // generate the expected deterministic ids to validate against
    private String generateExpectedDid(Map<String, String> naturalKeys, String tenantId, String entityType, String parentId) throws JsonParseException, JsonMappingException, IOException {
        NaturalKeyDescriptor nkd = new NaturalKeyDescriptor(naturalKeys, tenantId, entityType, parentId);
        return new DeterministicUUIDGeneratorStrategy().generateId(nkd);
    }

    // validate reference resolution
    @SuppressWarnings("unchecked")
    private void checkId(Entity entity, String referenceField, Map<String, String> naturalKeys, String collectionName) throws JsonParseException, JsonMappingException, IOException {
        String expectedDid =  generateExpectedDid(naturalKeys, TENANT_ID, collectionName, null);
        Map<String, Object> body = entity.getBody();
        Object resolvedRef = null;

        try {
			resolvedRef = PropertyUtils.getProperty(body, referenceField);
		} catch (Exception e) {
			Assert.fail("Exception thrown accessing resolved reference: " + e);
		}

        Assert.assertNotNull(resolvedRef);

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
