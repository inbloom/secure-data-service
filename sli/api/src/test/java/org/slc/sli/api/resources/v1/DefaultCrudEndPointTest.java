package org.slc.sli.api.resources.v1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sun.jersey.api.uri.UriBuilderImpl;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.api.config.ResourceNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.util.ResourceConstants;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Unit tests for the default crud endpoint
 * @author srupasinghe
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class DefaultCrudEndPointTest {
    
    @Autowired
    DefaultCrudEndpoint crudEndPoint; //class under test
    
    @Autowired
    private SecurityContextInjector injector;
    
    private UriInfo uriInfo;
    private HttpHeaders httpHeaders;
    private List<String> resourceList = new ArrayList<String>();
    
    @Before
    public void setup() throws Exception {
        uriInfo = buildMockUriInfo(null);
        
        // inject administrator security context for unit testing
        injector.setAdminContextWithElevatedRights();
        
        List<String> acceptRequestHeaders = new ArrayList<String>();
        acceptRequestHeaders.add(HypermediaType.VENDOR_SLC_JSON);
        
        httpHeaders = mock(HttpHeaders.class);
        when(httpHeaders.getRequestHeader("accept")).thenReturn(acceptRequestHeaders);
        
        //expand this list
        resourceList.add(ResourceNames.SCHOOLS);
    }
    
    public Map<String, Object> createTestEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("field1", "1");
        entity.put("field2", 2);
        entity.put("studentUniqueStateId", 1234);
        return entity;
    }
    
    public Map<String, Object> createTestUpdateEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("field1", 8);
        entity.put("field2", 2);
        entity.put("studentUniqueStateId", 1234);
        return entity;
    }
    
    public Map<String, Object> createTestSecondaryEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("field1", 5);
        entity.put("field2", 6);
        entity.put("studentUniqueStateId", 5678);
        return entity;
    }
    
    @Test
    public void testCreate() {
        for (String resource : resourceList) {
            Response response = crudEndPoint.create(resource,  new EntityBody(createTestEntity()), httpHeaders, uriInfo);
            assertEquals("Status code should be 201", Status.CREATED.getStatusCode(), response.getStatus());
            assertNotNull("ID should not be null", parseIdFromLocation(response));
        }
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testReadMultipleResources() {
        for (String resource : resourceList) {
            String idList = getIDList(resource);
            Response response = crudEndPoint.read(resource, idList, httpHeaders, uriInfo);
            assertEquals("Status code should be 200", Status.OK.getStatusCode(), response.getStatus());
            
            List<EntityBody> results = (List<EntityBody>) response.getEntity();
            assertEquals("Should get 2 entities", results.size(), 2);

            EntityBody body1 = results.get(0);
            assertNotNull("Should not be null", body1);
            assertEquals("studentUniqueStateId should be 1234", body1.get("studentUniqueStateId"), 1234);
            assertNotNull("Should include links", body1.get(ResourceConstants.LINKS));
            
            EntityBody body2 = results.get(1);
            assertNotNull("Should not be null", body2);
            assertEquals("studentUniqueStateId should be 5678", body2.get("studentUniqueStateId"), 5678);
            assertNotNull("Should include links", body2.get(ResourceConstants.LINKS));
        }
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testReadResourceFromKey() {
        for (String resource : resourceList) {
            //create one entity
            crudEndPoint.create(resource,  new EntityBody(createTestEntity()), httpHeaders, uriInfo);
            Response response = crudEndPoint.read(resource, "field1", "1", httpHeaders, uriInfo);
            
            List<EntityBody> results = (List<EntityBody>) response.getEntity();
            assertTrue("Should have at least one entity", results.size() > 0);
        }
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testDelete() {
        for (String resource : resourceList) {
            //create one entity
            Response createResponse = crudEndPoint.create(resource,  new EntityBody(createTestEntity()), httpHeaders, uriInfo);
            String id = parseIdFromLocation(createResponse);
            
            //delete it
            Response response = crudEndPoint.delete(resource, id, httpHeaders, uriInfo);
            assertEquals("Status code should be NO_CONTENT", Status.NO_CONTENT.getStatusCode(), response.getStatus());

            try {
                Response getResponse = crudEndPoint.read(resource, id, httpHeaders, uriInfo);
                fail("should have thrown EntityNotFoundException");
            } catch (EntityNotFoundException e) {
                return;
            } catch (Exception e) {
                fail("threw wrong exception: " + e);
            }
        }
    }
    
    @Test
    public void testUpdate() {
        for (String resource : resourceList) {
            //create one entity
            Response createResponse = crudEndPoint.create(resource,  new EntityBody(createTestEntity()), httpHeaders, uriInfo);
            String id = parseIdFromLocation(createResponse);
            
            //update it
            Response response = crudEndPoint.update(resource, id, new EntityBody(createTestUpdateEntity()), httpHeaders, uriInfo);
            assertEquals("Status code should be NO_CONTENT", Status.NO_CONTENT.getStatusCode(), response.getStatus());
              
            //try to get it
            Response getResponse = crudEndPoint.read(resource, id, httpHeaders, uriInfo);
            assertEquals("Status code should be OK", Status.OK.getStatusCode(), getResponse.getStatus());            
            EntityBody body = (EntityBody) getResponse.getEntity();
            assertNotNull("Should return an entity", body);            
            assertEquals("studentUniqueStateId should be 1234", body.get("studentUniqueStateId"), 1234);
            assertEquals("studentUniqueStateId should be 8", body.get("field1"), 8);
            assertNotNull("Should include links", body.get(ResourceConstants.LINKS));
        }
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testReadAll() {
        for (String resource : resourceList) {
            //create one entity
            crudEndPoint.create(resource,  new EntityBody(createTestEntity()), httpHeaders, uriInfo);
            
            //read everything
            Response response = crudEndPoint.readAll(resource, httpHeaders, uriInfo);
            assertEquals("Status code should be OK", Status.OK.getStatusCode(), response.getStatus());
            
            List<EntityBody> results = (List<EntityBody>) response.getEntity();
            assertNotNull("Should return an entity", results);
            assertTrue("Should have at least one entity", results.size() > 0);
        }
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testReadEndPoint() {
        Map<String, Object> schoolEntity = new HashMap<String, Object>();
        schoolEntity.put("field1", "3");
        schoolEntity.put("field2", "4");
        
        Map<String, Object> studentEntity = new HashMap<String, Object>();
        studentEntity.put("field1", "5");
        studentEntity.put("field2", "6");
        
        Response schoolRes = crudEndPoint.create(ResourceNames.SCHOOLS, new EntityBody(schoolEntity), httpHeaders, uriInfo);
        String schoolId = parseIdFromLocation(schoolRes);
        
        Response studentRes = crudEndPoint.create(ResourceNames.STUDENTS, new EntityBody(studentEntity), httpHeaders, uriInfo);
        String studentId = parseIdFromLocation(studentRes);
        
        Map<String, Object> studentSchoolAssoc = new HashMap<String, Object>();
        studentSchoolAssoc.put("studentId", studentId);
        studentSchoolAssoc.put("schoolId", schoolId);
        
        crudEndPoint.create(ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, new EntityBody(studentSchoolAssoc), httpHeaders, uriInfo);
        
        Response response = crudEndPoint.read(ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, "schoolId", schoolId, 
                "studentId", ResourceNames.STUDENTS, httpHeaders, uriInfo);
        assertEquals("Status code should be OK", Status.OK.getStatusCode(), response.getStatus());
        
        @SuppressWarnings("unused")
        List<EntityBody> results = (List<EntityBody>) response.getEntity();
        //need to add to this test
        //MockRepo needs to be changed to get this test right
    }
    
    @Test
    public void testGettingTotalCountDoesNotCorruptNeutralQuery() {
        
        NeutralQuery neutralQuery1 = new NeutralQuery();
        neutralQuery1.setIncludeFields("field1,field2");
        neutralQuery1.setExcludeFields("field3,field4");
        neutralQuery1.setLimit(5);
        neutralQuery1.setOffset(4);
        neutralQuery1.setSortBy("field5");
        neutralQuery1.setSortOrder(NeutralQuery.SortOrder.ascending);
        neutralQuery1.addCriteria(new NeutralCriteria("x=1"));
        
        NeutralQuery neutralQuery2 = new NeutralQuery(neutralQuery1);
        
        EntityService mock = mock(EntityService.class);
        when(mock.count(any(NeutralQuery.class))).thenReturn(0L);
        DefaultCrudEndpoint.getTotalCount(mock, neutralQuery1);
        
        assertEquals(neutralQuery1, neutralQuery2);
    }
    
    private String getIDList(String resource) {
        //create one more resource
        Response createResponse1 = crudEndPoint.create(resource,  new EntityBody(createTestEntity()), httpHeaders, uriInfo);
        Response createResponse2 = crudEndPoint.create(resource,  new EntityBody(createTestSecondaryEntity()), httpHeaders, uriInfo);
        
        return parseIdFromLocation(createResponse1) + "," + parseIdFromLocation(createResponse2);
    }
    
    public UriInfo buildMockUriInfo(final String queryString) throws Exception {
        UriInfo mock = mock(UriInfo.class);
        when(mock.getAbsolutePathBuilder()).thenAnswer(new Answer<UriBuilder>() {
            
            @Override
            public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
                return new UriBuilderImpl().path("absolute");
            }
        });
        when(mock.getBaseUriBuilder()).thenAnswer(new Answer<UriBuilder>() {
            
            @Override
            public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
                return new UriBuilderImpl().path("base");
            }
        });
        when(mock.getRequestUriBuilder()).thenAnswer(new Answer<UriBuilder>() {
            
            @Override
            public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
                return new UriBuilderImpl().path("request");
            }
        });
        when(mock.getQueryParameters(true)).thenAnswer(new Answer<MultivaluedMap>() {
            @Override
            public MultivaluedMap answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new MultivaluedMapImpl();
            }
        });
        
        when(mock.getRequestUri()).thenReturn(new UriBuilderImpl().replaceQuery(queryString).build(new Object[] {}));
        return mock;
    }
    
    private static String parseIdFromLocation(Response response) {
        List<Object> locationHeaders = response.getMetadata().get("Location");
        assertNotNull(locationHeaders);
        assertEquals(1, locationHeaders.size());
        Pattern regex = Pattern.compile(".+/([\\w-]+)$");
        Matcher matcher = regex.matcher((String) locationHeaders.get(0));
        matcher.find();
        assertEquals(1, matcher.groupCount());
        return matcher.group(1);
    }
}
