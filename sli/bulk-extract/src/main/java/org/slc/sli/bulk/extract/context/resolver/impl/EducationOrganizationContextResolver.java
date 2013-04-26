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
import java.util.Set;

import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.bulk.extract.context.resolver.ContextResolver;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Resolves context for education organization to its top level LEA
 * 
 */
@Component
public class EducationOrganizationContextResolver implements ContextResolver {
    
    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;
    
    @Autowired
    private EdOrgHelper edOrgHelper;

    @Override
    public Set<String> findGoverningLEA(Entity entity) {
        Set<String> results = new HashSet<String>();

        if (!(edOrgHelper.isLEA(entity) || edOrgHelper.isSchool(entity))) {
            // SEA is not supported
            return results;
        }
        
        Entity topLevelLEA = edOrgHelper.getTopLEAOfEdOrg(entity);
        if (topLevelLEA != null) {
            results.add(topLevelLEA.getEntityId());
        }
        
        return results;
    }
    
}
