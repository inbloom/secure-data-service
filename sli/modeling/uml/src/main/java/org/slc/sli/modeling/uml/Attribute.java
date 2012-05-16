package org.slc.sli.modeling.uml;

import java.util.List;

/**
 * A feature of a class which is embedded.
 */
public final class Attribute extends Feature {

    public Attribute(final Identifier id, final String name, final Identifier type, final Multiplicity multiplicity,
            final List<TaggedValue> taggedValues) {
        super(id, name, type, multiplicity, taggedValues);
    }

    @Override
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean isAssociationEnd() {
        return false;
    }

    @Override
    public boolean isAttribute() {
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id: " + getId()).append(", ");
        sb.append("name: " + getName());
        sb.append(", ");
        sb.append("type: " + getType());
        sb.append(", ");
        sb.append("multiplicity: " + getMultiplicity());
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}