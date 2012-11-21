package org.slc.sli.modeling.docgen;

/**
 * Created with IntelliJ IDEA.
 * User: pghosh
 * Date: 11/21/12
 * Time: 11:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class DocumentGeneratorRuntimeException extends RuntimeException{
    public DocumentGeneratorRuntimeException(Throwable cause) {
        super(cause);
    }

    public DocumentGeneratorRuntimeException(String message) {
        super(message);
    }

    public DocumentGeneratorRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
