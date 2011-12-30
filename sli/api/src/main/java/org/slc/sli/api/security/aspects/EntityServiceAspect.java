package org.slc.sli.api.security.aspects;

import java.util.Collection;

import org.slc.sli.api.security.enums.DefaultRoles;
import org.slc.sli.api.security.enums.Rights;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Aspect for handling Entity Service operations.
 * 
 * @author shalka
 */

@Aspect
public class EntityServiceAspect {
    
    private static final Logger LOG = LoggerFactory.getLogger(EntityServiceAspect.class);
    
    @Around("call(* org.slc.sli.api.service.EntityService.*(..))")
    public Object controlAccess(ProceedingJoinPoint pjp) throws Throwable {
        Rights neededRight = null;
        String functionName = pjp.getSignature().getName();
        if (functionName.equals("get") || functionName.equals("exists") || functionName.equals("list")) {
            neededRight = Rights.READ_GENERAL;
        } else if (functionName.equals("create") || functionName.equals("update") || functionName.equals("delete")) {
            neededRight = Rights.WRITE_GENERAL;
        }
        
        LOG.debug("context: {}", SecurityContextHolder.getContext().getAuthentication().toString());
        LOG.debug("attempted access of {} function", pjp.getSignature());
        Collection<GrantedAuthority> myAuthorities = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities();
        LOG.debug("rights: {}", myAuthorities.toString());
        
        boolean hasAccess = false;
        for (GrantedAuthority auth : myAuthorities) {
            LOG.debug("checking rights for role: {}", auth.getAuthority());
            try {
                for (DefaultRoles role : DefaultRoles.values()) {
                    if (role.getSpringRoleName().equals(auth.getAuthority()) && role.hasRight(neededRight)) {
                        hasAccess = true;
                        LOG.debug("granting access to user for entity");
                        break;
                    }
                }
                if (hasAccess) {
                    break;
                }
            } catch (IllegalArgumentException ex) {
                LOG.debug("could not find role. skipping current entry...");
                continue;
            }
        }
        
        if (hasAccess) {
            LOG.debug("entering {} function [using Around]", pjp.getSignature());
            Object entityReturned = pjp.proceed();
            LOG.debug("exiting {} function [using Around]", pjp.getSignature());
            return entityReturned;
        } else {
            LOG.debug("user was denied access due to insufficient permissions.");
            throw new BadCredentialsException("User does not have authority to access entity.");
        }
    }
}
