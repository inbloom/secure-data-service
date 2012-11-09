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
import org.slc.sli.api.util.SecurityUtil;
import org.springframework.stereotype.Component;

/**
 * Validates the context of a staff member to see the requested set of non-transitive public
 * entities. Returns true if the staff member can see ALL of the entities, and false otherwise.
 *
 * @author mabernathy
 */
@Component
public class TeacherToNonTransitivePublicEntityValidator extends AbstractContextValidator {

	private List<String> entities = Arrays.asList(EntityNames.SCHOOL,EntityNames.EDUCATION_ORGANIZATION);
	
    @Override
    public boolean canValidate(String entityType, boolean through) {
        return entities.contains(entityType) && "teacher".equals(SecurityUtil.getSLIPrincipal().getEntity().getType());
    }

    @Override
    public boolean validate(String entityType, Set<String> entityIds) {

        if (!this.canValidate(entityType, true)) {
            throw new IllegalArgumentException("This resolver should not have been called for entityType " + entityType);
        }
        
        return true;
    }

}
