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

    public static final JavaType getAttributePrimeType(final String typeName, final boolean isEnum, final boolean isComplex) {
        // TODO: We should define some simple types to mirror XML schema data-types.
        if ("string".equals(typeName)) {
            return JavaType.JT_STRING;
        } else if ("boolean".equals(typeName)) {
            return JavaType.JT_BOOLEAN;
        } else if ("date".equals(typeName)) {
            return JavaType.JT_STRING;
        } else if ("double".equals(typeName)) {
            return JavaType.JT_DOUBLE;
        } else if ("Currency".equals(typeName)) {
            return JavaType.JT_BIG_DECIMAL;
        } else if ("decimal".equals(typeName)) {
            return JavaType.JT_BIG_DECIMAL;
        } else if ("int".equals(typeName)) {
            return JavaType.JT_INTEGER;
        } else if ("integer".equals(typeName)) {
            return JavaType.JT_BIG_INTEGER;
        } else if ("percent".equals(typeName)) {
            return JavaType.JT_INTEGER;
        } else if ("Reference".equals(typeName)) {
            return JavaType.simpleType("UUID");
        } else if ("text".equals(typeName)) {
            return JavaType.JT_STRING;
        } else if ("time".equals(typeName)) {
            return JavaType.JT_STRING;
        } else {
            // FIXME: This is suspect.
            return new JavaType(typeName, false, false, isEnum, isComplex);
        }
    }

    public static final String getNavigablePrimeTypeName(final String typeName) {
        return "UUID";
    }

    public static final JavaType getNavigablePrimeType(final String typeName) {
        return JavaType.simpleType("UUID");
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

    public static final JavaType getAttributeType(final Feature feature, final ModelIndex model,
            final JavaGenConfig config) {
        final Multiplicity multiplicity = feature.getMultiplicity();
        final Range range = multiplicity.getRange();
        final JavaType primeType = getAttributePrimeType(feature, model, config);
        if (range.getUpper() == Occurs.UNBOUNDED) {
            return JavaType.listType(primeType);
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

    public static final JavaType getNavigableType(final Feature feature, final ModelIndex model) {
        final Multiplicity multiplicity = feature.getMultiplicity();
        final Range range = multiplicity.getRange();
        final Type type = model.getType(feature.getType());
        final JavaType primeType = getNavigablePrimeType(type.getName());
        if (range.getUpper() == Occurs.UNBOUNDED) {
            return JavaType.listType(primeType);
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

    public static final JavaType getAttributePrimeType(final Feature feature, final ModelIndex model,
            final JavaGenConfig config) {
        final Type type = model.getType(feature.getType());
        if (type.isClassType() || type.isEnumType()) {
            return getAttributePrimeType(type.getName(), type.isEnumType(), type.isClassType());
        } else {
            if (config.useDataTypeBase()) {
                final List<Generalization> bases = model.getGeneralizationBase(type.getId());
                if (bases.isEmpty()) {
                    return JavaType.JT_STRING;
                } else {
                    return getAttributePrimeType(model.getType(feature.getType()).getName(), false, false);
                }
            } else {
                return getAttributePrimeType(type.getName(), false, false);
            }
        }
    }

    public static final String getNavigablePrimeTypeName(final Feature feature, final ModelIndex model) {
        return getNavigablePrimeTypeName(model.getType(feature.getType()).getName());
    }

    public static final JavaType getNavigablePrimeType(final Feature feature, final ModelIndex model) {
        return getNavigablePrimeType(model.getType(feature.getType()).getName());
    }
}
