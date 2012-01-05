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
     * @return the ids of association entities
     */
    public Iterable<String> getAssociationsWith(String id, int start, int numResults);
    
    /**
     * Gets the associations associated with a given target entity in the data store
     * 
     * @param id
     *            the id of the target to look for associations from
     * @param start
     *            the index of the first entity in the data store to return
     * @param numResults
     *            the number of results to return
     * @return the ids of associations associated to the given entity
     */
    public Iterable<String> getAssociationsTo(String id, int start, int numResults);
    
    /**
     * Get the entities associated with a given source entity in the data store
     * 
     * @param id
     *            the id of the entity to look for associations for
     * @param start
     *            the index of the first entity in the data store to return
     * @param numResults
     *            the number of results to return
     * @return the ids of associated entities
     */
    public Iterable<String> getAssociatedEntitiesWith(String id, int start, int numResults);
    
    /**
     * Gets the entities associated with a given target entity in the data store
     * 
     * @param id
     *            the id of the target to look for associations from
     * @param start
     *            the index of the first entity in the data store to return
     * @param numResults
     *            the number of results to return
     * @return the ids of entities associated to the given entity
     */
    public Iterable<String> getAssociatedEntitiesTo(String id, int start, int numResults);
    
}
