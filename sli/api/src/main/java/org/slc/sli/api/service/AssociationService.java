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

import org.slc.sli.domain.NeutralQuery;

/**
 * Extension of EntityService for associations.
 */
public interface AssociationService extends EntityService {
    /**
     * Get the associations associated with a given source entity in the data store
     * 
     * @param id
     *            the id of the entity to look for associations for
     * @param start
     *            the index of the first entity in the data store to return
     * @param numResults
     *            the number of results to return
     * @param queryString
     *            the query string to filter returned collection results
     * @param sortBy
     *            String indicating which fields should be used to sort by
     * @param sortOrder
     *            sort order
     * @return the ids of association entities
     */
    
    public Iterable<String> getAssociationsWith(String id, NeutralQuery neutralQuery);
    
    /**
     * Gets the associations associated with a given target entity in the data store
     * 
     * @param id
     *            the id of the target to look for associations from
     * @param start
     *            the index of the first entity in the data store to return
     * @param numResults
     *            the number of results to return
     * @param queryString
     *            the query string to filter returned collection results
     * @param sortBy
     *            String indicating which fields should be used to sort by
     * @param sortOrder
     *            sort order
     * @return the ids of associations associated to the given entity
     */
    public Iterable<String> getAssociationsTo(String id, NeutralQuery neutralQuery);
    
    /**
     * Get the entities associated with a given source entity in the data store
     * 
     * @param id
     *            the id of the entity to look for associations for
     * @param start
     *            the index of the first entity in the data store to return
     * @param numResults
     *            the number of results to return
     * @param queryString
     *            the query string to filter returned collection results
     * @param sortBy
     *            String indicating which fields should be used to sort by
     * @param sortOrder
     *            sort order
     * @return the ids of associated entities
     */
    public EntityIdList getAssociatedEntitiesWith(String id, NeutralQuery neutralQuery);
    
    /**
     * Gets the entities associated with a given target entity in the data store
     * 
     * @param id
     *            the id of the target to look for associations from
     * @param start
     *            the index of the first entity in the data store to return
     * @param numResults
     *            the number of results to return
     * @param queryString
     *            the query string to filter returned collection results
     * @param sortBy
     *            String indicating which fields should be used to sort by
     * @param sortOrder
     *            sort order
     * @return the ids of entities associated to the given entity
     */
    public EntityIdList getAssociatedEntitiesTo(String id, NeutralQuery neutralQuery);
    
    /**
     * Gets the entities associated with a given entity in the data store. Checks both target and
     * source
     * 
     * @param id
     *            the id of the target to look for associations from
     * @param start
     *            the index of the first entity in the data store to return
     * @param numResults
     *            the number of results to return
     * @param queryString
     *            the query string to filter returned collection results
     * @param sortBy
     *            String indicating which fields should be used to sort by
     * @param sortOrder
     *            sort order
     * @return the ids of entities associated to the given entity
     */
    public Iterable<String> getAssociationsFor(String id, NeutralQuery neutralQuery);
    
    /**
     * Get the count of associations associated with a given source entity in the data store
     * 
     * @param id
     *            the id of the target to look for associations from
     * @param queryString
     *            the query string to filter returned collection results
     * @return the number of associations
     */
    public long countAssociationsWith(String id, NeutralQuery neutralQuery);
    
    /**
     * Get the count of associations associated with a given target entity in the data store
     * 
     * @param id
     *            the id of the target to look for associations from
     * @param queryString
     *            the query string to filter returned collection results
     * @return the number of associations
     */
    public long countAssociationsTo(String id, NeutralQuery neutralQuery);
    
    /**
     * Gets the count of entities associated with a given entity in the data store. Checks both
     * target and source
     * 
     * @param id
     *            the id of the target to look for associations from
     * @param queryString
     *            the query string to filter returned collection results
     * @return the number of associations
     */
    public long countAssociationsFor(String id, NeutralQuery neutralQuery);
    
    /**
     * 
     */
    public static interface EntityIdList extends Iterable<String> {
        /**
         * Returns the total number of entities possible from the request, ignoring paging.
         */
        public long getTotalCount();
    }
    
}
