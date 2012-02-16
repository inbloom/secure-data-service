package org.slc.sli.api.security.oauth;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.test.annotation.DirtiesContext;
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
import org.slc.sli.domain.EntityRepository;
import org.slc.sli.domain.enums.Right;

/**
 * Test class for the TokenManager
 * @author jnanney
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@DirtiesContext
public class TokenManagerTest {
    
    
    @Spy private EntityRepository repo;
    
    private Date expirationDate;
    
    @Mock private EntityService service;
    
    @Mock private  RolesToRightsResolver rolesToRightsResolver;
    
    @InjectMocks private TokenManager tokenManager;
    
    private Set<GrantedAuthority> educatorAuthorities;
    
    @Before
    public void setUp() {
        repo = new MockRepo();
        Map<String, Object> t = new HashMap<String, Object>();
        
        service = Mockito.mock(EntityService.class);
        Mockito.when(service.create(Mockito.isA(EntityBody.class))).thenReturn("id");
        
        tokenManager = new TokenManager();
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("accessToken.refreshToken.value", "Test-refresh-token-one");
        expirationDate = new Date(1329278755L);
        data.put("accessToken.refreshToken.expiration", expirationDate);
        data.put("accessToken.value", "Test-access-token");
        data.put("userId", "user-one");
        data.put("userRealm", "test-realm");
        data.put("userRoles", Arrays.asList(new String[]{"Educator"}));
        
        Map<String, Object> refreshToken = new HashMap<String, Object>();
        refreshToken.put("expiration", expirationDate);
        refreshToken.put("value", "Test-refresh-token-one");
        
        Map<String, Object> accessToken = new HashMap<String, Object>();
        accessToken.put("tokenType", "test-type");
        accessToken.put("refreshToken", refreshToken);
        data.put("accessToken", accessToken);
        
        repo.create("authSession", data);
        
        rolesToRightsResolver = Mockito.mock(RolesToRightsResolver.class);
        educatorAuthorities = new HashSet<GrantedAuthority>();
        educatorAuthorities.add(Right.AGGREGATE_READ);
        educatorAuthorities.add(Right.READ_GENERAL);
        Mockito.when(rolesToRightsResolver.resolveRoles("test-realm", Arrays.asList(new String[]{"Educator"}))).thenReturn(educatorAuthorities);
        tokenManager.setService(service);
        MockitoAnnotations.initMocks(this);

    }
    
    @Test
    public void testStoreAccessToken() {
        OAuth2AccessToken token = new OAuth2AccessToken("Test-token");
        OAuth2Authentication auth = Mockito.mock(OAuth2Authentication.class);
        SLIPrincipal principal = new SLIPrincipal();
        principal.setRealm("test-realm");
        principal.setRoles(Arrays.asList(new String[]{"Educator"}));
        principal.setId("test-id");
        Mockito.when(auth.getPrincipal()).thenReturn(principal);

        EntityBody accessToken = new EntityBody();
        token.setExpiration(expirationDate);
        token.setTokenType("test-type");
        Date d = new Date(1329278740L);
        ExpiringOAuth2RefreshToken rt = new ExpiringOAuth2RefreshToken("test-refresh", d);
        token.setRefreshToken(rt);
        tokenManager.storeAccessToken(token, auth);
    }
    
    @Test
    public void testReadRefreshToken() {
        ExpiringOAuth2RefreshToken token = tokenManager.readRefreshToken("Test-refresh-token-one");
        assertEquals(expirationDate, token.getExpiration());
        assertEquals("Test-refresh-token-one", token.getValue());
    }
    
    @Test
    public void testReadAuthenticationWithAccessToken() {
        OAuth2AccessToken token = new OAuth2AccessToken("Test-access-token");
        OAuth2Authentication auth = tokenManager.readAuthentication(token);
        ClientToken clientToken = auth.getClientAuthentication();
        assertEquals(clientToken.getClientId(), "user-one");
    }
    
    @Test
    public void testReadAuthenticationWithRefreshToken() {
        ExpiringOAuth2RefreshToken refreshToken = new ExpiringOAuth2RefreshToken("Test-refresh-token-one", expirationDate);
        OAuth2Authentication auth = tokenManager.readAuthentication(refreshToken);
        ClientToken clientToken = auth.getClientAuthentication();
        assertEquals(clientToken.getClientId(), "user-one");
    }

    @Test
    public void testReadNonExistentAccessToken() {
        OAuth2AccessToken token = tokenManager.readAccessToken("bad-token");
        assertEquals(null, token);
    }
    
    @Test
    public void testReadAccessToken() {
        OAuth2AccessToken token = tokenManager.readAccessToken("Test-access-token");
        assertEquals("Test-access-token", token.getValue());
        assertEquals("test-type", token.getTokenType());
    }
    
    @Test
    public void testStoreRefreshToken() {
        ExpiringOAuth2RefreshToken refreshToken = new ExpiringOAuth2RefreshToken("token", new Date());
        ClientToken clientToken = new ClientToken(null, null, null);
        OAuth2Authentication auth = new OAuth2Authentication(clientToken, null);
        tokenManager.storeRefreshToken(refreshToken, auth);
    }
    
}
