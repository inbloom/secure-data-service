package org.slc.sli.modeling.jgen;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.namespace.QName;

/**
 * This is a coarse representation of a Java type, but one which is sufficiently rich as to
 * facilitate typical code generation tasks.
 */
public final class JavaType {

    public static final JavaType JT_VOID = simpleType("void");
    public static final JavaType JT_OBJECT = simpleType(Object.class.getSimpleName());
    public static final JavaType JT_STRING = simpleType(String.class.getSimpleName());
    public static final JavaType JT_BOOLEAN = simpleType(Boolean.class.getSimpleName());
    public static final JavaType JT_DOUBLE = simpleType(Double.class.getSimpleName());
    public static final JavaType JT_INTEGER = simpleType(Integer.class.getSimpleName());
    public static final JavaType JT_BIG_INTEGER = simpleType(BigInteger.class.getSimpleName());
    public static final JavaType JT_BIG_DECIMAL = simpleType(BigDecimal.class.getSimpleName());
    public static final JavaType JT_THROWABLE = simpleType(Exception.class.getSimpleName());
    public static final JavaType JT_EXCEPTION = simpleType(Exception.class.getSimpleName(), JT_THROWABLE);

    public static JavaType complexType(final String simpleName, final JavaType base) {
        return new JavaType(simpleName, false, false, false, true, base);
    }
    public static JavaType enumType(final String simpleName, final JavaType base) {
        return new JavaType(simpleName, false, false, true, false, base);
    }
    public static JavaType listType(final JavaType primeType) {
        return new JavaType(primeType.getSimpleName(), true, false, primeType.isEnum, primeType.isComplex, null);
    }
    public static JavaType mapType(final JavaType keyType, final JavaType valueType) {
        return new JavaType(keyType.getSimpleName(), false, true, keyType.isEnum, keyType.isComplex, null);
    }
    private static JavaType simpleType(final String simpleName) {
        return new JavaType(simpleName, false, false, false, false, null);
    }
    public static JavaType simpleType(final String simpleName, final JavaType base) {
        return new JavaType(simpleName, false, false, false, false, base);
    }

    private final QName name;

    private final boolean isList;

    private final boolean isMap;

    private final boolean isEnum;

    private final boolean isComplex;

    private final JavaType base;

    public JavaType(final String simpleName, final boolean isList, final boolean isMap, final boolean isEnum,
            final boolean isComplex, final JavaType base) {
        name = new QName(simpleName);
        this.isList = isList;
        this.isMap = isMap;
        this.isEnum = isEnum;
        this.isComplex = isComplex;
        this.base = base;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof JavaType) {
            final JavaType other = (JavaType) obj;
            // FIXME: Compare base types without going recursive (check for null).
            return this.getSimpleName().equals(other.getSimpleName()) && (this.isList() == other.isList())
                    && (this.isMap() == other.isMap()) && (this.isEnum() == other.isEnum())
                    && (this.isComplex() == other.isComplex());
        } else {
            return false;
        }
    }

    public JavaType getBase() {
        if (base != null) {
            return base;
        } else {
            return JT_OBJECT;
        }
    }

    public String getSimpleName() {
        return name.getLocalPart();
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public boolean isComplex() {
        return isComplex;
    }

    public boolean isEnum() {
        return isEnum;
    }

    public boolean isList() {
        return isList;
    }

    public boolean isMap() {
        return isMap;
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
