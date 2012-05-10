package org.slc.sli.validation.strategy;

import org.owasp.esapi.errors.ValidationException;

/**
 * Extends ValidationException to enable throwing a ValidationException without
 * creating a ServletRequest (drawback from OWASP ESAPI library)
 *
 * @author vmcglaughlin
 */
public class BlacklistValidationException extends ValidationException {

    private static final long serialVersionUID = 234902835409283542L;

    /**
     * Default constructor, sets logMessage to "default"
     */
    public BlacklistValidationException() {
        this.logMessage = "default";
    }

    /**
     * Constructor for specified message, saves as logMessage
     *
     * @param message
     */
    public BlacklistValidationException(String message) {
        this.logMessage = message;
    }
}
