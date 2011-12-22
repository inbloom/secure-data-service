package org.slc.sli.ingestion.validation;

/**
 * Dummy ValidationReport. Does nothing on failures.
 *
 * @author dduran
 *
 */
public final class DummyErrorReport implements ErrorReport {

    @Override
    public void fatal(String message, Object sender) {
        // nothing
    }
    @Override
    public void error(String message, Object sender) {
        // nothing
    }

    @Override
    public void warning(String message, Object sender) {
        // nothing
    }

}
