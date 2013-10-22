/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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
package org.slc.sli.bulk.extract.context.resolver.impl;

import java.util.*;

import org.slc.sli.bulk.extract.context.resolver.ContextResolver;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;

/**
 * Resolver for entities directly related to another entity
 * 
 * @author nbrown
 * 
 */
public abstract class RelatedContextResolver implements ContextResolver {

    private static Set<String> supportedNonCurrentEntities = new HashSet<String>(Arrays.asList(EntityNames.STUDENT_PROGRAM_ASSOCIATION,
            EntityNames.STUDENT_COHORT_ASSOCIATION, EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, EntityNames.STUDENT_PARENT_ASSOCIATION));

    public RelatedContextResolver() {
        super();
    }
    
    @Override
    public Set<String> findGoverningEdOrgs(Entity entity) {
        if (entity.getBody() == null) {
            return Collections.emptySet();
        }
        
        String referredId = getReferredId(entity.getType(), entity.getBody());
        if (referredId == null) {
            return Collections.emptySet();
        }

        if (supportedNonCurrentEntities.contains(entity.getType())) {
            return getReferredResolver().findGoverningEdOrgs(referredId, entity);
        }
        return getReferredResolver().findGoverningEdOrgs(referredId);
    }

    @Override
    public Set<String> findGoverningEdOrgs(Entity baseEntity, Entity actualEntity) {
        throw new UnsupportedOperationException();
    }
    
    protected String getReferredId(String type, Map<String, Object> body) {
        String reference = getReferenceProperty(type);
        if (reference == null) {
            return null;
        }
        String referredId = (String) body.get(reference);
        return referredId;
    }
    
    protected abstract String getReferenceProperty(String entityType);
    
    protected abstract ReferrableResolver getReferredResolver();
    
}