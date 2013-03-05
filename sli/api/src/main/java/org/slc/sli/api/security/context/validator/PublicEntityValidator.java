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

package org.slc.sli.api.security.context.validator;

import org.slc.sli.common.constants.EntityNames;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Validates the context of a staff/teacher member to see the requested set of non-transitive public
 * entities. Returns true if the staff member can see ALL of the entities, and false otherwise.
 *
 * @author mabernathy
 */
@Component
public class PublicEntityValidator extends AbstractContextValidator {
       
    protected static final Set<String> PUBLIC_RESOURCE = new HashSet<String>(Arrays.asList(
            EntityNames.ASSESSMENT, 
            EntityNames.LEARNING_OBJECTIVE, 
            EntityNames.LEARNING_STANDARD, 
            EntityNames.COMPETENCY_LEVEL_DESCRIPTOR,
            EntityNames.SESSION,
            EntityNames.COURSE_OFFERING,
            EntityNames.GRADING_PERIOD,
            EntityNames.COURSE));

    @Override
    public boolean canValidate(String entityType, boolean through) {
        return isPublic(entityType);
    }

    @Override
    public boolean validate(String entityType, Set<String> entityIds) {
        if (!areParametersValid(PUBLIC_RESOURCE, entityType, entityIds)) {
            return false;
        }
        return true;
    }


    /**
     * Determines if the entity type is public.
     *
     * @param type Entity type.
     * @return True if the entity is public, false otherwise.
     */
    protected boolean isPublic(String type) {
        return PUBLIC_RESOURCE.contains(type);
    }

}
