package org.slc.sli.ingestion.validation;

/**
 * Dummy ValidationReport. Does nothing on failures.
 *
 * @author dduran
 *
 */
public final class DummyValidationReport implements ValidationReport {

    @Override
    public void fail(String message, Object sender) {
        // nothing
    }

}
