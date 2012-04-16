package org.slc.sli.modeling.uml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The meta-data for a class.
 */
public final class ClassType extends NamespaceOwnedElement implements Type {
    /**
     * The attributes of this class.
     */
    private final List<Attribute> attributes;
    /**
     * Determines whether the class can be instantiated.
     */
    private final boolean isAbstract;
    
    public ClassType(final Identifier id, final String name, final boolean isAbstract,
            final List<Attribute> attributes, final List<TaggedValue> taggedValues) {
        super(id, name, taggedValues);
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (attributes == null) {
            throw new NullPointerException("attributes");
        }
        this.isAbstract = isAbstract;
        this.attributes = Collections.unmodifiableList(new ArrayList<Attribute>(attributes));
    }
    
    @Override
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ClassType) {
            final ClassType that = (ClassType) obj;
            return this.getId().equals(that.getId());
        } else {
            return false;
        }
    }
    
    public List<Attribute> getAttributes() {
        // We've already made defensive copy in initializer, and have made immutable.
        return attributes;
    }
    
    @Override
    public int hashCode() {
        return getId().hashCode();
    }
    
    public boolean isAbstract() {
        return isAbstract;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id: " + getId()).append(", ");
        sb.append("name: " + getName()).append(", ");
        sb.append("attributes: " + attributes);
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}
