package org.slc.sli.modeling.xmi.reader;

/**
 * Intentionally package-protected for use by the {@link XmiReader} only.
 */
final class XmiBadModelElementException extends RuntimeException {
    private static final long serialVersionUID = -4938178032912350951L;
    
    public XmiBadModelElementException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
