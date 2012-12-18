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

/**
 * Interface for keeping track of stats for errors and warnings for an arbitrary context scope;
 *
 * @author dduran
 *
 */
public abstract class AbstractReportStats {

    private final Source source;

    /**
     * Source constructor
     *
     * @param source
     *            non-null Source object giving context to the stats.
     * @throws IllegalArgumentException
     *             if source is <code>null</code>
     */
    public AbstractReportStats(Source source) {
        if (source == null) {
            throw new IllegalArgumentException("source cannot be null");
        }
        this.source = source;
    }

    /**
     * The source that the stats correspond to.
     *
     * @return Source object
     */
    public Source getSource() {
        return source;
    }

    /**
     * Increase the error count by one.
     */
    public abstract void incError();

    /**
     * Increase the warning count by one.
     */
    public abstract void incWarning();

    /**
     * Report whether any errors have been reported for this context;
     *
     * @return <code>true</code> if one or more errors.
     */
    public abstract boolean hasErrors();

    /**
     * Report whether any warnings have been reported for this context;
     *
     * @return <code>true</code> if one or more warnings.
     */
    public abstract boolean hasWarnings();

}
