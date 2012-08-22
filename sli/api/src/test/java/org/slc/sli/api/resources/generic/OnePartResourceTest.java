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
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.generic.representation.Resource;
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

/**
 * Unit Tests
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class OnePartResourceTest {

    @Autowired
    private OnePartResource onePartResource;

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    private EntityDefinitionStore entityDefs;

    @javax.annotation.Resource(name = "resourceSupportedMethods")
    private Map<String, Set<String>> resourceSupprtedMethods;

    private Resource resource = null;
    private java.net.URI requestURI;
    private UriInfo uriInfo;

    private static final String URI = "http://some.net/api/rest/v1/students";
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

        requestURI = new java.net.URI(URI);

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
    public void testGetAll() {
        Response response = onePartResource.getAll(uriInfo);

        assertEquals("Status code should be OK", Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testPost() {
        Response response = onePartResource.post(createTestEntity(), uriInfo);

        assertEquals("Status code should be OK", Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertNotNull("Should not be null", parseIdFromLocation(response));
    }

    private EntityBody createTestEntity() {
        EntityBody entity = new EntityBody();
        entity.put("sex", "Male");
        entity.put("studentUniqueStateId", 1234);
        return entity;
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
