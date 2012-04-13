package org.slc.sli.modeling.uml;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * A member of an enumeration type.
 */
public final class EnumLiteral extends AbstractModelElement implements HasName {
    
    private final QName name;
    
    public EnumLiteral(final Identifier id, final QName name, final List<TaggedValue> taggedValues) {
        super(id, ReferenceType.ENUM_LITERAL, taggedValues);
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
    }
    
    @Override
    public QName getName() {
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
