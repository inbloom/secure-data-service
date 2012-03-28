package org.slc.sli.ingestion.landingzone.validation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * Test ErrorReport. Records and logs each error message.
 *
 * @author Tom Shewchuk <tshewchuk@wgen.net>
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
    public boolean hasErrors() {
        // Return whether messages has entries or not.
        return (!getMessages().isEmpty());
    }

    // Added methods for AVRORecordValidatorTest.
    public List<String> getMessages() {
        return messages;
    }
}
