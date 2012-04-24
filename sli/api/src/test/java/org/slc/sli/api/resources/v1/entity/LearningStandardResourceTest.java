package org.slc.sli.api.resources.v1.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.util.ResourceTestUtil;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.constants.ResourceConstants;
import org.slc.sli.common.constants.v1.ParameterConstants;

/**
 * Unit tests for LearningStandardResource
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class LearningStandardResourceTest {
    
    @Autowired
    LearningStandardResource learningStandardResource; // class under test
    
    @Autowired
    private SecurityContextInjector injector;
    
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
        Map<String, String> learningStandardId = new HashMap<String, String>();
        learningStandardId.put("identificationCode", "G.SRT.1");
        entity.put("learningStandardId", learningStandardId);
        entity.put("description", "a learning standard description");
        entity.put("contentStandard", "State Standard");
        entity.put("gradeLevel", "Twelfth grade");
        entity.put(ParameterConstants.LEARNING_STANDARD_ID, 1234);
        return entity;
    }
    
    private Map<String, Object> createTestUpdateEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        Map<String, String> learningStandardId = new HashMap<String, String>();
        learningStandardId.put("identificationCode", "G.SRT.1");
        entity.put("learningStandardId", learningStandardId);
        entity.put("description", "a learning standard description");
        entity.put("contentStandard", "School Standard");
        entity.put("gradeLevel", "Fifth grade");
        entity.put(ParameterConstants.LEARNING_STANDARD_ID, 1234);
        return entity;
    }
    
    private Map<String, Object> createTestSecondaryEntity() {
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

    @SuppressWarnings("unchecked")
    @Test
    public void testReadAll() {
        // create two entities
        learningStandardResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        learningStandardResource.create(new EntityBody(createTestSecondaryEntity()), httpHeaders, uriInfo);

        // read everything
        Response response = learningStandardResource.readAll(0, 100, httpHeaders, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), response.getStatus());

        EntityResponse entityResponse = (EntityResponse) response.getEntity();
        List<EntityBody> results = (List<EntityBody>) entityResponse.getEntity();
        assertNotNull("Should return entities", results);
        assertTrue("Should have at least two entities", results.size() >= 2);
    }
    
    @Test
    public void testRead() {
        // create one entity
        Response createResponse = learningStandardResource.create(new EntityBody(createTestEntity()), httpHeaders,
                uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);
        Response response = learningStandardResource.read(id, httpHeaders, uriInfo);
        
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
    public void testCreate() {
        Response response = learningStandardResource.create(new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        assertEquals("Status code should be 201", Status.CREATED.getStatusCode(), response.getStatus());
        
        String id = ResourceTestUtil.parseIdFromLocation(response);
        assertNotNull("ID should not be null", id);
    }
    
    @Test
    public void testDelete() {
        // create one entity
        Response createResponse = learningStandardResource.create(new EntityBody(createTestEntity()), httpHeaders,
                uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);
        
        // delete it
        Response response = learningStandardResource.delete(id, httpHeaders, uriInfo);
        assertEquals("Status code should be NO_CONTENT", Status.NO_CONTENT.getStatusCode(), response.getStatus());
        
        try {
            @SuppressWarnings("unused")
            Response getResponse = learningStandardResource.read(id, httpHeaders, uriInfo);
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
        Response createResponse = learningStandardResource.create(new EntityBody(createTestEntity()), httpHeaders,
                uriInfo);
        String id = ResourceTestUtil.parseIdFromLocation(createResponse);

        // update it
        Response response = learningStandardResource.update(id, new EntityBody(createTestUpdateEntity()), httpHeaders,
                uriInfo);
        assertEquals("Status code should be NO_CONTENT", Status.NO_CONTENT.getStatusCode(), response.getStatus());

        // try to get it
        Response getResponse = learningStandardResource.read(id, httpHeaders, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), getResponse.getStatus());
        EntityResponse entityResponse = (EntityResponse) getResponse.getEntity();
        EntityBody body = (EntityBody) entityResponse.getEntity();
        assertNotNull("Should return an entity", body);
        assertEquals(ParameterConstants.LEARNING_STANDARD_ID + " should be 1234",
                body.get(ParameterConstants.LEARNING_STANDARD_ID), 1234);
        assertEquals("gradeLevel should be Fifth grade", body.get("gradeLevel"), "Fifth grade");
        assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
    }
}
