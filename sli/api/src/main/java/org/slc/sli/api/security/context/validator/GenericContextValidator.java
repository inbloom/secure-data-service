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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slc.sli.api.security.context.ContextResolverStore;
import org.slc.sli.api.security.context.resolver.EntityContextResolver;
import org.slc.sli.api.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Generic context validator that makes use of the old context resolvers until we can
 * fully transition to the new logic.
 * 
 */
public class GenericContextValidator implements IContextValidator {
    
    @Autowired
    private ContextResolverStore store;
    
    private EntityContextResolver resolver;

    @Override
    public boolean canValidate(String entityType, boolean through) {
        String userType = SecurityUtil.getSLIPrincipal().getEntity().getType();
        resolver = store.findResolver(userType, entityType);
        return resolver.canResolve(userType, entityType);
    }
    
    @Override
    public boolean validate(Collection<String> ids) {
        Set<String> contextIds = new HashSet<String>(
                resolver.findAccessible(SecurityUtil.getSLIPrincipal().getEntity()));
        return contextIds.containsAll(ids);
    }
    
}
