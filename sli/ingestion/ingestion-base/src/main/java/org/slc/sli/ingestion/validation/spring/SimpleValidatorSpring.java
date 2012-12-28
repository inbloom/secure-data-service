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

package org.slc.sli.ingestion.validation.spring;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.MessageCode;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.util.spring.MessageSourceHelper;
import org.slc.sli.ingestion.validation.Validator;

/**
 * Abstract validator with Spring MessageSource support.
 *
 * @author okrook
 *
 * @param <T>
 *            Type of the object being validated
 */
public abstract class SimpleValidatorSpring<T> implements Validator<T>, MessageSourceAware {

    private MessageSource messageSource;

    /**
     * Helper to report errors.
     * @param report
     *            report implementation for tracking and persisting errors.
     * @param reportStats
     *            statistics to be updated
     * @param source
     *            TODO
     * @param code
     *            code associated with this error.
     * @param args
     *            optional arguments for substitution into message.
     */
    protected void error(AbstractMessageReport report, ReportStats reportStats, Source source,
            MessageCode code, Object... args) {
        if (report != null) {
            report.error(reportStats, source, code, args);
        }
    }

    /**
     * Helper to report warnings.
     *
     * @param report
     *            report implementation for tracking and persisting warnings.
     * @param reportStats
     *            statistics to be updated
     * @param source
     *            TODO
     * @param code
     *            code associated with this warning.
     * @param args
     *            optional arguments for substitution into message.
     */
    protected void warn(AbstractMessageReport report, ReportStats reportStats, Source source, MessageCode code,
            Object... args) {
        if (report != null) {
            report.warning(reportStats, source, code, args);
        }
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    protected String getFailureMessage(String code, Object... args) {
        return MessageSourceHelper.getMessage(messageSource, code, args);
    }

}
