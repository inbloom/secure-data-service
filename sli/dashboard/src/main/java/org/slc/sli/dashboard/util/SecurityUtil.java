/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.dashboard.util;

import java.util.Collection;

import org.slc.sli.dashboard.security.SLIPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Utility that allows user to access security context
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
                    if  (authority.getAuthority().equals(Constants.ROLE_EDUCATOR)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean isAdmin() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            Authentication authentication = context.getAuthentication();
            if (authentication != null) {
                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                if (authorities != null) {
                    for (GrantedAuthority authority : authorities) {
                        if (authority != null) {
                            String authorityString = authority.getAuthority();
                            
                            if (Constants.ROLE_IT_ADMINISTRATOR.equals(authorityString)) {
                                return true;
                            }
                        }
                        
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
