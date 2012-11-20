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

import java.util.List;

import org.slc.sli.modeling.uml.Feature;
import org.slc.sli.modeling.uml.Generalization;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.ModelIndex;

/**
 * Java type helper methods.
 */
public class JavaTypeHelper {

    @SuppressWarnings("unused")
    private static final JavaTypeKind computeKind(final Type type) {
        if (type.isEnumType()) {
            return JavaTypeKind.ENUM;
        } else if (type.isClassType()) {
            return JavaTypeKind.COMPLEX;
        } else {
            return JavaTypeKind.SIMPLE;
        }
    }

    public static final JavaType getAttributePrimeType(final Feature feature, final ModelIndex model,
            final JavaGenConfig config) {
        final Type type = model.getType(feature.getType());
        return getJavaType(type, model, config);
    }

    public static final JavaType getAttributePrimeType(final String simpleName,
            final JavaCollectionKind collectionKind, final JavaTypeKind typeKind, final JavaType base) {
        // TODO: We should define some simple types to mirror XML schema data-types.
        if ("string".equals(simpleName)) {
            return JavaType.JT_STRING;
        } else if ("boolean".equals(simpleName)) {
            return JavaType.JT_BOOLEAN;
        } else if ("date".equals(simpleName)) {
            return JavaType.JT_STRING;
        } else if ("double".equals(simpleName)) {
            return JavaType.JT_DOUBLE;
        } else if ("Currency".equals(simpleName)) {
            return JavaType.JT_BIG_DECIMAL;
        } else if ("decimal".equals(simpleName)) {
            return JavaType.JT_BIG_DECIMAL;
        } else if ("int".equals(simpleName)) {
            return JavaType.JT_INTEGER;
        } else if ("integer".equals(simpleName)) {
            return JavaType.JT_BIG_INTEGER;
        } else if ("percent".equals(simpleName)) {
            return JavaType.JT_INTEGER;
        } else if ("Reference".equals(simpleName)) {
            return JavaType.JT_STRING;
        } else if ("text".equals(simpleName)) {
            return JavaType.JT_STRING;
        } else if ("time".equals(simpleName)) {
            return JavaType.JT_STRING;
        } else {
            if (JavaTypeKind.ENUM == typeKind) {
                return JavaType.enumType(simpleName, base);
            } else {
                return new JavaType(simpleName, collectionKind, typeKind, base);
            }
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

    public static final JavaType getAttributeType(final Feature feature, final ModelIndex model,
            final JavaGenConfig config) {
        final Multiplicity multiplicity = feature.getMultiplicity();
        final Range range = multiplicity.getRange();
        final JavaType primeType = getAttributePrimeType(feature, model, config);
        if (range.getUpper() == Occurs.UNBOUNDED) {
            return JavaType.collectionType(JavaCollectionKind.LIST, primeType);
        } else {
            return primeType;
        }
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

    private static final JavaType getJavaType(final Type type, final ModelIndex model, final JavaGenConfig config) {
        if (type.isClassType()) {
            return getAttributePrimeType(type.getName(), JavaCollectionKind.NONE, JavaTypeKind.COMPLEX,
                    JavaType.JT_OBJECT);
        } else if (type.isEnumType()) {
            return getAttributePrimeType(type.getName(), JavaCollectionKind.NONE, JavaTypeKind.ENUM, JavaType.JT_STRING);
        } else if (type.isDataType()) {
            // Simple type, but not an enumeration.
            final List<Generalization> bases = model.getGeneralizationBase(type.getId());
            if (bases.isEmpty()) {
                return getAttributePrimeType(type.getName(), JavaCollectionKind.NONE, JavaTypeKind.SIMPLE,
                        JavaType.JT_STRING);
            } else {
                final Generalization generalization = bases.get(0);
                final Identifier parentId = generalization.getParent();
                final Type base = model.getType(parentId);
                final JavaType baseType = getJavaType(base, model, config);
                return getAttributePrimeType(type.getName(), JavaCollectionKind.NONE, JavaTypeKind.SIMPLE, baseType);
            }
        } else {
            throw new AssertionError();
        }
    }

    public static final JavaType getNavigablePrimeType(final Feature feature, final ModelIndex model) {
        return getNavigablePrimeType(model.getType(feature.getType()).getName());
    }

    public static final JavaType getNavigablePrimeType(final String typeName) {
        return JavaType.JT_STRING;
    }

    public static final String getNavigablePrimeTypeName(final Feature feature, final ModelIndex model) {
        return getNavigablePrimeTypeName(model.getType(feature.getType()).getName());
    }

    public static final String getNavigablePrimeTypeName(final String typeName) {
        return "String";
    }

    public static final JavaType getNavigableType(final Feature feature, final ModelIndex model) {
        final Multiplicity multiplicity = feature.getMultiplicity();
        final Range range = multiplicity.getRange();
        final Type type = model.getType(feature.getType());
        final JavaType primeType = getNavigablePrimeType(type.getName());
        if (range.getUpper() == Occurs.UNBOUNDED) {
            return JavaType.collectionType(JavaCollectionKind.LIST, primeType);
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
}
