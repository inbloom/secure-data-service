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

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.utils.EdOrgHierarchyHelper;

@Component
public class EducationOrganizationContextResolver extends ReferrableResolver {

    private EdOrgHierarchyHelper helper;
    
    @PostConstruct
    public void init() {
        helper = new EdOrgHierarchyHelper(getRepo());
    }

    private static final Logger LOG = LoggerFactory.getLogger(EducationOrganizationContextResolver.class);

    @Override
    protected Set<String> resolve(Entity entity) {
        LOG.debug("finding governing edOrg of {}", entity);
        Set<String> results = new HashSet<String>();

        if (helper.isLEA(entity) || helper.isSchool(entity)) {
            List<Entity> topLevelLEAs = helper.getTopLEAOfEdOrg(entity);
            for(Entity topLevelLEA: topLevelLEAs) {
            	if (topLevelLEA != null) {
            		results.add(topLevelLEA.getEntityId());
            	}
            }
        } else if (helper.isSEA(entity)) {
            // Governing edOrg of an SEA is itself
            results.add(entity.getEntityId());
        }

//        Entity topLevelLEA = helper.getTopLEAOfEdOrg(entity);
//        if (topLevelLEA != null) {
//            results.add(topLevelLEA.getEntityId());
//        }
        LOG.debug("Results are {}", results);
        return results;
    }
    
    @Override
    public String getCollection() {
        return EntityNames.EDUCATION_ORGANIZATION;
    }

    void setHelper(EdOrgHierarchyHelper helper) {
        this.helper = helper;
    }

}
