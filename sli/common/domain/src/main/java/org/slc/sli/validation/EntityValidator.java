package org.slc.sli.validation;

import org.slc.sli.domain.Entity;



/**
 * Interface that can validate an Entity.
 * @author Sean Melody <smelody@wgen.net>
 *
 */
public interface EntityValidator {
    
    /**
     * Validates the given entity
     * Returns true if the validation was successful or a ValidationException if the validation was unsuccessful.
     * @param entity
     */
    public boolean validate( Entity entity ) throws EntityValidationException;


}
