package org.slc.sli.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.slc.sli.security.SLIPrincipal;


/**
 * Class, which allows user to access security context
 * @author svankina
 *
 */
public class SecurityUtil {

    public static UserDetails getPrincipal() {
        return  (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    
    public static String getToken() {
        UserDetails user = getPrincipal();
        System.out.println("******** User.getUsername: " + user.getUsername());
        if (user instanceof SLIPrincipal) {
            System.out.println("******** User.getId: " + ((SLIPrincipal) user).getId());
            return ((SLIPrincipal) user).getId();
        } else {
            // gets here in mock server mode
            return user.getUsername();
        }
    }
    
}
