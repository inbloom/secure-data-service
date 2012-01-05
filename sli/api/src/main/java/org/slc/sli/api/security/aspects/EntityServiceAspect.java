package org.slc.sli.api.security.aspects;

import java.util.Arrays;
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
import org.slc.sli.api.security.enums.Right;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.domain.Entity;
import org.slc.sli.validation.EntitySchemaRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

// add this import if we move to CoreEntityService paradigm
// import org.slc.sli.api.service.CoreEntityService;

/**
 * Aspect for handling Entity Service operations.
 * @author shalka
 */

@Aspect
public class EntityServiceAspect {
    
    private static final Logger LOG = LoggerFactory.getLogger(EntityServiceAspect.class);
    
    @Autowired
    private EntitySchemaRegistry mySchemaRegistry;
    
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
    
    /**
     * Controls access to functions in the EntityService class.
     * 
     * @param pjp
     *            Method invoked if principal has required rights.
     * @return Entity returned from invoked method (if method is entered).
     * @throws Throwable
     *             AccessDeniedException (HTTP 403).
     */
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
            
            Set<Right> myRights = getGrantedRights();
            LOG.debug("user rights: {}", myRights.toString());
            
            for (Right currentRight : myRights) {
                if (currentRight.equals(neededRight)) {
                    LOG.debug("granting access to user for entity");
                    hasAccess = true;
                    break;
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
    
    @Around("call(* org.slc.sli.api.service.CoreEntityService.get(..))")
    public Entity filterEntityRead(ProceedingJoinPoint pjp) throws Throwable {
        LOG.debug("[ASPECT] filtering read");
        Entity entity = (Entity) pjp.proceed();
        
        if (entity != null) {
            Set<Right> grantedRights = getGrantedRights();
            LOG.debug("Rights {}", grantedRights);
            
            if (!grantedRights.contains(Right.READ_RESTRICTED)) {
                LOG.debug("Filtering restricted on {}", entity.getEntityId());
                filterReadRestricted(entity);
            }
            if (!grantedRights.contains(Right.READ_GENERAL)) {
                LOG.debug("Filtering general on {}", entity.getEntityId());
                filterReadGeneral(entity);
            }
        }
        
        return entity;
    }
    
    private void filterReadGeneral(Entity entity) {
        Schema schema = mySchemaRegistry.findSchemaForType(entity);
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
        Schema schema = mySchemaRegistry.findSchemaForType(entity);
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
    
    /**
     * Returns true if the Field is marked "restricted" under "read_enforcement".
     * 
     * @param field
     *            Field to be checked for a 'restricted' read enforcement flag.
     * @return Boolean indicating whether or not the Field requires READ_RESTRICTED right to be
     *         read.
     */
    private boolean isRestrictedField(Schema.Field field) {
        if (field == null) {
            return false;
        }
        
        String readProp = field.getProp("read_enforcement");
        return (readProp != null && readProp.equals("restricted"));
    }
    
    private Set<Right> getGrantedRights() {
        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getRights();
    }
    
    public void setSchemaRegistry(EntitySchemaRegistry schemaRegistry) {
        this.mySchemaRegistry = schemaRegistry;
    }
}