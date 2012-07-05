package org.slc.sli.shtick;

import java.math.BigInteger;
import java.util.Map;

public final class Coercions {

    public static final BigInteger toBigInteger(final Object obj) {
        if (obj != null) {
            return new BigInteger(obj.toString());
        } else {
            return null;
        }
    }

    public static final Boolean toBoolean(final Object obj) {
        if (obj != null) {
            if (obj instanceof Boolean) {
                return (Boolean) obj;
            } else {
                return Boolean.valueOf(obj.toString());
            }
        } else {
            return null;
        }
    }

    public static final Double toDouble(final Object obj) {
        if (obj != null) {
            if (obj instanceof Double) {
                return (Double) obj;
            } else {
                return Double.valueOf(obj.toString());
            }
        } else {
            return null;
        }
    }

    public static final Integer toInteger(final Object obj) {
        if (obj != null) {
            return Integer.valueOf(obj.toString());
        } else {
            return null;
        }
    }

    public static final Map<String, Object> toMap(final Object obj) {
        if (obj != null) {
            if (obj instanceof Map) {
                @SuppressWarnings("unchecked")
                final Map<String, Object> map = (Map<String, Object>) obj;
                return map;
            } else {
                throw new IllegalArgumentException("obj");
            }
        } else {
            return null;
        }
    }

    public static final String toString(final Object obj) {
        if (obj != null) {
            if (obj instanceof String) {
                return (String) obj;
            } else {
                return obj.toString();
            }
        } else {
            return null;
        }
    }
}
