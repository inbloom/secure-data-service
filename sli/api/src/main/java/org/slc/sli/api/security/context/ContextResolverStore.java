package org.slc.sli.api.security.context;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.context.resolver.AllowAllEntityContextResolver;
import org.slc.sli.api.security.context.resolver.EntityContextResolver;

/**
 * Stores context based permission resolvers.
 * Can determine if a principal entity has permission to access a request entity.
 */
@Component
public class ContextResolverStore implements ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(ContextResolverStore.class);
    
    private Collection<EntityContextResolver> resolvers;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        resolvers = applicationContext.getBeansOfType(EntityContextResolver.class).values();
    }
    
    /**
     * Locates a resolver that can naviage the security context path from source entity type to target entity type
     * 
     * @param fromEntityType
     * @param toEntityType
     * @return
     * @throws IllegalStateException
     */
    public EntityContextResolver findResolver(String fromEntityType, String toEntityType) throws IllegalStateException {
        
        EntityContextResolver found = null;
        for (EntityContextResolver resolver : this.resolvers) {
            if (resolver.canResolve(fromEntityType, toEntityType)) {
                found = resolver;
                break;
            }
        }
        
        if (found == null) { // FIXME make secure by default!
            found = new AllowAllEntityContextResolver();
            LOG.warn("No path resolver defined for {} -> {} returning a yes-man (for now)", fromEntityType, toEntityType);
            // throw new IllegalStateException("Requested an usupported resolution path " + fromEntityType + " -> " + toEntityType);
        }
        
        return found;
    }
}
