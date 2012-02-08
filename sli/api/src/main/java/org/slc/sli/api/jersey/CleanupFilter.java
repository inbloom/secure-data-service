package org.slc.sli.api.jersey;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Performs cleanup after every call
 * Namely, it clears context holder
 * 
 * @author dkornishev
 * 
 */
public class CleanupFilter implements ContainerResponseFilter {
    
    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        SecurityContextHolder.clearContext();
        return response;
    }
    
}
