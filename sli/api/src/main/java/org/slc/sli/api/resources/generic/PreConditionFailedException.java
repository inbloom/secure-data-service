package org.slc.sli.api.resources.generic;

/**
 * Custom exception for pre condition failures
 *
 * @author srupasinghe
 */

public class PreConditionFailedException extends RuntimeException {


    public PreConditionFailedException(String message) {
        super(message);
    }

}
