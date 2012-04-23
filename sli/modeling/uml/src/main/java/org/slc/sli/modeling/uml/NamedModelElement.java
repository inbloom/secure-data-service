package org.slc.sli.modeling.uml;

import java.util.List;

public abstract class NamedModelElement extends ModelElement implements HasName {
    private final String name;

    /**
     * Convenience initializer with no tags.
     */
    public NamedModelElement(final Identifier id, final String name) {
        this(id, name, EMPTY_TAGGED_VALUES);
    }

    /**
     * Canonical initializer for this abstract class.
     *
     * @param id
     *            The identifier.
     * @param name
     *            The name of this class.
     * @param taggedValues
     *            The tags for this class.
     */
    public NamedModelElement(final Identifier id, final String name, final List<TaggedValue> taggedValues) {
        super(id, taggedValues);
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
    }

    /**
     * Convenience initializer with a randomly generated identifier and no tags.
     */
    public NamedModelElement(final String name) {
        this(Identifier.random(), name, EMPTY_TAGGED_VALUES);
    }

    /**
     * Convenience initializer with a randomly generated identifier.
     */
    public NamedModelElement(final String name, final List<TaggedValue> taggedValues) {
        this(Identifier.random(), name, taggedValues);
    }

    @Override
    public final String getName() {
        return name;
    }
}
