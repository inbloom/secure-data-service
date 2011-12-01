package org.slc.sli.api.service;

import java.util.Map;

/**
 * Interface for objects used to transform entities between their database representations and their
 * exposed representations on the ReST URI.
 * 
 * The following invarients must hold:
 * --Transformer.toStored(Transformer.toExposed(entity)).equals(entity)
 * --Transformer.toExposed(Transformer.toStored(entity)).equals(entity)
 * 
 * @author nbrown
 * 
 */
public interface Transformer {
    
    /**
     * Transform from an exposed entity to a stored entity
     * 
     * @param exposed
     *            The entity in the form it is exposed via ReST
     * @return The entity in the form it is stored in the DB
     */
    public Map<String, Object> toStored(Map<String, Object> exposed);
    
    /**
     * Transform from a stored entity to an exposed entity
     * 
     * @param exposed
     *            The entity in the form it is stored in the DB
     * @return The entity in the form it is exposed via ReST
     */
    public Map<String, Object> toExposed(Map<String, Object> stored);
    
}
