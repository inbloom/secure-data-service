package org.slc.sli.ingestion.validation;

/**
 * Validator Interface.
 *
 * @author okrook
 *
 */
public interface Validator<T> {

    /**
     * Validates the object.
     *
     * @param object Object to validate
     * @param callback validation report callback
     * @return true if valid; false otherwise
     */
    boolean isValid(T object, ErrorReport callback);

}
