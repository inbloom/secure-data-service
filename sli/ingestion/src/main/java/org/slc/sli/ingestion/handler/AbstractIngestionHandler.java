package org.slc.sli.ingestion.handler;

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

    abstract O doHandling(T item, ErrorReport errorReport);

    void pre(T item, ErrorReport errorReport) {
        if (preValidator != null) {
            preValidator.isValid(item, errorReport);
        }
    };

    void post(T item, ErrorReport errorReport) {
        if (postValidator != null) {
            preValidator.isValid(item, errorReport);
        }
    };

    @Override
    public O handle(T item) {

        ErrorReport errorReport = null;
        if (item instanceof ErrorReportSupport) {
            errorReport = ((ErrorReportSupport) item).getErrorReport();
        } else {
            errorReport = new DummyErrorReport();
        }

        return handle(item, errorReport);
    }

    @Override
    public O handle(T item, ErrorReport errorReport) {
        O o;

        pre(item, errorReport);

        o = doHandling(item, errorReport);

        post(item, errorReport);

        return o;
    }

    public void setPreValidator(Validator<T> preValidator) {
        this.preValidator = preValidator;
    }

    public void setPostValidator(Validator<T> postValidator) {
        this.postValidator = postValidator;
    }
}
