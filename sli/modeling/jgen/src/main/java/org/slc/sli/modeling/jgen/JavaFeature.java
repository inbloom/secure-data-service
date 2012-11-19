/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.modeling.jgen;

import org.slc.sli.modeling.psm.helpers.TagName;
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
            throw new IllegalArgumentException("name");
        }
        if (type == null) {
            throw new IllegalArgumentException("type");
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

    public boolean isExposed(final JavaGenConfig config) {
        if (isAttribute()) {
            return true;
        } else if (isNavigable()) {
            return true;
        } else {
            return false;
        }
    }

    public String getPrimeTypeName(final JavaGenConfig config) {
        if (isExposed(config)) {
            if (isAttribute()) {
                return JavaTypeHelper.getAttributePrimeTypeName(feature, model, config);
            } else if (isNavigable()) {
                return JavaTypeHelper.getNavigablePrimeTypeName(feature, model);
            } else {
                throw new AssertionError("getPrimeTypeName(" + feature + ")");
            }
        } else {
            throw new IllegalStateException("isExposed(" + feature + ")");
        }
    }

    public JavaType getAttributeType(final JavaGenConfig config) {
        return JavaTypeHelper.getAttributeType(feature, model, config);
    }

    public String getAttributeTypeName(final JavaGenConfig config) {
        return JavaTypeHelper.getAttributeTypeName(feature, model, config);
    }

    public String getNavigableTypeName() {
        return JavaTypeHelper.getNavigableTypeName(feature, model);
    }

    public JavaType getNavigableType() {
        return JavaTypeHelper.getNavigableType(feature, model);
    }

    public boolean isOptional() {
        final Multiplicity multiplicity = feature.getMultiplicity();
        final Range range = multiplicity.getRange();
        return (range.getLower() == Occurs.ZERO && range.getUpper() == Occurs.ONE);
    }

    public boolean isRequired() {
        final Multiplicity multiplicity = feature.getMultiplicity();
        final Range range = multiplicity.getRange();
        return (range.getLower() == Occurs.ONE && range.getUpper() == Occurs.ONE);
    }

    public boolean isZeroOrMore() {
        final Multiplicity multiplicity = feature.getMultiplicity();
        final Range range = multiplicity.getRange();
        return (range.getLower() == Occurs.ZERO && range.getUpper() == Occurs.UNBOUNDED);
    }

    public boolean isOneOrMore() {
        final Multiplicity multiplicity = feature.getMultiplicity();
        final Range range = multiplicity.getRange();
        return (range.getLower() == Occurs.ONE && range.getUpper() == Occurs.UNBOUNDED);
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
