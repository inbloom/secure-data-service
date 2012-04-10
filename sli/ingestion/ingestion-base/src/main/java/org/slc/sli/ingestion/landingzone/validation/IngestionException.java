package org.slc.sli.ingestion.landingzone.validation;

/**
 * Submission Level Exception class.
 *
 * @author okrook
 *
 */
public class IngestionException extends Exception {

    private static final long serialVersionUID = -6220525338718056313L;

    /**
     * Default constructor.
     */
    public IngestionException() {
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message
     *            the detail message
     */
    public IngestionException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause and a detail message of (cause==null ?
     * null : cause.toString()) (which typically contains the class and detail message of cause).
     *
     * @param cause
     *            the cause
     */
    public IngestionException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message
     *            the detail message
     * @param cause
     *            the cause
     */
    public IngestionException(String message, Throwable cause) {
        super(message, cause);
    }

}
