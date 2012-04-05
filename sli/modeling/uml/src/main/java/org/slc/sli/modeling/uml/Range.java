package org.slc.sli.modeling.uml;

import java.util.List;

/**
 * Provides the lower and upper bounds for a type.
 */
public final class Range extends AbstractModelElement {
    /**
     * The lower bound of the attribute. Typically 0 or 1. Never <code>null</code>.
     */
    private final Occurs lowerBound;
    /**
     * The upper bound of the attribute. Typically 1 or unbounded. Never <code>null</code>.
     */
    private final Occurs upperBound;
    
    public Range(final Identifier id, final Occurs lower, final Occurs upper, final List<TaggedValue> taggedValues) {
        super(id, taggedValues);
        if (lower == null) {
            throw new NullPointerException("lower");
        }
        if (upper == null) {
            throw new NullPointerException("upper");
        }
        this.lowerBound = lower;
        this.upperBound = upper;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        // sb.append("id: " + id).append(", ");
        sb.append("lower: " + lowerBound).append(", ");
        sb.append("upper: " + upperBound);
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
    
    public Occurs getLower() {
        return lowerBound;
    }
    
    public Occurs getUpper() {
        return upperBound;
    }
}
