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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.bulk.extract.context.resolver.ContextResolver;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Many entities have two path of associations to LEAs:
 * 1. They either have a direct relationship with a LEA based on a "schoolId" in body
 * 2. or they can be transitively associated to LEAs
 * 
 * This class abstract out the direct relationship
 * 
 * @author ycao
 * 
 */
public abstract class EdOrgRelatedReferrableResolver extends ReferrableResolver {
    private static final Logger LOG = LoggerFactory.getLogger(EdOrgRelatedReferrableResolver.class);
    
    @Autowired
    private EducationOrganizationContextResolver edorgResolver;

    @Override
    protected Set<String> resolve(Entity entity) {
        String schoolId = (String) entity.getBody().get("schoolId");
        Set<String> leas = new HashSet<String>();
        if (schoolId == null) {
            LOG.warn("Entity found without a school id: {}", entity);
        } else {
            leas.addAll(edorgResolver.findGoverningEdOrgs(schoolId));
        }
        
        leas.addAll(getTransitiveAssociations(entity));

        return leas;
    }
    
    /**
     * Walk the other part of transitive association
     * 
     * @param entity
     * @return set of LEA ids this entity belongs to
     */
    protected abstract Set<String> getTransitiveAssociations(Entity entity);
    
    /**
     * helper method for retrieve direct associations
     * 
     * @param entity
     * @param referredCollection
     * @param referredField
     * @param resolver
     * @return set of LEAs
     */
    protected Set<String> getDirectTransitiveAssocitions(Entity entity, String referredCollection, String referredField, ContextResolver resolver) {
        Set<String> leas = new HashSet<String>();
        String id = entity.getEntityId();
        if (id != null) {
            Iterator<Entity> referred = getRepo().findEach(referredCollection,
                    new NeutralQuery(new NeutralCriteria(referredField, NeutralCriteria.OPERATOR_EQUAL, id)));
            while (referred.hasNext()) {
                leas.addAll(resolver.findGoverningEdOrgs(referred.next()));
            }
        }
        
        return leas;
    }

}
