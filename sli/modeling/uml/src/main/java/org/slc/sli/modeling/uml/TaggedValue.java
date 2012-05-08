package org.slc.sli.modeling.uml;

import java.util.List;

/**
 * A tagged value is a tag value that is placed on a model element and conforms to a tag definition.
 */
public final class TaggedValue extends AbstractModelElement {
    /**
     * The value of the tagged value. Never <code>null</code>.
     */
    private final String value;
    /**
     * The tag definition of the tagged value. Never <code>null</code>.
     */
    private final Reference tagDefinition;
    
    private final LazyLookup lookup;
    
    public TaggedValue(final Identifier id, final List<TaggedValue> taggedValues, final String value,
            final Reference tagDefinition, final LazyLookup lookup) {
        super(id, taggedValues);
        if (value == null) {
            throw new NullPointerException("value");
        }
        if (tagDefinition == null) {
            throw new NullPointerException("tagDefinition");
        }
        if (lookup == null) {
            throw new NullPointerException("lookup");
        }
        this.value = value;
        this.tagDefinition = tagDefinition;
        this.lookup = lookup;
    }
    
    public String getValue() {
        return value;
    }
    
    public TagDefinition getTagDefinition() {
        return lookup.getTagDefinition(tagDefinition);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        // sb.append("id: " + id).append(", ");
        sb.append("value: \"" + value + "\"");
        sb.append(", ");
        sb.append("tagDefinition: " + getTagDefinition().getName());
        if (!getTaggedValues().isEmpty()) {
            sb.append(", ");
            sb.append("taggedValues: " + getTaggedValues());
        }
        sb.append("}");
        return sb.toString();
    }
}
