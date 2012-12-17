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

package org.slc.sli.ingestion.reporting;

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
     *            statistics state and source
     * @param code
     *            message defined by a code
     * @param args
     *            additional arguments for the message
     */
    public void error(ReportStats reportStats, MessageCode code, Object... args) {

        if (reportStats != null) {
            reportStats.incError();
        }
        reportError(reportStats, code, args);
    }

    /**
     * Reports an message as a warning and updates the wider-scope warning state in the provided
     * reportStats. Will also log the warning message if the implementation supports logging.
     *
     * @param reportStats
     *            statistics state and source
     * @param code
     *            message defined by a code
     * @param args
     *            additional arguments for the message
     */
    public void warning(ReportStats reportStats, MessageCode code, Object... args) {

        if (reportStats != null) {
            reportStats.incWarning();
        }
        reportWarning(reportStats, code, args);
    }

    /**
     * Look up the corresponding message for a MessageCode.
     *
     * @param code
     * @param args
     * @return Message String mapped to the provided MessageCode, if one exists, with any args
     *         provided substituted in. If no message is mapped for this code, return #?CODE?# were
     *         CODE is the MessageCode provided.
     */
    protected String getMessage(MessageCode code, Object... args) {
        return messageSource.getMessage(code.getCode(), args, "#?" + code.getCode() + "?#", null);
    }

    protected abstract void reportError(ReportStats reportStats, MessageCode code, Object... args);

    protected abstract void reportWarning(ReportStats reportStats, MessageCode code, Object... args);

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

}
