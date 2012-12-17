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

import org.slf4j.Logger;

/**
 * MessageReport implementation that is constructed with a logger and uses that logger to act on
 * errors and warnings
 *
 * @author npandey
 *
 */
public class LoggingMessageReport extends AbstractMessageReport {

    private Logger logger;

    public LoggingMessageReport() {
    }

    public LoggingMessageReport(Logger logger) {
        this.logger = logger;
    }

    @Override
    protected void reportError(ReportStats reportStats, MessageCode code, Object... args) {
        logger.error(getMessage(code, args));
    }

    @Override
    protected void reportWarning(ReportStats reportStats, MessageCode code, Object... args) {
        logger.warn(getMessage(code, args));
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
