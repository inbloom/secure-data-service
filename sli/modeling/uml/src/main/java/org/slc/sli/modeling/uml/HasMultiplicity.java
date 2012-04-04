package org.slc.sli.modeling.uml;

/**
 * Provides the multiplicity of a model element.
 */
public interface HasMultiplicity {
    /**
     * The multiplicity of the model element. Never <code>null</code>.
     */
    Multiplicity getMultiplicity();
}
