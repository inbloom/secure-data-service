package org.slc.sli.api.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

/**
 * Facade that simplifies obtaining application details from the security context
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@Component
public class CallingApplicationInfoProvider {
    
    /**
     * Returns the application clientId of the current request if authentication was performed using
     * oauth. Otherwise returns null.
     */
    public String getClientId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof OAuth2Authentication)) {
            return null;
        }
        return ((OAuth2Authentication) auth).getClientAuthentication().getClientId();
    }
}
