package org.slc.sli.util;

import java.util.Collection;

import org.slc.sli.security.SLIPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Class, which allows user to access security context
 * 
 * @author svankina
 * 
 */
public class SecurityUtil {
    
    private static final String ADMIN_KEY = "IT Administrator";
    private static Logger logger = LoggerFactory.getLogger(SecurityUtil.class);
    
    public static UserDetails getPrincipal() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    
    public static boolean isAdmin() {
        Collection<GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        for (GrantedAuthority authority: authorities) {
            if (authority.getAuthority().equals(ADMIN_KEY)) {
                return true;
            }
                
        }
        return false;
    }
    
    public static String getUsername() {
        return getPrincipal().getUsername();
    }
    
    public static String getToken() {
        UserDetails user = getPrincipal();
        logger.info("******** User.getUsername: {}", user.getUsername());
        if (user instanceof SLIPrincipal) {
            logger.info("******** User.getId: {}", ((SLIPrincipal) user).getId());
            return ((SLIPrincipal) user).getId();
        } else {
            // gets here in mock server mode
            return user.getUsername();
        }
    }
    
}
