package org.slc.sli.modeling.sdkgen;

public class SdkGenRuntimeException extends RuntimeException {

    public SdkGenRuntimeException(Exception e) {
        super(e);
    }

    public SdkGenRuntimeException(String message) {
        super(message);
    }

    public SdkGenRuntimeException(String message, Exception e) {
        super(message, e);
    }
}
