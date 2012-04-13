package org.slc.sli.modeling.uml;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * The end of an association between two classes.
 */
public final class AssociationEnd extends AbstractModelElementWithLookup implements HasName, HasType, HasMultiplicity {
    /**
     * The multiplicity of the association end. Typically 0 or 1. Never <code>null</code>.
     */
    private final Multiplicity multiplicity;
    /**
     * The name of the association end.
     */
    private final QName name;
    /**
     * Determines whether the association is navigable in this direction.
     */
    private final boolean isNavigable;
    /**
     * The class being referenced. Never <code>null</code>.
     */
    private final Reference reference;
    
    public AssociationEnd(final Multiplicity multiplicity, final QName name, final boolean isNavigable,
            final Identifier id, final List<TaggedValue> taggedValues, final Reference reference,
            final LazyLookup lookup) {
        super(id, ReferenceType.ASSOCIATION_END, taggedValues, lookup);
        if (name == null) {
            throw new NullPointerException("name");
        } else if (name.getLocalPart().trim().isEmpty()) {
            // throw new IllegalArgumentException("name must not be blank");
        }
        if (reference == null) {
            throw new NullPointerException("reference");
        }
        if (multiplicity == null) {
            throw new NullPointerException("multiplicity");
        }
        if (lookup == null) {
            throw new NullPointerException("lookup");
        }
        this.multiplicity = multiplicity;
        this.name = name;
        this.isNavigable = isNavigable;
        this.reference = reference;
    }
    
    public Multiplicity getMultiplicity() {
        return multiplicity;
    }
    
    @Override
    public QName getName() {
        return name;
    }
    
    public boolean isNavigable() {
        return isNavigable;
    }
    
    public Type getType() {
        return lookup.getType(reference);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id: " + getId());
        sb.append(", ");
        if (lookup.isEnabled()) {
            sb.append("reference: " + lookup.getType(reference).getName());
        } else {
            sb.append("reference: " + reference);
        }
        sb.append(", ");
        sb.append("multiplicity: " + multiplicity);
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}
