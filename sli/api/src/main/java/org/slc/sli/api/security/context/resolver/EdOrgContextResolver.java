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


package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.security.context.traversal.cache.SecurityCachingStrategy;
import org.slc.sli.api.security.context.traversal.graph.NodeFilter;
import org.slc.sli.domain.Entity;

/**
 * Returns all Education Organization Ids a principal entity has access to
 *
 * @author srupasinghe
 *
 */
@Component
public class EdOrgContextResolver implements EntityContextResolver {

    @Autowired
    private AssociativeContextHelper helper;

    @Autowired
    private StaffEdOrgEdOrgIDNodeFilter staffEdOrgEdOrgIDNodeFilter;

    @Autowired
    private ResolveCreatorsEntitiesHelper creatorResolverHelper;

    private String toEntity;

    @Autowired
    private SecurityCachingStrategy securityCachingStrategy;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        if (toEntityType.equals(EntityNames.LEARNING_OBJECTIVE) || toEntityType.equals(EntityNames.LEARNING_STANDARD)) {
            return false;
        }
        return ((fromEntityType != null) && fromEntityType.equals(EntityNames.STAFF));
    }

    @Override
    public List<String> findAccessible(Entity principal) {
        if ((toEntity != null) && (toEntity.equals(EntityNames.LEARNING_OBJECTIVE) || toEntity.equals(EntityNames.LEARNING_STANDARD))) {
            return AllowAllEntityContextResolver.SUPER_LIST;
        }

        if (securityCachingStrategy.contains(EntityNames.STAFF)) {
            List<String> cachedIds = new ArrayList<String>();
            cachedIds.addAll(securityCachingStrategy.retrieve(EntityNames.STAFF));

            return cachedIds;
        }

        //get the ed org ids
        List<String> ids = helper.findEntitiesContainingReference(EntityNames.STAFF_ED_ORG_ASSOCIATION, "staffReference",
                "educationOrganizationReference", Arrays.asList(principal.getEntityId()),
                Arrays.asList((NodeFilter) staffEdOrgEdOrgIDNodeFilter));

        //get the created edorgs
        ids.addAll(creatorResolverHelper.getAllowedForCreator(EntityNames.EDUCATION_ORGANIZATION));

        securityCachingStrategy.warm(EntityNames.STAFF, new HashSet<String>(ids));

        return ids;
    }
}
