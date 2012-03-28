package org.slc.sli.ingestion.validation;

/**
 * Abstract validator.
 *
 * @author okrook
 *
 */
public abstract class SimpleValidator<T> implements Validator<T> {

    @Override
    public abstract boolean isValid(T object, ErrorReport callback);

    /**
     * Helper to report a validation failure.
     *
     * @param report Validation report callback
     * @param message Validation message
     */
    protected void fail(ErrorReport report, String message) {
        if (report != null) {
            report.error(message, this);
        }
    }

}
