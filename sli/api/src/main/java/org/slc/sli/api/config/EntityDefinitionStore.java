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


package org.slc.sli.api.config;

import java.util.Collection;

/**
 * Store to look up entity definition information
 *
 * @author nbrown
 *
 */
public interface EntityDefinitionStore {

    /**
     * Find an entity definition based on the resource name in the URI
     *
     * @param resourceName
     *            the resource name (from the URI)
     * @return the definition of the entity
     */
    public EntityDefinition lookupByResourceName(String resourceName);

    /**
     * Find an entity definition based on the entity type
     *
     * @param entityType
     *            the entity type
     * @return the definition of the entity
     */
    public EntityDefinition lookupByEntityType(String entityType);

    /**
     * Gets the collection of association definitions that are linked to the given definition
     *
     * @param defn
     *            the definition to look up
     * @return the linked entity definitions
     */
    public Collection<EntityDefinition> getLinked(EntityDefinition defn);
}
