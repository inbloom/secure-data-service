package org.slc.sli.modeling.uml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A model element is something that has an identifier and can be tagged.
 */
public abstract class AbstractModelElement implements HasIdentity, HasTaggedValues {
    
    private final Identifier id;
    private final List<TaggedValue> taggedValues;
    /**
     * Intentionally protected and available to derived classes.
     */
    protected final LazyLookup lookup;
    
    public AbstractModelElement(final Identifier id, final List<TaggedValue> taggedValues, final LazyLookup lookup) {
        if (id == null) {
            throw new NullPointerException("id");
        }
        if (taggedValues == null) {
            throw new NullPointerException("taggedValues");
        }
        if (lookup == null) {
            throw new NullPointerException("lookup");
        }
        this.id = id;
        this.taggedValues = Collections.unmodifiableList(new ArrayList<TaggedValue>(taggedValues));
        this.lookup = lookup;
    }
    
    @Override
    public final Identifier getId() {
        return id;
    }
    
    @Override
    public List<TaggedValue> getTaggedValues() {
        return taggedValues;
    }
}
