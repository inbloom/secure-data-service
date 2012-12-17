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

package org.slc.sli.ingestion.validation;

import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.MessageCode;
import org.slc.sli.ingestion.reporting.ReportStats;

/**
 * Abstract validator.
 *
 * @author okrook
 *
 * @param <T> Type of the object being validated
 */
public abstract class SimpleValidator<T> implements Validator<T> {

    /**
     * Helper to report a validation failure.
     *
     * @param report
     *            Validation report callback
     * @param message
     *            Validation message
     */
    protected void fail(ErrorReport report, String message) {
        if (report != null) {
            report.error(message, this);
        }
    }

    /**
     * Helper to report errors.
     *
     * @param report
     *            report implementation for tracking and persisting errors.
     * @param reportStats
     *            statistics to be updated
     * @param code
     *            code associated with this error.
     * @param args
     *            optional arguments for substitution into message.
     */
    protected void error(AbstractMessageReport report, ReportStats reportStats, MessageCode code, Object... args) {
        if (report != null) {
            report.error(reportStats, code, args);
        }
    }

    /**
     * Helper to report warnings.
     *
     * @param report
     *            report implementation for tracking and persisting warnings.
     * @param reportStats
     *            statistics to be updated
     * @param code
     *            code associated with this warning.
     * @param args
     *            optional arguments for substitution into message.
     */
    protected void warn(AbstractMessageReport report, ReportStats reportStats, MessageCode code, Object... args) {
        if (report != null) {
            report.warning(reportStats, code, args);
        }
    }

}
