package org.slc.sli.api.service;

import java.util.Map;

/**
 * Extension of EntityService for associations.
 */
public interface AssociationService extends EntityService {
    /**
     * Get the entities associated with a given source entity in the data store
     * 
     * @param id
     *            the id of the entity to look for associations for
     * @return the ids of associated entities
     */
    public Iterable<String> getAssociatedWith(String id, int start, int numResults, Map<String, String> queryFields);
    
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
    public Iterable<String> getAssociatedTo(String id, int start, int numResults, Map<String, String> queryFields);

}
