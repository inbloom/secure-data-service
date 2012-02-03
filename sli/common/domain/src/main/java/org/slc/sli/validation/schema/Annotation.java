package org.slc.sli.validation.schema;


/**
 * Container for an individual Annotation node for SLI.
 * 
 * @author asaarela
 * 
 */
public abstract class Annotation {
    
    /**
     * Supported annotation types.
     */
    public enum AnnotationType {
        UNKNOWN, DOCUMENTATION, NOTATION, APPINFO
    };
    
    /**
     * Get the annotation type associated with this node.
     * 
     * @return AnnotationType
     */
    public abstract AnnotationType getType();
    
    
    @Override
    /**
     * String representation.
     * 
     * @return string
     */
    public abstract String toString();
    
}
