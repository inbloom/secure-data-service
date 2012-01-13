package org.slc.sli.api.security.context;

import org.slc.sli.domain.Entity;

public interface EntityContextResolver {
    public String getSourceType();

    public String getTargetType();

    public boolean hasPermission(Entity source, Entity target);

}
