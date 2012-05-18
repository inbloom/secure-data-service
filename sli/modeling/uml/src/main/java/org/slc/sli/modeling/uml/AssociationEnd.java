package org.slc.sli.modeling.uml;

import java.util.List;

/**
 * The end of an association between two classes.
 */
public final class AssociationEnd extends Feature {
    /**
     * Determines whether the association is navigable in this direction.
     */
    private final boolean isNavigable;

    public AssociationEnd(final Multiplicity multiplicity, final String name, final boolean isNavigable,
            final Identifier type) {
        this(multiplicity, name, isNavigable, Identifier.random(), EMPTY_TAGGED_VALUES, type);
    }

    public AssociationEnd(final Multiplicity multiplicity, final String name, final boolean isNavigable,
            final Identifier id, final List<TaggedValue> taggedValues, final Identifier type) {
        super(id, name, type, multiplicity, taggedValues);
        this.isNavigable = isNavigable;
    }

    public AssociationEnd(final Multiplicity multiplicity, final String name, final boolean isNavigable,
            final List<TaggedValue> taggedValues, final Identifier type) {
        this(multiplicity, name, isNavigable, Identifier.random(), taggedValues, type);
    }

    @Override
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean isAssociationEnd() {
        return true;
    }

    @Override
    public boolean isAttribute() {
        return false;
    }

    public boolean isNavigable() {
        return isNavigable;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id: " + getId());
        sb.append(", ");
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
