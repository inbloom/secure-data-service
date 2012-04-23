package org.slc.sli.api.resources.security;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.uri.UriBuilderImpl;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 *
 * @author pwolf
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class,
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class })
@DirtiesContext
public class ApplicationResourceTest {

    @Autowired
    private ApplicationResource resource;

    @Autowired
    private SecurityContextInjector injector;

    UriInfo uriInfo = null;
    HttpHeaders headers = null;

    private static final int STATUS_CREATED = 201;
    private static final int STATUS_DELETED = 204;
    private static final int STATUS_NO_CONTENT = 204;
    private static final int STATUS_NOT_FOUND = 404;
    private static final int STATUS_FOUND = 200;
    private static final int STATUS_BAD_REQUEST = 400;


    @Before
    public void setUp() throws Exception {

        uriInfo = buildMockUriInfo(null);
        injector.setDeveloperContext();
        List<String> acceptRequestHeaders = new ArrayList<String>();
        acceptRequestHeaders.add(HypermediaType.VENDOR_SLC_JSON);

        headers = mock(HttpHeaders.class);
        when(headers.getRequestHeader("accept")).thenReturn(acceptRequestHeaders);
        when(headers.getRequestHeaders()).thenReturn(new MultivaluedMapImpl());
    }

    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testGoodCreate() {
        EntityBody app = getNewApp();

        // test create during dup check
//        Mockito.when(
//                service.listIds(any(NeutralQuery.class)))
//                .thenReturn(new ArrayList<String>());


        Response resp = resource.createApplication(app, headers, uriInfo);
        assertEquals(STATUS_CREATED, resp.getStatus());
        assertTrue("Client id set", app.get("client_id").toString().length() == 10);
        assertTrue("Client secret set", app.get("client_secret").toString().length() == 48);
    }

    @Test
    public void testBadCreate1() {   //include id in POST
        EntityBody app = getNewApp();
        app.put("id", "123");

        Response resp = resource.createApplication(app, headers, uriInfo);
        assertEquals(STATUS_BAD_REQUEST, resp.getStatus());
    }

    @Test
    public void testBadCreate2() {   //include client_id in POST
        EntityBody app = getNewApp();
        app.put("client_id", "123");
        
        Response resp = resource.createApplication(app, headers, uriInfo);
        assertEquals(STATUS_BAD_REQUEST, resp.getStatus());
    }

    @Test
    public void testBadCreate3() {   // include client_secret in POST
        EntityBody app = getNewApp();
        app.put("client_secret", "123");
        
        Response resp = resource.createApplication(app, headers, uriInfo);
        assertEquals(STATUS_BAD_REQUEST, resp.getStatus());
    }
    
    @Test
    public void testBadAsAdmin() {   // include client_secret in POST
        SecurityContextHolder.clearContext();
        injector.setAdminContextWithElevatedRights();
        EntityBody app = getNewApp();
        app.put("client_secret", "123");
        
        Response resp = resource.createApplication(app, headers, uriInfo);
        assertEquals(STATUS_BAD_REQUEST, resp.getStatus());
    }

    private EntityBody getNewApp() {
        EntityBody app = new EntityBody();
        app.put("client_type", "PUBLIC");
        app.put("redirect_uri", "https://slidev.org");
        app.put("description", "blah blah blah");
        app.put("name", "TestApp");
        app.put("client_type", "PUBLIC");
        app.put("behavior", "Full Window App");
        app.put("is_admin", false);
        app.put("application_url", "https://slidev.org");
        app.put("administration_uri", "https://slidev.org");

        app.put("scope", "ENABLED");
        Map<String, Object> developer = new HashMap<String, Object>();
        developer.put("organization", "Acme");
        developer.put("license_accepted", true);
        app.put("developer_info", developer);
        return app;
    }

    @Test
    public void testGoodDelete() {
        EntityBody toDelete = getNewApp();
        Response created = resource.createApplication(toDelete, headers, uriInfo);
        String uuid = parseIdFromLocation(created);
        Response resp = resource.deleteApplication(uuid, headers, uriInfo);
        assertEquals(STATUS_DELETED, resp.getStatus());
    }

    @Test
    public void testBadDelete() {
        String uuid = "9999999999";
        try {
            Response resp = resource.deleteApplication(uuid, headers, uriInfo);
        } catch (EntityNotFoundException e) {
            assertTrue(true);
            return;
        }
        assertTrue(false);
    }

    @Test
    public void testGoodGet() {
        EntityBody toGet = getNewApp();
        Response created = resource.create(toGet, headers, uriInfo);
        assertEquals(STATUS_CREATED, created.getStatus());
        String uuid = parseIdFromLocation(created);
        Response resp = resource.getApplication(uuid, headers, uriInfo);
        assertEquals(STATUS_FOUND, resp.getStatus());
    }

    @Test
    public void testBadGet() {
        String uuid = "9999999999";
        try {
            Response resp = resource.getApplication(uuid, headers, uriInfo);
            assertEquals(STATUS_NOT_FOUND, resp.getStatus());
        } catch (EntityNotFoundException e) {
            assertTrue(true);
            return;
        }
        assertTrue(false);
    }




    @Test
    public void testUpdate() {
        EntityBody app = getNewApp();
        Response created = resource.createApplication(app, headers, uriInfo);
        String uuid = parseIdFromLocation(created);
        assertEquals(STATUS_NO_CONTENT, resource.updateApplication(uuid, app, headers, uriInfo).getStatus());

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

        when(mock.getQueryParameters(true)).thenAnswer(new Answer<MultivaluedMapImpl>() {
            @Override
            public MultivaluedMapImpl answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new MultivaluedMapImpl();
            }
        });

        when(mock.getRequestUri()).thenReturn(new UriBuilderImpl().replaceQuery(queryString).build(new Object[] {}));
        return mock;
    }

    private static String parseIdFromLocation(Response response) {
        List<Object> locationHeaders = response.getMetadata().get("Location");
        Assert.assertNotNull(locationHeaders);
        Assert.assertEquals(1, locationHeaders.size());
        Pattern regex = Pattern.compile(".+/([\\w-]+)$");
        Matcher matcher = regex.matcher((String) locationHeaders.get(0));
        matcher.find();
        Assert.assertEquals(1, matcher.groupCount());
        return matcher.group(1);
    }


}
