package org.slc.sli.api.security;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

/**
 * A security filter responsible for checking SLI session
 * 
 * @author dkornishev
 * 
 */
public class SliRequestFilter extends GenericFilterBean {
    
    private static final Logger   LOG                 = LoggerFactory.getLogger(SliRequestFilter.class);
    
    private static final String   PARAM_SESSION       = "sessionId";
    private static final String   HEADER_SESSION_NAME = "sessionId";
    
    private SecurityTokenResolver resolver;
    
    /**
     * Intercepter method called by spring
     * Checks cookies to see if SLI session id exists
     * If session does exist, resolution will be attempted
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        
        String sessionId = null;
        
        @SuppressWarnings("unchecked")
        Enumeration<String> e = req.getHeaderNames();
        
        while (e.hasMoreElements()) {
            String header = e.nextElement();
            String headerValue = req.getHeader(header);
            
            LOG.debug("[H]" + header + "->" + headerValue);
            
            if (HEADER_SESSION_NAME.equalsIgnoreCase(header)) {
                sessionId = headerValue;
                break;
            }
            
        }
        
        if (req.getParameter(PARAM_SESSION) != null) {
            sessionId = req.getParameter(PARAM_SESSION);
        }
        
        if (sessionId != null) {
            SecurityContextHolder.getContext().setAuthentication(resolver.resolve(sessionId));
        }
        
        chain.doFilter(request, response);
    }
    
    public void setResolver(SecurityTokenResolver resolver) {
        this.resolver = resolver;
    }
}
