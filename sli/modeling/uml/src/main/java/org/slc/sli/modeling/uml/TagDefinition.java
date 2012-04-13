package org.slc.sli.modeling.uml;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * A UML TagDefinition defines a the type of a TaggedValue.
 */
public final class TagDefinition extends AbstractModelElement implements HasName, HasMultiplicity {
    private final QName name;
    private final Multiplicity multiplicity;
    
    public static final QName NAME_DOCUMENTATION = new QName("documentation");
    public static final QName NAME_MAX_LENGTH = new QName("maxLength");
    public static final QName NAME_MIN_LENGTH = new QName("minLength");
    public static final QName NAME_MAX_INCLUSIVE = new QName("maxInclusive");
    public static final QName NAME_MIN_INCLUSIVE = new QName("minInclusive");
    public static final QName NAME_MAX_EXCLUSIVE = new QName("maxExclusive");
    public static final QName NAME_MIN_EXCLUSIVE = new QName("minExclusive");
    public static final QName NAME_TOTAL_DIGITS = new QName("totalDigits");
    public static final QName NAME_FRACTION_DIGITS = new QName("fractionDigits");
    public static final QName NAME_LENGTH = new QName("length");
    public static final QName NAME_PATTERN = new QName("pattern");
    
    public TagDefinition(final Identifier id, final List<TaggedValue> taggedValues, final QName name,
            final Multiplicity multiplicity) {
        super(id, ReferenceType.TAG_DEFINITION, taggedValues);
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
        if (multiplicity == null) {
            throw new NullPointerException("multiplicity");
        }
        this.multiplicity = multiplicity;
    }
    
    public Reference getReference() {
        return new Reference(getId(), ReferenceType.TAG_DEFINITION);
    }
    
    @Override
    public QName getName() {
        return name;
    }
    
    @Override
    public Multiplicity getMultiplicity() {
        return multiplicity;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        // sb.append("id: " + id).append(", ");
        sb.append("name: " + name).append(", ");
        sb.append("multiplicity: " + multiplicity);
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}
