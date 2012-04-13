package org.slc.sli.modeling.uml;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * An association between two types.
 */
public final class Association extends AbstractModelElement implements HasName {
    private final QName name;
    private final AssociationEnd lhs;
    private final AssociationEnd rhs;
    
    public Association(final Identifier id, final QName name, final AssociationEnd lhs, final AssociationEnd rhs,
            final List<TaggedValue> taggedValues) {
        super(id, ReferenceType.ASSOCIATION, taggedValues);
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (lhs == null) {
            throw new NullPointerException("lhs");
        }
        if (rhs == null) {
            throw new NullPointerException("rhs");
        }
        this.name = name;
        this.lhs = lhs;
        this.rhs = rhs;
    }
    
    @Override
    public QName getName() {
        return name;
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
        // sb.append("id: " + id).append(", ");
        sb.append("name: \"" + name + "\"");
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
