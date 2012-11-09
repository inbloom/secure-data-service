package org.slc.sli.ingestion.transformation.normalization.did;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
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
		Assert.assertNotNull(body.get(referenceField));

		Object resolvedRef = body.get(referenceField);
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
