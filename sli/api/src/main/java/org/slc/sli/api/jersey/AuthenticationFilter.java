package org.slc.sli.api.jersey;

import org.slc.sli.api.security.OauthMongoSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

/**
 * Authentication filter
 * 
 * @author dkornishev
 * 
 */
@Component
public class AuthenticationFilter implements ContainerRequestFilter {
        
    @Autowired
    private OauthMongoSessionManager manager;
    
    @Override
    public ContainerRequest filter(ContainerRequest request) {
        OAuth2Authentication auth = manager.getAuthentication(request.getHeaderValue("Authorization"));
        SecurityContextHolder.getContext().setAuthentication(auth);
        return request;
    }
}
