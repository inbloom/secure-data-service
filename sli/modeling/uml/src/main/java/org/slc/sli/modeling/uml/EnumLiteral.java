package org.slc.sli.modeling.uml;

import java.util.List;

/**
 * A member of an enumeration type.
 */
public final class EnumLiteral extends UmlNamedModelElement implements HasName {
    
    public EnumLiteral(final Identifier id, final String name, final List<TaggedValue> taggedValues) {
        super(id, name, taggedValues);
    }
    
    public EnumLiteral(final String name, final List<TaggedValue> taggedValues) {
        this(Identifier.random(), name, taggedValues);
    }
    
    @Override
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id: " + getId()).append(", ");
        sb.append("name: \"" + getName() + "\"");
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}
