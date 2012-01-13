package org.slc.sli.api.security.context;

import org.slc.sli.domain.Entity;
import org.springframework.stereotype.Component;

@Component
public class DefaultEntityContextResolver implements EntityContextResolver {
    @Override
    public String getSourceType() {
        return null;
    }

    @Override
    public String getTargetType() {
        return null;
    }

    @Override
    public boolean hasPermission(Entity principal, Entity resource) {
        return true;
    }

}
