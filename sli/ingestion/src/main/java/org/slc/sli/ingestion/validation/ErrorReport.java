package org.slc.sli.ingestion.validation;

/**
 * Validation reporting callback;
 *
 * @author okrook
 *
 */
public interface ErrorReport {

    /**
     * Callback for validation fatal error reporting.
     *
     * @param message
     *            Validation message
     * @param sender
     *            Sender of the message
     */
    void fatal(String message, Object sender);

    /**
     * Callback for validation error reporting.
     *
     * @param message
     *            Validation message
     * @param sender
     *            Sender of the message
     */
    void error(String message, Object sender);

    /**
     * Callback for validation warning reporting.
     *
     * @param message
     *            Validation message
     * @param sender
     *            Sender of the message
     */
    void warning(String message, Object sender);

    /**
     * Indicates whether this ErrorReport contains any errors.
     *
     * @return true if errors have been reported
     */
    boolean hasErrors();

}
