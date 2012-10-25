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

import java.util.Set;

/**
 * Determines if a user has context to see an entity that is directly associated to a student
 * object.
 *
 * @author shalka
 */
public interface SubStudentEntityValidator {

    /**
     * Determines whether or not the context for the entity type can be validated.
     *
     * @param entityType
     *            String representing type of entity for which context will be validated.
     * @return True if user can access entity, false otherwise.
     */
    public abstract boolean canValidate(String entityType);

    /**
     * Validates the user's context to the set of entities with _id contained within set 'ids' and
     * of type 'type'.
     *
     * @param ids
     *            Set of _id's to validate the user's context against.
     * @param type
     *            Type of entities being validated.
     * @return True if the user has access to each entity with a corresponding _id in the Set of
     *         'ids'.
     */
    public abstract boolean validate(Set<String> ids, String type);
}
