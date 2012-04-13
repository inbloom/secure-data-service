package org.slc.sli.modeling.uml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * The representation of an enumeration.
 */
public final class EnumType extends AbstractModelElementWithLookup implements Type {
    /**
     * The name of the enumeration type.
     */
    private final QName name;
    /**
     * The literals that are part of the enumeration.
     */
    private final List<EnumLiteral> literals;
    
    public EnumType(final Identifier id, final QName name, final List<EnumLiteral> literals,
            final List<TaggedValue> taggedValues, final LazyLookup lookup) {
        super(id, ReferenceType.ENUM_TYPE, taggedValues, lookup);
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
        this.literals = Collections.unmodifiableList(new ArrayList<EnumLiteral>(literals));
    }
    
    @Override
    public QName getName() {
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
    public List<Generalization> getGeneralizationBase() {
        return lookup.getGeneralizationBase(this);
    }
    
    @Override
    public List<Generalization> getGeneralizationDerived() {
        return lookup.getGeneralizationDerived(this);
    }
    
    @Override
    public List<AssociationEnd> getAssociationEnds() {
        return lookup.getAssociationEnds(this);
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
