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

import java.util.Set;

/**
 * Set of methods pertaining to validating access to entities via context checks.
 * 
 * 
 * @author kmyers
 *
 */
public interface IContextValidator {

    /**
     * Simple method that can return true if it knows how to validate who you are to the
     * type of entity asked for. Also is split up into transitive vs non-transitive access
     *
     * Transitive means that your ability to see these things goes beyond direct association
     * and includes extra context through other means. (i.e. Transitive access to section is
     * through both your direct section associations and the sections of the students that a
     * teacher teaches)
     *
     * @param entityType
     *            Type of entity being requested.
     * @param isTransitive
     *            Flag including transitive access to entity type.
     * @return True if access is allowed, false otherwise.
     */
    public abstract boolean canValidate(String entityType, boolean isTransitive);

    /**
     * Validates that the user has access to the set of entities of type 'entityType' with _id
     * contained within the specified 'ids' Set.
     *
     * @param entityType
     *            Type of entities being requested.
     * @param ids
     *            Set of Strings representing the _id's of entities being requested.
     * @return True if the user has access to ALL of the entities requested, false otherwise.
     * @throws IllegalStateException thrown iw e
     */
    public abstract boolean validate(String entityType, Set<String> ids) throws IllegalStateException;

    /**
     * Gets a subset of ids that the user has access to from the provided set of entities of type 'entityType'.
     *
     * @param entityType
     *            Type of entities being requested.
     * @param ids
     *            Set of Strings representing the _id's of entities being requested.
     * @return Set of valid ids that the user has access to to
     */
    public abstract Set<String> getValid(String entityType, Set<String> ids);
}
