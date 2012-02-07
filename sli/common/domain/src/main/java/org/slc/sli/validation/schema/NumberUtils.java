package org.slc.sli.validation.schema;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Utility functions for converting numbers and counting digits.
 *
 * Always convert to either a double or long.
 *
 * @author Ryan Farris <rfarris@wgen.net>
 *
 */
public class NumberUtils {

    public static int totalDigits(double d) {
        return totalDigits(BigDecimal.valueOf(d));
    }

    public static int totalDigits(long l) {
        return totalDigits(BigDecimal.valueOf(l));
    }

    public static int totalDigits(BigDecimal d) {
        /*
         * Yes, this can be done through the power of maths:
         * --- int digits = d.unscaledValue().bitLength() * Math.log10(2) - d.scale();
         * --- return digits > 0 ? digits : d.scale
         * ... but precision issues make it difficult.
         */
        String num = d.toPlainString();
        int length = num.length();
        if (num.startsWith("-")) {
            length--;
        }
        if (num.contains(".")) {
            length--;
        }
        if (num.matches("\\d+\\.0")) {
            length--;
        }
        return length;
    }

    public static int fractionalDigits(double d) {
        String num = BigDecimal.valueOf(d).toPlainString();
        // BigDecimal.toPlainString() should always use period, regardless of system locale.
        if (num.contains(".")) {
            num = num.substring(num.indexOf('.') + 1);
            if (num.matches("^0$")) {
                // if there are no fractional digits, toPlainString() includes a 0
                return 0;
            } else {
                return num.length();
            }
        } else {
            return 0;
        }
    }

    /**
     * Takes an object and attempts to convert it to a double, returning null if it's not a numeric
     * type.
     */
    public static Double toDouble(Object data) {
        if (data instanceof Integer) {
            return ((Integer) data).doubleValue();
        } else if (data instanceof Long) {
            return ((Long) data).doubleValue();
        } else if (data instanceof Float) {
            return ((Float) data).doubleValue();
        } else if (data instanceof Double) {
            return (Double) data;
        } else if (data instanceof BigDecimal) {
            return ((BigDecimal) data).doubleValue();
        } else if (data instanceof BigInteger) {
            return ((BigInteger) data).doubleValue();
        }
        return null;
    }

    /**
     * Takes an object and attempts to convert it to a long, returning null if it's not a numeric
     * type.
     */
    public static Long toLong(Object data) {
        if (data instanceof Integer) {
            return ((Integer) data).longValue();
        } else if (data instanceof Long) {
            return (Long) data;
        } else if (data instanceof Float) {
            return ((Float) data).longValue();
        } else if (data instanceof Double) {
            return ((Double) data).longValue();
        } else if (data instanceof BigDecimal) {
            return ((BigDecimal) data).longValue();
        } else if (data instanceof BigInteger) {
            return ((BigInteger) data).longValue();
        }
        return null;
    }

    /**
     * Takes an object and attempts to convert it to a integer, returning null if it's not a numeric
     * type.
     */
    public static Integer toInteger(Object data) {
        if (data instanceof Integer) {
            return ((Integer) data).intValue();
        } else if (data instanceof Float) {
            return ((Float) data).intValue();
        } else if (data instanceof Double) {
            return ((Double) data).intValue();
        } else if (data instanceof BigDecimal) {
            return ((BigDecimal) data).intValue();
        } else if (data instanceof BigInteger) {
            return ((BigInteger) data).intValue();
        }
        return null;
    }
}
