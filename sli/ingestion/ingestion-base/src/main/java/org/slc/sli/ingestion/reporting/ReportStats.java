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

/**
 * Interface for keeping track of stats for errors and warnings for an arbitrary context scope;
 *
 * @author dduran
 *
 */
public interface ReportStats {

    /**
     * Increase the error count by one.
     */
    void incError();

    /**
     * Get the number of errors tracked.
     *
     * @return
     */
    long getErrorCount();

    /**
     * Increase the warning count by one.
     */
    void incWarning();

    /**
     * Get the number of warnings tracked.
     *
     * @return
     */
    long getWarningCount();

    /**
     * Report whether any errors have been reported for this context;
     *
     * @return <code>true</code> if one or more errors.
     */
    boolean hasErrors();

    /**
     * Report whether any warnings have been reported for this context;
     *
     * @return <code>true</code> if one or more warnings.
     */
    boolean hasWarnings();

}
