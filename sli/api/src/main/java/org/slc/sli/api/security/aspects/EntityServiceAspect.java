package org.slc.sli.api.security.aspects;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.avro.Schema;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.enums.DefaultRoles;
import org.slc.sli.api.security.enums.Right;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.domain.Entity;
import org.slc.sli.validation.EntitySchemaRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
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
    
    private EntitySchemaRegistry schemaRegistry;
    
    public void setSchemaRegistry(EntitySchemaRegistry schemaRegistry) {
        this.schemaRegistry = schemaRegistry;
    }
    
    private static List<String> entitiesAlwaysAllow = Arrays.asList("realm");
    private static List<String> methodsAlwaysAllow = Arrays.asList("getEntityDefinition");
    private static Map<String, Right> neededRights = new HashMap<String, Right>();
    
    static {
        neededRights.put("get", Right.READ_GENERAL);
        neededRights.put("list", Right.READ_GENERAL);
        neededRights.put("exists", Right.READ_GENERAL);
        neededRights.put("create", Right.WRITE_GENERAL);
        neededRights.put("update", Right.WRITE_GENERAL);
        neededRights.put("delete", Right.WRITE_GENERAL);
    }
    
    @Around("call(* EntityService.*(..)) && !within(EntityServiceAspect) && !call(* EntityService.getEntityDefinition(..))")
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
            throw new AccessDeniedException("User does not have authority to access entity.");
        }
    }
    
    @Around("call(* CoreEntityService.get(..))")
    public Entity filterEntityRead(ProceedingJoinPoint pjp) throws Throwable {
        LOG.debug("[ASPECT] filtering read");
        Entity entity = (Entity) pjp.proceed();
        
        Set<Right> grantedRights = getGrantedRights();
        LOG.debug("Rights {}", grantedRights);
        
        if (!grantedRights.contains(Right.READ_RESTRICTED.getRight())) {
            LOG.debug("Filtering restricted on {}", entity.getEntityId());
            filterReadRestricted(entity);
        }
        if (!grantedRights.contains(Right.READ_GENERAL.getRight())) {
            LOG.debug("Filtering general on {}", entity.getEntityId());
            filterReadGeneral(entity);
        }
        
        return entity;
    }
    
    private void filterReadGeneral(Entity entity) {
        Schema schema = schemaRegistry.findSchemaForType(entity);
        LOG.debug("schema fields {}", schema.getFields());
        Iterator<String> keyIter = entity.getBody().keySet().iterator();
        
        while (keyIter.hasNext()) {
            String fieldName = keyIter.next();
            
            Schema.Field field = schema.getField(fieldName);
            LOG.debug("Field {} is general {}", fieldName, isReadGeneral(field));
            if (isReadGeneral(field)) {
                keyIter.remove();
            }
        }
    }
    
    private boolean isReadGeneral(Schema.Field field) {
        if (field == null) {
            return false;
        }
        
        String readProp = field.getProp("read_enforcement");
        return (readProp != null && !readProp.matches("restricted") && !readProp.matches("aggregate"));
    }
    
    private void filterReadRestricted(Entity entity) {
        Schema schema = schemaRegistry.findSchemaForType(entity);
        Iterator<String> keyIter = entity.getBody().keySet().iterator();
        while (keyIter.hasNext()) {
            String fieldName = keyIter.next();
            
            Schema.Field field = schema.getField(fieldName);
            LOG.debug("Field {} is restricted {}", fieldName, isRestrictedField(field));
            if (isRestrictedField(field)) {
                keyIter.remove();
            }
        }
    }
    
    private boolean isRestrictedField(Schema.Field field) {
        if (field == null) {
            return false;
        }
        
        Map props = field.props();
        if (props.containsKey("read_enforcement")) {
            LOG.debug("Found read_enforcement");
        }
        String readProp = field.getProp("read_enforcement");
        return (readProp != null && readProp.equals("restricted"));
    }
    
    private Set<Right> getGrantedRights() {
        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getRights();
    }
}
