package org.slc.sli.api.util;

import java.util.Arrays;
import java.util.HashMap;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.enums.Right;
import org.slc.sli.domain.MongoEntity;

/**
 * Holder for security utilities
 * 
 * @author dkornishev
 * 
 */
public class SecurityUtil {
    
    private static final Authentication        FULL_ACCESS_AUTH;
    
    private static ThreadLocal<Authentication> cachedAuth = new ThreadLocal<Authentication>();
    
    static {
        SLIPrincipal system = new SLIPrincipal("SYSTEM");
        system.setEntity(new MongoEntity("system_entity", new HashMap<String, Object>()));
        
        FULL_ACCESS_AUTH = new PreAuthenticatedAuthenticationToken(system, "API", Arrays.asList(Right.FULL_ACCESS));
    }
    
    public static <T> T sudoRun(SecurityTask<T> task) {
        T toReturn = null;
        
        cachedAuth.set(SecurityContextHolder.getContext().getAuthentication());
        
        try {
            SecurityContextHolder.getContext().setAuthentication(FULL_ACCESS_AUTH);
            toReturn = task.execute();
        } finally {
            SecurityContextHolder.getContext().setAuthentication(cachedAuth.get());
        }
        
        return toReturn;
    }
    
    /**
     * Callback for security-related tasks
     * 
     * @author dkornishev
     * 
     */
    public static interface SecurityTask<T> {
        public T execute();
    }
}
