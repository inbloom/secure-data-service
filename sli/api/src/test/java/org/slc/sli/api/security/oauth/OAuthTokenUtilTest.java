package org.slc.sli.api.security.oauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.resolve.UserLocator;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.api.util.OAuthTokenUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.UnconfirmedAuthorizationCodeClientToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
*
* @author pwolf
*
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
   DirtiesContextTestExecutionListener.class })
@DirtiesContext
public class OAuthTokenUtilTest {
    
    @Autowired
    @InjectMocks
    OAuthTokenUtil util;

    @Autowired
    private Repository<Entity> repo;

    @Mock
    UserLocator locator;
    
    SLIPrincipal principal;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        List<String> roles = new ArrayList<String>();
        roles.add("FULL_ACCESS");
        principal = new SLIPrincipal();
        principal.setExternalId("demo");
        principal.setAdminRealm("adminRealm");
        principal.setId("testId");
        principal.setName("demo user");
        principal.setRealm("foo");
        principal.setRoles(roles);
        Mockito.when(locator.locate(Mockito.anyString(), Mockito.anyString())).thenReturn(principal);
    }
    
    @Test
    public void testTokenNotExpired() {
        long expiration = System.currentTimeMillis() + 9999;
        assertFalse((new Date(expiration)).toString() + " shouldn't be expired.", 
                OAuthTokenUtil.isTokenExpired(expiration));
    }
    
    @Test
    public void testTokenExpired() {
        long expiration = System.currentTimeMillis() - 9999;
        assertTrue((new Date(expiration)).toString() + " should be expired.", 
                OAuthTokenUtil.isTokenExpired(expiration));
    }
    
    @Test
    public void testAccessTokenSerialization() {
        String value = "ABCDEFG";
        String type = "bearer";
        Set<String> scopes = new HashSet<String>();
        scopes.add("TEST_SCOPE1");
        scopes.add("TEST_SCOPE2");
        Date expiration = new Date();
        
        OAuth2AccessToken original = new OAuth2AccessToken(value);
        original.setExpiration(expiration);
        original.setTokenType(type);
        original.setScope(scopes);
        
        EntityBody body = util.serializeAccessToken(original);
        DBObject obj = BasicDBObjectBuilder.start(body).get();
        
        Map data = (Map) JSON.parse(JSON.serialize(obj));
        OAuth2AccessToken reconst = util.deserializeAccessToken(data);
        assertEquals("checking value", value, reconst.getValue());
        assertEquals("checking type", type, reconst.getTokenType());
        assertEquals("checking expiration", expiration, reconst.getExpiration());
        assertTrue("checking scopes", reconst.getScope().containsAll(scopes));
    }
    

    @Test
    public void testRemoveExpiredTokens() throws Exception {
        for (int i = 0; i < 20; ++i) {
            repo.create("oauth_access_token", createAccessToken(true));
        }
        for (int i = 0; i < 10; ++i) {
            repo.create("oauth_access_token", createAccessToken(false));
        }
        //We have 30 tokens
        assert(repo.count("oauth_access_tokens", null) == 30);
        //Lets destroy them.


    }

    private EntityBody createAccessToken(boolean expired) {
        EntityBody body = new EntityBody();
        long time = new Date().getTime();
        if (expired) {
            time -= 1000;
        } else {
            time += 100000;
        }
        Map<String, Object> accessToken = new HashMap<String, Object>();
        accessToken.put("expires", time);
        body.put("accessToken", accessToken);
        return body;
    }

    @Test
    public void testOauth2Serialization() {
        ArrayList<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
        auths.add(Right.ADMIN_ACCESS);
        auths.add(Right.AGGREGATE_WRITE);
        
        HashSet<String> resIds = new HashSet<String>();
        resIds.add("foo");
        resIds.add("bar");
        
        HashSet<String> scopes = new HashSet<String>();
        scopes.add("one");
        scopes.add("two");
        
        String clientId = "ABC";
        String clientSecret = "EFG";
        
        ClientToken client = new ClientToken(clientId, resIds, clientSecret, scopes, auths);
        UnconfirmedAuthorizationCodeClientToken creds = new UnconfirmedAuthorizationCodeClientToken(clientId, 
                clientSecret, 
                scopes, 
                "state", 
                "https://redirect.com/callback");
        PreAuthenticatedAuthenticationToken user = new PreAuthenticatedAuthenticationToken(principal, creds);
        OAuth2Authentication auth = new OAuth2Authentication(client, user);
        
        EntityBody body = util.serializeOauth2Auth(auth);
        DBObject obj = BasicDBObjectBuilder.start(body).get();
        Map data = (Map) JSON.parse(JSON.serialize(obj));
        OAuth2Authentication reconst = util.createOAuth2Authentication(data);
        
        SLIPrincipal slip = (SLIPrincipal) reconst.getPrincipal();
        assertEquals(principal.getExternalId(), slip.getExternalId());
        assertEquals(principal.getId(), slip.getId());
        assertEquals(principal.getName(), slip.getName());
        assertEquals(principal.getRealm(), slip.getRealm());
        assertEquals(principal.getAdminRealm(), slip.getAdminRealm());
        assertTrue(principal.getRoles().containsAll(slip.getRoles()));
        
        assertTrue(reconst.getAuthorities().containsAll(auth.getAuthorities()));
        
        ClientToken reconstClient = reconst.getClientAuthentication();
        assertEquals(client.getClientId(), reconstClient.getClientId());
        assertEquals(client.getClientSecret(), reconstClient.getClientSecret());
        assertTrue(client.getResourceIds().containsAll(client.getResourceIds()));
        assertTrue(client.getScope().containsAll(client.getScope()));
        assertTrue(client.getAuthorities().containsAll(client.getAuthorities()));
        
        PreAuthenticatedAuthenticationToken reconstUser = (PreAuthenticatedAuthenticationToken) reconst.getUserAuthentication();
        assertEquals(user.getName(), reconst.getName());
        assertTrue(reconstUser.getPrincipal() instanceof SLIPrincipal);
        assertTrue(user.getAuthorities().containsAll(reconstUser.getAuthorities()));
        
        UnconfirmedAuthorizationCodeClientToken reconstCred = (UnconfirmedAuthorizationCodeClientToken) reconstUser.getCredentials();
        assertEquals(creds.getClientId(), reconstCred.getClientId());
        assertEquals(creds.getClientSecret(), reconstCred.getClientSecret());
        assertEquals(creds.getState(), reconstCred.getState());
        assertEquals(creds.getRequestedRedirect(), reconstCred.getRequestedRedirect());
    }

}
