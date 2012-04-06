package org.slc.sli.modeling.uml;

import java.util.List;

/**
 * The end of an association between two classes.
 */
public final class AssociationEnd extends AbstractModelElement {
    /**
     * The class being referenced. Never <code>null</code>.
     */
    private final Reference reference;
    /**
     * The multiplicity of the association end. Typically 0 or 1. Never <code>null</code>.
     */
    private final Multiplicity multiplicity;
    private final LazyLookup lookup;
    
    public AssociationEnd(final Identifier id, final List<TaggedValue> taggedValues, final Reference reference,
            final Multiplicity multiplicity, final LazyLookup lookup) {
        super(id, taggedValues);
        if (reference == null) {
            throw new NullPointerException("reference");
        }
        if (multiplicity == null) {
            throw new NullPointerException("multiplicity");
        }
        if (lookup == null) {
            throw new NullPointerException("lookup");
        }
        this.reference = reference;
        this.multiplicity = multiplicity;
        this.lookup = lookup;
    }
    
    public Type getType() {
        return lookup.getType(reference);
    }
    
    public Multiplicity getMultiplicity() {
        return multiplicity;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        // sb.append("id: " + id).append(", ");
        sb.append("reference: " + getType().getName());
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
