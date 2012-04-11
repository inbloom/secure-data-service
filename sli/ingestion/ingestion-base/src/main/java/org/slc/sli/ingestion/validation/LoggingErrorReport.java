package org.slc.sli.ingestion.validation;

import org.slf4j.Logger;

/**
 * ErrorReport implementation that is constructed with a logger and uses that logger to act on
 * errors and warnings.
 *
 * @author dduran
 *
 */
public class LoggingErrorReport implements ErrorReport {

    private final Logger logger;
    private boolean hasErrors;

    public LoggingErrorReport(Logger logger) {
        this.logger = logger;
        this.hasErrors = false;
    }

    @Override
    public void fatal(String message, Object sender) {
        logger.error(message);

        if (!hasErrors)
            hasErrors = true;
    }

    @Override
    public void error(String message, Object sender) {
        logger.error(message);

        if (!hasErrors)
            hasErrors = true;
    }

    @Override
    public void warning(String message, Object sender) {
        logger.warn(message);
    }

    @Override
    public boolean hasErrors() {
        return hasErrors;
    }

}
