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
        return new JavaType(simpleName, JavaCollectionKind.NONE, JavaTypeKind.COMPLEX, base);
    }

    public static JavaType enumType(final String simpleName, final JavaType base) {
        return new JavaType(simpleName, JavaCollectionKind.NONE, JavaTypeKind.ENUM, base);
    }

    public static JavaType collectionType(final JavaCollectionKind collectionKind, final JavaType primeType) {
        if (collectionKind == null) {
            throw new NullPointerException("collectionKind");
        }
        if (primeType == null) {
            throw new NullPointerException("primeType");
        }
        return new JavaType(primeType.getSimpleName(), collectionKind, primeType.getTypeKind(), null);
    }

    public static JavaType mapType(final JavaType keyType, final JavaType valueType) {
        return new JavaType(keyType.getSimpleName(), JavaCollectionKind.MAP, keyType.getTypeKind(), null);
    }

    private static JavaType simpleType(final String simpleName) {
        return new JavaType(simpleName, JavaCollectionKind.NONE, JavaTypeKind.SIMPLE, null);
    }

    public static JavaType simpleType(final String simpleName, final JavaType base) {
        return new JavaType(simpleName, JavaCollectionKind.NONE, JavaTypeKind.SIMPLE, base);
    }

    private final QName name;
    private final JavaCollectionKind collectionKind;
    private final JavaTypeKind typeKind;
    private final JavaType base;

    public JavaType(final String simpleName, final JavaCollectionKind collectionKind, final JavaTypeKind typeKind,
            final JavaType base) {
        name = new QName(simpleName);
        this.base = base;
        this.collectionKind = collectionKind;
        this.typeKind = typeKind;

    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof JavaType) {
            final JavaType other = (JavaType) obj;
            // FIXME: Compare base types without going recursive (check for null).
            return this.getSimpleName().equals(other.getSimpleName())
                    && (this.getCollectionKind() == other.getCollectionKind())
                    && (this.getTypeKind() == other.getTypeKind());
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

    public JavaType getBaseType() {
        if (!getBase().equals(JavaType.JT_OBJECT) && !(typeKind == JavaTypeKind.ENUM)) {
            return getBase().getBaseType();
        } else {
            return this;
        }
    }

    public JavaCollectionKind getCollectionKind() {
        return collectionKind;
    }

    public String getSimpleName() {
        return name.getLocalPart();
    }

    public JavaTypeKind getTypeKind() {
        return typeKind;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public JavaType primeType() {
        switch (collectionKind) {
        case NONE: {
            return this;
        }
        case LIST: {
            return new JavaType(name.getLocalPart(), JavaCollectionKind.NONE, typeKind, base);
        }
        default: {
            throw new AssertionError(collectionKind);
        }
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("name : " + name);
        sb.append(", collectionKind : " + collectionKind);
        sb.append(", typeKind : " + typeKind);
        sb.append("}");
        return sb.toString();
    }
}
