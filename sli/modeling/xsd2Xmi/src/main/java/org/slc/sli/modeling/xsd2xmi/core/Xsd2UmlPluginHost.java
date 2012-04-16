package org.slc.sli.modeling.xsd2xmi.core;

import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.TagDefinition;

/**
 * Provides information required by an {@link Xsd2UmlPlugin}.
 */
public interface Xsd2UmlPluginHost {
    
    /**
     * Ensures that there is a tag definition for the specified name and obtains the identifier.
     * 
     * @param name
     *            The name of the tag definition.
     * @return The identifier of the tag definition.
     */
    Identifier ensureTagDefinitionId(final String name);
    
    /**
     * Returns the tag definition for the specified identifier.
     * 
     * @param id
     *            The identifier of the tag definition.
     * @return The tag definition corresponding to the identifier.
     */
    TagDefinition getTagDefinition(Identifier id);
}
