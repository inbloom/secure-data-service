package org.slc.sli.api.security.oauth;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;

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
    
    @Before
    public void setUp() {
        tokenManager = new TokenManager();
        EntityRepository repo = mock(EntityRepository.class);
        
        List<Entity> list = new ArrayList<Entity>();
        Mockito.when(repo.findByQuery("authorizedSessions", 
                new Query(Criteria.where("body.access_token.refresh_token.value").is("Test-refresh-token-one")), 0, 1)).thenReturn(list);
        
        tokenManager.setEntityRepository(repo);
        Map<String, Object> token1 = new HashMap<String, Object>();
        Map<String, Object> refreshToken1 = new HashMap<String, Object>();
        Map<String, Object> container1 = new HashMap<String, Object>();
        refreshToken1.put("value", "Test-refresh-token-one");
        refreshToken1.put("expiration", new Date());

        token1.put("refresh_token", refreshToken1);
        container1.put("access_token", token1);
        

    }
    
    @Test
    public void testStoreAccessToken() {
        
    }
    
    @Test
    public void testReadRefreshToken() {
        ExpiringOAuth2RefreshToken token = tokenManager.readRefreshToken("Test-refresh-token-one");
        System.out.println("The token is " + token);
    }
}
