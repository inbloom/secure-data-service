package org.slc.sli.api.security.aspects;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slc.sli.api.security.enums.DefaultRoles;
import org.slc.sli.api.security.enums.Right;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.validation.EntitySchemaRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Aspect for handling Entity Service operations.
 * 
 * @author shalka
 */

@Aspect
public class EntityServiceAspect {
    
    private static final Logger LOG = LoggerFactory.getLogger(EntityServiceAspect.class);
    
    private static List<String> entitiesAlwaysAllow = Arrays.asList("realm");
    private static List<String> methodsAlwaysAllow = Arrays.asList("getEntityDefinition");
    private static Map<String, Right> neededRights = new HashMap<String, Right>();
    
    private EntitySchemaRegistry schemaRegistry;
    
    public void setSchemaRegistry(EntitySchemaRegistry schemaRegistry) {
        this.schemaRegistry = schemaRegistry;
    }
    
    static {
        neededRights.put("get", Right.READ_GENERAL);
        neededRights.put("list", Right.READ_GENERAL);
        neededRights.put("exists", Right.READ_GENERAL);
        neededRights.put("create", Right.WRITE_GENERAL);
        neededRights.put("update", Right.WRITE_GENERAL);
        neededRights.put("delete", Right.WRITE_GENERAL);
    }
    
    @Around("call(* EntityService.*(..)) && !within(EntityServiceAspect) && !withincode(* *.mock*())")
    public Object controlAccess(ProceedingJoinPoint pjp) throws Throwable {
        boolean hasAccess = false;
        Right neededRight = null;
        Signature entitySignature = pjp.getSignature();
        String entityFunctionName = entitySignature.getName();

        
        EntityService service = (EntityService) pjp.getTarget();
        String entityDefinitionType = service.getEntityDefinition().getType();
        
        if (entitiesAlwaysAllow.contains(entityDefinitionType) || methodsAlwaysAllow.contains(entityFunctionName)) {
            LOG.debug("granting access to user for entity");
            hasAccess = true;
        } else {
            neededRight = neededRights.get(entityFunctionName);
            
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
