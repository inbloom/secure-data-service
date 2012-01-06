package org.slc.sli.api.security.aspects;

import org.apache.avro.Schema;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.enums.Right;
import org.slc.sli.domain.Entity;
import org.slc.sli.validation.EntitySchemaRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Iterator;

// add this import if we move to CoreEntityService paradigm
// import org.slc.sli.api.service.CoreEntityService;

/**
 * Aspect for handling Entity Service operations.
 *
 * @author shalka
 */

@Aspect
public class EntityServiceAspect {
    
    private static final Logger LOG = LoggerFactory.getLogger(EntityServiceAspect.class);
    
    @Autowired
    private EntitySchemaRegistry mySchemaRegistry;

    /**
     * Controls access to functions in the EntityService class.
     *
     * @param pjp Method invoked if principal has required rights.
     * @return Entity returned from invoked method (if method is entered).
     * @throws Throwable AccessDeniedException (HTTP 403).
     */
    @Around("call(* org.slc.sli.api.service.CoreEntityService.create(..)) || " +
            "call(* org.slc.sli.api.service.CoreEntityService.update(..)) || " +
            "call(* org.slc.sli.api.service.CoreEntityService.delete(..))")
    public Object authorizeWrite(ProceedingJoinPoint pjp) throws Throwable {
        if (!isPublicContext() && !getGrantedRights().contains(Right.WRITE_GENERAL)) {
            throwAccessDeniedException();
        }

        return pjp.proceed();
    }

    /**
     * Controls access to functions in the EntityService class.
     *
     * @param pjp Method invoked if principal has required rights.
     * @return Entity returned from invoked method (if method is entered).
     * @throws Throwable AccessDeniedException (HTTP 403).
     */
    @Around("call(* org.slc.sli.api.service.CoreEntityService.get(..))")
    public Entity filterEntityRead(ProceedingJoinPoint pjp) throws Throwable {
        if (!isPublicContext() && !getGrantedRights().contains(Right.READ_GENERAL)) {
            throwAccessDeniedException();
        }
        Entity entity = (Entity) pjp.proceed();

        if (entity != null && !isPublicContext()) {
            Collection<Right> grantedRights = getGrantedRights();
            LOG.debug("Rights {}", grantedRights);

            if (!grantedRights.contains(Right.READ_RESTRICTED)) {
                LOG.debug("Filtering restricted on {}", entity.getEntityId());
                removeReadRestrictedAttributes(entity);
            }
            if (!grantedRights.contains(Right.READ_GENERAL)) {
                LOG.debug("Filtering general on {}", entity.getEntityId());
                removeReadGeneralAttributes(entity);
            }
        }

        return entity;
    }

    /**
     * Controls access to functions in the EntityService class.
     *
     * @param pjp Method invoked if principal has required rights.
     * @return Entity returned from invoked method (if method is entered).
     * @throws Throwable AccessDeniedException (HTTP 403).
     */
    @Around("call(* org.slc.sli.api.service.EntityService.list(..)) || " +
            "call(* org.slc.sli.api.service.EntityService.exists(..))")
    public Object authorizeExists(ProceedingJoinPoint pjp) throws Throwable {
        if (!isPublicContext() && !getGrantedRights().contains(Right.READ_GENERAL)) {
            throwAccessDeniedException();
        }
        return pjp.proceed();
    }


    private void throwAccessDeniedException() throws Throwable {
        LOG.debug("user was denied access due to insufficient permissions.");
        throw new AccessDeniedException("User does not have authority to access entity.");
    }

    private boolean isPublicContext() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private void removeReadGeneralAttributes(Entity entity) {
        if (entity.getBody() == null)
            return;

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

    private void removeReadRestrictedAttributes(Entity entity) {
        if (entity.getBody() == null)
            return;

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
     * @param field Field to be checked for a 'restricted' read enforcement flag.
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

    private Collection<Right> getGrantedRights() {
        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getRights();
    }


    public void setSchemaRegistry(EntitySchemaRegistry schemaRegistry) {
        this.mySchemaRegistry = schemaRegistry;
    }
}