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

/**
 * Proxy ErrorReport. Plays as a proxy. Keeps local hasError flag.
 *
 * @author dduran
 *
 */
public final class ProxyErrorReport implements ErrorReport {

    private ErrorReport innerErrorReport;
    private boolean hasErrors;

    public ProxyErrorReport(ErrorReport errorReport) {
        this.innerErrorReport = errorReport;
    }

    @Override
    public void fatal(String message, Object sender) {
        this.innerErrorReport.fatal(message, sender);
        this.hasErrors = true;
    }

    @Override
    public void error(String message, Object sender) {
        this.innerErrorReport.error(message, sender);
        this.hasErrors = true;
    }

    @Override
    public void warning(String message, Object sender) {
        this.innerErrorReport.warning(message, sender);
    }

    @Override
    public void fatal(String message, String resourceId, Object sender) {
        this.innerErrorReport.fatal(message, resourceId, sender);
        this.hasErrors = true;
    }

    @Override
    public void error(String message, String resourceId, Object sender) {
        this.innerErrorReport.error(message, resourceId, sender);
        this.hasErrors = true;
    }

    @Override
    public void warning(String message, String resourceId, Object sender) {
        this.innerErrorReport.warning(message, resourceId, sender);
    }

    @Override
    public boolean hasErrors() {
        return this.hasErrors;
    }

}
