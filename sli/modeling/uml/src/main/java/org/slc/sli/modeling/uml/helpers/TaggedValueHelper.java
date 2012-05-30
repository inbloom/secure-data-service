package org.slc.sli.modeling.uml.helpers;

import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.Taggable;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.index.ModelIndex;

/**
 * Convenience functions for accessing tagged values.
 */
public final class TaggedValueHelper {

    public static final boolean getBooleanTag(final String name, final Taggable element, final ModelIndex lookup,
            final boolean defaultValue) {
        for (final TaggedValue taggedValue : element.getTaggedValues()) {
            final TagDefinition tagDefinition = lookup.getTagDefinition(taggedValue.getTagDefinition());
            if (name.equals(tagDefinition.getName())) {
                return Boolean.valueOf(taggedValue.getValue());
            }
        }
        return defaultValue;
    }

    public static final String getStringTag(final String name, final Taggable element, final ModelIndex lookup,
            final String defaultValue) {
        for (final TaggedValue taggedValue : element.getTaggedValues()) {
            final TagDefinition tagDefinition = lookup.getTagDefinition(taggedValue.getTagDefinition());
            if (name.equals(tagDefinition.getName())) {
                return taggedValue.getValue();
            }
        }
        return defaultValue;
    }

    public static final boolean hasTag(final String name, final Taggable element, final ModelIndex lookup) {
        for (final TaggedValue taggedValue : element.getTaggedValues()) {
            final TagDefinition tagDefinition = lookup.getTagDefinition(taggedValue.getTagDefinition());
            if (name.equals(tagDefinition.getName())) {
                return true;
            }
        }
        return false;
    }
}
