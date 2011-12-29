package org.slc.sli.api.security.aspects;

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slc.sli.api.security.enums.DefaultRoles;
import org.slc.sli.api.security.enums.Rights;

/*
 * Aspects invoked when performing operations on EntityService.
 */

@Aspect
public class EntityServiceAspect {
    
    private static final Logger ASPECT_LOG = LoggerFactory.getLogger(EntityServiceAspect.class);

    /**
     * 
     * @param pjp
     * @return
     * @throws Throwable
     */
    //@Around("call(* org.slc.sli.api.service.EntityService.*(..))")
    public Object controlAccess(ProceedingJoinPoint pjp) throws Throwable {
        Rights neededRight = Rights.READ_GENERAL;
        String functionName = pjp.getSignature().getName();
        if (functionName == "get" || functionName == "exists" || functionName == "list") {
            neededRight = Rights.READ_GENERAL;
        } else if (functionName == "create" || functionName == "update" || functionName == "delete") {
            neededRight = Rights.WRITE_GENERAL;
        }        
        // add an else here to set the Rights to be as restrictive as possible
        // -> better case would be to add a Right that denotes failed to find a Right (no one has the Right so it always fails)
        
        ASPECT_LOG.debug("attempted access of {} function", pjp.getSignature());      
        Collection<GrantedAuthority> myAuthorities
            = SecurityContextHolder.getContext().getAuthentication().getAuthorities(); 
        ASPECT_LOG.debug("rights: {}", myAuthorities.toString());

        boolean hasAccess = false;
        for (GrantedAuthority auth : myAuthorities) { 
            ASPECT_LOG.debug("checking rights for role: {}", auth.getAuthority());
            try { 
                for (DefaultRoles role : DefaultRoles.values()) { 
                    if (role.getSpringRoleName().equals(auth.getAuthority()) && role.hasRight(neededRight)) {
                        hasAccess = true;
                        ASPECT_LOG.debug("granting access to user for entity");
                        break; 
                    }
                }
                if (hasAccess) { 
                    break;
                }
            } catch (IllegalArgumentException ex) { 
                ASPECT_LOG.debug("could not find role. skipping current entry..."); 
                continue; 
            }
        }
        
        if (hasAccess) { 
            ASPECT_LOG.debug("[iii] entering {} function [using Around]", pjp.getSignature()); 
            Object entityReturned = pjp.proceed();
            ASPECT_LOG.debug("[iii] exiting {} function [using Around]", pjp.getSignature()); 
            return entityReturned; 
        } else { 
            ASPECT_LOG.debug("user was denied access due to insufficient permissions."); 
            throw new BadCredentialsException("User does not have authority to access entity."); 
        }
    }
}
