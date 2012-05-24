package org.slc.sli.util;

import java.util.Collection;

import org.slc.sli.security.SLIPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Class, which allows user to access security context
 * 
 * @author svankina
 * 
 */
public class SecurityUtil {
    
    public static UserDetails getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication != null) && (authentication.getPrincipal() instanceof UserDetails)) {
            return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } else {
            // Allow exception handling to cover authentication issues
            SLIPrincipal principal = new SLIPrincipal();
            principal.setName("");
            principal.setId("");
            return principal;
        }
    }
    
    /**
     * find if a user is IT Administrator or Leader
     * 
     * @return
     */
    public static boolean isNotEducator() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            Authentication authentication = context.getAuthentication();
            if (authentication != null) {
                Collection<GrantedAuthority> authorities = authentication.getAuthorities();
                for (GrantedAuthority authority : authorities) {
                    if (authority.getAuthority().equals(Constants.ROLE_IT_ADMINISTRATOR)) {
                        return true;
                    } else if (authority.getAuthority().equals(Constants.ROLE_LEADER)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean isAdmin() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            Authentication authentication = context.getAuthentication();
            if (authentication != null) {
                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                for (GrantedAuthority authority : authorities) {
                    if (authority.getAuthority().equals(Constants.ROLE_IT_ADMINISTRATOR)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static String getUsername() {
        return getPrincipal().getUsername();
    }
    
    public static String getToken() {
        UserDetails user = getPrincipal();
        if (user instanceof SLIPrincipal) {
            return ((SLIPrincipal) user).getId();
        }
        return user.getUsername();
    }
    
}
