package org.slc.sli.api.security;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.security.mock.Mockery;
import org.slc.sli.api.security.resolve.UserLocator;
import org.slc.sli.api.test.WebContextTestExecutionListener;

/**
 * 
 * @author dkornishev
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class UserLocatorTest {
    
    @Resource(name = "mockUserLocator")
    private UserLocator         locator;
    
    @Test
    public void testUserFound() {
        SLIPrincipal principal = this.locator.locate(Mockery.VALID_REALM, Mockery.VALID_USER_ID);
        
        Assert.assertNotNull(principal);
    }
    
    @Ignore
    @Test
    public void testuserNotFound() {
        SLIPrincipal principal = this.locator.locate(Mockery.VALID_REALM, Mockery.VALID_USER_ID);
        
        Assert.assertNull(principal);
    }
    
    @Ignore
    @Test
    public void testGarbageInput() {
        SLIPrincipal principal = this.locator.locate(null, null);
        
        Assert.assertNull(principal);
    }
}
