package org.slc.sli.modeling.uml;

import java.util.List;

/**
 * A field of a class.
 */
public final class Attribute extends AbstractModelElement implements HasName {
    /**
     * The name of the attribute. Never <code>null</code>.
     */
    private final String name;
    /**
     * The type of the attribute. Never <code>null</code>.
     */
    private final Reference type;
    /**
     * The lower bound of the attribute. Typically 0 or 1. Never <code>null</code>.
     */
    private final Multiplicity multiplicity;
    
    private final LazyLookup lookup;
    
    public Attribute(final Identifier id, final String name, final Reference type, final Multiplicity multiplicity,
            final List<TaggedValue> taggedValues, final LazyLookup lookup) {
        super(id, taggedValues);
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (type == null) {
            throw new NullPointerException("type");
        }
        if (multiplicity == null) {
            throw new NullPointerException("multiplicity");
        }
        if (lookup == null) {
            throw new NullPointerException("lookup");
        }
        this.name = name;
        this.type = type;
        this.multiplicity = multiplicity;
        this.lookup = lookup;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    public Type getType() {
        try {
            return lookup.getType(type);
        } catch (final RuntimeException e) {
            throw new RuntimeException(name, e);
        }
    }
    
    public Multiplicity getMultiplicity() {
        return multiplicity;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        // sb.append("id: " + id).append(", ");
        sb.append("name: " + name);
        sb.append(", ");
        sb.append("type: " + getType().getName());
        sb.append(", ");
        sb.append("multiplicity: " + multiplicity);
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}