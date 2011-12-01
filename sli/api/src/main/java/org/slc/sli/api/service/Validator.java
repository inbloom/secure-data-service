package org.slc.sli.api.service;

import java.util.Map;

/**
 * Class to validate new or modified entities.
 * 
 * @author nbrown
 * 
 */
public interface Validator {
    /**
     * Determine whether or not the given entity is valid
     * 
     * @param entity
     *            the entity to validate
     * @return true iff the entity is valid
     */
    public boolean validate(Map<String, Object> entity);
}
