package org.slc.sli.ingestion.validation;

/**
 * Proxy ErrorReport. Plays as a proxy. Keeps local hasError flag.
 *
 * @author dduran
 *
 */
public final class ProxyErrorReport implements ErrorReport {

    private ErrorReport innerErrorReport;
    boolean hasErrors;

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
    public boolean hasErrors() {
        return this.hasErrors;
    }

}
