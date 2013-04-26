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
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.bulk.extract.context.resolver.ContextResolver;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

@Component
public class EducationOrganizationContextResolver implements ContextResolver {
    /*
     * TODO: REMOVE THIS CLASS
     * 
     * COPY / PASTE ALERT!!!
     * Why add api dependency when this code is going to be thrown away?
     * 
     * This is a hack so that we don't need to wait for inter team dependency to finish stories,
     * but this code will be removed either way we decided to go.
     * 1. introduce proper API dependency if we need to walk the tree
     * or
     * 2. Just look at the edorg id stored inside the entity
     */
    
    private static final Logger LOG = LoggerFactory.getLogger(EducationOrganizationContextResolver.class);

    @Autowired
    @Qualifier("validationRepo")
    Repository<Entity> repo;

    @Override
    public Set<String> findGoverningLEA(Entity entity) {
        Set<String> results = new HashSet<String>();

        if (!(isLEA(entity) || isSchool(entity))) {
            // SEA is not supported
            return results;
        }
        
        Entity topLevelLEA = getTopLEAOfEdOrg(entity);
        if (topLevelLEA != null) {
            results.add(topLevelLEA.getEntityId());
        }
        
        return results;
    }
    
    @SuppressWarnings("unchecked")
    private boolean isLEA(Entity entity) {
        if (entity == null) {
            return false;
        }

        List<String> category = (List<String>) entity.getBody().get("organizationCategories");
        
        if (category != null && category.contains("Local Education Agency")) {
            return true;
        }
        return false;
    }
    
    @SuppressWarnings("unchecked")
    private boolean isSchool(Entity entity) {
        if (entity == null) {
            return false;
        }

        List<String> category = (List<String>) entity.getBody().get("organizationCategories");
        
        if (category != null && category.contains("School")) {
            return true;
        }
        return false;
    }
    
    private Entity getTopLEAOfEdOrg(Entity entity) {
        if (entity.getBody().containsKey("parentEducationAgencyReference")) {
            Entity parentEdorg = repo.findById(EntityNames.EDUCATION_ORGANIZATION,
                    (String) entity.getBody().get("parentEducationAgencyReference"));
            if (isLEA(parentEdorg)) {
                return getTopLEAOfEdOrg(parentEdorg);
            }
        }
        
        if (isLEA(entity)) {
            return entity;
        }
        
        return null;
    }

}
