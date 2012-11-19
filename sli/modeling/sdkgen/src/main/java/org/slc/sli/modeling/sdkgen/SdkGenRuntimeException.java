package org.slc.sli.modeling.sdkgen;

public class SdkGenRuntimeException extends RuntimeException {

    public SdkGenRuntimeException(Throwable cause) {
        super(cause);
    }

    public SdkGenRuntimeException(String message) {
        super(message);
    }

    public SdkGenRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
