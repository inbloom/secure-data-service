package org.slc.sli.api.representation;

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
        LOG.error("--- Access Denied Exception ---: ", e);
        LOG.debug("security context: {}", SecurityContextHolder.getContext().toString());
        LOG.debug("authentication: {}", SecurityContextHolder.getContext().getAuthentication().toString());
        LOG.debug("principal: {}", SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        LOG.debug("stack trace: {}", e.getStackTrace().toString());
        LOG.debug("--- this will be removed once 403 errors are figured out ---");
    	// remove after 403 errors' root cause is discovered
        
        return Response
                .status(errorStatus)
                .entity(new ErrorResponse(errorStatus.getStatusCode(), errorStatus.getReasonPhrase(), "Access DENIED: "
                        + e.getMessage())).build();
    }
}
