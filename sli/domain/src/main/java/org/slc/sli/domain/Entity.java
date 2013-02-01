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

package org.slc.sli.domain;

import java.util.List;
import java.util.Map;

/**
 * Define the entity interface that provides getter method to retrieve the fields
 * for entities including core entities and association entities
 *
 * @author Dong Liu dliu@wgen.net
 *
 */
public interface Entity {

    /**
     * @return the entity type as string, can be entity type for
     *         core entity or association entity
     */
    public String getType();

    /**
     * @return the global unique id of the entity as string
     */
    public String getEntityId();

    /**
     * @return the entity body wrapped by a map
     */
    public Map<String, Object> getBody();

    /**
     * Returns the metaData.
     */
    public Map<String, Object> getMetaData();

    /**
     * Returns the uuid of the staged entity.
     */
    public String getStagedEntityId();

    /**
     * Get the calculated values
     *
     * @return
     */
    public CalculatedData<String> getCalculatedValues();

    /**
     * Get the aggregates
     *
     * @return
     */
    public CalculatedData<Map<String, Integer>> getAggregates();

    /**
     * Get data embedded on this entity. This refers to entities that are ENTIRELY embedded on this
     * entity.
     *
     * @return
     */
    public Map<String, List<Entity>> getEmbeddedData();

    /**
     * Get data denormalized onto this entity. This refers to entities that are PARTIALLY embedded
     * on this entity.
     *
     * @return
     */
    public Map<String, List<Map<String, Object>>> getDenormalizedData();
}
