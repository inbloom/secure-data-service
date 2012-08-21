package org.slc.sli.api.resources.generic;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.ws.rs.core.UriInfo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests
 *
 * @author srupasinghe
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class GenericResourceTest {

    @Autowired
    @Qualifier("onePartResource")
    private GenericResource resource;

    @javax.annotation.Resource(name = "resourceSupportedMethods")
    private Map<String, Set<String>> resourceSupprtedMethods;

    private static final String URI_KEY = "v1/students";
    private static final String URI = "http://some.net/api/generic/v1/students";

    private URI requestURI;
    private UriInfo uriInfo;

    @Before
    public void setup() throws URISyntaxException {
        Set<String> methods = new HashSet<String>();
        methods.add("GET");

        resourceSupprtedMethods.put(URI_KEY, methods);

        requestURI = new URI(URI);

        uriInfo = mock(UriInfo.class);
        when(uriInfo.getRequestUri()).thenReturn(requestURI);
    }

    @Test(expected = MethodNotAllowedException.class)
    public void testNotSupportedMethod() {
        resource.constructAndCheckResource(uriInfo, ResourceTemplate.ONE_PART, ResourceMethod.POST);
    }

    @Test
    public void testConstructAndCheckResource() {
        Resource resourceContainer = resource.constructAndCheckResource(uriInfo, ResourceTemplate.ONE_PART, ResourceMethod.GET);

        assertNotNull("Should not be null", resourceContainer);
        assertEquals("Should match", "v1", resourceContainer.getNamespace());
        assertEquals("Should match", "students", resourceContainer.getResourceType());
    }


}
