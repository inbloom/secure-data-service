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


package org.slc.sli.api.service;

import java.util.Map;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.domain.CalculatedData;
import org.slc.sli.domain.NeutralQuery;

/**
 * Service for retrieving entities in DB
 *
 * @author nbrown
 *
 */
public interface EntityService {

    /**
     * Returns number of entities stored.
     *
     * @return number of entities that have been stored/exist
     */
    public long count(NeutralQuery neutralQuery);

    /**
     * Create an entity and store it in the data store
     *
     * @param content
     *            the body of the entity
     * @return id of the new entity
     */
    public String create(EntityBody content);

    /**
     * Delete an entity from the data store
     *
     * @param id
     *            the id of the entity to delete
     */
    public void delete(String id);

    /**
     * Change an entity in the data store
     *
     * @param id
     *            the id of the entity to update
     * @param content
     *            the new body of the entity
     * @return if the entity was changed
     */
    public boolean update(String id, EntityBody content);

    /**
     * Change an entity in the data store with only the provided content, the rest remains as is
     *
     * @param id
     *            the id of the entity to update
     * @param content
     *            the new portion of the body of the entity
     * @return if the entity was changed
     */
    public boolean patch(String id, EntityBody content);

    /**
     * Retrieves an entity from the data store
     *
     * @param id
     *            the id of the entity to retrieve
     * @return the body of the entity
     */
    public EntityBody get(String id);

    /**
     * Retrieves an entity from the data store with certain fields added/removed.
     *
     * @param id
     *            the id of the entity to retrieve
     * @param queryParameters
     *            all parameters to be included in query
     * @return the body of the entity
     */
    public EntityBody get(String id, NeutralQuery neutralQuery);

    /**
     * Retrieves an entity from the data store with certain fields added/removed.
     *
     * @param queryParameters
     *            all parameters to be included in query
     * @return the body of the entity
     */
    public Iterable<EntityBody> list(NeutralQuery neutralQuery);

    /**
     * Retrieves an entity from the data store with certain fields added/removed.
     *
     * @param queryParameters
     *            all parameters to be included in query
     * @return the body of the entity
     */
    public Iterable<String> listIds(final NeutralQuery neutralQuery);

    /**
     * Get multiple entities from the data store
     *
     * @param ids
     *            the ids of the entities to retrieve
     * @return the entities matching the given ids
     */
    public Iterable<EntityBody> get(Iterable<String> ids);

    /**
     * Get multiple entities from the data store
     *
     * @param ids
     *            the ids of the entities to retrieve
     * @param sortBy
     *            field to sort the ids by
     * @param sortOrder
     *            sort order
     * @return the entities matching the given ids
     */
    public Iterable<EntityBody> get(Iterable<String> ids, NeutralQuery neutralQuery);

    /**
     * Whether or not an element exists with the given id
     *
     * @param id
     *            the id to check
     * @return true iff there is an entity with this id
     */
    public boolean exists(String id);

    /**
     * Retrieve entity definition
     *
     * @return the definition of the entity
     */
    public EntityDefinition getEntityDefinition();

    /**
     * Retrieve the custom entity associated with the specified entity.
     *
     * @param id
     *            entity id
     * @return custom entity for this combination of entityId and requesting application
     */
    public EntityBody getCustom(String id);

    /**
     * Deletes the custom entity associated with the specified entity and requesting application.
     *
     * @param id
     *            entity id
     */
    public void deleteCustom(String id);

    /**
     * Creates/Updates the custom entity associated with the specified entity and requesting
     * application.
     *
     * There is only one custom entity per entity, application. If one already exists, it will be
     * overwritten.
     *
     * @param id
     *            entity id
     * @param customEntity
     *            custom entity to be saved
     */
    public void createOrUpdateCustom(String id, EntityBody customEntity);

    /**
     * Get the calculated value information
     *
     * @param id the id of the entity to get calculated value information for
     * @return the map of aggregates
     */
    public CalculatedData<String> getCalculatedValues(String id);

    /**
     * Get the aggregate information
     *
     * @param id the id of the entity to get aggregate information for
     * @return the map of aggregates
     */
    public CalculatedData<Map<String, Integer>> getAggregates(String id);

    /**
     * check if the collection exists in database
     *
     * @param collection
     *            : name of the collection
     * @return
     */
    public boolean collectionExists(String collection);

}
