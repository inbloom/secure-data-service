package org.slc.sli.modeling.uml;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * A generalization expresses the child isA parent relationship between two model elements.
 */
public final class Generalization extends AbstractModelElementWithLookup implements HasName {
    
    private final QName name;
    private final Reference child;
    private final Reference parent;
    
    public Generalization(final QName name, final Identifier id, final List<TaggedValue> taggedValues,
            final Reference child, final Reference parent, final LazyLookup lookup) {
        super(id, ReferenceType.GENERALIZATION, taggedValues, lookup);
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (child == null) {
            throw new NullPointerException("child");
        }
        if (parent == null) {
            throw new NullPointerException("parent");
        }
        this.name = name;
        this.child = child;
        this.parent = parent;
    }
    
    @Override
    public QName getName() {
        return name;
    }
    
    public Type getChild() {
        return lookup.getType(child);
    }
    
    public Type getParent() {
        return lookup.getType(parent);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        // sb.append("id: " + id).append(", ");
        sb.append("name: \"" + name + "\"");
        sb.append(", ");
        sb.append("parent: \"" + getParent().getName() + "\"");
        sb.append(", ");
        sb.append("child: \"" + getChild().getName() + "\"");
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}
