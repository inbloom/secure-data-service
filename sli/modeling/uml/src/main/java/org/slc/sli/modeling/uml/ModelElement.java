package org.slc.sli.modeling.uml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A model element is something that has an identifier, can be tagged, and can
 * be visited.
 */
public abstract class ModelElement implements HasIdentity, Taggable,
		Visitable {

	protected static final List<TaggedValue> EMPTY_TAGGED_VALUES = Collections
			.emptyList();

	private final Identifier id;
	private final List<TaggedValue> taggedValues;

	public ModelElement() {
		this(Identifier.random(), EMPTY_TAGGED_VALUES);
	}

	public ModelElement(final Identifier id) {
		this(id, EMPTY_TAGGED_VALUES);
	}

	public ModelElement(final Identifier id,
			final List<TaggedValue> taggedValues) {
		if (id == null) {
			throw new NullPointerException("id");
		}
		if (taggedValues == null) {
			throw new NullPointerException("taggedValues");
		}
		this.id = id;
		this.taggedValues = Collections
				.unmodifiableList(new ArrayList<TaggedValue>(taggedValues));
	}

	public ModelElement(final List<TaggedValue> taggedValues) {
		this(Identifier.random(), taggedValues);
	}

	@Override
	public final Identifier getId() {
		return id;
	}

	@Override
	public final List<TaggedValue> getTaggedValues() {
		return taggedValues;
	}

	@Override
	public final boolean equals(final Object obj) {
		if (obj instanceof ModelElement) {
			final ModelElement other = (ModelElement) obj;
			return id.equals(other.id);
		} else {
			return false;
		}
	}

	@Override
	public final int hashCode() {
		return id.hashCode();
	}
}
