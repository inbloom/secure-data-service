package org.slc.sli.api.service;

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
     * @return the ids of association entities
     */

    public Iterable<String> getAssociationsWith(String id, int start, int numResults, String queryString);

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
     * @return the ids of associations associated to the given entity
     */
    public Iterable<String> getAssociationsTo(String id, int start, int numResults, String queryString);

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
     * @return the ids of associated entities
     */
    public Iterable<String> getAssociatedEntitiesWith(String id, int start, int numResults, String queryString);

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
     * @return the ids of entities associated to the given entity
     */
    public Iterable<String> getAssociatedEntitiesTo(String id, int start, int numResults, String queryString);

    /**
     * Gets the entities associated with a given entity in the data store. Checks both target and source
     *
     * @param id
     *            the id of the target to look for associations from
     * @param start
     *            the index of the first entity in the data store to return
     * @param numResults
     *            the number of results to return
     * @param queryString
     *            the query string to filter returned collection results
     * @return the ids of entities associated to the given entity
     */
    public Iterable<String> getAssociationsFor(String id, int start, int numResults, String queryString);

}
