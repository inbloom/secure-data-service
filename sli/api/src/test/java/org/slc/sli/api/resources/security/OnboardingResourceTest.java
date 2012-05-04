package org.slc.sli.api.resources.security;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.core.util.MultivaluedMapImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.service.MockRepo;
import org.slc.sli.api.test.WebContextTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class,
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class })
@DirtiesContext
public class OnboardingResourceTest {

	@Autowired
	private SecurityContextInjector injector;

	@Autowired
	@InjectMocks private OnboardingResource resource;

    @Autowired
    private MockRepo repo;

    UriInfo uriInfo = null;
    HttpHeaders headers = null;

    @Before
    public void setUp() throws Exception {
        injector.setAdminContext();
        List<String> acceptRequestHeaders = new ArrayList<String>();
        acceptRequestHeaders.add(HypermediaType.VENDOR_SLC_JSON);
        headers = mock(HttpHeaders.class);
        when(headers.getRequestHeader("accept")).thenReturn(acceptRequestHeaders);
        when(headers.getRequestHeaders()).thenReturn(new MultivaluedMapImpl());
    }

    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
        repo.deleteAll("educationalOrganization");
    }

	@Test
	public void testCreateEdOrg() {
       	Response res = resource.createEdOrg("TestOrg", null);
        assertTrue(res.getStatus() == 201);

        // Attempt to create the same edorg.
       	res = resource.createEdOrg("TestOrg", null);
        assertTrue(res.getStatus() != 201);
	}
}
