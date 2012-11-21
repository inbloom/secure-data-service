package org.slc.sli.modeling.tools.edfisli.cmdline;

/**
 * Exception for EdFiSLICmdLineTest utility.
 */
public class EdFiSLIRuntimeException extends RuntimeException {

    public EdFiSLIRuntimeException(Throwable cause) {
        super(cause);
    }

    public EdFiSLIRuntimeException(String message) {
        super(message);
    }

    public EdFiSLIRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
