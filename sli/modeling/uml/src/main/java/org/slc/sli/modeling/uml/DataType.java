package org.slc.sli.modeling.uml;

import java.util.List;

/**
 * A data type is a type that typically has no references to other types.
 */
public final class DataType extends NamespaceOwnedElement implements Type {
    private final boolean isAbstract;
    
    public DataType(final Identifier id, final String name) {
        this(id, name, false, EMPTY_TAGGED_VALUES);
    }
    
    public DataType(final Identifier id, final String name, final boolean isAbstract) {
        this(id, name, isAbstract, EMPTY_TAGGED_VALUES);
    }
    
    public DataType(final Identifier id, final String name, final boolean isAbstract,
            final List<TaggedValue> taggedValues) {
        super(id, name, taggedValues);
        this.isAbstract = isAbstract;
    }
    
    @Override
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public boolean isAbstract() {
        return isAbstract;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id: " + getId()).append(", ");
        sb.append("name: \"" + getName() + "\"").append(", ");
        sb.append("isAbstract: " + isAbstract);
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}