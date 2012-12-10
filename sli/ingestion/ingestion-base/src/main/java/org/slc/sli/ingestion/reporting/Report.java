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
 * The interface is intended for ingestion engine to report user-facing warnings/errors;
 *
 * @author okrook
 *
 */
public interface Report {

    /**
     * Reports a message as error.
     *
     * @param source
     *            Source of the report
     * @param code
     *            message defined by a code
     */
    void error(Source source, MessageCode code);

    /**
     * Reports a message as error.
     *
     * @param source
     *            Source of the report
     * @param code
     *            message defined by a code
     * @param args
     *            additional arguments for the message
     */
    void error(Source source, MessageCode code, Object... args);

    /**
     * Reports a message as warning.
     *
     * @param source
     *            Source of the report
     * @param code
     *            message defined by a code
     */
    void warning(Source source, MessageCode code);

    /**
     * Reports a message as warning.
     *
     * @param source
     *            Source of the report
     * @param code
     *            message defined by a code
     * @param args
     *            additional arguments for the message
     */
    void warning(Source source, MessageCode code, Object... args);

    /**
     * Indicates whether this Report contains any errors.
     *
     * @return true if errors have been reported
     */
    boolean hasErrors();

}
