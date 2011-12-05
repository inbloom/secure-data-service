package org.slc.sli.api.service;

import org.slc.sli.api.representation.EntityBody;


/**
 * Interface for objects used to transform entities between their database representations and their
 * exposed representations on the ReST URI.
 * 
 * The following invarients must hold:
 * --Treatment.toStored(Transformer.toExposed(object)).equals(object)
 * --Treatment.toExposed(Transformer.toStored(object)).equals(object)
 * 
 * @author nbrown
 * 
 */
public interface Treatment {
    
    /**
     * Transform from an exposed entity to a stored entity
     * 
     * @param exposed
     *            The entity in the form it is exposed via ReST
     * @return The entity in the form it is stored in the DB
     */
    public EntityBody toStored(EntityBody exposed);
    
    /**
     * Transform from a stored entity to an exposed entity
     * 
     * @param exposed
     *            The entity in the form it is stored in the DB
     * @return The entity in the form it is exposed via ReST
     */
    public EntityBody toExposed(EntityBody stored);
    
}
