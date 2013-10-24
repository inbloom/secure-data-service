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


import org.slc.sli.bulk.extract.context.resolver.ContextResolver;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Resolver for entities directly related to another entity
 * 
 * @author nbrown
 * 
 */
public abstract class RelatedContextResolver implements ContextResolver {

    //TODO: Remove after all all entities are finished
    private static Set<String> supportedNonCurrentEntities = new HashSet<String>(Arrays.asList(EntityNames.STUDENT_PROGRAM_ASSOCIATION,
            EntityNames.STUDENT_COHORT_ASSOCIATION, EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, EntityNames.STUDENT_PARENT_ASSOCIATION,
            EntityNames.STUDENT_SCHOOL_ASSOCIATION, EntityNames.STUDENT_ASSESSMENT, EntityNames.STUDENT_SECTION_ASSOCIATION, EntityNames.GRADE,
            EntityNames.REPORT_CARD, EntityNames.STUDENT_ACADEMIC_RECORD, EntityNames.STUDENT_COHORT_ASSOCIATION, EntityNames.ATTENDANCE, EntityNames.STUDENT_GRADEBOOK_ENTRY));

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
    public Set<String> findGoverningEdOrgs(String id, Entity actualEntity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> findGoverningEdOrgs(Entity entity, Entity actualEntity) {
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