package org.slc.sli.modeling.uml;

import java.util.List;

/**
 * A model element that can be found in a name-space.
 */
public abstract class NamespaceOwnedElement extends NamedModelElement {
    
    public NamespaceOwnedElement(final Identifier id, final String name, final List<TaggedValue> taggedValues) {
        super(id, name, taggedValues);
    }
    
    public NamespaceOwnedElement(final String name, final List<TaggedValue> taggedValues) {
        this(Identifier.random(), name, taggedValues);
    }
    
    public NamespaceOwnedElement(final Identifier id, final String name) {
        this(id, name, EMPTY_TAGGED_VALUES);
    }
    
    public NamespaceOwnedElement(final String name) {
        this(Identifier.random(), name, EMPTY_TAGGED_VALUES);
    }
}
