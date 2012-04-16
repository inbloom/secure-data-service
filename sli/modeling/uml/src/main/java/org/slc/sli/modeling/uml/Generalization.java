package org.slc.sli.modeling.uml;

import java.util.List;

/**
 * A generalization expresses the child isA parent relationship between two model elements.
 */
public final class Generalization extends NamespaceOwnedElement {
    
    private final Identifier child;
    private final Identifier parent;
    
    public Generalization(final Identifier child, final Identifier parent) {
        this("", Identifier.random(), EMPTY_TAGGED_VALUES, child, parent);
    }
    
    public Generalization(final String name, final Identifier child, final Identifier parent) {
        this(name, Identifier.random(), EMPTY_TAGGED_VALUES, child, parent);
    }
    
    public Generalization(final String name, final Identifier id, final List<TaggedValue> taggedValues,
            final Identifier child, final Identifier parent) {
        super(id, name, taggedValues);
        if (child == null) {
            throw new NullPointerException("child");
        }
        if (parent == null) {
            throw new NullPointerException("parent");
        }
        this.child = child;
        this.parent = parent;
    }
    
    public Generalization(final String name, final List<TaggedValue> taggedValues, final Identifier child,
            final Identifier parent) {
        this(name, Identifier.random(), taggedValues, child, parent);
    }
    
    @Override
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
    
    public Identifier getChild() {
        return child;
    }
    
    public Identifier getParent() {
        return parent;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id: " + getId()).append(", ");
        sb.append("name: \"" + getName() + "\"");
        sb.append(", ");
        sb.append("parent: \"" + parent + "\"");
        sb.append(", ");
        sb.append("child: \"" + child + "\"");
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}
