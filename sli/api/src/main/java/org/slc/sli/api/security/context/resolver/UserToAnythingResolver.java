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


/**
 *
 */
package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.resources.security.SecurityEventResource;
import org.slc.sli.api.security.schema.SchemaDataProvider;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;

/**
 * Resolves "Users" to anything.
 *
 * In particular we only want Users to resolve to administrative resources and be
 * denied to anything else.
 *
 * @author rlatta
 *
 */
@Component
public class UserToAnythingResolver implements EntityContextResolver {
    
    private static ThreadLocal<String> toEntity = new ThreadLocal<String>();
    private static final String ADMIN_SPHERE = "Admin";
    private static final String PUBLIC_SPHERE = "Public";
    
    @Autowired
    private EntityDefinitionStore store;

    @Autowired
    private SchemaDataProvider provider;

    @Autowired
    private PagingRepositoryDelegate<Entity> repository;

    /* (non-Javadoc)
     * @see org.slc.sli.api.security.context.resolver.EntityContextResolver#canResolve(java.lang.String, java.lang.String)
     */
    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        if (toEntityType.equals(SecurityEventResource.RESOURCE_NAME)) {
            return false;
        }
        
        if (fromEntityType.equals("user") || fromEntityType.equals("admin-staff")) {
            debug("Resolving {} -> {}", fromEntityType, toEntityType);
            toEntity.set(toEntityType);
            return true;
        } else {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see org.slc.sli.api.security.context.resolver.EntityContextResolver#findAccessible(org.slc.sli.domain.Entity)
     */
    @Override
    public List<String> findAccessible(Entity principal) {
        EntityDefinition def = store.lookupByEntityType(toEntity.get());

        if (PUBLIC_SPHERE.equals(provider.getDataSphere(def.getType()))) {
            info("Granting public sphere to resource: {}", def.getResourceName());
        } else if (ADMIN_SPHERE.equals(provider.getDataSphere(def.getType()))) {
            info("Granting admin sphere to resource: {}", def.getResourceName());
        } else {
            info("Denying access to all entities of type {}", toEntity);
            return new ArrayList<String>();
        }
        // We give access to all admin or to public
        List<String> ids = new ArrayList<String>();

        Iterable<String> it = this.repository.findAllIds(def.getStoredCollectionName(), new NeutralQuery());
        for (String id : it) {
            ids.add(id);
        }
        toEntity.remove();
        return ids;
    }

}
