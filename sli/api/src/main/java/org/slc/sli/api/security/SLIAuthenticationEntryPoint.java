package org.slc.sli.api.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * Spring interceptor for calls that don't have a session
 * This implementation simply redirects to the login URL
 * 
 * @author dkornishev
 *
 */
public class SLIAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    private static final Logger LOG = LoggerFactory.getLogger(SLIAuthenticationEntryPoint.class);
    
    private String              authUrl;
   
    /**
     * Redirects user to login URL
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        LOG.info("Redirecting user to authentication url");
        response.sendRedirect(authUrl);
    }
    
    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }    
}
