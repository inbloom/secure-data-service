package org.slc.sli.validation.schema;


/**
 * Utility functions for converting numbers and counting digits.
 * 
 * Always convert to either a double or long.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
public class NumberUtils {

    /**
     * Helper interface for converting objects
     */
    public interface Converter {
        public Object convert(Object value);
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
        }
        return null;
    }
    
    /**
     * Takes an object and attempts to convert it to a integer, returning null if it's not a numeric
     * type.
     */
    public static Integer toInteger(Object data) {
        if (data instanceof Integer) {
            return (Integer) data;
        }
        return null;
    }

    /**
     * Converts a string object to the desired object
     * @param value Value to convert
     * @param converter The actual converter doing the work
     * @return
     */
    public static Object converterHelper(Object value, Converter converter) {
        Object retValue = value;
        try {
            if (value != null && String.class.isInstance(value)
                    && !((String) value).isEmpty()) {
                retValue = converter.convert(value);
            }
        } catch (NumberFormatException e) {
            retValue = value;
        }

        return retValue;
    }
}
