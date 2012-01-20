package org.slc.sli.api.security.context;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;

/**
 * TODO: add javadoc to this file
 * this must be done prior to committing code to prevent checkstyle breaks
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
    
    @Override
    public List<String> findAccessible(Entity principal) {
        return Collections.<String>emptyList();
    }
    
}
