package org.slc.sli.common.util.logging;

/**
 * Exception indicates there is a problem initializing LoggingUtils
 *
 */
public class LoggingUtilException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public LoggingUtilException(final Throwable throwable) {
        super(throwable);
    }
}
