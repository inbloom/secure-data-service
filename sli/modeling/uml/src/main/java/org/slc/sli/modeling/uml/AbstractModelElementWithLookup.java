package org.slc.sli.modeling.uml;

import java.util.List;

/**
 * Only some model elements have the need to be able to reference others.
 */
public abstract class AbstractModelElementWithLookup extends AbstractModelElement {
    /**
     * Intentionally protected and available to derived classes.
     */
    protected final LazyLookup lookup;
    
    public AbstractModelElementWithLookup(final Identifier id, final ReferenceType kind,
            final List<TaggedValue> taggedValues, final LazyLookup lookup) {
        super(id, kind, taggedValues);
        if (lookup == null) {
            throw new NullPointerException("lookup");
        }
        this.lookup = lookup;
    }
}
