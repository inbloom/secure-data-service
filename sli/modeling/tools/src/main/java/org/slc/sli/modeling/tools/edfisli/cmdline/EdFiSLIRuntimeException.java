package org.slc.sli.modeling.tools.edfisli.cmdline;

public class EdFiSLIRuntimeException extends RuntimeException {

    public EdFiSLIRuntimeException(Exception e) {
        super(e);
    }

    public EdFiSLIRuntimeException(String message) {
        super(message);
    }

    public EdFiSLIRuntimeException(String message, Exception e) {
        super(message, e);
    }
}
