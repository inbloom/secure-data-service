package org.slc.sli.api.security;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.security.mock.Mocker;
import org.slc.sli.api.test.WebContextTestExecutionListener;

/**
 * Tests functioning of the Authentication filter used to authenticate users
 * 
 * @author dkornishev
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class SLIProcessingFilterTest {
    
    private SliRequestFilter        filter;
    
    private MockHttpServletRequest  request;
    private MockHttpServletResponse response;
    private MockFilterChain         chain;
    
    @Before
    public void init() {
        filter = new SliRequestFilter();
        filter.setResolver(Mocker.getMockedOpenamResolver());
        filter.setRealmSelectionUrl("Valhala");
        
        this.request = new MockHttpServletRequest();
        this.response = new MockHttpServletResponse();
        this.chain = new MockFilterChain();
    }
    
    @After
    public void teardown() {
        SecurityContextHolder.clearContext();
    }
    
    @Test
    public void testSessionInParam() throws Exception {
        
        this.request.setParameter("sessionId", Mocker.VALID_TOKEN);
        this.filter.doFilter(request, response, chain);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        Assert.assertNotNull("Authentication can't be null", auth);
    }
    
    @Test
    public void testSessionInParamFail() throws Exception {
        
        this.request.setParameter("sessionId", Mocker.INVALID_TOKEN);
        this.filter.doFilter(request, response, chain);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        Assert.assertNull("Authentication must be null", auth);
    }
}
