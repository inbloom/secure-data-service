package org.slc.sli.api.service;

public interface AssociationService extends EntityService {
    /**
     * Get the entities associated with a given source entity in the data store
     * 
     * @param id
     *            the id of the entity to look for associations for
     * @return the ids of associated entities
     */
    public Iterable<String> getAssociatedWith(String id, int start, int numResults);
    
}
