package org.slc.sli.modeling.uml;

import java.util.List;

/**
 * A member of an enumeration type.
 */
public final class EnumLiteral extends AbstractModelElement implements HasName {
    
    private final String name;
    
    public EnumLiteral(final Identifier id, final String name, final List<TaggedValue> taggedValues) {
        super(id, taggedValues);
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        // sb.append("id: " + id).append(", ");
        sb.append("name: \"" + name + "\"");
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}
