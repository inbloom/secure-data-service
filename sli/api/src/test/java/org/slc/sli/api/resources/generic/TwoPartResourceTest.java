package org.slc.sli.api.resources.generic;

import com.sun.jersey.api.uri.UriBuilderImpl;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.service.DefaultResourceService;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit Tests
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class TwoPartResourceTest {
    @Autowired
    private TwoPartResource twoPartResource;

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    private EntityDefinitionStore entityDefs;

    @Autowired
    private DefaultResourceService resourceService;

    @javax.annotation.Resource(name = "resourceSupportedMethods")
    private Map<String, Set<String>> resourceSupprtedMethods;

    private Resource resource = null;
    private java.net.URI requestURI;
    private UriInfo uriInfo;

    private static final String BASE_URI = "http://some.net/api/rest/v1/students";
    private static final String URI_KEY = "v1/students";

    @Before
    public void setup() throws Exception {
        Set<String> methods = new HashSet<String>();
        methods.add("GET");
        methods.add("POST");

        resourceSupprtedMethods.put(URI_KEY, methods);

        // inject administrator security context for unit testing
        injector.setAdminContextWithElevatedRights();

        resource = new Resource("v1", "students");
    }

    private void setupMocks(String uri) throws URISyntaxException {
        requestURI = new java.net.URI(uri);

        MultivaluedMap map = new MultivaluedMapImpl();
        uriInfo = mock(UriInfo.class);
        when(uriInfo.getRequestUri()).thenReturn(requestURI);
        when(uriInfo.getQueryParameters()).thenReturn(map);
        when(uriInfo.getBaseUriBuilder()).thenAnswer(new Answer<UriBuilder>() {
            @Override
            public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
                return new UriBuilderImpl().path("base");
            }
        });
    }

    @Test
    public void testGetWithId() throws URISyntaxException {
        String id = resourceService.postEntity(resource, createTestEntity());
        setupMocks(BASE_URI + "/" + id);

        Response response = twoPartResource.getWithId(id, uriInfo);
        EntityResponse entityResponse = (EntityResponse) response.getEntity();
        EntityBody body = (EntityBody) entityResponse.getEntity();

        assertEquals("Status code should be OK", Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Should match", id, body.get("id"));
        assertEquals("Should match", 1234, body.get("studentUniqueStateId"));
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetInvalidId() throws URISyntaxException {
        setupMocks(BASE_URI + "/1234");

        twoPartResource.getWithId("1234", uriInfo);
    }

    @Test
    public void testPut() throws URISyntaxException {
        String id = resourceService.postEntity(resource, createTestEntity());
        setupMocks(BASE_URI + "/" + id);

        Response response = twoPartResource.put(id, createTestUpdateEntity(), uriInfo);

        assertEquals("Status code should be OK", Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        Response getResponse = twoPartResource.getWithId(id, uriInfo);
        EntityResponse entityResponse = (EntityResponse) getResponse.getEntity();
        EntityBody body = (EntityBody) entityResponse.getEntity();

        assertEquals("Status code should be OK", Response.Status.OK.getStatusCode(), getResponse.getStatus());
        assertEquals("Should match", id, body.get("id"));
        assertEquals("Should match", 1234, body.get("studentUniqueStateId"));
        assertEquals("Should match", "Female", body.get("sex"));
    }

    @Test
    public void testPatch() throws URISyntaxException {
        String id = resourceService.postEntity(resource, createTestEntity());
        setupMocks(BASE_URI + "/" + id);

        Response response = twoPartResource.patch(id, createTestPatchEntity(), uriInfo);

        assertEquals("Status code should be OK", Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        Response getResponse = twoPartResource.getWithId(id, uriInfo);
        EntityResponse entityResponse = (EntityResponse) getResponse.getEntity();
        EntityBody body = (EntityBody) entityResponse.getEntity();

        assertEquals("Status code should be OK", Response.Status.OK.getStatusCode(), getResponse.getStatus());
        assertEquals("Should match", id, body.get("id"));
        assertEquals("Should match", 1234, body.get("studentUniqueStateId"));
        assertEquals("Should match", "Female", body.get("sex"));
    }

    @Test(expected = EntityNotFoundException.class)
    public void testDelete() throws URISyntaxException {
        String id = resourceService.postEntity(resource, createTestEntity());
        setupMocks(BASE_URI + "/" + id);

        Response response = twoPartResource.delete(id, uriInfo);
        assertEquals("Status code should be OK", Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        twoPartResource.getWithId(id, uriInfo);
    }

    private EntityBody createTestEntity() {
        EntityBody entity = new EntityBody();
        entity.put("sex", "Male");
        entity.put("studentUniqueStateId", 1234);
        return entity;
    }

    private EntityBody createTestUpdateEntity() {
        EntityBody entity = new EntityBody();
        entity.put("sex", "Female");
        entity.put("studentUniqueStateId", 1234);
        return entity;
    }

    private EntityBody createTestPatchEntity() {
        EntityBody entity = new EntityBody();
        entity.put("sex", "Female");
        return entity;
    }
}
