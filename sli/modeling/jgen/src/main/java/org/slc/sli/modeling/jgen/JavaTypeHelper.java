package org.slc.sli.modeling.jgen;

import java.util.List;

import org.slc.sli.modeling.uml.Feature;
import org.slc.sli.modeling.uml.Generalization;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.ModelIndex;

public class JavaTypeHelper {

    public static final String getAttributePrimeTypeName(final String typeName) {
        // TODO: We should define some simple types to mirror XML schema data-types.
        if ("string".equals(typeName)) {
            return "String";
        } else if ("boolean".equals(typeName)) {
            return "Boolean";
        } else if ("date".equals(typeName)) {
            return "String";
        } else if ("double".equals(typeName)) {
            return "Double";
        } else if ("Currency".equals(typeName)) {
            return "BigDecimal";
        } else if ("decimal".equals(typeName)) {
            return "BigDecimal";
        } else if ("int".equals(typeName)) {
            return "Integer";
        } else if ("integer".equals(typeName)) {
            return "BigInteger";
        } else if ("percent".equals(typeName)) {
            return "Integer";
        } else if ("Reference".equals(typeName)) {
            return "UUID";
        } else if ("text".equals(typeName)) {
            return "String";
        } else if ("time".equals(typeName)) {
            return "String";
        } else {
            return typeName;
        }
    }

    public static final String getNavigablePrimeTypeName(final String typeName) {
        return "UUID";
    }

    public static final String getAttributeTypeName(final Feature feature, final ModelIndex model,
            final JavaGenConfig config) {
        final Multiplicity multiplicity = feature.getMultiplicity();
        final Range range = multiplicity.getRange();
        final String primeType = getAttributePrimeTypeName(feature, model, config);
        if (range.getUpper() == Occurs.UNBOUNDED) {
            return "List<" + primeType + ">";
        } else {
            return primeType;
        }
    }

    public static final String getNavigableTypeName(final Feature feature, final ModelIndex model) {
        final Multiplicity multiplicity = feature.getMultiplicity();
        final Range range = multiplicity.getRange();
        final Type type = model.getType(feature.getType());
        final String primeType = getNavigablePrimeTypeName(type.getName());
        if (range.getUpper() == Occurs.UNBOUNDED) {
            return "List<" + primeType + ">";
        } else {
            return primeType;
        }
    }

    public static final String getAttributePrimeTypeName(final Feature feature, final ModelIndex model,
            final JavaGenConfig config) {
        final Type type = model.getType(feature.getType());
        if (type.isClassType() || type.isEnumType()) {
            return getAttributePrimeTypeName(type.getName());
        } else {
            if (config.useDataTypeBase()) {
                final List<Generalization> bases = model.getGeneralizationBase(type.getId());
                if (bases.isEmpty()) {
                    return "String";
                } else {
                    return getAttributePrimeTypeName(model.getType(feature.getType()).getName());
                }
            } else {
                return getAttributePrimeTypeName(type.getName());
            }
        }
    }

    public static final String getNavigablePrimeTypeName(final Feature feature, final ModelIndex model) {
        return getNavigablePrimeTypeName(model.getType(feature.getType()).getName());
    }
}
