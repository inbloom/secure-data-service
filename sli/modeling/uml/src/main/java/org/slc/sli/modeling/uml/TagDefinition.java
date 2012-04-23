package org.slc.sli.modeling.uml;

import java.util.List;

/**
 * A UML TagDefinition defines a the type of a TaggedValue.
 */
public final class TagDefinition extends NamespaceOwnedElement implements
		HasMultiplicity {
	public static final String NAME_DOCUMENTATION = "documentation";
	public static final String NAME_FRACTION_DIGITS = "fractionDigits";

	public static final String NAME_LENGTH = "length";
	public static final String NAME_MAX_EXCLUSIVE = "maxExclusive";
	public static final String NAME_MAX_INCLUSIVE = "maxInclusive";
	public static final String NAME_MAX_LENGTH = "maxLength";
	public static final String NAME_MIN_EXCLUSIVE = "minExclusive";
	public static final String NAME_MIN_INCLUSIVE = "minInclusive";
	public static final String NAME_MIN_LENGTH = "minLength";
	public static final String NAME_PATTERN = "pattern";
	public static final String NAME_TOTAL_DIGITS = "totalDigits";

	private final Multiplicity multiplicity;

	public TagDefinition(final Identifier id,
			final List<TaggedValue> taggedValues, final String name,
			final Multiplicity multiplicity) {
		super(id, name, taggedValues);
		if (multiplicity == null) {
			throw new NullPointerException("multiplicity");
		}
		this.multiplicity = multiplicity;
	}

	public TagDefinition(final Identifier id, final String name,
			final Multiplicity multiplicity) {
		this(id, EMPTY_TAGGED_VALUES, name, multiplicity);
	}

	@Override
	public void accept(final Visitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Multiplicity getMultiplicity() {
		return multiplicity;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id: " + getId()).append(", ");
		sb.append("name: " + getName()).append(", ");
		sb.append("multiplicity: " + multiplicity);
		if (!getTaggedValues().isEmpty()) {
			sb.append(", ");
			sb.append("taggedValues: " + getTaggedValues());
		}
		sb.append("}");
		return sb.toString();
	}
}
