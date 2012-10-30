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

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StaffToNonTransitivePublicEntityValidator extends GenericContextValidator {

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

	private static final List<String> publicEntities = Arrays.asList(EntityNames.ASSESSMENT, EntityNames.LEARNING_OBJECTIVE, EntityNames.LEARNING_STANDARD);

    @Override
    public boolean canValidate(String entityType, boolean through) {
        return this.publicEntities.contains(entityType) && SecurityUtil.getSLIPrincipal().getEntity().getType().equals(EntityNames.STAFF);
    }

    @Override
    public boolean validate(String entityType, Set<String> entityIds) {

    	/* Minor guard to prevent calling this on non-public entities */
    	if (!this.canValidate(entityType, true)) {
    		throw new IllegalArgumentException("This resolver should not have been called for entityType "+entityType);
    	}

    	/* Check if the entities being asked for exist in the repo
    	 * This is done by checking sizes of the input set and 
    	 * the return from the database */
        NeutralQuery nq = new NeutralQuery(new NeutralCriteria("_id", "in", entityIds, false));
    	
    	long repoCount = repo.count(entityType, nq);
    	
    	return repoCount == (long) entityIds.size();
    }

}
