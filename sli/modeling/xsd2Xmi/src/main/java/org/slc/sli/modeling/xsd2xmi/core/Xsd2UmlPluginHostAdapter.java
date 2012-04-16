package org.slc.sli.modeling.xsd2xmi.core;

import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.index.Mapper;

/**
 * Intentionally package protected.
 */
final class Xsd2UmlPluginHostAdapter implements Xsd2UmlPluginHost {
    
    private final Mapper mapper;
    
    public Xsd2UmlPluginHostAdapter(final Mapper mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        this.mapper = mapper;
    }
    
    @Override
    public Identifier ensureTagDefinitionId(final String name) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public TagDefinition getTagDefinition(final Identifier id) {
        return mapper.getTagDefinition(id);
    }
}
