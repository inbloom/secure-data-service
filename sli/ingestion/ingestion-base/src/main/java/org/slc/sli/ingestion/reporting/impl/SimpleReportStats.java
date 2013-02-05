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

package org.slc.sli.ingestion.reporting.impl;

import org.slc.sli.ingestion.reporting.ReportStats;

/**
 * Simple implementation of AbstractReportStats
 *
 * @author dduran
 *
 */
public final class SimpleReportStats implements ReportStats {

    private long errorCount = 0L;
    private long warningCount = 0L;

    public SimpleReportStats() {
        // used by unit test
    }

    @Override
    public void incError() {
        errorCount++;
    }

    @Override
    public void incWarning() {
        warningCount++;
    }

    @Override
    public boolean hasErrors() {
        return errorCount > 0;
    }

    @Override
    public boolean hasWarnings() {
        return warningCount > 0;
    }

    @Override
    public long getErrorCount() {
        return errorCount;
    }

    @Override
    public long getWarningCount() {
        return warningCount;
    }

}
