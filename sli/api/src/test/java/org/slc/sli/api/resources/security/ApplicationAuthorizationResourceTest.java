package org.slc.sli.api.resources.security;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
public class ApplicationAuthorizationResourceTest {

    @Autowired
    @InjectMocks private ApplicationAuthorizationResource resource;

    @Autowired
    SecurityContextInjector securityContextInjector;

    @Mock EntityService service;

    @Mock Repository<Entity> repo;
    
    @Mock DelegationUtil delegationUtil;

    UriInfo uriInfo = null;

    private static final int STATUS_CREATED = 201;
    private static final int STATUS_DELETED = 204;
    private static final int STATUS_NO_CONTENT = 204;
    private static final int STATUS_NOT_FOUND = 404;
    private static final int STATUS_FOUND = 200;
    private static final int STATUS_BAD_REQUEST = 400;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        uriInfo = buildMockUriInfo(null);
        
    }

    @Test
    public void testGoodCreate() {
        setupAuth("MY-DISTRICT");
        EntityBody auth = getNewAppAuth("MY-DISTRICT");
        Response resp = resource.createAuthorization(auth, uriInfo);
        assertEquals(STATUS_CREATED, resp.getStatus());
    }

    @Test(expected = AccessDeniedException.class)
    public void testBadCreate() {   //authId doesn't match principal
        setupAuth("MY-DISTRICT");
        EntityBody auth = getNewAppAuth("OTHER-DISTRICT");
        Mockito.when(delegationUtil.getDelegateEdOrgs()).thenReturn(new ArrayList<String>());
        Response resp = resource.createAuthorization(auth, uriInfo);
    }

    @Test
    public void testGoodUpdate() {
        setupAuth("MY-DISTRICT");
        EntityBody auth = getNewAppAuth("MY-DISTRICT");
        EntityBody oldAuth = (EntityBody) auth.clone();

        auth.put("appIds", Arrays.asList(new String[] {"appId1"}));
        Mockito.when(service.get("some-uuid")).thenReturn(oldAuth);
        Mockito.when(service.update("some-uuid", auth)).thenReturn(Boolean.TRUE);
        UriInfo uriInfo = null;
        Response resp = resource.updateAuthorization("some-uuid", auth, uriInfo);

        assertEquals(STATUS_NO_CONTENT, resp.getStatus());
    }

    @Test(expected = AccessDeniedException.class)
    public void testBadUpdate1() {  //edorg mismatch
        setupAuth("MY-DISTRICT");
        EntityBody auth = getNewAppAuth("MY-DISTRICT1");
        EntityBody oldAuth = (EntityBody) auth.clone();

        auth.put("appIds", Arrays.asList(new String[] {"appId1"}));
        Mockito.when(service.get("some-uuid")).thenReturn(oldAuth);
        Mockito.when(service.update("some-uuid", auth)).thenReturn(Boolean.TRUE);
        UriInfo uriInfo = null;
        Response resp = resource.updateAuthorization("some-uuid", auth, null);

    }

    @Test
    public void testBadUpdate2() {  //read-only authtype change
        setupAuth("MY-DISTRICT");
        EntityBody auth = getNewAppAuth("MY-DISTRICT");
        EntityBody oldAuth = (EntityBody) auth.clone();
        auth.put("authType", "blah");
        auth.put("appIds", Arrays.asList(new String[] {"appId1"}));
        Mockito.when(service.get("some-uuid")).thenReturn(oldAuth);
        UriInfo uriInfo = null;
        Response resp = resource.updateAuthorization("some-uuid", auth, null);
        assertEquals(STATUS_BAD_REQUEST, resp.getStatus());
    }

    @Test
    public void testBadUpdate3() {  //read-only authid change
        setupAuth("MY-DISTRICT");
        EntityBody auth = getNewAppAuth("MY-DISTRICT");
        EntityBody oldAuth = (EntityBody) auth.clone();
        auth.put("authId", "somethingDifferent");
        auth.put("appIds", Arrays.asList(new String[] {"appId1"}));
        Mockito.when(service.get("some-uuid")).thenReturn(oldAuth);
        UriInfo uriInfo = null;
        Response resp = resource.updateAuthorization("some-uuid", auth, null);
        assertEquals(STATUS_BAD_REQUEST, resp.getStatus());
    }

    @Test
    public void testList1() {   //no matching data
        setupAuth("MY-DISTRICT");
        Mockito.when(repo.findOne(Mockito.anyString(), Mockito.any(NeutralQuery.class))).thenReturn(null);
        List<Map<String, Object>> data;
        Response response;
        try {
            response = resource.getAuthorizations(buildMockUriInfo(""));
            data = (List<Map<String, Object>>) response.getEntity();
            assertEquals(0, data.size());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Test
    public void testList2() {
        setupAuth("MY-DISTRICT");
        EntityBody auth = getNewAppAuth("MY-DISTRICT");
        MongoEntity ent = new MongoEntity("blah", auth);
        Mockito.when(repo.findOne(Mockito.anyString(), Mockito.any(NeutralQuery.class))).thenReturn(ent);
        List<Map<String, Object>> data;
        Response response;
        try {
            response = resource.getAuthorizations(buildMockUriInfo(""));
            data = (List<Map<String, Object>>) response.getEntity();
            assertEquals(1, data.size());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Test
    public void testGoodGet() {
        setupAuth("MY-DISTRICT");
        EntityBody auth = getNewAppAuth("MY-DISTRICT");
        Mockito.when(service.get("some-uuid")).thenReturn(auth);
        Response resp = resource.getAuthorization("some-uuid");
        assertEquals(STATUS_FOUND, resp.getStatus());
    }

    @Test(expected = AccessDeniedException.class)
    public void testBadGet() {  //edorg mismatch
        setupAuth("MY-DISTRICT");
        EntityBody auth = getNewAppAuth("SOME-DISTRICT");
        Mockito.when(service.get("some-uuid")).thenReturn(auth);
        Response resp = resource.getAuthorization("some-uuid");
        assertEquals(STATUS_FOUND, resp.getStatus());
    }

    @Test(expected = InsufficientAuthenticationException.class)
    public void testBadGet2() {  //not authenticated
        setupAuth(null);
        EntityBody auth = getNewAppAuth("MY-DISTRICT");
        Mockito.when(service.get("some-uuid")).thenReturn(auth);
        Response resp = resource.getAuthorization("some-uuid");
    }

    @Test(expected = InsufficientAuthenticationException.class)
    public void testBadGet3() {  //not authenticated
        setupAuth(null);

        EntityBody auth = getNewAppAuth("MY-DISTRICT");
        Mockito.when(service.get("some-uuid")).thenReturn(auth);
        Response resp = resource.getAuthorization("some-uuid");
        assertEquals(STATUS_NOT_FOUND, resp.getStatus());
    }


    private EntityBody getNewAppAuth(String edOrg) {
        EntityBody auth = new EntityBody();
        auth.put("authType", "EDUCATION_ORGANIZATION");
        auth.put("authId", edOrg);
        auth.put("appIds", new ArrayList<String>());
        return auth;
    }


    private void setupAuth(String edorg) {
        Authentication mockAuth = Mockito.mock(Authentication.class);
        ArrayList<GrantedAuthority> rights = new ArrayList<GrantedAuthority>();
        rights.add(Right.ADMIN_ACCESS);
        rights.add(Right.EDORG_APP_AUTHZ);
        Mockito.when(mockAuth.getAuthorities()).thenReturn(rights);
        SLIPrincipal principal = new SLIPrincipal();
        principal.setEdOrg(edorg);
        Mockito.when(mockAuth.getPrincipal()).thenReturn(principal);
        SecurityContextHolder.getContext().setAuthentication(mockAuth);
        
        Mockito.when(delegationUtil.getUsersStateUniqueId()).thenReturn(edorg);
    }

    public UriInfo buildMockUriInfo(final String queryString) throws Exception {
        UriInfo mock = mock(UriInfo.class);
        when(mock.getBaseUri()).thenReturn(new URI("http://blah.org"));
        when(mock.getPath()).thenReturn("blah");
        return mock;
    }


}
