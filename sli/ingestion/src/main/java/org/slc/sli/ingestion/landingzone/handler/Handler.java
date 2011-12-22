package org.slc.sli.ingestion.landingzone.handler;

import org.slc.sli.ingestion.validation.ValidationReport;

/**
 *
 * @author dduran
 *
 * @param <T>
 */
public interface Handler<T> {

    /**
     * Handle the provided type.
     *
     * @param item
     */
    void handle(T item);

    /**
     * Handle the provided type and utilize the provided ValidationReport for any type of validation
     * that may be implemented.
     *
     * @param item
     * @param vr
     */
    void handle(T item, ValidationReport vr);

}
