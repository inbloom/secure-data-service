package org.slc.sli.modeling.uml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The representation of an enumeration.
 */
public final class EnumType extends AbstractModelElement implements Type {
    /**
     * The name of the enumeration type.
     */
    private final String name;
    /**
     * The literals that are part of the enumeration.
     */
    private final List<EnumLiteral> literals;
    
    public EnumType(final Identifier id, final String name, final List<EnumLiteral> literals,
            final List<TaggedValue> taggedValues) {
        super(id, taggedValues);
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
        this.literals = Collections.unmodifiableList(new ArrayList<EnumLiteral>(literals));
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public boolean isAbstract() {
        return false;
    }
    
    public List<EnumLiteral> getLiterals() {
        return literals;
    }
    
    @Override
    public Reference getReference() {
        return new Reference(getId(), ReferenceType.ENUM_TYPE);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        // sb.append("id: " + id).append(", ");
        sb.append("name: \"" + name + "\"");
        sb.append(", ");
        sb.append("literals: " + literals);
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}
