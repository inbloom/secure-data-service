package org.slc.sli.modeling.xmi.reader;

/**
 * Intentionally package-protected for use by the {@link XmiReader} only.
 */
final class XmiMissingAttributeException extends RuntimeException {
    private static final long serialVersionUID = -4938178032912350951L;
    
    public XmiMissingAttributeException(final String msg) {
        super(msg);
    }
}
