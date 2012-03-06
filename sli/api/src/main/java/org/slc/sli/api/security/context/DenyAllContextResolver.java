package org.slc.sli.api.security.context;

import org.slc.sli.domain.Entity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Default context traversing implementation that denies access to everything
 */
@Component
public class DenyAllContextResolver implements EntityContextResolver {

    @Override
    public List<String> findAccessible(Entity principal) {
        return new ArrayList<String>();
    }

    @Override
    public String getSourceType() {
        return null;
    }

    @Override
    public String getTargetType() {
        return null;
    }

}
