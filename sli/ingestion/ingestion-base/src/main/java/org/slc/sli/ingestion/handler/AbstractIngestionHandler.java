package org.slc.sli.ingestion.handler;

import java.util.List;

import org.slc.sli.ingestion.FileProcessStatus;
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

    List<? extends Validator<T>> preValidators;

    List<? extends Validator<T>> postValidators;

    protected abstract O doHandling(T item, ErrorReport errorReport, FileProcessStatus fileProcessStatus);

    void pre(T item, ErrorReport errorReport) {
        if (preValidators != null) {
            for (Validator<T> validator : preValidators) {
                validator.isValid(item, errorReport);
            }
        }
    };

    void post(T item, ErrorReport errorReport) {
        if (postValidators != null) {
            for (Validator<T> validator : postValidators) {
                validator.isValid(item, errorReport);
            }
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
        return handle(item, errorReport, new FileProcessStatus());
    }

    public O handle(T item, ErrorReport errorReport, FileProcessStatus fileProcessStatus) {

        O o = null;

        pre(item, errorReport);

        if (!errorReport.hasErrors()) {

            o = doHandling(item, errorReport, fileProcessStatus);

            post(item, errorReport);
        }

        return o;
    }

    public void setPreValidators(List<Validator<T>> preValidators) {
        this.preValidators = preValidators;
    }

    public void setPostValidators(List<Validator<T>> postValidators) {
        this.postValidators = postValidators;
    }
}
