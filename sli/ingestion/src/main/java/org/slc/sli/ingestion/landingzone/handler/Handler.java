package org.slc.sli.ingestion.landingzone.handler;

import org.slc.sli.ingestion.validation.ErrorReport;

/**
 *
 * @author dduran
 *
 * @param <T>
 */
public interface Handler<T, O> {

    /**
     * Handle the provided type.
     *
     * @param item
     */
    O handle(T item);

    /**
     * Handle the provided type and utilize the provided ValidationReport for any type of validation
     * that may be implemented.
     *
     * @param item
     * @param vr
     */
    O handle(T item, ErrorReport vr);

}
