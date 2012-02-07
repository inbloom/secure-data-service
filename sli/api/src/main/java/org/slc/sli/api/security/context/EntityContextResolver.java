package org.slc.sli.api.security.context;

import java.util.List;

import org.slc.sli.domain.Entity;

/**
 * TODO: add javadoc to this file
 * this must be done prior to committing code to prevent checkstyle breaks
 */
public interface EntityContextResolver {
    public String getSourceType();

    public String getTargetType();

    public List<String> findAccessible(Entity principal);

}
