package org.slc.sli.modeling.uml;

import java.util.List;

/**
 * An association between two types.
 */
public final class Association extends NamespaceOwnedElement {
    private final AssociationEnd lhs;
    private final AssociationEnd rhs;
    
    public Association(final Identifier id, final String name, final AssociationEnd lhs, final AssociationEnd rhs,
            final List<TaggedValue> taggedValues) {
        super(id, name, taggedValues);
        if (lhs == null) {
            throw new NullPointerException("lhs");
        }
        if (rhs == null) {
            throw new NullPointerException("rhs");
        }
        if (lhs.getId().equals(rhs.getId())) {
            throw new IllegalArgumentException();
        }
        this.lhs = lhs;
        this.rhs = rhs;
    }
    
    public Association(final String name, final AssociationEnd lhs, final AssociationEnd rhs,
            final List<TaggedValue> taggedValues) {
        this(Identifier.random(), name, lhs, rhs, taggedValues);
    }
    
    public Association(final String name, final AssociationEnd lhs, final AssociationEnd rhs) {
        this(Identifier.random(), name, lhs, rhs, EMPTY_TAGGED_VALUES);
    }
    
    public Association(final AssociationEnd lhs, final AssociationEnd rhs) {
        this(Identifier.random(), "", lhs, rhs, EMPTY_TAGGED_VALUES);
    }
    
    @Override
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
    
    public AssociationEnd getLHS() {
        return lhs;
    }
    
    public AssociationEnd getRHS() {
        return rhs;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id: " + getId()).append(", ");
        sb.append("name: \"" + getName() + "\"");
        sb.append(", ");
        sb.append("lhs: " + lhs).append(", ");
        sb.append("rhs: " + rhs);
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}
