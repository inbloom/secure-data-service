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

package org.slc.sli.api.security.context.validator;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.security.context.ContextResolverStore;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.context.resolver.AllowAllEntityContextResolver;
import org.slc.sli.api.security.context.resolver.EntityContextResolver;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 * Generic context validator that makes use of the old context resolvers until we can
 * fully transition to the new logic.
 *
 */
@Component
public class GenericContextValidator implements IContextValidator {

    @Autowired
    private ContextResolverStore store;

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Override
    public boolean canValidate(String entityType, boolean through) {
        String userType = SecurityUtil.getSLIPrincipal().getEntity().getType();
        if (userType.equals("staff")) {
            return false;
        }
        return store.findResolver(userType, entityType) != null;
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) {
        String userType = SecurityUtil.getSLIPrincipal().getEntity().getType();
        EntityContextResolver resolver = store.findResolver(userType, entityType);
        if (resolver instanceof AllowAllEntityContextResolver) {
            return true;
        }
        Set<String> contextIds = new HashSet<String>(
                resolver.findAccessible(SecurityUtil.getSLIPrincipal().getEntity()));
        return contextIds.containsAll(ids);
    }

    /**
     * Determines if the entity type is public.
     *
     * @param type
     *            Entity type.
     * @return True if the entity is public, false otherwise.
     */
    protected boolean isPublic(String type) {
        return type.equals(EntityNames.ASSESSMENT) || type.equals(EntityNames.LEARNING_OBJECTIVE)
                || type.equals(EntityNames.LEARNING_STANDARD);
    }

    /**
     * Determines if the user is of type 'staff'.
     *
     * @return True if user is of type 'staff', false otherwise.
     */
    protected boolean isStaff() {
        return EntityNames.STAFF.equals(SecurityUtil.getSLIPrincipal().getEntity().getType());
    }

    protected Repository<Entity> getRepo() {
        return this.repo;
    }

    protected void setRepo(PagingRepositoryDelegate<Entity> repo) {
        this.repo = repo;
    }
}
