package org.slc.sli.modeling.uml;

import java.util.List;

/**
 * Provides the tagged values.
 */
public interface HasTaggedValues {
    /**
     * Returns the {@link TaggedValue} list for this model element.
     */
    List<TaggedValue> getTaggedValues();
}
