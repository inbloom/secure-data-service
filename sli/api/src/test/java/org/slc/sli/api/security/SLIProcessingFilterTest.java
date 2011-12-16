package org.slc.sli.api.security;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.slc.sli.api.security.mock.Mocker;

public class SLIProcessingFilterTest {
    
    private SLIProcessingFilter     filter;
    
    private MockHttpServletRequest  request;
    private MockHttpServletResponse response;
    private MockFilterChain         chain;
    
    @Before
    public void init() {
        filter = new SLIProcessingFilter();
        filter.setResolver(Mocker.getMockedOpenamResolver());
        
        this.request = new MockHttpServletRequest();
        this.response = new MockHttpServletResponse();
        this.chain = new MockFilterChain();
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
