package org.slc.sli.api.resources.generic.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.NeutralCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

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
    private DefaultResourceService resourceService;

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    private EntityDefinitionStore entityDefs;

    private Resource resource = null;
    private URI requestURI;

    private static final String URI = "http://some.net/api/generic/v1/students";
    private Resource ssaResource = null;
    private Resource sectionResource = null;

    @Before
    public void setup() throws Exception {
        // inject administrator security context for unit testing
        injector.setAdminContextWithElevatedRights();

        resource = new Resource("v1", "students");
        ssaResource = new Resource("v1", "studentSectionAssociations");
        sectionResource = new Resource("v1", "sections");

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

    @Test
    public void testAddTypeCriteria() {
        EntityDefinition def = entityDefs.lookupByResourceName(ResourceNames.TEACHERS);
        ApiQuery query = new ApiQuery();

        query = resourceService.addTypeCriteria(def, query);

        List<NeutralCriteria> criteriaList = query.getCriteria();
        assertEquals("Should match", 1, criteriaList.size());

        NeutralCriteria criteria = criteriaList.get(0);
        assertEquals("Should match", "type", criteria.getKey());
        assertEquals("Should match", NeutralCriteria.CRITERIA_IN, criteria.getOperator());
        assertEquals("Should match", Arrays.asList(def.getType()), criteria.getValue());
    }

    @Test
    public void testAddTypeCriteriaNoChange() {
        EntityDefinition def = entityDefs.lookupByResourceName(ResourceNames.STAFF);
        ApiQuery query = new ApiQuery();

        query = resourceService.addTypeCriteria(def, query);

        List<NeutralCriteria> criteriaList = query.getCriteria();
        assertEquals("Should match", 0, criteriaList.size());
    }

    @Test
    public void testAddTypeCriteriaNullValues() {
        ApiQuery query = null;

        assertNull("Should be null", resourceService.addTypeCriteria(null, null));

        query = new ApiQuery();
        query = resourceService.addTypeCriteria(null, query);
        List<NeutralCriteria> criteriaList = query.getCriteria();
        assertEquals("Should match", 0, criteriaList.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReadAll() {
        // create one entity
        resourceService.postEntity(resource, new EntityBody(createTestEntity()));

        ServiceResponse response = resourceService.getEntities(resource, requestURI, false);

        List<EntityBody> entities = response.getEntityBodyList();
        assertNotNull("Should return an entity", entities);
        assertTrue("Should have at least one entity", entities.size() > 0);
    }

    @Test
    public void testGetEntityCount() {
        // create one entity
        String id = resourceService.postEntity(resource, new EntityBody(createTestEntity()));

        ApiQuery apiQuery = new ApiQuery();
        apiQuery.addCriteria(new NeutralCriteria("_id", "in", Arrays.asList(id)));

        Long count = resourceService.getEntityCount(entityDefs.lookupByResourceName(resource.getResourceType()), apiQuery);

        assertEquals("Should match", 1, count.longValue());
    }

    @Test
    public void testThreePartUri() {
        //post new student entity
        String id = resourceService.postEntity(resource, new EntityBody(createTestEntity()));
        String secId = resourceService.postEntity(sectionResource,new EntityBody(createTestSecondaryEntity()));
        String ssaId = resourceService.postEntity(ssaResource,new EntityBody(createTestSSAEntity(id,secId)));

        List<EntityBody> entityBodyList = resourceService.getEntities(resource,id,ssaResource,requestURI).getEntityBodyList();
        assertNotNull("Should return an entity", entityBodyList);
        assertEquals(ssaId, entityBodyList.get(0).get("id").toString());
    }

    @Test
    public void testGetEntitiesWithAssociation() {
        //post new student entity
        String id = resourceService.postEntity(resource, new EntityBody(createTestEntity()));
        String secId = resourceService.postEntity(sectionResource,new EntityBody(createTestSecondaryEntity()));
        String ssaId = resourceService.postEntity(ssaResource,new EntityBody(createTestSSAEntity(id,secId)));

        List<EntityBody> entityBodyList = resourceService.getEntities(ssaResource,ssaId,resource,requestURI).getEntityBodyList();
        System.out.print(entityBodyList);
        assertNotNull("Should return an entity", entityBodyList);
        assertEquals(id, entityBodyList.get(0).get("id").toString());
    }

    @Test
    public void testFourPartURI() {
        //post new student entity
        String id = resourceService.postEntity(resource, new EntityBody(createTestEntity()));
        String secId = resourceService.postEntity(sectionResource,new EntityBody(createTestSecondaryEntity()));
        String ssaId = resourceService.postEntity(ssaResource,new EntityBody(createTestSSAEntity(id,secId)));

        List<EntityBody> entityBodyList = resourceService.getEntities(resource,id,ssaResource,sectionResource,requestURI).getEntityBodyList();
        assertNotNull("Should return an entity", entityBodyList);
        assertEquals(secId, entityBodyList.get(0).get("id").toString());
    }

    private String getIDList() {
        // create one entity
        String id1 = resourceService.postEntity(resource, new EntityBody(createTestEntity()));
        // create one entity
        String id2 = resourceService.postEntity(resource, new EntityBody(createTestSecondaryEntity()));

        return id1 + "," + id2;
    }

    private Map<String, Object> createTestEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("sex", "Male");
        entity.put("studentUniqueStateId", 1234);
        return entity;
    }

    private Map<String,String> createTestSSAEntity(String studentId,String secId) {
        Map<String, String> entity = new HashMap<String,String>();
        entity.put("studentId",studentId);
        entity.put("sectionId",secId);
        return  entity;
    }
    private Map<String, Object> createTestSectionEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("sectionUniqueId", 1234);
        return entity;
    }

    private Map<String, Object> createTestUpdateEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("sex", "Female");
        entity.put("studentUniqueStateId", 1234);
        return entity;
    }

    private Map<String, Object> createTestPatchEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("sex", "Female");
        return entity;
    }

    private Map<String, Object> createTestSecondaryEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("sex", "Female");
        entity.put("studentUniqueStateId", 5678);
        return entity;
    }
}
