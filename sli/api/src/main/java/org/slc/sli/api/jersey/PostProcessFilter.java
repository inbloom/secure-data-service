package org.slc.sli.api.jersey;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

import org.slc.sli.api.security.context.traversal.cache.SecurityCachingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Post Processing filter
 * Outputs time elapsed for request
 * Cleans up security context
 * 
 * @author dkornishev
 * 
 */
public class PostProcessFilter implements ContainerResponseFilter {
    
    private static final int LONG_REQUEST = 1000;

    @Autowired
    private SecurityCachingStrategy securityCachingStrategy;
    
    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        SecurityContextHolder.clearContext();
        printElapsed(request);
        expireCache();

        return response;
    }

    private void printElapsed(ContainerRequest request) {
        long startTime = (Long) request.getProperties().get("startTime");
        long elapsed = System.currentTimeMillis() - startTime;
        
        info("{} finished in {} ms", request.getRequestUri().toString(), elapsed);
        
        if (elapsed > LONG_REQUEST) {
            warn("Long request: {} elapsed {}ms > {}ms", request.getRequestUri().toString(), elapsed, LONG_REQUEST);
        }
    }

    private void expireCache() {
        if (securityCachingStrategy != null) {
            securityCachingStrategy.expire();
        }
    }
    
}
