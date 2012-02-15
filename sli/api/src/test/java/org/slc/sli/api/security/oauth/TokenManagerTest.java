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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    
    private TokenManager tokenManager;
    
    @Autowired
    private MockRepo repo;
    
    private Date expirationDate;
    
    private Set<GrantedAuthority> educatorAuthorities;
    
    @Before
    public void setUp() {
        
        Map<String, Object> t = new HashMap<String, Object>();
        
        EntityService service = Mockito.mock(EntityService.class);
        Mockito.when(service.create(Mockito.isA(EntityBody.class))).thenReturn("id");
        
        tokenManager = new TokenManager();
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("access_token.refresh_token.value", "Test-refresh-token-one");
        expirationDate = new Date(1329278755L);
        data.put("access_token.refresh_token.expiration", expirationDate);
        data.put("access_token.value", "Test-access-token");
        data.put("user_id", "user-one");
        data.put("user_realm", "test-realm");
        data.put("user_roles", Arrays.asList(new String[]{"Educator"}));
        
        Map<String, Object> refreshToken = new HashMap<String, Object>();
        refreshToken.put("expiration", expirationDate);
        refreshToken.put("value", "Test-refresh-token-one");
        
        Map<String, Object> accessToken = new HashMap<String, Object>();
        accessToken.put("refresh_token", refreshToken);
        data.put("access_token", accessToken);
        
        repo.create("authorizedSessions", data);
        
        RolesToRightsResolver rolesToRightsResolver = Mockito.mock(RolesToRightsResolver.class);
        educatorAuthorities = new HashSet<GrantedAuthority>();
        educatorAuthorities.add(Right.AGGREGATE_READ);
        educatorAuthorities.add(Right.READ_GENERAL);
        Mockito.when(rolesToRightsResolver.resolveRoles("test-realm", Arrays.asList(new String[]{"Educator"}))).thenReturn(educatorAuthorities);
        tokenManager.setRolesToRightsResolver(rolesToRightsResolver);
        tokenManager.setEntityRepository(repo);
        tokenManager.setService(service);
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
        assertEquals(token.getExpiration(), expirationDate);
        assertEquals(token.getValue(), "Test-refresh-token-one");
    }
    
    @Test
    public void testReadAuthenticationWithAccessToken() {
        OAuth2AccessToken token = new OAuth2AccessToken("Test-access-token");
        OAuth2Authentication auth = tokenManager.readAuthentication(token);
        ClientToken clientToken = auth.getClientAuthentication();
        assertEquals(clientToken.getClientId(), "user-one");
        assertEquals(clientToken.getAuthorities(), educatorAuthorities);
    }
    
    @Test
    public void testReadAuthenticationWithRefreshToken() {
        ExpiringOAuth2RefreshToken refreshToken = new ExpiringOAuth2RefreshToken("Test-refresh-token-one", expirationDate);
        OAuth2Authentication auth = tokenManager.readAuthentication(refreshToken);
        ClientToken clientToken = auth.getClientAuthentication();
        assertEquals(clientToken.getClientId(), "user-one");
        assertEquals(clientToken.getAuthorities(), educatorAuthorities);
    }

    
    @Test
    public void testReadAccessToken() {
        
    }
    
    @Test
    public void testRemoveAccessToken() {
        
    }

    @Test
    public void testStoreRefreshToken() {
        
    }

    @Test
    public void testRemoveRefreshToken() {
        
    }

    @Test
    public void testRemoveAccessTokenUsingRefreshToken() {
        
    }
 
    
    
}
