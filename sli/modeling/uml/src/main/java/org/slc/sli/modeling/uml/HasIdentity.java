package org.slc.sli.modeling.uml;

/**
 * Provides the unique identifier for the model element.
 * 
 * If something has identity then it is meaningful to label it with a synthetic identifier.
 */
public interface HasIdentity {
    
    Identifier getId();
}
