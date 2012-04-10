package org.slc.sli.api.jersey;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.Oauth2Helper;

/**
 * Authentication filter
 * 
 * @author dkornishev
 * 
 */
@Component
public class AuthenticationFilter implements ContainerRequestFilter {
    
    @Autowired
    private Oauth2Helper helper;
    
    @Override
    public ContainerRequest filter(ContainerRequest request) {
        OAuth2Authentication auth = helper.getAuthentication(request.getHeaderValue("Authorization"));
        SecurityContextHolder.getContext().setAuthentication(auth);
        return request;
    }
}
