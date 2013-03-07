/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

package org.slc.sli.ingestion.reporting;

import java.text.MessageFormat;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

/**
 * Abstract class for reporting user-facing errors and warnings.
 *
 * @author dduran
 *
 */
public abstract class AbstractMessageReport implements MessageSourceAware {

    protected MessageSource messageSource;

    /**
     * Reports an message as an error and updates the wider-scope error state in the provided
     * reportStats. Will also log the error message if the implementation supports logging.
     *
     * @param reportStats
     *            statistics state
     * @param source
     *            source where the error is from
     * @param code
     *            message defined by a code
     * @param args
     *            additional arguments for the message
     * @throws IllegalStateException
     *             if reportStats is <code>null</code>
     */
    public void error(ReportStats reportStats, Source source, MessageCode code, Object... args) {
        if (reportStats == null || source == null) {
            throw new IllegalStateException();
        }

        reportStats.incError();

        reportError(reportStats, source, code, args);
    }

    /**
     * Method to report an error but not treat it as an error during pre validation to match the behavior of the parser
     * This should be deleted after US5390
     * @param reportStats
     * @param source
     * @param code
     * @param args
     */
    public void warningAsError(ReportStats reportStats, Source source, MessageCode code, Object... args) {
        if (reportStats == null || source == null) {
            throw new IllegalStateException();
        }

        reportStats.incWarning();
        reportError(reportStats, source, code, args);
    }

    /**
     * Reports an message as a warning and updates the wider-scope warning state in the provided
     * reportStats. Will also log the warning message if the implementation supports logging.
     *
     * @param reportStats
     *            statistics state
     * @param source
     *            source where the warning is from
     * @param code
     *            message defined by a code
     * @param args
     *            additional arguments for the message
     * @throws IllegalStateException
     *             if reportStats is <code>null</code>
     */
    public void warning(ReportStats reportStats, Source source, MessageCode code, Object... args) {
        if (reportStats == null) {
            throw new IllegalStateException();
        }

        reportStats.incWarning();

        reportWarning(reportStats, source, code, args);
    }

    /**
     * Reports an message as a info
     *
     * @param reportStats
     *            statistics state
     * @param source
     *            source where the info is from
     * @param code
     *            message defined by a code
     * @param args
     *            additional arguments for the message
     * @throws IllegalStateException
     *             if reportStats is <code>null</code>
     */
    public void info(ReportStats reportStats, Source source, MessageCode code, Object... args) {
        if (reportStats == null) {
            throw new IllegalStateException();
        }

        reportInfo(reportStats, source, code, args);
    }

    /**
     * Look up the corresponding message for a MessageCode.
     *
     * @param reportStats
     * @param source
     * @param code
     * @param args
     * @return Message String mapped to the provided MessageCode, if one exists, with any args
     *         provided substituted in. If no message is mapped for this code, return #?CODE?# were
     *         CODE is the MessageCode provided.
     */
    protected String getMessage(ReportStats reportStats, Source source, MessageCode code, Object... args) {

        Object[] arguments = { code.getCode(), source.getUserFriendlyMessage(),
                messageSource.getMessage(code.getCode(), args, "#?" + code.getCode() + "?#", null) };
        return MessageFormat.format("{0}:[{1}]-{2}", arguments);
    }

    protected abstract void reportError(ReportStats reportStats, Source source, MessageCode code,
            Object... args);

    protected abstract void reportWarning(ReportStats reportStats, Source source, MessageCode code,
            Object... args);

    protected abstract void reportInfo(ReportStats reportStats, Source source, MessageCode code, Object... args);

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

}
