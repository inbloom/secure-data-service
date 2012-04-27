package org.slc.sli.modeling.uml;

import java.util.List;

/**
 * This multiplicity element is supported distinct from {@link Range} to maintain round-trip
 * fidelity.
 */
public final class Multiplicity extends ModelElement {
    /**
     * The lower and upper bound range.
     */
    private final Range range;
    
    public Multiplicity(final Identifier id, final List<TaggedValue> taggedValues, final Range range) {
        super(id, taggedValues);
        if (range == null) {
            throw new NullPointerException("range");
        }
        this.range = range;
    }
    
    public Multiplicity(final List<TaggedValue> taggedValues, final Range range) {
        this(Identifier.random(), taggedValues, range);
    }
    
    public Multiplicity(final Range range) {
        this(Identifier.random(), EMPTY_TAGGED_VALUES, range);
    }
    
    @Override
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
    
    public Range getRange() {
        return range;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        // sb.append("id: " + id).append(", ");
        sb.append("range: " + range);
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}
