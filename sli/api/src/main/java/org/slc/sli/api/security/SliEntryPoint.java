package org.slc.sli.api.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Spring intercepter for calls that don't have a session
 * This implementation just tells the user he/she needs a session first
 * 
 * @author dkornishev
 * 
 */
@Component
public class SliEntryPoint implements AuthenticationEntryPoint {
    
    private static final Logger LOG = LoggerFactory.getLogger(SliEntryPoint.class);
    
    @Autowired
    @Value("${security.noSession.landing.url}")
    private String realmSelectionUrl;
    
    /**
     * Redirects user to login URL
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        LOG.warn("Unauthorized access");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("WWW-Authenticate", this.realmSelectionUrl);
    }
    
}
