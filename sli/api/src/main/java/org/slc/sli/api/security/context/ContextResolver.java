package org.slc.sli.api.security.context;

import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityExistsException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class ContextResolver {

    private Map<String, EntityContextResolver> contexts = new HashMap<String, EntityContextResolver>();

    @Autowired
    private Collection<EntityContextResolver> staticContextResolvers;

    @Autowired
    private DefaultEntityContextResolver defaultEntityContextResolver;

    public void addContextResolver(EntityContextResolver resolver) throws EntityExistsException {
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

    @PostConstruct
    public void init() {
        setContexts(staticContextResolvers);
    }

    public void setStaticContextResolvers(Collection<EntityContextResolver> staticContextResolvers) {
        this.staticContextResolvers = staticContextResolvers;
    }

    public EntityContextResolver getContextResolver(String sourceType, String targetType) {
        EntityContextResolver resolver = contexts.get(getContextKey(sourceType, targetType));
        return resolver == null ? defaultEntityContextResolver : resolver;
    }

    public EntityContextResolver getContextResolver(Entity principalEntity, Entity requestEntity) {
        return getContextResolver(principalEntity.getType(), requestEntity.getType());
    }

    private void setContexts(Collection<EntityContextResolver> contextResolvers) {
        for (EntityContextResolver resolver : contextResolvers) {
            if (resolver.getSourceType() != null) {
                contexts.put(getContextKey(resolver), resolver);
            }
        }
    }

    private String getContextKey(EntityContextResolver resolver) {
        return getContextKey(resolver.getSourceType(), resolver.getTargetType());
    }

    private String getContextKey(String sourceType, String targetType) {
        return sourceType + targetType;
    }

}
