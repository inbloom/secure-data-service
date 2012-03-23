package org.slc.sli.api.security.oauth;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import javax.ws.rs.core.UriBuilder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.resolve.RolesToRightsResolver;
import org.slc.sli.api.security.resolve.UserLocator;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Test class for MongoAuthorizationCodeServices class.
 * 
 * @author jnanney
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class MongoAuthorizationCodeServicesTest {
    
    @Mock
    private SliClientDetailService clientDetailsService;
    
    @Mock
    private Repository<Entity> repo;

    @Mock
    private RolesToRightsResolver roleResolver;

    @Mock
    private UserLocator userLocator;
    
    
    @Mock
    private EntityDefinitionStore store;
    
    @InjectMocks
    private MongoAuthorizationCodeServices services;

    public static final String CLIENT_ID = "fake-client-id";
    public static final String CLIENT_SECRET = "fake-client-secret";
    public static final String REDIRECT_URI = "http://local.slidev.org";
    public static final String SAML_ID = "fake-SAML-ID";
    

    
    
    @Before
    public void setUp() {
        services = new MongoAuthorizationCodeServices();
        clientDetailsService = Mockito.mock(SliClientDetailService.class);
        repo = Mockito.mock(Repository.class);
        roleResolver = Mockito.mock(RolesToRightsResolver.class);
        userLocator = Mockito.mock(UserLocator.class);
        store = Mockito.mock(EntityDefinitionStore.class);

        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void testCreate() {
        ApplicationDetails mockClientDetails = new ApplicationDetails();
        mockClientDetails.setClientId(CLIENT_ID);
        mockClientDetails.setClientSecret(CLIENT_SECRET);
        mockClientDetails.setWebServerRedirectUri(REDIRECT_URI);
        mockClientDetails.setIsScoped(false);
        mockClientDetails.setIsSecretRequired(true);
        Mockito.when(clientDetailsService.loadClientByClientId(CLIENT_ID)).thenReturn(mockClientDetails);
        
        EntityDefinition defn = Mockito.mock(EntityDefinition.class);
        EntityService service = Mockito.mock(EntityService.class);
        Mockito.when(service.create((EntityBody) Mockito.any())).thenReturn("fake-id");
        Mockito.when(defn.getService()).thenReturn(service);

        Mockito.when(store.lookupByResourceName(MongoAuthorizationCodeServices.OAUTH_AUTHORIZATION_CODE)).thenReturn(defn);

        services.create(CLIENT_ID, "NC", REDIRECT_URI, SAML_ID);
    }
    
    @Test
    public void testCreateWithBadRedirectUri() {
        ApplicationDetails mockClientDetails = new ApplicationDetails();
        mockClientDetails.setClientId(CLIENT_ID);
        mockClientDetails.setClientSecret(CLIENT_SECRET);
        mockClientDetails.setWebServerRedirectUri(REDIRECT_URI);
        mockClientDetails.setIsScoped(false);
        mockClientDetails.setIsSecretRequired(true);
        Mockito.when(clientDetailsService.loadClientByClientId(CLIENT_ID)).thenReturn(mockClientDetails);
        
        boolean caughtException = false;
        try {
            services.create(CLIENT_ID, "NC", "https://local.slidev.org", SAML_ID);
        } catch (OAuth2Exception e) {
            caughtException = true;
        }
        Assert.assertTrue(caughtException);
    }
    
    @Test 
    public void createAuthorizationCodeForMessageIdWithState() throws Exception {
        testCreateAuthorizationCodeForMessageId("NC");
    }
    
    @Test 
    public void createAuthorizationCodeForMessageIdWithoutState() throws Exception {
        testCreateAuthorizationCodeForMessageId(null);
    }

    
    public void testCreateAuthorizationCodeForMessageId(String state) throws Exception {
        String samlId = "SAML-ID";
        String entityId = "ENTITY-ID";
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(1);
        neutralQuery.addCriteria(new NeutralCriteria("samlId", "=", samlId));
        
        ArrayList<Entity> entities = new ArrayList<Entity>();
        Entity entity = new MongoEntity("test", entityId, null, null);
        entities.add(entity);
        Mockito.when(repo.findAll(MongoAuthorizationCodeServices.OAUTH_AUTHORIZATION_CODE, neutralQuery)).thenReturn(entities);
        
        
        EntityDefinition defn = Mockito.mock(EntityDefinition.class);
        EntityService service = Mockito.mock(EntityService.class);
        Mockito.when(defn.getService()).thenReturn(service);
        EntityBody body = new EntityBody();
        body.put("redirectUri", REDIRECT_URI);
        if (state != null) {
            body.put("state", state);
        }
        Mockito.when(service.get(entityId)).thenReturn(body);

        
        Mockito.when(store.lookupByResourceName(MongoAuthorizationCodeServices.OAUTH_AUTHORIZATION_CODE)).thenReturn(defn);
        services.afterPropertiesSet();
        SLIPrincipal principal = new SLIPrincipal();
        principal.setExternalId("fake-external-id");
        principal.setRoles(Arrays.asList(new String[]{"Educator", "Leader"}));
        principal.setRealm("fake-user-realm");
        principal.setName("fake-user-name");
        principal.setAdminRealm("fake-admin-realm");
        String url = services.createAuthorizationCodeForMessageId(samlId, principal);
        Assert.assertTrue(url != null);
        Assert.assertTrue(url.length() > 0);
        URI uri = UriBuilder.fromUri(url).build(null);
        String query = uri.getQuery();
        Assert.assertTrue(query.matches(".*code=[0-9a-zA-Z]+.*"));
        if (state != null) {
            Assert.assertTrue(query.matches(".*state=" + state + ".*"));
        }
    }
    
    @Test
    public void testRemove() {
        
    }
}
