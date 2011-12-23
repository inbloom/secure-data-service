package org.slc.sli.ingestion.landingzone.handler;

import org.slc.sli.ingestion.validation.DummyErrorReport;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.ErrorReportSupport;
import org.slc.sli.ingestion.validation.Validator;

/**
 * Abstract class for all handlers.
 *
 * @author dduran
 *
 */
public abstract class AbstractIngestionHandler<T, O> implements Handler<T, O> {

    Validator<T> preValidator;

    Validator<T> postValidator;

    abstract O doHandling(T item);

    void pre(T item, ErrorReport validationReport) {
        if (preValidator != null) {
            preValidator.isValid(item, validationReport);
        }
    };

    void post(T item, ErrorReport validationReport) {
        if (postValidator != null) {
            preValidator.isValid(item, validationReport);
        }
    };

    @Override
    public O handle(T item) {

        ErrorReport vr = (item instanceof ErrorReportSupport) ? ((ErrorReportSupport) item)
                .getValidationReport() : new DummyErrorReport();

        return handle(item, vr);
    }

    @Override
    public O handle(T item, ErrorReport vr) {
        O o;

        pre(item, vr);

        o = doHandling(item);

        post(item, vr);

        return o;
    }

    public void setPreValidator(Validator<T> preValidator) {
        this.preValidator = preValidator;
    }

    public void setPostValidator(Validator<T> postValidator) {
        this.postValidator = postValidator;
    }
}
