package org.slc.sli.modeling.tools.xmi2Java.cmdline;

import org.slc.sli.modeling.tools.TagName;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.Feature;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.index.ModelIndex;

/**
 * A Java feature is a feature of a class such as an attribute or an association end.
 */
public final class JavaFeature {

    private final Feature feature;
    private final ModelIndex model;

    public JavaFeature(final Feature name, final ModelIndex type) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (type == null) {
            throw new NullPointerException("type");
        }
        this.feature = name;
        this.model = type;
    }

    public String getName(final JavaGenConfig config) {
        final String name = feature.getName().trim();
        if (name.length() > 0) {
            return name;
        } else {
            return getPrimeTypeName(config);
        }
    }

    public Feature getFeature() {
        return feature;
    }

    public ModelIndex getModel() {
        return model;
    }

    public String getPrimeTypeName(final JavaGenConfig config) {
        if (isAttribute()) {
            return TypeHelper.getAttributePrimeTypeName(feature, model, config);
        } else if (isNavigable()) {
            return TypeHelper.getNavigablePrimeTypeName(feature, model);
        } else {
            throw new IllegalStateException("");
        }
    }

    public String getAttributeTypeName(final JavaGenConfig config) {
        return TypeHelper.getAttributeTypeName(feature, model, config);
    }

    public String getNavigableTypeName() {
        return TypeHelper.getNavigableTypeName(feature, model);
    }

    public boolean isOptional() {
        final Multiplicity multiplicity = feature.getMultiplicity();
        final Range range = multiplicity.getRange();
        return (range.getLower() == Occurs.ZERO & range.getUpper() == Occurs.ONE);
    }

    public boolean isRequired() {
        final Multiplicity multiplicity = feature.getMultiplicity();
        final Range range = multiplicity.getRange();
        return (range.getLower() == Occurs.ONE & range.getUpper() == Occurs.ONE);
    }

    public boolean isZeroOrMore() {
        final Multiplicity multiplicity = feature.getMultiplicity();
        final Range range = multiplicity.getRange();
        return (range.getLower() == Occurs.ZERO & range.getUpper() == Occurs.UNBOUNDED);
    }

    public boolean isOneOrMore() {
        final Multiplicity multiplicity = feature.getMultiplicity();
        final Range range = multiplicity.getRange();
        return (range.getLower() == Occurs.ONE & range.getUpper() == Occurs.UNBOUNDED);
    }

    public boolean isAttribute() {
        return feature instanceof Attribute;
    }

    public boolean isAssociationEnd() {
        return feature instanceof AssociationEnd;
    }

    public boolean isNavigable() {
        for (final TaggedValue taggedValue : feature.getTaggedValues()) {
            final TagDefinition tagDefinition = model.getTagDefinition(taggedValue.getTagDefinition());
            if (tagDefinition.getName().equals(TagName.MONGO_NAVIGABLE)) {
                return Boolean.valueOf(taggedValue.getValue());
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return feature.toString();
    }
}
