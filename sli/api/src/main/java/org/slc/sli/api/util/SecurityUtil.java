package org.slc.sli.api.util;

import java.util.Arrays;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import org.slc.sli.api.security.enums.Right;

/**
 * Holder for security utilities
 * 
 * @author dkornishev
 * 
 */
public class SecurityUtil {
    
    private static final Authentication FULL_ACCESS_AUTH = new PreAuthenticatedAuthenticationToken("SYSTEM", "API", Arrays.asList(Right.FULL_ACCESS));
    
    public static void sudoRun(SecurityTask task) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        try {
            SecurityContextHolder.getContext().setAuthentication(FULL_ACCESS_AUTH);
            task.execute();
        } finally {
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    }
    
    /**
     * Callback for security-related tasks
     * 
     * @author dkornishev
     * 
     */
    public static interface SecurityTask {
        public void execute();
    }
}
