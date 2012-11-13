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
    public void shouldResolveProgramDidCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/programReference.json");
        ErrorReport errorReport = new TestErrorReport();

        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("programId", "program ID");

        checkId(entity, "ProgramReference", naturalKeys, "program");
    }

    @Test
    public void shouldResolveCalendarDateDidCorrectly() throws JsonParseException, JsonMappingException, IOException {
        Entity entity = loadEntity("didTestEntities/calendarDateReference.json");
        ErrorReport errorReport = new TestErrorReport();

        didResolver.resolveInternalIds(entity, TENANT_ID, errorReport);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("date", "2011-03-04");

        checkId(entity, "CalendarDateReference", naturalKeys, "calendarDate");
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
