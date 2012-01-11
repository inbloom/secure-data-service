package org.slc.sli.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


/**
 * Class, which allows user to access security context
 * @author svankina
 *
 */
public class SecurityUtil {

    public static UserDetails getPrincipal() {
        return  (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    
}
