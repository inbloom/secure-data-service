package org.slc.sli.util;

import java.util.Collection;

import org.slc.sli.security.SLIPrincipal;
import org.springframework.security.core.Authentication;
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

    public static boolean isAdmin() {
        Collection<GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        for (GrantedAuthority authority : authorities) {
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
        if (user instanceof SLIPrincipal) {
            return ((SLIPrincipal) user).getId();
        }
        return user.getUsername();
    }

}
