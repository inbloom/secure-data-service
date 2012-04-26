package org.slc.sli.util;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.slc.sli.security.SLIPrincipal;

/**
 * Class, which allows user to access security context
 *
 * @author svankina
 *
 */
public class SecurityUtil {

    private static final String ADMIN_KEY = "IT Administrator";

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
        if (user instanceof SLIPrincipal) {
            return ((SLIPrincipal) user).getId();
        }
        return user.getUsername();
    }

}
