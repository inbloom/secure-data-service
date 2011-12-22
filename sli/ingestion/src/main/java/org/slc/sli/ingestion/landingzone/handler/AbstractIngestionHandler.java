package org.slc.sli.ingestion.landingzone.handler;

import org.slc.sli.ingestion.validation.DummyValidationReport;
import org.slc.sli.ingestion.validation.ValidationReport;
import org.slc.sli.ingestion.validation.ValidationReportAble;
import org.slc.sli.ingestion.validation.Validator;

/**
 * Abstract class for all handlers.
 *
 * @author dduran
 *
 */
public abstract class AbstractIngestionHandler<T> implements Handler<T> {

    Validator<T> preValidator;

    Validator<T> postValidator;

    abstract void doHandling(T item);

    void pre(T item, ValidationReport validationReport) {
        if (preValidator != null) {
            preValidator.isValid(item, validationReport);
        }
    };

    void post(T item, ValidationReport validationReport) {
        if (postValidator != null) {
            preValidator.isValid(item, validationReport);
        }
    };

    @Override
    public void handle(T item) {

        ValidationReport vr = (item instanceof ValidationReportAble) ? ((ValidationReportAble) item)
                .getValidationReport() : new DummyValidationReport();

        handle(item, vr);
    }

    @Override
    public void handle(T item, ValidationReport vr) {

        pre(item, vr);

        doHandling(item);

        post(item, vr);
    }

    public void setPreValidator(Validator<T> preValidator) {
        this.preValidator = preValidator;
    }

    public void setPostValidator(Validator<T> postValidator) {
        this.postValidator = postValidator;
    }
}
