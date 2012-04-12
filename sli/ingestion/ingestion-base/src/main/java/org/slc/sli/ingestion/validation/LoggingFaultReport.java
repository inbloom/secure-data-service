package org.slc.sli.ingestion.validation;

import org.slc.sli.ingestion.FaultsReport;
import org.slf4j.Logger;

/**
 * ErrorReport implementation that is constructed with a logger and uses that logger to act on
 * errors and warnings.
 *
 * @author dduran
 *
 */
public class LoggingFaultReport extends FaultsReport {

    private final Logger logger;
    private boolean hasErrors;

    public LoggingFaultReport(Logger logger) {
        this.logger = logger;
        this.hasErrors = false;
    }

    @Override
    public void fatal(String message, Object sender) {
        super.fatal(message, sender);
        logger.error(message);

        if (!hasErrors)
            hasErrors = true;
    }

    @Override
    public void error(String message, Object sender) {
        super.error(message, sender);
        logger.error(message);

        if (!hasErrors)
            hasErrors = true;
    }

    @Override
    public void warning(String message, Object sender) {
        super.warning(message, sender);
        logger.warn(message);
    }

    @Override
    public boolean hasErrors() {
        return hasErrors;
    }

}
