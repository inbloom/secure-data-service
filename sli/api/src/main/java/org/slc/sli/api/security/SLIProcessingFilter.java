package org.slc.sli.api.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class SLIProcessingFilter extends GenericFilterBean {
    
    private static final Logger   LOG         = LoggerFactory.getLogger(SLIAuthenticationEntryPoint.class);
    
    private static final String   COOKIE_NAME = "sliSessionId";
    
    private SecurityTokenResolver resolver;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        LOG.info("[CUSTOM FILTER] I've been called, yay me!");
        
        HttpServletRequest req = (HttpServletRequest) request;
        
        String sessionId = null;
        
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                LOG.debug((c.getName() + "->" + c.getValue()));
                
                if (COOKIE_NAME.equals(c.getName())) {
                    sessionId = c.getValue();
                    break;
                }
                
            }
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
