package org.slc.sli.api.security.context.resolver;

import org.slc.sli.domain.Entity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Default context traversing implementation to deny access to everything
 */
@Component
public class DenyAllContextResolver implements EntityContextResolver {
    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return false;
    }

    @Override
    public List<String> findAccessible(Entity principal) {
        return Collections.emptyList();
    }
}
