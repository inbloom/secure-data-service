package org.slc.sli.ingestion.handler;

import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * Generic interface for handlers that requires handle methods with and without error support.
 *
 * @author dduran
 *
 * @param <T>
 *            type to handle
 * @param <O>
 *            type to return
 */
public interface Handler<T, O> {

    /**
     * Handle the provided item.
     *
     * @param item
     *            the object we want to handle
     * @return object defined in concrete implementation
     */
    O handle(T item);

    /**
     * Handle the provided type and utilize the provided ErrorReport to track errors.
     *
     * @param item
     *            the object we want to handle
     * @param errorReport
     *            an ErrorReport implementation in which errors can be tracked
     * @return object defined in concrete implementation
     */
    O handle(T item, ErrorReport errorReport);

}
