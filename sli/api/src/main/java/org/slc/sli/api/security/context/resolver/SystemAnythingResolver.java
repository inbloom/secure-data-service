package org.slc.sli.api.security.context.resolver;

import java.util.List;

import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;

/**
 * Resolves paths for system_entity
 * @author dkornishev
 *
 */
@Component
public class SystemAnythingResolver implements EntityContextResolver {
    
    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return "system_entity".equals(fromEntityType);
    }
    
    @Override
    public List<String> findAccessible(Entity principal) {
        return AllowAllEntityContextResolver.SUPER_LIST;
    }
    
}
