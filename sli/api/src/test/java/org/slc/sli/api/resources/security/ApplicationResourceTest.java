package org.slc.sli.api.resources.security;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.uri.UriBuilderImpl;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.security.oauth.SliClientDetailService;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.test.WebContextTestExecutionListener;

import org.slc.sli.domain.Entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.ClientDetails;
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

    @Autowired
    private SliClientDetailService detailsService;

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
        injector.setAdminContextWithElevatedRights();
        List<String> acceptRequestHeaders = new ArrayList<String>();
        acceptRequestHeaders.add(HypermediaType.VENDOR_SLC_JSON);

        headers = mock(HttpHeaders.class);
        when(headers.getRequestHeader("accept")).thenReturn(acceptRequestHeaders);
        when(headers.getRequestHeaders()).thenReturn(new MultivaluedMapImpl());
    }

//    @After
//    public void tearDown() throws Exception {
//        SecurityContextHolder.clearContext();
//    }

    @Test
    public void testGoodCreate() {
        EntityBody app = getNewApp();

        // test create during dup check
//        Mockito.when(
//                service.listIds(any(NeutralQuery.class)))
//                .thenReturn(new ArrayList<String>());


        Response resp = resource.createApplication(app, headers, uriInfo);
        assertEquals(Response.Status.NO_CONTENT, resp.getStatus());
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
    public void testBadCreate3() {   //include client_secret in POST
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
        app.put("scope", "ENABLED");
        return app;
    }

    @Test
    public void testGoodDelete() {
        String clientId = "1234567890";
        String uuid = "123";

        EntityBody toDelete = getNewApp();
        ArrayList<String> existingEntitiesIds = new ArrayList<String>();

        toDelete.put("client_id", clientId);
        toDelete.put("id", uuid);
        existingEntitiesIds.add(uuid);
        resource.createApplication(toDelete, headers, uriInfo);
        Response resp = resource.deleteApplication(uuid, headers, uriInfo);
        assertEquals(STATUS_DELETED, resp.getStatus());
    }

    @Test
    public void testBadDelete() {
        String uuid = "9999999999";
//        Mockito.doThrow(new EntityNotFoundException("Entity Not Found")).when(service).delete(uuid);
        Response resp = resource.deleteApplication(uuid, headers, uriInfo);
        assertEquals(STATUS_NOT_FOUND, resp.getStatus());
    }

    @Test
    public void testGoodGet() {
        String clientId = "1234567890";
        String uuid = "123";
        Entity mockEntity = new Entity() {

            @Override
            public String getType() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getEntityId() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Map<String, Object> getBody() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Map<String, Object> getMetaData() {
                Map<String, Object> mockMetaData = new HashMap<String, Object>();
                mockMetaData.put("created", "1331746153");
                mockMetaData.put("updated", "1331747375");
                return mockMetaData;
            }

        };

//        Mockito.when(repo.findById(ApplicationResource.RESOURCE_NAME, uuid))
//                .thenReturn(mockEntity);


        EntityBody toGet = getNewApp();
        ArrayList<String> existingEntitiesIds = new ArrayList<String>();

        toGet.put("client_id", clientId);
        toGet.put("id", uuid);
        existingEntitiesIds.add(uuid);
//        Mockito.when(
//                service.listIds(any(NeutralQuery.class)))
//                .thenReturn(existingEntitiesIds);
//        Mockito.when(service.get(uuid)).thenReturn(toGet);
        Response resp = resource.getApplication(clientId);
        assertEquals(STATUS_FOUND, resp.getStatus());
    }

    @Test
    public void testBadGet() {
        String uuid = "9999999999";

//        Mockito.doThrow(new EntityNotFoundException("EntityNotFound")).when(service).get(uuid);
        Response resp = resource.getApplication(uuid);
        assertEquals(STATUS_NOT_FOUND, resp.getStatus());
    }

    @Test
    public void testClientLookup() {
        String clientId = "1234567890";
        String uuid = "123";

        ArrayList<String> existingEntitiesIds = new ArrayList<String>();
        existingEntitiesIds.add(uuid);
//        Mockito.when(
//                service.listIds(any(NeutralQuery.class)))
//                .thenReturn(existingEntitiesIds);

        EntityBody mockApp = getNewApp();
        mockApp.put("client_id", clientId);
        mockApp.put("id", uuid);
        mockApp.put("client_secret", "ldkafjladsfjdsalfadsl");
//        Mockito.when(service.get(uuid)).thenReturn(mockApp);

        ClientDetails details = detailsService.loadClientByClientId(clientId);
        assertNotNull(details);
        assertNotNull("Checking for client id", details.getClientId());
        assertNotNull("Checking for client secret", details.getClientSecret());
        assertNotNull("Checking for redirect uri", details.getWebServerRedirectUri());
        assertNotNull("Checking for scope", details.getScope());
    }

    @Test(expected = OAuth2Exception.class)
    public void testBadClientLookup() {
        String clientId = "1234567890";
        //return empty list
//        Mockito.when(
//                service.listIds(any(NeutralQuery.class)))
//                .thenReturn(new ArrayList<String>());
        detailsService.loadClientByClientId(clientId);
    }

    @Test
    public void testUpdate() {
        String uuid = "123";

        List<String> existingUuids = new ArrayList<String>();
        existingUuids.add(uuid);
//        Mockito.when(service.listIds(any(NeutralQuery.class))).thenReturn(existingUuids);

        EntityBody app = getNewApp();
//        Mockito.when(service.update(uuid, app)).thenReturn(true);
        assertEquals(Response.Status.NO_CONTENT, resource.updateApplication(uuid, app, headers, uriInfo).getStatus());

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
        return mock;    }


}
