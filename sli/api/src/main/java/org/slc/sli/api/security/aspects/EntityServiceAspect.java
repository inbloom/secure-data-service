package org.slc.sli.api.security.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.enums.DefaultRoles;
import org.slc.sli.api.security.enums.Rights;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.validation.EntitySchemaRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private static Map<String, Rights> neededRights = new HashMap<String, Rights>();

    private EntitySchemaRegistry schemaRegistry;

    public void setSchemaRegistry(EntitySchemaRegistry schemaRegistry) {
        this.schemaRegistry = schemaRegistry;
    }

    static {
        neededRights.put("get", Rights.READ_GENERAL);
        neededRights.put("list", Rights.READ_GENERAL);
        neededRights.put("exists", Rights.READ_GENERAL);
        neededRights.put("create", Rights.WRITE_GENERAL);
        neededRights.put("update", Rights.WRITE_GENERAL);
        neededRights.put("delete", Rights.WRITE_GENERAL);
    }

    @Around("call(* EntityService.*(..)) && !within(EntityServiceAspect) && !withincode(* *.mock*())")
    public Object controlAccess(ProceedingJoinPoint pjp) throws Throwable {

        if (!isAuthorized(pjp)) {
            throw new BadCredentialsException("User does not have authority to access entity.");
        }

        LOG.debug("entering {} function [using Around]", pjp.getSignature());
        Object entityReturned = pjp.proceed();
        LOG.debug("exiting {} function [using Around]", pjp.getSignature());

        return entityReturned;
    }

    private boolean isAuthorized(ProceedingJoinPoint pjp) {
        boolean hasAccess = false;
        Rights neededRight = null;
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
        return hasAccess;
    }

    @Around("call(EntityBody EntityService.get(..)) && !within(EntityServiceAspect) && !withincode(* *.mock*())")
    public EntityBody filterEntityRead(ProceedingJoinPoint pjp) throws Throwable {
        EntityBody entityBody = (EntityBody) pjp.proceed();

        //READ_GENERAL("READ_GENERAL"), WRITE_GENERAL("WRITE_GENERAL"), READ_RESTRICTED("READ_RESTRICTED")

        Set<Rights> grantedRights = getGrantedRights();

        if (!grantedRights.equals(Rights.READ_RESTRICTED)) {
            filterReadRestricted(entityBody);
        }
        if (!grantedRights.equals(Rights.READ_GENERAL)) {
            filterReadGeneral(entityBody);
        }

        return entityBody;
    }

    private void filterReadGeneral(EntityBody entityBody) {
        //TODO
    }

    private void filterReadRestricted(EntityBody entityBody) {
        //TODO
    }

    private Set<Rights> getGrantedRights() {
        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getRights();
    }

}


