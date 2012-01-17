package org.slc.sli.api.security.context;

import org.slc.sli.domain.Entity;
import org.springframework.stereotype.Component;

/**
 *  TODO: add javadoc to this file
 *  this must be done prior to committing code to prevent checkstyle breaks
 */
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
