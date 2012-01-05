package org.slc.sli.api.security;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.security.enums.Right;
import org.slc.sli.api.security.mock.Mocker;
import org.slc.sli.api.test.WebContextTestExecutionListener;

/**
 * Unit tests for the OpenamRestTokenResolver.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class OpenamRestTokenResolverTest {
    
    private SecurityTokenResolver resolver;
    
    @Before
    public void init() {
        resolver = Mocker.getMockedOpenamResolver();
    }
    
    @Test
    public void testResolveSuccess() {
        Authentication auth = resolver.resolve(Mocker.VALID_TOKEN);
        Assert.assertNotNull(auth);
        Assert.assertTrue(auth.getAuthorities().contains(Right.READ_GENERAL));
    }
    
    @Test
    public void testResolveFailure() {
        Authentication auth = resolver.resolve(Mocker.INVALID_TOKEN);
        Assert.assertNull(auth);
    }
}
