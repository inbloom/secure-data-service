package org.slc.sli.api.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

/**
 * A security filter responsible for checking SLI session
 * 
 * @author dkornishev
 */
@Component
public class SliRequestFilter extends GenericFilterBean {
    
    private static final Logger   LOG                 = LoggerFactory.getLogger(SliRequestFilter.class);
    
    private static final String   PARAM_SESSION       = "sessionId";
    private static final String   HEADER_SESSION_NAME = "sessionId";
    
    @Autowired
    private SecurityTokenResolver resolver;
    
    /**
     * Intercepter method called by spring
     * Checks cookies to see if SLI session id exists
     * If session does exist, resolution will be attempted
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest http = (HttpServletRequest) request;
        String sessionId = getSessionIdFromRequest(http);
        
        LOG.debug("Request URL: " + http.getRequestURL() + (http.getQueryString() == null ? "" : http.getQueryString()));
        
        Authentication auth = resolver.resolve(sessionId);
        if (auth != null) {
            LOG.debug("Created Auth Hash: {}@{}", auth.getClass(), Integer.toHexString(auth.hashCode()));
            
        }
        
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        chain.doFilter(request, response);
    }
    
    private String getSessionIdFromRequest(HttpServletRequest req) {
        
        String sessionId = req.getParameter(PARAM_SESSION);
        
        // Allow for sessionId to come in both request or header
        if (sessionId == null) {
            sessionId = req.getHeader(HEADER_SESSION_NAME);
        }
        
        LOG.debug("Session Id: " + sessionId);
        
        return sessionId;
    }
    
    public void setResolver(SecurityTokenResolver resolver) {
        this.resolver = resolver;
    }
}
