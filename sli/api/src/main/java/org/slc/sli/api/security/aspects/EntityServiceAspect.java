package org.slc.sli.api.security.aspects;

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slc.sli.api.security.enums.DefaultRoles;
import org.slc.sli.api.security.enums.Rights;

/*
 * Aspects invoked when performing operations on EntityService.
 */

@Aspect
public class EntityServiceAspect {
    
    private static final Logger ASPECT_LOG = LoggerFactory.getLogger(EntityServiceAspect.class);
    
    /*
     * Permission enforcement on Read ('get', 'list', and 'exists') and Write ('create', 'update', 
     * and 'delete') operations.
     */
    @Around("call(* org.slc.sli.api.service.EntityService.*(..))")
    public Object logRead(ProceedingJoinPoint pjp) throws Throwable {
        Rights neededRight = Rights.READ_GENERAL;                           // Initialize default right
        String functionName = pjp.getSignature().getName();                 // Get invoked method name
        if (functionName == "get"             ||                            // If the method is 'get'
            functionName == "exists"          ||                            //               or 'exists'
            functionName == "list"           ) {                            //               or 'list'
            neededRight = Rights.READ_GENERAL;                              // - set needed right to READ_GENERAL
        } else if (functionName == "create"   ||                            // Otherwise, if the method is 'create'
            functionName == "update"          ||                            //                          or 'update'
            functionName == "delete"         ) {                            //                          or 'delete'
            neededRight = Rights.WRITE_GENERAL;                             // - set needed right to WRITE_GENERAL
        }        
        // add an else here to set the Rights to be as restrictive as possible
        // -> better case would be to add a Right that denotes failed to find a Right (no one has the Right so it always fails)
        
        ASPECT_LOG.debug("attempted access of {} function", pjp.getSignature());        // debug message        
        Collection<GrantedAuthority> myAuthorities                                      // Initialize granted authority collection
            = SecurityContextHolder.getContext().getAuthentication().getAuthorities();  //  using security context holder
        ASPECT_LOG.debug("rights: {}", myAuthorities.toString());                       // debug message

        boolean hasAccess = false;                                                      // Deny access to entity by default
        for (GrantedAuthority auth : myAuthorities) {                                   // Iterate over granted authorities
            ASPECT_LOG.debug("checking rights for role: {}", auth.getAuthority());      // - output authority signature
            try {                                                                       // Try to map the authority to a default role
                for(DefaultRoles role : DefaultRoles.values()) {                        // Iterate over Default Roles
                    if(role.getSpringRoleName().equals(auth.getAuthority()) &&          // - If the Spring Role Name maps to the current role
                       role.hasRight(neededRight)) {                                    //   and the role has required right
                        hasAccess = true;                                               // - grant access
                        ASPECT_LOG.debug("granting access to user for entity");         // - debug message
                        break;                                                          // - break inner for loop
                    }
                }
                if (hasAccess) {                                                        // If hasAccess is now true
                    break;                                                              // - break outer for loop
                }
            } catch (IllegalArgumentException ex) {                                     // Catch IllegalArgumentException (no matching role)
                ASPECT_LOG.debug("could not find role. skipping current entry...");     // - log skipping
                continue;                                                               // - continue to next for loop entry
            }
        }
        
        if (hasAccess) {                                                                        // If incoming role has access
            ASPECT_LOG.debug("[iii] entering {} function [using Around]", pjp.getSignature());  // - debug message
            Object entityReturned = pjp.proceed();                                              // - proceed into invoked method
            ASPECT_LOG.debug("[iii] exiting {} function [using Around]", pjp.getSignature());   // - debug message  
            return entityReturned;                                                              // - return object returned by invoked method
        } else {                                                                                // Otherwise (permission is not granted)
            ASPECT_LOG.debug("user was denied access due to insufficient permissions.");        // - log reason for exception
            throw new BadCredentialsException("User does not have authority to access entity."); // - throw access denied exception
        }
    }
}
