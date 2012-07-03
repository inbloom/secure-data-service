/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.api.security.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.context.resolver.DenyAllContextResolver;
import org.slc.sli.api.security.context.resolver.EntityContextResolver;

/**
 * Stores context based permission resolvers.
 * Can determine if a principal entity has permission to access a request entity.
 */
@Component
public class ContextResolverStore implements ApplicationContextAware {

    @Autowired
    private DenyAllContextResolver denyAllContextResolver;
    
    private Collection<EntityContextResolver> resolvers;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        resolvers = applicationContext.getBeansOfType(EntityContextResolver.class).values();  
    }
    
    /**
     * Locates a resolver that can naviage the security context path from source entity type to
     * target entity type
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
        
        if (found == null) {
            found = denyAllContextResolver;
            warn("No path resolver defined for {} -> {}. Returning deny-all resolver.", fromEntityType, toEntityType);
        }

        debug("From entity '{}' to endity '{}' using context resolver '{}'", fromEntityType, toEntityType, found.getClass().getName());
        return found;
    }
}
