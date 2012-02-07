package org.slc.sli.api.representation;

import org.slc.sli.api.security.SLIPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Handler for catching access denied exceptions.
 * 
 * @author shalka
 */
@Provider
@Component
public class AccessDeniedExceptionHandler implements ExceptionMapper<AccessDeniedException> {
    
	// remove after 403 errors' root cause is discovered
	private static final Logger LOG = LoggerFactory.getLogger(GenericExceptionHandler.class);
	// remove after 403 errors' root cause is discovered
	
	public Response toResponse(AccessDeniedException e) {
        Response.Status errorStatus = Response.Status.FORBIDDEN;
    
    	// remove after 403 errors' root cause is discovered
        LOG.debug("principal name and roles: {}, {}", 
        		( (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getName().toString(),
        		( (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRoles().toString());
        LOG.error("--- Access Denied Exception --- ");
        // remove after 403 errors' root cause is discovered
        
        return Response
                .status(errorStatus)
                .entity(new ErrorResponse(errorStatus.getStatusCode(), errorStatus.getReasonPhrase(), "Access DENIED: "
                        + e.getMessage())).build();
    }
}
