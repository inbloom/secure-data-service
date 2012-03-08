package org.slc.sli.api.security.context.resolver;

import java.util.List;

import org.slc.sli.domain.Entity;

/**
 * TODO: add javadoc to this file
 * this must be done prior to committing code to prevent checkstyle breaks
 */
public interface EntityContextResolver {
    public boolean canResolve(String fromEntityType, String toEntityType);
    public List<String> findAccessible(Entity principal);

}
