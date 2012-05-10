package org.slc.sli.validation.strategy;

import org.owasp.esapi.errors.ValidationException;

public class BlacklistValidationException extends ValidationException {

    /**
    *
    */
    private static final long serialVersionUID = 1L;

    public BlacklistValidationException() {

    }

    public BlacklistValidationException(String message) {
        this.logMessage = message;
    }
}
