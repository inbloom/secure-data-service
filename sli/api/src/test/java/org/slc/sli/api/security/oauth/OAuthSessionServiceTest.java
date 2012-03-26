package org.slc.sli.api.security.oauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.api.util.OAuthTokenUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
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
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
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
    

}
