package org.slc.sli.ingestion.validation;

/**
 * Validation reporting callback;
 *
 * @author okrook
 *
 */
public interface ValidationReport {

    /**
     * Callback for validation failure reporting.
     *
     * @param message Validation message
     * @param sender Sender of the message
     */
    void fail(String message, Object sender);

}
