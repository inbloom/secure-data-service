package org.slc.sli.validation.schema;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration representing a restriction on a value.
 *
 * @author Ryan Farris <rfarris@wgen.net>
 *
 */
public enum Restriction {
    /**
     * regular expression a value must conform to
     */
    PATTERN("pattern"),
    /**
     * exact length of a string or list
     */
    LENGTH("length"),
    /**
     * minimum length of a string or list
     */
    MIN_LENGTH("min-length"),
    /**
     * max length of a string or list
     */
    MAX_LENGTH("max-length"),
    /**
     * minimum value of a number
     */
    MIN_INCLUSIVE("min"),
    /**
     * maximum value of a number
     */
    MAX_INCLUSIVE("max"),
    /**
     * minimum value of a number, exclusive
     */
    MIN_EXCLUSIVE("min-exclusive"),
    /**
     * maximum value of a number, exclusive
     */
    MAX_EXCLUSIVE("max-exclusive"),
    /**
     * total allowable digits in a number
     */
    TOTAL_DIGITS("total-digits"),
    /**
     * number of fractional digits allowed in a number
     */
    FRACTION_DIGITS("fraction-digits");

    private final String value;

    private static final Map<String, Restriction> LOOKUP = new HashMap<String, Restriction>();
    static {
        for (Restriction r : values()) {
            LOOKUP.put(r.getValue(), r);
        }
    }

    private Restriction(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean isRestriction(String value) {
        return LOOKUP.containsKey(value);
    }

    public static Restriction fromValue(String value) {
        return LOOKUP.get(value);
    }
}
