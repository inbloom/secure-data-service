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
 * Validation reporting callback;
 *
 * @author okrook
 *
 */
public interface ErrorReport {

    /**
     * Callback for validation fatal error reporting.
     *
     * @param message
     *            Validation message
     * @param sender
     *            Sender of the message
     */
    void fatal(String message, Object sender);

    /**
     * Callback for validation error reporting.
     *
     * @param message
     *            Validation message
     * @param sender
     *            Sender of the message
     */
    void error(String message, Object sender);

    /**
     * Callback for validation warning reporting.
     *
     * @param message
     *            Validation message
     * @param sender
     *            Sender of the message
     */
    void warning(String message, Object sender);

    /**
     * Overloaded callback for validation fatal error reporting.
     *
     * @param message
     *            Validation message
     * @param resourceId
     *            New Resource ID to include
     * @param sender
     *            Sender of the message
     */
    void fatal(String message, String resourceId, Object sender);

    /**
     * Overloaded callback for validation error reporting.
     *
     * @param message
     *            Validation message
     * @param resourceId
     *            New Resource ID to include
     * @param sender
     *            Sender of the message
     */
    void error(String message, String resourceId, Object sender);

    /**
     * Overloaded callback for validation warning reporting.
     *
     * @param message
     *            Validation message
     * @param resourceId
     *            New Resource ID to include
     * @param sender
     *            Sender of the message
     */
    void warning(String message, String resourceId, Object sender);

    /**
     * Indicates whether this ErrorReport contains any errors.
     *
     * @return true if errors have been reported
     */
    boolean hasErrors();

}
