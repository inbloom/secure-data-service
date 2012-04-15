package org.slc.sli.modeling.uml;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * A field of a class.
 */
public final class Attribute extends AbstractModelElementWithLookup implements HasName, HasType, HasMultiplicity {
    /**
     * The name of the attribute. Never <code>null</code>.
     */
    private final QName name;
    /**
     * The type of the attribute. Never <code>null</code>.
     */
    private final Reference type;
    /**
     * The lower bound of the attribute. Typically 0 or 1. Never <code>null</code>.
     */
    private final Multiplicity multiplicity;
    
    public Attribute(final Identifier id, final QName name, final Reference type, final Multiplicity multiplicity,
            final List<TaggedValue> taggedValues, final LazyLookup lookup) {
        super(id, ReferenceType.ATTRIBUTE, taggedValues, lookup);
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
    }
    
    @Override
    public QName getName() {
        return name;
    }
    
    public Type getType() {
        try {
            return lookup.getType(type);
        } catch (final RuntimeException e) {
            throw new RuntimeException(name.toString() + ", type: " + type, e);
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