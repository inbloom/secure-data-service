package org.slc.sli.api.security;

import org.slc.sli.domain.Entity;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextResolver {
    public static boolean hasPermission(Entity principalEntity, Entity requestEntity) {
        //TODO: determine context
        return true;
    }
}
