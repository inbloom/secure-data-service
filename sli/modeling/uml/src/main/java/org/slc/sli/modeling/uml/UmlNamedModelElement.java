package org.slc.sli.modeling.uml;

import java.util.List;

public abstract class UmlNamedModelElement extends UmlModelElement implements HasName {
    private final String name;
    
    public UmlNamedModelElement(final Identifier id, final String name) {
        this(id, name, EMPTY_TAGGED_VALUES);
    }
    
    public UmlNamedModelElement(final Identifier id, final String name, final List<TaggedValue> taggedValues) {
        super(id, taggedValues);
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
    }
    
    public UmlNamedModelElement(final String name) {
        this(Identifier.random(), name, EMPTY_TAGGED_VALUES);
    }
    
    public UmlNamedModelElement(final String name, final List<TaggedValue> taggedValues) {
        this(Identifier.random(), name, taggedValues);
    }
    
    @Override
    public final String getName() {
        return name;
    }
}
