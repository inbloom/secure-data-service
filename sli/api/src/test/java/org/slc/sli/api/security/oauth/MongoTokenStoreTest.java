package org.slc.sli.api.security.oauth;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.ClientToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.resolve.RolesToRightsResolver;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.service.MockRepo;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;
import org.slc.sli.domain.enums.Right;

/**
 * Test class for the MongoTokenStore class.
 * 
 * @author jnanney
 * @author shalka
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@DirtiesContext
@Ignore
public class MongoTokenStoreTest {
    
    private static final String ACCESS_TOKEN_ONE_VALUE = "test-access-token-one-unexpired";
    private static final String ACCESS_TOKEN_ONE_TYPE = "test-type-unexpired";
    private static final String REFRESH_TOKEN_ONE_VALUE = "test-refresh-token-one-unexpired";
    private static final String ACCESS_TOKEN_TWO_VALUE = "test-access-token-two-expired";
    private static final String ACCESS_TOKEN_TWO_TYPE = "test-type-expired";
    private static final String REFRESH_TOKEN_TWO_VALUE = "test-refresh-token-two-expired";
    private static final String BAD_TOKEN_VALUE = "bad-token";
    
    @Spy
    private EntityRepository repo;
    
    @Mock
    private EntityService service;
    
    @Mock
    private RolesToRightsResolver rolesToRightsResolver;
    
    @InjectMocks
    private MongoTokenStore mongoTokenStore;
    
    private long expirationOffset = 900 * 1000L;
    private long expiredDate;
    private long unexpiredDate;
    private Set<GrantedAuthority> educatorAuthorities;
    
    @Before
    public void setUp() {
        repo = new MockRepo();
        mongoTokenStore = new MongoTokenStore();
        educatorAuthorities = new HashSet<GrantedAuthority>();
        educatorAuthorities.add(Right.AGGREGATE_READ);
        educatorAuthorities.add(Right.READ_GENERAL);
        service = Mockito.mock(EntityService.class);
        rolesToRightsResolver = Mockito.mock(RolesToRightsResolver.class);
        
        Mockito.when(service.create(Mockito.isA(EntityBody.class))).thenReturn("id");
        Mockito.when(rolesToRightsResolver.resolveRoles("test-realm", Arrays.asList(new String[] { "Educator" })))
                .thenReturn(educatorAuthorities);
        unexpiredDate = new Date().getTime() + expirationOffset;
        expiredDate = new Date().getTime() - expirationOffset;
        
        Map<String, Object> refreshToken1 = new HashMap<String, Object>();
        refreshToken1.put("value", REFRESH_TOKEN_ONE_VALUE);
        refreshToken1.put("expiration", unexpiredDate);
        
        Map<String, Object> accessToken1 = new HashMap<String, Object>();
        accessToken1.put("value", ACCESS_TOKEN_ONE_VALUE);
        accessToken1.put("expiration", unexpiredDate);
        accessToken1.put("tokenType", ACCESS_TOKEN_ONE_TYPE);
        accessToken1.put("refreshToken", refreshToken1);
        
        Map<String, Object> auth1 = new HashMap<String, Object>();
        auth1.put("accessToken.refreshToken.value", REFRESH_TOKEN_ONE_VALUE);
        auth1.put("accessToken.refreshToken.expiration", unexpiredDate);
        auth1.put("accessToken.value", ACCESS_TOKEN_ONE_VALUE);
        auth1.put("accessToken.tokenType", ACCESS_TOKEN_ONE_TYPE);
        auth1.put("accessToken.expiration", unexpiredDate);
        auth1.put("userAuthn.userId", "user-one");
        auth1.put("userAuthn.userRealm", "test-realm");
        auth1.put("userAuthn.userRoles", Arrays.asList(new String[] { "Educator" }));
        auth1.put("userAuthn.externalId", "user-one-external-id");
        auth1.put("accessToken", accessToken1);
        
        Map<String, Object> refreshToken2 = new HashMap<String, Object>();
        refreshToken2.put("value", REFRESH_TOKEN_ONE_VALUE);
        refreshToken2.put("expiration", expiredDate);
        
        Map<String, Object> accessToken2 = new HashMap<String, Object>();
        accessToken2.put("value", ACCESS_TOKEN_ONE_VALUE);
        accessToken2.put("expiration", expiredDate);
        accessToken2.put("tokenType", ACCESS_TOKEN_ONE_TYPE);
        accessToken2.put("refreshToken", refreshToken2);
        
        Map<String, Object> auth2 = new HashMap<String, Object>();
        auth2.put("accessToken.refreshToken.value", REFRESH_TOKEN_TWO_VALUE);
        auth2.put("accessToken.refreshToken.expiration", expiredDate);
        auth2.put("accessToken.value", ACCESS_TOKEN_TWO_VALUE);
        auth2.put("accessToken.tokenType", ACCESS_TOKEN_TWO_TYPE);
        auth2.put("accessToken.expiration", expiredDate);
        auth2.put("userAuthn.userId", "user-one");
        auth2.put("userAuthn.userRealm", "test-realm");
        auth2.put("userAuthn.userRoles", Arrays.asList(new String[] { "Educator" }));
        auth2.put("userAuthn.externalId", "user-one-external-id");
        auth2.put("accessToken", accessToken2);
        
        MockitoAnnotations.initMocks(this);
        
        repo.create("oauthSession", auth1);
        repo.create("oauthSession", auth2);
    }
    
    @Test
    public void testReadRefreshToken() {
        ExpiringOAuth2RefreshToken token = mongoTokenStore.readRefreshToken(REFRESH_TOKEN_ONE_VALUE);
        assertEquals(unexpiredDate, token.getExpiration().getTime());
        assertEquals(REFRESH_TOKEN_ONE_VALUE, token.getValue());
    }
    
    @Test
    public void testReadAuthenticationWithUnexpiredAccessToken() {
        OAuth2Authentication auth = mongoTokenStore.readAuthentication(new OAuth2AccessToken(ACCESS_TOKEN_ONE_VALUE));
        SLIPrincipal userAuth = (SLIPrincipal) auth.getUserAuthentication().getPrincipal();
        assertEquals("user-one", userAuth.getId());
    }
    
    @Test
    public void testReadAuthenticationWithUnexpiredRefreshToken() {
        OAuth2Authentication auth = mongoTokenStore.readAuthentication(new ExpiringOAuth2RefreshToken(
                REFRESH_TOKEN_ONE_VALUE, new Date(unexpiredDate)));
        SLIPrincipal userAuth = (SLIPrincipal) auth.getUserAuthentication().getPrincipal();
        assertEquals("user-one", userAuth.getId());
    }
    
    @Test
    @ExpectedException(InvalidTokenException.class)
    public void testReadAuthenticationWithExpiredAccessToken() {
        @SuppressWarnings("unused")
        OAuth2Authentication auth = mongoTokenStore.readAuthentication(new OAuth2AccessToken(ACCESS_TOKEN_TWO_VALUE));
    }
    
    @Test
    @ExpectedException(InvalidTokenException.class)
    public void testReadAuthenticationWithExpiredRefreshToken() {
        @SuppressWarnings("unused")
        OAuth2Authentication auth = mongoTokenStore.readAuthentication(new ExpiringOAuth2RefreshToken(
                REFRESH_TOKEN_TWO_VALUE, new Date(expiredDate)));
    }
    
    @Test
    public void testReadNonExistentAccessToken() {
        OAuth2AccessToken token = mongoTokenStore.readAccessToken(BAD_TOKEN_VALUE);
        assertEquals(null, token);
    }
    
    @Test
    public void testReadAccessToken() {
        OAuth2AccessToken token = mongoTokenStore.readAccessToken(ACCESS_TOKEN_ONE_VALUE);
        assertEquals(ACCESS_TOKEN_ONE_VALUE, token.getValue());
        assertEquals(ACCESS_TOKEN_ONE_TYPE, token.getTokenType());
    }
    
    @Test
    public void testStoreUnexpiredAccessToken() {
        Entity principalEntity = Mockito.mock(Entity.class);
        Authentication userAuthentication = Mockito.mock(Authentication.class);
        SLIPrincipal principal = Mockito.mock(SLIPrincipal.class);
        
        OAuth2AccessToken accessToken = new OAuth2AccessToken("new-access-token");
        accessToken.setExpiration(new Date(unexpiredDate));
        accessToken.setTokenType(ACCESS_TOKEN_ONE_TYPE);
        accessToken.setRefreshToken(new ExpiringOAuth2RefreshToken("test-refresh", new Date(unexpiredDate)));
        
        ClientToken clientToken = new ClientToken("test-client-id", "test-client-secret", null);
        OAuth2Authentication auth = new OAuth2Authentication(clientToken, userAuthentication);
        
        Mockito.when(principalEntity.getEntityId()).thenReturn("mongo-entity-id");
        Mockito.when(principal.getId()).thenReturn("test-id");
        Mockito.when(principal.getRealm()).thenReturn("test-realm");
        Mockito.when(principal.getRoles()).thenReturn(Arrays.asList(new String[] { "Educator" }));
        Mockito.when(principal.getExternalId()).thenReturn("test-external-id");
        Mockito.when(principal.getEntity()).thenReturn(principalEntity);
        Mockito.when(auth.getPrincipal()).thenReturn(principal);
        
        mongoTokenStore.storeAccessToken(accessToken, auth);
    }
    
    @Test
    @ExpectedException(InvalidTokenException.class)
    public void testStoreExpiredAccessToken() {
        Entity principalEntity = Mockito.mock(Entity.class);
        Authentication userAuthentication = Mockito.mock(Authentication.class);
        SLIPrincipal principal = Mockito.mock(SLIPrincipal.class);
        
        OAuth2AccessToken accessToken = new OAuth2AccessToken("new-access-token-value");
        accessToken.setExpiration(new Date(expiredDate));
        accessToken.setTokenType(ACCESS_TOKEN_TWO_TYPE);
        accessToken.setRefreshToken(new ExpiringOAuth2RefreshToken(REFRESH_TOKEN_TWO_VALUE, new Date(expiredDate)));
        
        ClientToken clientToken = new ClientToken("test-client-id", "test-client-secret", null);
        OAuth2Authentication auth = new OAuth2Authentication(clientToken, userAuthentication);
        
        Mockito.when(principalEntity.getEntityId()).thenReturn("mongo-entity-id");
        Mockito.when(principal.getId()).thenReturn("test-id");
        Mockito.when(principal.getRealm()).thenReturn("test-realm");
        Mockito.when(principal.getRoles()).thenReturn(Arrays.asList(new String[] { "Educator" }));
        Mockito.when(principal.getExternalId()).thenReturn("test-external-id");
        Mockito.when(principal.getEntity()).thenReturn(principalEntity);
        Mockito.when(auth.getPrincipal()).thenReturn(principal);
        
        mongoTokenStore.storeAccessToken(accessToken, auth);
    }
    
    @Test
    public void testStoreUnexpiredRefreshToken() {
        Entity principalEntity = Mockito.mock(Entity.class);
        Authentication userAuthentication = Mockito.mock(Authentication.class);
        SLIPrincipal principal = Mockito.mock(SLIPrincipal.class);
        
        ClientToken clientToken = new ClientToken("test-client-id", "test-client-secret", null);
        OAuth2Authentication auth = new OAuth2Authentication(clientToken, userAuthentication);
        
        Mockito.when(principalEntity.getEntityId()).thenReturn("mongo-entity-id");
        Mockito.when(principal.getId()).thenReturn("test-id");
        Mockito.when(principal.getRealm()).thenReturn("test-realm");
        Mockito.when(principal.getRoles()).thenReturn(Arrays.asList(new String[] { "Educator" }));
        Mockito.when(principal.getExternalId()).thenReturn("test-external-id");
        Mockito.when(principal.getEntity()).thenReturn(principalEntity);
        Mockito.when(auth.getPrincipal()).thenReturn(principal);
        
        mongoTokenStore.storeRefreshToken(new ExpiringOAuth2RefreshToken("new-refresh-token", new Date(unexpiredDate)), auth);
    }
    
    @Test
    @ExpectedException(InvalidTokenException.class)
    public void testStoreExpiredRefreshToken() {
        Entity principalEntity = Mockito.mock(Entity.class);
        Authentication userAuthentication = Mockito.mock(Authentication.class);
        SLIPrincipal principal = Mockito.mock(SLIPrincipal.class);
        
        ClientToken clientToken = new ClientToken("test-client-id", "test-client-secret", null);
        OAuth2Authentication auth = new OAuth2Authentication(clientToken, userAuthentication);
        
        Mockito.when(principalEntity.getEntityId()).thenReturn("mongo-entity-id");
        Mockito.when(principal.getId()).thenReturn("test-id");
        Mockito.when(principal.getRealm()).thenReturn("test-realm");
        Mockito.when(principal.getRoles()).thenReturn(Arrays.asList(new String[] { "Educator" }));
        Mockito.when(principal.getExternalId()).thenReturn("test-external-id");
        Mockito.when(principal.getEntity()).thenReturn(principalEntity);
        Mockito.when(auth.getPrincipal()).thenReturn(principal);
        
        mongoTokenStore.storeRefreshToken(new ExpiringOAuth2RefreshToken("new-refresh-token", new Date(expiredDate)), auth);
    }
    
}
