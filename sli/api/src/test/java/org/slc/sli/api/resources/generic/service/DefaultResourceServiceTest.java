package org.slc.sli.api.resources.generic.service;

import com.sun.jersey.api.uri.UriBuilderImpl;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit Tests
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class DefaultResourceServiceTest {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    private EntityDefinitionStore entityDefs;

    private Resource resource = null;
    private URI requestURI;

    private static final String URI = "http://some.net/api/generic/v1/students";

    @Before
    public void setup() throws Exception {
        // inject administrator security context for unit testing
        injector.setAdminContextWithElevatedRights();

        resource = new Resource("v1", "students");

        requestURI = new java.net.URI(URI);
    }

    @Test
    public void testCreate() {
        String id = resourceService.postEntity(resource, new EntityBody(createTestEntity()));

        assertNotNull("ID should not be null", id);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testDelete() {
        // create one entity
        String id = resourceService.postEntity(resource, new EntityBody(createTestEntity()));
        //delete it
        resourceService.deleteEntity(resource, id);
        //try to read it
        resourceService.getEntitiesByIds(resource, id, requestURI);
    }

    @Test
    public void testUpdate() {
        // create one entity
        String id = resourceService.postEntity(resource, new EntityBody(createTestEntity()));

        resourceService.putEntity(resource, id, new EntityBody(createTestUpdateEntity()));

        ServiceResponse response = resourceService.getEntitiesByIds(resource, id, requestURI);

        List<EntityBody> entities = response.getEntityBodyList();
        assertNotNull("Should return an entity", entities);
        assertEquals("Should match", 1, entities.size());
        assertEquals("Should match", 1, response.getEntityCount());
        assertEquals("studentUniqueStateId should be 1234", entities.get(0).get("studentUniqueStateId"), 1234);
        assertEquals("sex should be Female", entities.get(0).get("sex"), "Female");
    }

    @Test
    public void testPatch() {
        // create one entity
        String id = resourceService.postEntity(resource, new EntityBody(createTestEntity()));

        resourceService.patchEntity(resource, id, new EntityBody(createTestPatchEntity()));

        ServiceResponse response = resourceService.getEntitiesByIds(resource, id, requestURI);

        List<EntityBody> entities = response.getEntityBodyList();
        assertNotNull("Should return an entity", entities);
        assertEquals("Should match", 1, entities.size());
        assertEquals("Should match", 1, response.getEntityCount());
        assertEquals("studentUniqueStateId should be 1234", entities.get(0).get("studentUniqueStateId"), 1234);
        assertEquals("sex should be Female", entities.get(0).get("sex"), "Female");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReadMultipleResources() {
        String idList = getIDList();

        ServiceResponse response = resourceService.getEntitiesByIds(resource, idList, requestURI);

        List<EntityBody> entities = response.getEntityBodyList();
        assertNotNull("Should return an entity", entities);
        assertEquals("Should match", 2, entities.size());
        assertEquals("Should match", 2, response.getEntityCount());

        EntityBody body1 = entities.get(0);
        assertNotNull("Should not be null", body1);
        assertEquals("studentUniqueStateId should be 1234", body1.get("studentUniqueStateId"), 1234);

        EntityBody body2 = entities.get(1);
        assertNotNull("Should not be null", body2);
        assertEquals("studentUniqueStateId should be 5678", body2.get("studentUniqueStateId"), 5678);
    }

    @Test
    public void testGetEntityType() {
        assertEquals("Should match", "student", resourceService.getEntityType(new Resource("v1", "students")));
        assertEquals("Should match", "staff", resourceService.getEntityType(new Resource("v1", "staff")));
        assertEquals("Should match", "teacher", resourceService.getEntityType(new Resource("v1", "teachers")));
    }

    private String getIDList() {
        // create one entity
        String id1 = resourceService.postEntity(resource, new EntityBody(createTestEntity()));
        // create one entity
        String id2 = resourceService.postEntity(resource, new EntityBody(createTestSecondaryEntity()));

        return id1 + "," + id2;
    }

    public Map<String, Object> createTestEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("sex", "Male");
        entity.put("studentUniqueStateId", 1234);
        return entity;
    }

    public Map<String, Object> createTestUpdateEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("sex", "Female");
        entity.put("studentUniqueStateId", 1234);
        return entity;
    }

    public Map<String, Object> createTestPatchEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("sex", "Female");
        return entity;
    }

    public Map<String, Object> createTestSecondaryEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("sex", "Female");
        entity.put("studentUniqueStateId", 5678);
        return entity;
    }
}
