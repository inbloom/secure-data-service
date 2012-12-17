/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.ingestion.handler;

import java.util.List;

import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.AbstractReportStats;
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

    protected abstract List<O> doHandling(List<T> items, ErrorReport errorReport, FileProcessStatus fileProcessStatus);

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

    // TODO: switch this to use new interface methods once everything is migrated over.
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

    @Override
    public List<O> handle(List<T> items, ErrorReport errorReport) {
        return handle(items, errorReport, new FileProcessStatus());
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

    public List<O> handle(List<T> items, ErrorReport errorReport, FileProcessStatus fileProcessStatus) {
        // TODO: add pre and post validation that will iterate through the list and perform
        // appropriate validations
        if (!errorReport.hasErrors()) {
            return doHandling(items, errorReport, fileProcessStatus);
        }
        return null;
    }

    public void setPreValidators(List<Validator<T>> preValidators) {
        this.preValidators = preValidators;
    }

    public void setPostValidators(List<Validator<T>> postValidators) {
        this.postValidators = postValidators;
    }

    /////// NEW INTERFACE METHODS ///////

    protected abstract O doHandling(T item, AbstractMessageReport report, AbstractReportStats reportStats,
            FileProcessStatus fileProcessStatus);

    protected abstract List<O> doHandling(List<T> items, AbstractMessageReport report, AbstractReportStats reportStats,
            FileProcessStatus fileProcessStatus);

    void pre(T item, AbstractMessageReport report, AbstractReportStats reportStats) {
        if (preValidators != null) {
            for (Validator<T> validator : preValidators) {
                validator.isValid(item, report, reportStats);
            }
        }
    };

    void post(T item, AbstractMessageReport report, AbstractReportStats reportStats) {
        if (postValidators != null) {
            for (Validator<T> validator : postValidators) {
                validator.isValid(item, report, reportStats);
            }
        }
    };

    @Override
    public O handle(T item, AbstractMessageReport report, AbstractReportStats reportStats) {
        return handle(item, report, reportStats, new FileProcessStatus());
    }

    public O handle(T item, AbstractMessageReport report, AbstractReportStats reportStats, FileProcessStatus fileProcessStatus) {
        O o = null;
        pre(item, report, reportStats);
        if (!reportStats.hasErrors()) {
            o = doHandling(item, report, reportStats, fileProcessStatus);
            post(item, report, reportStats);
        }
        return o;
    }

    @Override
    public List<O> handle(List<T> items,AbstractMessageReport report, AbstractReportStats reportStats) {
        return handle(items, report, reportStats, new FileProcessStatus());
    }

    public List<O> handle(List<T> items, AbstractMessageReport report, AbstractReportStats reportStats, FileProcessStatus fileProcessStatus) {
        // TODO: add pre and post validation that will iterate through the list and perform
        // appropriate validations
        if (!reportStats.hasErrors()) {
            return doHandling(items, report, reportStats, fileProcessStatus);
        }
        return null;
    }
}
