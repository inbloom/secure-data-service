package org.slc.sli.modeling.jgen;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.namespace.QName;

public final class JavaType {

    public static final JavaType JT_OBJECT = simpleType(Object.class.getSimpleName());
    public static final JavaType JT_STRING = simpleType(String.class.getSimpleName());
    public static final JavaType JT_BOOLEAN = simpleType(Boolean.class.getSimpleName());
    public static final JavaType JT_DOUBLE = simpleType(Double.class.getSimpleName());
    public static final JavaType JT_INTEGER = simpleType(Integer.class.getSimpleName());
    public static final JavaType JT_BIG_INTEGER = simpleType(BigInteger.class.getSimpleName());
    public static final JavaType JT_BIG_DECIMAL = simpleType(BigDecimal.class.getSimpleName());

    private final QName name;
    private final boolean isList;
    private final boolean isMap;
    private final boolean isEnum;
    private final boolean isComplex;

    public static JavaType simpleType(final String simpleName) {
        return new JavaType(simpleName, false, false, false, false);
    }

    public static JavaType enumType(final String simpleName) {
        return new JavaType(simpleName, false, false, true, false);
    }

    public static JavaType listType(final JavaType primeType) {
        return new JavaType(primeType.getSimpleName(), true, false, primeType.isEnum, primeType.isComplex);
    }

    public static JavaType mapType(final JavaType keyType, final JavaType valueType) {
        return new JavaType(keyType.getSimpleName(), false, true, keyType.isEnum, keyType.isComplex);
    }

    public JavaType(final String simpleName, final boolean isList, final boolean isMap, final boolean isEnum,
            final boolean isComplex) {
        name = new QName(simpleName);
        this.isList = isList;
        this.isMap = isMap;
        this.isEnum = isEnum;
        this.isComplex = isComplex;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof JavaType) {
            final JavaType other = (JavaType) obj;
            return this.name.equals(other.name) && (this.isList == other.isList);
        } else {
            return false;
        }
    }

    public String getSimpleName() {
        return name.getLocalPart();
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public boolean isList() {
        return isList;
    }

    public boolean isMap() {
        return isMap;
    }

    public boolean isEnum() {
        return isEnum;
    }

    public boolean isComplex() {
        return isComplex;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("name : " + name);
        sb.append(", isList : " + isList);
        sb.append(", isMap : " + isMap);
        sb.append(", isEnum : " + isEnum);
        sb.append(", isComplex : " + isComplex);
        sb.append("}");
        return sb.toString();
    }
}
