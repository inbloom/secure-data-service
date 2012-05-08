package org.slc.sli.modeling.uml;

import java.util.List;

/**
 * A UML TagDefinition defines a the type of a TaggedValue.
 */
public final class TagDefinition extends AbstractModelElement implements HasName, HasMultiplicity {
    private final String name;
    private final Multiplicity multiplicity;
    
    public TagDefinition(final Identifier id, final List<TaggedValue> taggedValues, final String name,
            final Multiplicity multiplicity) {
        super(id, taggedValues);
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
        if (multiplicity == null) {
            throw new NullPointerException("multiplicity");
        }
        this.multiplicity = multiplicity;
    }
    
    public Reference getReference() {
        return new Reference(getId(), ReferenceType.TAG_DEFINITION);
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public Multiplicity getMultiplicity() {
        return multiplicity;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        // sb.append("id: " + id).append(", ");
        sb.append("name: " + name).append(", ");
        sb.append("multiplicity: " + multiplicity);
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}
