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

import org.slf4j.Logger;

import org.slc.sli.ingestion.FaultsReport;

/**
 * FaultReport implementation that gathers errors and also logs them to file.
 *
 * @author ldalgado
 *
 */
public class LoggingFaultReport extends FaultsReport {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final Logger logger;
    private boolean hasErrors;

    public LoggingFaultReport(Logger logger) {
        this.logger = logger;
        this.hasErrors = false;
    }

    @Override
    public void fatal(String message, Object sender) {
        super.fatal(message, sender);
        logger.error(message);

        if (!hasErrors) {
            hasErrors = true;
        }
    }

    @Override
    public void error(String message, Object sender) {
        super.error(message, sender);
        logger.error(message);

        if (!hasErrors) {
            hasErrors = true;
        }
    }

    @Override
    public void warning(String message, Object sender) {
        super.warning(message, sender);
        logger.warn(message);
    }

    @Override
    public void fatal(String message, String resourceId, Object sender) {
        fatal(message, sender);
    }

    @Override
    public void error(String message, String resourceId, Object sender) {
        error(message, sender);
    }

    @Override
    public void warning(String message, String resourceId, Object sender) {
        warning(message, sender);
    }

    @Override
    public boolean hasErrors() {
        return hasErrors;
    }

}
