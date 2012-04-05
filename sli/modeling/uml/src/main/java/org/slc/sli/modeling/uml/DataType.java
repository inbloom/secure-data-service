package org.slc.sli.modeling.uml;

import java.util.List;

/**
 * A data type is a type that typically has no references to other types.
 */
public final class DataType extends AbstractModelElement implements Type {
    private final String name;
    private final boolean isAbstract;
    
    public DataType(final Identifier id, final String name, final boolean isAbstract,
            final List<TaggedValue> taggedValues) {
        super(id, taggedValues);
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
        this.isAbstract = isAbstract;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public boolean isAbstract() {
        return isAbstract;
    }
    
    @Override
    public Reference getReference() {
        return new Reference(getId(), ReferenceType.DATA_TYPE);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        // sb.append("id: " + id).append(", ");
        sb.append("name: \"" + name + "\"").append(", ");
        sb.append("isAbstract: " + isAbstract);
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}