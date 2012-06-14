package org.slc.sli.api.jersey;

import java.util.ArrayList;
import java.util.List;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

import org.slc.sli.api.validation.URLValidator;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.OauthSessionManager;

import javax.annotation.Resource;

/**
 * Pre-request processing filter.  
 * Adds security information for the user
 * Records start time of the request
 * 
 * @author dkornishev
 * 
 */
@Component
public class PreProcessFilter implements ContainerRequestFilter {

    @Resource(name = "urlValidators")
    private List<URLValidator> urlValidators;
        
    @Autowired
    private OauthSessionManager manager;

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        recordStartTime(request);
        validate(request);
        populateSecurityContext(request);

        return request;
    }

    private void populateSecurityContext(ContainerRequest request) {
        OAuth2Authentication auth = manager.getAuthentication(request.getHeaderValue("Authorization"));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private void recordStartTime(ContainerRequest request) {
        request.getProperties().put("startTime", System.currentTimeMillis());
    }

    /**
     * Validate the request url
     * @param request
     */
    private void validate(ContainerRequest request) {

        for (URLValidator validator : urlValidators) {
            if (!validator.validate(request.getRequestUri())) {
                List<ValidationError> errors = new ArrayList<ValidationError>();
                errors.add(0, new ValidationError(ValidationError.ErrorType.INVALID_VALUE, "URL", request.getRequestUri().toString(), null));
                throw new EntityValidationException("", "", errors);
            }
        }
    }


}
