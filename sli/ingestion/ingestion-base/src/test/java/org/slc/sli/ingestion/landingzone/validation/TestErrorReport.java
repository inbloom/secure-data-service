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


package org.slc.sli.ingestion.landingzone.validation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * Test ErrorReport. Records and logs each error message.
 *
 * @author Tom Shewchuk
 *
 */
public final class TestErrorReport implements ErrorReport {

    // Added members for AVRORecordValidatorTest.
    private List<String> messages = new ArrayList<String>();

    private static final Logger LOG = LoggerFactory.getLogger(TestErrorReport.class);

    @Override
    public void fatal(String message, Object sender) {
        messages.add(message);
        LOG.error("FATAL - " + message);
    }

    @Override
    public void error(String message, Object sender) {
        messages.add(message);
        LOG.error("ERROR - " + message);
    }

    @Override
    public void warning(String message, Object sender) {
        messages.add(message);
        LOG.error("WARNING - " + message);
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
        // Return whether messages has entries or not.
        return (!getMessages().isEmpty());
    }

    // Added methods for AVRORecordValidatorTest.
    public List<String> getMessages() {
        return messages;
    }
}
