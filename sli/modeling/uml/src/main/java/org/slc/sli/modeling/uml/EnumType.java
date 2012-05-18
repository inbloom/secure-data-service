package org.slc.sli.modeling.uml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The representation of an enumeration.
 */
public final class EnumType extends NamespaceOwnedElement implements SimpleType {
    /**
     * The literals that are part of the enumeration.
     */
    private final List<EnumLiteral> literals;

    public EnumType(final Identifier id, final String name, final List<EnumLiteral> literals,
            final List<TaggedValue> taggedValues) {
        super(id, name, taggedValues);
        this.literals = Collections.unmodifiableList(new ArrayList<EnumLiteral>(literals));
    }

    @Override
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<EnumLiteral> getLiterals() {
        return literals;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public boolean isClassType() {
        return false;
    }

    @Override
    public boolean isDataType() {
        return false;
    }

    @Override
    public boolean isEnumType() {
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id: " + getId()).append(", ");
        sb.append("name: \"" + getName() + "\"");
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
