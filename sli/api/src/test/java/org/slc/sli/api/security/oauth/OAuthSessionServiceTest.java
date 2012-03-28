package org.slc.sli.api.security.oauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.api.config.EntityNames;
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
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        //set up the resolver store to resolve a couple of edorgs
        List<String> edOrgIds = new ArrayList<String>();
        edOrgIds.add("district1");
        edOrgIds.add("school1");
        Mockito.when(resolver.findAccessible(Mockito.any(Entity.class))).thenReturn(edOrgIds);
        Mockito.when(store.findResolver(EntityNames.TEACHER, EntityNames.EDUCATION_ORGANIZATION)).thenReturn(resolver);
        
        //Set up the LEA
        HashMap body = new HashMap();
        List<String> categories = new ArrayList<String>();
        categories.add("Local Education Agency");
        Entity district1 = new MongoEntity("educationOrganization", "district1", body, new HashMap<String, Object>());
        district1.getBody().put("organizationCategories", categories);
        Mockito.when(repo.findById(EntityNames.EDUCATION_ORGANIZATION, "district1")).thenReturn(district1);
        
        //Set up a school
        body = new HashMap();
        categories = new ArrayList<String>();
        categories.add("School");
        Entity school1 = new MongoEntity("educationOrganization", "school1", body, new HashMap<String, Object>());
        school1.getBody().put("organizationCategories", categories);
        Mockito.when(repo.findById(EntityNames.EDUCATION_ORGANIZATION, "school1")).thenReturn(school1);
        
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
        OAuth2Authentication auth =  new OAuth2Authentication(new ClientToken("blah", "blah", new HashSet<String>()), 
                new PreAuthenticatedAuthenticationToken(new SLIPrincipal(), "blah"));
        service.validateAppAuthorization(auth);
    }
    
    @Test
    public void testAppAuthorizationNoAppAuth() {
                
        //Create an auth token to use
        SLIPrincipal principal = new SLIPrincipal();
        principal.setEntity(new MongoEntity("teacher", "teacherUniqueId", new HashMap<String, Object>(), new HashMap<String, Object>()));
        OAuth2Authentication auth =  new OAuth2Authentication(new ClientToken("clientId", "blah", new HashSet<String>()), 
                new PreAuthenticatedAuthenticationToken(principal, "blah"));
        
        //The store won't contain a list of registered applications for the LEA, so it will bypass auth checking
        service.validateAppAuthorization(auth);
    }
    
    @Test
    public void testAppAuthorizationAllowed() {
                
        //Create an auth token to use
        SLIPrincipal principal = new SLIPrincipal();
        principal.setEntity(new MongoEntity("teacher", "teacherUniqueId", new HashMap<String, Object>(), new HashMap<String, Object>()));
        OAuth2Authentication auth =  new OAuth2Authentication(new ClientToken("clientId", "blah", new HashSet<String>()), 
                new PreAuthenticatedAuthenticationToken(principal, "blah"));
        
        //Register an app list with district1 containing the requested app
        Entity appAuthEnt = new MongoEntity("applicationAuthorization", new HashMap<String, Object>());
        appAuthEnt.getBody().put("authId", "district1");
        appAuthEnt.getBody().put("authType", "EDUCATION_ORGANIZATION");
        List<String> allowedApps = new ArrayList<String>();
        allowedApps.add("appId");
        appAuthEnt.getBody().put("appIds", allowedApps);
        Mockito.when(repo.findOne(Mockito.eq("applicationAuthorization"), Mockito.any(NeutralQuery.class))).thenReturn(appAuthEnt);
        
        service.validateAppAuthorization(auth);
    }

    @Test(expected = UnauthorizedClientException.class)
    public void testAppAuthorizationNotAllowed() {
                
        //Create an auth token to use
        SLIPrincipal principal = new SLIPrincipal();
        principal.setEntity(new MongoEntity("teacher", "teacherUniqueId", new HashMap<String, Object>(), new HashMap<String, Object>()));
        OAuth2Authentication auth =  new OAuth2Authentication(new ClientToken("clientId", "blah", new HashSet<String>()), 
                new PreAuthenticatedAuthenticationToken(principal, "blah"));
        
        //Register an app list with district1 that doesn't contain the request app
        Entity appAuthEnt = new MongoEntity("applicationAuthorization", new HashMap<String, Object>());
        appAuthEnt.getBody().put("authId", "district1");
        appAuthEnt.getBody().put("authType", "EDUCATION_ORGANIZATION");
        List<String> allowedApps = new ArrayList<String>();
        allowedApps.add("someOtherAppId");
        appAuthEnt.getBody().put("appIds", allowedApps);
        Mockito.when(repo.findOne(Mockito.eq("applicationAuthorization"), Mockito.any(NeutralQuery.class))).thenReturn(appAuthEnt);
        
        service.validateAppAuthorization(auth);
    }

}
