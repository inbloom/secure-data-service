package org.slc.sli.api.security.context;

import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityExistsException;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores context based permission resolvers.
 * Can determine if a principal entity has permission to access a request entity.
 */
@Component
public class ContextResolver {

    private Map<String, EntityContextResolver> contexts = new HashMap<String, EntityContextResolver>();

    @Autowired
    private DefaultEntityContextResolver defaultEntityContextResolver;

    public void addContext(EntityContextResolver resolver) throws EntityExistsException {
        EntityContextResolver putResult =
                contexts.put(getContextKey(resolver), resolver);
        if (putResult != null) {
            throw new EntityExistsException();
        }
    }

    public boolean hasPermission(Entity principalEntity, Entity requestEntity) {
        EntityContextResolver resolver = getContextResolver(principalEntity, requestEntity);
        return resolver.hasPermission(principalEntity, requestEntity);
    }

    public EntityContextResolver getContextResolver(String sourceType, String targetType) {
        EntityContextResolver resolver = contexts.get(getContextKey(sourceType, targetType));
        return resolver == null ? defaultEntityContextResolver : resolver;
    }

    public EntityContextResolver getContextResolver(Entity principalEntity, Entity requestEntity) {
        return getContextResolver(principalEntity.getType(), requestEntity.getType());
    }

    public void clearContexts() {
        contexts.clear();
    }

    private String getContextKey(EntityContextResolver resolver) {
        return getContextKey(resolver.getSourceType(), resolver.getTargetType());
    }

    private String getContextKey(String sourceType, String targetType) {
        return sourceType + targetType;
    }

}
