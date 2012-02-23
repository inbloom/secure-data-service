package org.slc.sli.api.service;

import org.slc.sli.api.service.query.SortOrder;

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
    
    public Iterable<String> getAssociationsWith(String id, int start, int numResults, String queryString,
            String sortBy, SortOrder sortOrder);
    
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
    public Iterable<String> getAssociationsTo(String id, int start, int numResults, String queryString, String sortBy,
            SortOrder sortOrder);
    
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
    public EntityIdList getAssociatedEntitiesWith(String id, int start, int numResults, String queryString,
            String sortBy, SortOrder sortOrder);
    
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
    public EntityIdList getAssociatedEntitiesTo(String id, int start, int numResults, String queryString,
            String sortBy, SortOrder sortOrder);
    
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
    public Iterable<String> getAssociationsFor(String id, int start, int numResults, String queryString, String sortBy,
            SortOrder sortOrder);
    
    /**
     * Get the count of associations associated with a given source entity in the data store
     * 
     * @param id
     *            the id of the target to look for associations from
     * @param queryString
     *            the query string to filter returned collection results
     * @return the number of associations
     */
    public long countAssociationsWith(String id, String queryString);
    
    /**
     * Get the count of associations associated with a given target entity in the data store
     * 
     * @param id
     *            the id of the target to look for associations from
     * @param queryString
     *            the query string to filter returned collection results
     * @return the number of associations
     */
    public long countAssociationsTo(String id, String queryString);
    
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
    public long countAssociationsFor(String id, String queryString);
    
    public static interface EntityIdList extends Iterable<String> {
        /**
         * Returns the total number of entities possible from the request, ignoring paging.
         */
        public long getTotalCount();
    }
    
}
