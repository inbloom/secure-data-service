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
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.domain.Entity;

/**
 * Resolves which discipline actions a given staff is allowed to see
 *
 * @author syau
 *
 */
//@Component
public class StaffDisciplineActionResolver implements EntityContextResolver {

    @Autowired
    private ResolveCreatorsEntitiesHelper creatorResolverHelper;

    @Autowired
    private AssociativeContextHelper helper;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.STAFF.equals(fromEntityType) && EntityNames.DISCIPLINE_ACTION.equals(toEntityType);
    }

    // Staff should have access to discipline actions to which they are referred.
    @Override
    public List<String> findAccessible(Entity principal) {

        List<String> referenceIds = new ArrayList<String>();
        referenceIds.add(principal.getEntityId());

        List<String> references = helper.findEntitiesContainingReference(EntityNames.DISCIPLINE_ACTION, "staffId", referenceIds);

        references.addAll(creatorResolverHelper.getAllowedForCreator(EntityNames.DISCIPLINE_ACTION));
        return references;
    }
}
