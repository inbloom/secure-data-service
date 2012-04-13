package org.slc.sli.ingestion.validation;

import java.util.List;

/**
 * Abstract validator.
 *
 * @author okrook
 *
 */
public class ComplexValidator<T> extends SimpleValidator<T> {
    private List<? extends Validator<T>> validators;

    public List<? extends Validator<T>> getValidators() {
        return validators;
    }

    public void setValidators(List<? extends Validator<T>> validators) {
        this.validators = validators;
    }

    @Override
    public boolean isValid(T object, ErrorReport callback) {
        for (Validator<T> validator : validators) {
            if (!validator.isValid(object, callback)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Helper to report a validation failure.
     *
     * @param report Validation report callback
     * @param message Validation message
     */
    @Override
    protected void fail(ErrorReport report, String message) {
        if (report != null) {
            report.error(message, this);
        }
    }

}
