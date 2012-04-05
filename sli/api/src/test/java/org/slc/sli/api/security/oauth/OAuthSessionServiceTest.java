package org.slc.sli.api.security.oauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.ContextResolverStore;
import org.slc.sli.api.security.context.resolver.EntityContextResolver;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.api.util.OAuthTokenUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedClientException;
import org.springframework.security.oauth2.provider.ClientToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
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
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@DirtiesContext
public class OAuthSessionServiceTest {
    
    @Autowired
    @InjectMocks
    OAuthSessionService service;
    
    @Mock
    Repository<Entity> repo;
    
    @Mock
    OAuthTokenUtil util;
    
    @Mock
    ContextResolverStore store;
    
    @Mock
    EntityContextResolver resolver;
    
    @Mock
    ApplicationAuthorizationValidator validator;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        //Configure application repo to resolve the app
        Entity appEntity = new MongoEntity("application", "appId", new HashMap<String, Object>(), new HashMap<String, Object>());
        Mockito.when(repo.findOne(Mockito.eq("application"), Mockito.any(NeutralQuery.class))).thenReturn(appEntity);
    }
        
    @Test
    public void testExpired() {
        OAuth2AccessToken oldAccessToken = new OAuth2AccessToken("blah");
        oldAccessToken.setExpiration(new Date(System.currentTimeMillis() - 60000));
        Mockito.when(repo.findOne(Mockito.anyString(), Mockito.any(NeutralQuery.class))).thenReturn(new MongoEntity("blah", new HashMap<String, Object>()));
        Mockito.when(util.createOAuth2Authentication(Mockito.anyMap())).thenReturn(
                new OAuth2Authentication(new ClientToken("blah", "blah", new HashSet<String>()), 
                        new PreAuthenticatedAuthenticationToken("blah", "blah")));
        Mockito.when(util.deserializeAccessToken(Mockito.anyMap())).thenReturn(oldAccessToken);
        OAuth2Authentication auth = service.loadAuthentication("blah");
        
        //For an expired or non-existent token, we should get an AnonymousAuthenticationToken
        assertEquals(AnonymousAuthenticationToken.class, auth.getUserAuthentication().getClass());
    }
    
    @Test
    public void testNoToken() {
        OAuth2Authentication auth = service.loadAuthentication("blah");
        
        //For an expired or non-existent token, we should get an AnonymousAuthenticationToken
        assertEquals(AnonymousAuthenticationToken.class, auth.getUserAuthentication().getClass());
    }
    
    @Test
    public void testNotExpired() {
        OAuth2AccessToken newAccessToken = new OAuth2AccessToken("blah");
        newAccessToken.setExpiration(new Date(System.currentTimeMillis() + 60000));
        Mockito.when(repo.findOne(Mockito.anyString(), Mockito.any(NeutralQuery.class))).thenReturn(new MongoEntity("blah", new HashMap<String, Object>()));
        Mockito.when(util.createOAuth2Authentication(Mockito.anyMap())).thenReturn(
                new OAuth2Authentication(new ClientToken("blah", "blah", new HashSet<String>()), new PreAuthenticatedAuthenticationToken("blah", "blah")));
        Mockito.when(util.deserializeAccessToken(Mockito.anyMap())).thenReturn(newAccessToken);
        OAuth2Authentication value = service.loadAuthentication("blah");
        assertNotNull(value);
    }
    
    @Test
    public void testAppAuthorizationNoEntity() {
        //If no entity exists on the SLIPrincipal, don't bother checking
        SLIPrincipal principal = new SLIPrincipal();
        OAuth2Authentication auth =  new OAuth2Authentication(new ClientToken("blah", "blah", new HashSet<String>()), 
                new PreAuthenticatedAuthenticationToken(principal, "blah"));
        Mockito.when(validator.getAuthorizedApps(principal)).thenReturn(null);
        try {
            service.validateAppAuthorization(auth);
            Assert.fail();
        } catch (UnauthorizedClientException e) {
            Assert.assertTrue(e instanceof UnauthorizedClientException);
        }
    }
    
    @Test
    public void testAppAuthorizationAllowed() {
                
        //Create an auth token to use
        SLIPrincipal principal = new SLIPrincipal();
        principal.setEntity(new MongoEntity("teacher", "teacherUniqueId", new HashMap<String, Object>(), new HashMap<String, Object>()));
        OAuth2Authentication auth =  new OAuth2Authentication(new ClientToken("clientId", "blah", new HashSet<String>()), 
                new PreAuthenticatedAuthenticationToken(principal, "blah"));
       
        ArrayList<String> appList = new ArrayList<String>();
        appList.add("appId"); 
        Mockito.when(validator.getAuthorizedApps(principal)).thenReturn(appList);
        
        service.validateAppAuthorization(auth);
    }

    @Test(expected = UnauthorizedClientException.class)
    public void testAppAuthorizationNotAllowed() {
                
        //Create an auth token to use
        SLIPrincipal principal = new SLIPrincipal();
        principal.setEntity(new MongoEntity("teacher", "teacherUniqueId", new HashMap<String, Object>(), new HashMap<String, Object>()));
        OAuth2Authentication auth =  new OAuth2Authentication(new ClientToken("clientId", "blah", new HashSet<String>()), 
                new PreAuthenticatedAuthenticationToken(principal, "blah"));
                
        ArrayList<String> appList = new ArrayList<String>();
        appList.add("someOtherId");
        
        Mockito.when(validator.getAuthorizedApps(principal)).thenReturn(appList);
        
        service.validateAppAuthorization(auth);
    }

}
