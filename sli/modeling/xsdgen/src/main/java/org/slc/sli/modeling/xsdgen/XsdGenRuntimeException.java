package org.slc.sli.modeling.xsdgen;

/**
 * A RunTimeException that occurs during the XSD generation process.
 * 
 * @author kmyers
 *
 */
public class XsdGenRuntimeException extends RuntimeException {

    public XsdGenRuntimeException(Throwable cause) {
        super(cause);
    }

    public XsdGenRuntimeException(String message) {
        super(message);
    }

    public XsdGenRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
