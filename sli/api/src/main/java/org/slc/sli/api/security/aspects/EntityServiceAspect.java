package org.slc.sli.api.security.aspects;

import java.util.Collection;

import org.slc.sli.api.security.enums.DefaultRoles;
import org.slc.sli.api.security.enums.Rights;
import org.slc.sli.api.service.BasicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;

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
        
        boolean hasAccess = false;
        Rights neededRight = null;
        Signature entitySignature = pjp.getSignature();
        String entityFunctionName = entitySignature.getName();
        
        // if the class invoking the underlying EntityService call is 'BasicService' (realm
        // discovery)
        if (pjp.getTarget().getClass().equals(BasicService.class)) {
            String entityDefinitionType = ((BasicService) pjp.getTarget()).getDefn().getType().toString();
            
            if (entityFunctionName.equals("get")) {
                if (entityDefinitionType.equals("realm")) {
                    LOG.debug("realm entity requested - passing straight through");
                    LOG.debug("granting access to user for entity");
                    hasAccess = true;
                } else {
                    neededRight = Rights.READ_GENERAL;
                }
            } else {
                if (entityFunctionName.equals("exists") || entityFunctionName.equals("list")) {
                    neededRight = Rights.READ_GENERAL;
                } else if (entityFunctionName.equals("create") || entityFunctionName.equals("update")
                        || entityFunctionName.equals("delete")) {
                    neededRight = Rights.WRITE_GENERAL;
                }
            }
        } else {
            if (entityFunctionName.equals("get") || entityFunctionName.equals("exists")
                    || entityFunctionName.equals("list")) {
                neededRight = Rights.READ_GENERAL;
            } else if (entityFunctionName.equals("create") || entityFunctionName.equals("update")
                    || entityFunctionName.equals("delete")) {
                neededRight = Rights.WRITE_GENERAL;
            }
        }
        
        LOG.debug("attempted access of {} function", entitySignature.toString());
        Collection<GrantedAuthority> myAuthorities = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities();
        LOG.debug("user rights: {}", myAuthorities.toString());
        
        for (GrantedAuthority auth : myAuthorities) {
            LOG.debug("checking rights for role: {}", auth.getAuthority());
            try {
                for (DefaultRoles role : DefaultRoles.values()) {
                    if (role.getSpringRoleName().equals(auth.getAuthority()) && role.hasRight(neededRight)) {
                        LOG.debug("granting access to user for entity");
                        hasAccess = true;
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
            LOG.debug("entering {} function [using Around]", entitySignature.toString());
            Object entityReturned = pjp.proceed();
            LOG.debug("exiting {} function [using Around]", entitySignature.toString());
            return entityReturned;
        } else {
            LOG.debug("user was denied access due to insufficient permissions.");
            throw new BadCredentialsException("User does not have authority to access entity.");
        }
    }
}
