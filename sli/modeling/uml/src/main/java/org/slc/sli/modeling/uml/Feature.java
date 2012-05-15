package org.slc.sli.modeling.uml;

import java.util.List;

/**
 * A feature is an abstract part of a class such as an attribute or association end.
 */
public abstract class Feature extends NamedModelElement implements HasType, HasMultiplicity {
    /**
     * The lower bound of the attribute. Typically 0 or 1. Never <code>null</code>.
     */
    private final Multiplicity multiplicity;
    /**
     * The type of the attribute. Never <code>null</code>.
     */
    private final Identifier type;

    public Feature(final Identifier id, final String name, final Identifier type, final Multiplicity multiplicity,
            final List<TaggedValue> taggedValues) {
        super(id, name, taggedValues);
        if (type == null) {
            throw new NullPointerException("type");
        }
        if (multiplicity == null) {
            throw new NullPointerException("multiplicity");
        }
        this.type = type;
        this.multiplicity = multiplicity;
    }

    public abstract boolean isAttribute();

    public abstract boolean isAssociationEnd();

    @Override
    public final Multiplicity getMultiplicity() {
        return multiplicity;
    }

    @Override
    public final Identifier getType() {
        return type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id: " + getId()).append(", ");
        sb.append("name: " + getName());
        sb.append(", ");
        sb.append("type: " + type);
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