package org.slc.sli.api.resources.v1.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.core.util.MultivaluedMapImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.client.constants.ResourceConstants;
import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.util.ResourceTestUtil;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.test.WebContextTestExecutionListener;

/**
 * Unit tests for the resource representing an learningObjective
 * 
 * @author dliu
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class LearningObjectiveResourceTest {


    @Autowired
    LearningObjectiveResource learningObjResource; // class under test
    
    @Autowired
    LearningStandardResource learningStdResource;

    @Autowired
    private StudentCompetencyResource studentCompetencyResource;

    @Autowired
    private SecurityContextInjector injector;

    public static final String LEARNING_OBJECTIVE_RESOURCE = "LearningObjectiveResource";

    private UriInfo uriInfo;
    private HttpHeaders httpHeaders;

    @Before
    public void setup() throws Exception {
        uriInfo = ResourceTestUtil.buildMockUriInfo(null);

        // inject administrator security context for unit testing
        injector.setAdminContextWithElevatedRights();

        List<String> acceptRequestHeaders = new ArrayList<String>();
        acceptRequestHeaders.add(HypermediaType.VENDOR_SLC_JSON);

        httpHeaders = mock(HttpHeaders.class);
        when(httpHeaders.getRequestHeader("accept")).thenReturn(acceptRequestHeaders);
        when(httpHeaders.getRequestHeaders()).thenReturn(new MultivaluedMapImpl());
    }

    private Map<String, Object> createTestEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("academicSubject", "Reading");
        entity.put("objectiveGradeLevel", "Eighth grade");
        entity.put(ParameterConstants.LEARNINGOBJECTIVE_ID, 1234);
        return entity;
    }
    
    private Map<String, Object> createTestEntityWithLearningStdRef(List<String> idLists) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("academicSubject", "Reading");
        entity.put("objectiveGradeLevel", "Eighth grade");
        entity.put(ParameterConstants.LEARNINGOBJECTIVE_ID, 1234);
        entity.put(ParameterConstants.LEARNING_STANDARDS, idLists);
        return entity;
    }
    
    private Map<String, Object> createTestEntityWithParentRef(String parentId) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("academicSubject", "Writing");
        entity.put("objectiveGradeLevel", "Eighth grade");
        entity.put(ParameterConstants.LEARNINGOBJECTIVE_ID, 1234);
        entity.put(ParameterConstants.PARENT_LEARNING_OBJECTIVE, parentId);
        return entity;
    }

    private Map<String, Object> createTestUpdateEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("academicSubject", "Writing");
        entity.put("objectiveGradeLevel", "Fifth grade");
        entity.put(ParameterConstants.LEARNINGOBJECTIVE_ID, 1234);
        return entity;
    }

    private Map<String, Object> createTestSecondaryEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("academicSubject", "Math");
        entity.put("objectiveGradeLevel", "Ninth grade");
        entity.put(ParameterConstants.LEARNINGOBJECTIVE_ID, 5678);
        return entity;
    }
    
    private Map<String, Object> createLearningStandardEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        Map<String, String> learningStandardId = new HashMap<String, String>();
        learningStandardId.put("identificationCode", "G.SRT.1");
        entity.put("learningStandardId", learningStandardId);
        entity.put("description", "a learning standard description");
        entity.put("contentStandard", "School Standard");
        entity.put("gradeLevel", "Eighth grade");
        entity.put(ParameterConstants.LEARNING_STANDARD_ID, 5678);
        return entity;
    }

    @Test
    public void testCreate() {
        Response response = learningObjResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        assertEquals("Status code should be 201", Status.CREATED.getStatusCode(), response.getStatus());

        String id = ResourceTestUtil.parseIdFromLocation(response);
        assertNotNull("ID should not be null", id);
    }

    @Test
    public void testRead() {
        // create one entity
        Response createResponse = learningObjResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);
        Response response = learningObjResource.read(id, httpHeaders, uriInfo);

        Object responseEntityObj = null;

        if (response.getEntity() instanceof EntityResponse) {
            EntityResponse resp = (EntityResponse) response.getEntity();
            responseEntityObj = resp.getEntity();
        } else {
            fail("Should always return EntityResponse: " + response);
        }

        if (responseEntityObj instanceof EntityBody) {
            assertNotNull(responseEntityObj);
        } else if (responseEntityObj instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<EntityBody> results = (List<EntityBody>) responseEntityObj;
            assertTrue("Should have one entity", results.size() == 1);
        } else {
            fail("Response entity not recognized: " + response);
        }
    }

    @Test
    public void testDelete() {
        // create one entity
        Response createResponse = learningObjResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        // delete it
        Response response = learningObjResource.delete(id, httpHeaders, uriInfo);
        assertEquals("Status code should be NO_CONTENT", Status.NO_CONTENT.getStatusCode(), response.getStatus());

        try {
            @SuppressWarnings("unused")
            Response getResponse = learningObjResource.read(id, httpHeaders, uriInfo);
            fail("should have thrown EntityNotFoundException");
        } catch (EntityNotFoundException e) {
            return;
        } catch (Exception e) {
            fail("threw wrong exception: " + e);
        }
    }

    @Test
    public void testUpdate() {
        // create one entity
        Response createResponse = learningObjResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        // update it
        Response response = learningObjResource.update(id, new EntityBody(createTestUpdateEntity()), httpHeaders,
                uriInfo);
        assertEquals("Status code should be NO_CONTENT", Status.NO_CONTENT.getStatusCode(), response.getStatus());

        // try to get it
        Response getResponse = learningObjResource.read(id, httpHeaders, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), getResponse.getStatus());
        EntityResponse entityResponse = (EntityResponse) getResponse.getEntity();
        EntityBody body = (EntityBody) entityResponse.getEntity();
        assertNotNull("Should return an entity", body);
        assertEquals(ParameterConstants.LEARNINGOBJECTIVE_ID + " should be 1234",
                body.get(ParameterConstants.LEARNINGOBJECTIVE_ID), 1234);
        assertEquals("academicSubject should be Writing", body.get("academicSubject"), "Writing");
        assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
    }

    @Test
    public void testReadAll() {
        // create two entities
        learningObjResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        learningObjResource.create(new EntityBody(createTestSecondaryEntity()), httpHeaders, uriInfo);

        // read everything
        Response response = learningObjResource.readAll(0, 100, httpHeaders, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), response.getStatus());

        @SuppressWarnings("unchecked")
        EntityResponse entityResponse = (EntityResponse) response.getEntity();
        List<EntityBody> results = (List<EntityBody>) entityResponse.getEntity();
        assertNotNull("Should return entities", results);
        assertTrue("Should have at least two entities", results.size() >= 2);
    }

    @Test
    public void testReadCommaSeparatedResources() {
        Response response = learningObjResource.read(getIDList(ResourceNames.LEARNINGOBJECTIVES), httpHeaders, uriInfo);
        assertEquals("Status code should be 200", Status.OK.getStatusCode(), response.getStatus());

        @SuppressWarnings("unchecked")
        EntityResponse entityResponse = (EntityResponse) response.getEntity();
        List<EntityBody> results = (List<EntityBody>) entityResponse.getEntity();
        assertEquals("Should get 2 entities", results.size(), 2);

        EntityBody body1 = results.get(0);
        assertNotNull("Should not be null", body1);
        assertEquals(ParameterConstants.LEARNINGOBJECTIVE_ID + " should be 1234",
                body1.get(ParameterConstants.LEARNINGOBJECTIVE_ID), 1234);
        assertNotNull("Should include links", body1.get(ResourceConstants.LINKS));

        EntityBody body2 = results.get(1);
        assertNotNull("Should not be null", body2);
        assertEquals(ParameterConstants.LEARNINGOBJECTIVE_ID + " should be 5678",
                body2.get(ParameterConstants.LEARNINGOBJECTIVE_ID), 5678);
        assertNotNull("Should include links", body2.get(ResourceConstants.LINKS));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetLearningStandards() {
        // create first learning standard entity
        Response response = learningStdResource.create(new EntityBody(createLearningStandardEntity()), httpHeaders,
                uriInfo);
        assertEquals("Status code should be 201", Status.CREATED.getStatusCode(), response.getStatus());
        
        String learningStandardId1 = ResourceTestUtil.parseIdFromLocation(response);
        
        // create second learning standard entity
        response = learningStdResource.create(new EntityBody(createLearningStandardEntity()), httpHeaders, uriInfo);
        assertEquals("Status code should be 201", Status.CREATED.getStatusCode(), response.getStatus());
        
        String learningStandardId2 = ResourceTestUtil.parseIdFromLocation(response);
        List<String> idLists = Arrays.asList(learningStandardId1, learningStandardId2);
        
        // create learning objective entity with reference to 2 learning standards entities
        Response learningObjResponse = learningObjResource.create(new EntityBody(
                createTestEntityWithLearningStdRef(idLists)), httpHeaders, uriInfo);

        assertEquals("Status code should be 201", Status.CREATED.getStatusCode(), learningObjResponse.getStatus());
        
        String learningObjectiveId = ResourceTestUtil.parseIdFromLocation(learningObjResponse);
        assertNotNull("ID should not be null", learningObjectiveId);
        
        // test getLearningStandards that get all referenced learning standards
        Response res = learningObjResource.getLearningStandards(learningObjectiveId, 0, 50, httpHeaders, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), res.getStatus());
        
        EntityResponse entityResponse = (EntityResponse) res.getEntity();
        List<EntityBody> results = (List<EntityBody>) entityResponse.getEntity();
        assertNotNull("Should return entities", results);
        assertTrue("Should have at least two entities", results.size() >= 2);
        
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetParentLearningObjectives() {
        // create parent learning objective entity
        Response learningObjResponse = learningObjResource.create(new EntityBody(createTestEntity()), httpHeaders,
                uriInfo);
        
        assertEquals("Status code should be 201", Status.CREATED.getStatusCode(), learningObjResponse.getStatus());
        
        String parentId = ResourceTestUtil.parseIdFromLocation(learningObjResponse);
        assertNotNull("ID should not be null", parentId);
        
        // create child learning objective entity
        Response response = learningObjResource.create(new EntityBody(createTestEntityWithParentRef(parentId)),
                httpHeaders, uriInfo);
        
        assertEquals("Status code should be 201", Status.CREATED.getStatusCode(), response.getStatus());
        
        String childId = ResourceTestUtil.parseIdFromLocation(response);
        assertNotNull("ID should not be null", childId);
        
        Response res = learningObjResource.getParentLearningObjective(childId, httpHeaders, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), res.getStatus());
        EntityResponse entityResponse = (EntityResponse) res.getEntity();
        List<EntityBody> results = (List<EntityBody>) entityResponse.getEntity();
        assertNotNull("Should return entities", results);
        assertEquals("objectiveGradeLevel in Parent learningObjective  entity should be Eighth grade", "Eighth grade",
                results.get(0).get("objectiveGradeLevel"));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetChildLearningObjectives() {
        // create parent learning objective entity
        Response learningObjResponse = learningObjResource.create(new EntityBody(createTestEntity()), httpHeaders,
                uriInfo);
        
        assertEquals("Status code should be 201", Status.CREATED.getStatusCode(), learningObjResponse.getStatus());
        
        String parentId = ResourceTestUtil.parseIdFromLocation(learningObjResponse);
        assertNotNull("ID should not be null", parentId);
        
        // create first child learning objective entity
        Response response = learningObjResource.create(new EntityBody(createTestEntityWithParentRef(parentId)),
                httpHeaders, uriInfo);
        assertEquals("Status code should be 201", Status.CREATED.getStatusCode(), response.getStatus());

        // create second child learning objective entity
        response = learningObjResource.create(new EntityBody(createTestEntityWithParentRef(parentId)), httpHeaders,
                uriInfo);
        assertEquals("Status code should be 201", Status.CREATED.getStatusCode(), response.getStatus());
        
        // test getChildLearningObjectives
        Response res = learningObjResource.getChildrenLearningObjective(parentId, httpHeaders, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), res.getStatus());
        EntityResponse entityResponse = (EntityResponse) res.getEntity();
        List<EntityBody> results = (List<EntityBody>) entityResponse.getEntity();
        assertNotNull("Should return entities", results);
        assertTrue("Should have at least two entities", results.size() >= 2);
        assertEquals("academicSubject in child learningObjective should be Writing", "Writing",
                results.get(0).get("academicSubject"));

    }

    @Test
    public void testGetStudentCompetencies() {
        Response createResponse = learningObjResource.create(new EntityBody(
                ResourceTestUtil.createTestEntity(LEARNING_OBJECTIVE_RESOURCE)), httpHeaders, uriInfo);

        String learningObjId = ResourceTestUtil.parseIdFromLocation(createResponse);
        Map<String, Object> map = ResourceTestUtil.createTestEntity("StudentCompetencyResource");
        map.put(ParameterConstants.LEARNINGOBJECTIVE_ID, learningObjId);

        studentCompetencyResource.create(new EntityBody(map), httpHeaders, uriInfo);

        Response response = learningObjResource.getStudentCompetencies(learningObjId, httpHeaders, uriInfo);
        EntityBody body = ResourceTestUtil.assertions(response);
        assertEquals(learningObjId, body.get(ParameterConstants.LEARNINGOBJECTIVE_ID));
    }

    private String getIDList(String resource) {
        // create more resources
        Response createResponse1 = learningObjResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        Response createResponse2 = learningObjResource.create(new EntityBody(createTestSecondaryEntity()), httpHeaders,
                uriInfo);
        
        return ResourceTestUtil.parseIdFromLocation(createResponse1) + ","
                + ResourceTestUtil.parseIdFromLocation(createResponse2);
    }
}
