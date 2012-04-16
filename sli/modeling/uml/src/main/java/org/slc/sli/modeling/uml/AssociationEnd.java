package org.slc.sli.modeling.uml;

import java.util.List;

/**
 * The end of an association between two classes.
 */
public final class AssociationEnd extends UmlNamedModelElement implements HasType, HasMultiplicity {
    /**
     * Determines whether the association is navigable in this direction.
     */
    private final boolean isNavigable;
    /**
     * The multiplicity of the association end. Typically 0 or 1. Never <code>null</code>.
     */
    private final Multiplicity multiplicity;
    /**
     * The class being referenced. Never <code>null</code>.
     */
    private final Identifier reference;
    
    public AssociationEnd(final Multiplicity multiplicity, final String name, final boolean isNavigable,
            final Identifier id, final List<TaggedValue> taggedValues, final Identifier reference) {
        super(id, name, taggedValues);
        if (reference == null) {
            throw new NullPointerException("reference");
        }
        if (multiplicity == null) {
            throw new NullPointerException("multiplicity");
        }
        this.multiplicity = multiplicity;
        this.isNavigable = isNavigable;
        this.reference = reference;
    }
    
    public AssociationEnd(final Multiplicity multiplicity, final String name, final boolean isNavigable,
            final List<TaggedValue> taggedValues, final Identifier reference) {
        this(multiplicity, name, isNavigable, Identifier.random(), taggedValues, reference);
    }
    
    public AssociationEnd(final Multiplicity multiplicity, final String name, final boolean isNavigable,
            final Identifier reference) {
        this(multiplicity, name, isNavigable, Identifier.random(), EMPTY_TAGGED_VALUES, reference);
    }
    
    @Override
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
    
    public Multiplicity getMultiplicity() {
        return multiplicity;
    }
    
    public Identifier getType() {
        return reference;
    }
    
    public boolean isNavigable() {
        return isNavigable;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id: " + getId());
        sb.append(", ");
        sb.append("name: " + getName());
        sb.append(", ");
        sb.append("reference: " + reference);
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
