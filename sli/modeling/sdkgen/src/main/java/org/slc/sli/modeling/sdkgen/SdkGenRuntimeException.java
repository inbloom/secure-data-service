package org.slc.sli.modeling.sdkgen;

/**
 * SDK Generation exception.
 */
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
