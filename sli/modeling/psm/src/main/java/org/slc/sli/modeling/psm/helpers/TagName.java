package org.slc.sli.modeling.psm.helpers;

/**
 * Names of standard tag definitions.
 */
public final class TagName {
    public static final String DOCUMENTATION = "documentation";
    public static final String FRACTION_DIGITS = "fractionDigits";

    public static final String LENGTH = "length";
    public static final String MAX_EXCLUSIVE = "maxExclusive";
    public static final String MAX_INCLUSIVE = "maxInclusive";
    public static final String MAX_LENGTH = "maxLength";
    public static final String MIN_EXCLUSIVE = "minExclusive";
    public static final String MIN_INCLUSIVE = "minInclusive";
    public static final String MIN_LENGTH = "minLength";
    public static final String PATTERN = "pattern";
    public static final String TOTAL_DIGITS = "totalDigits";
    public static final String WHITE_SPACE = "whiteSpace";
    /**
     * The MongoDB collection (reserved for future use).
     */
    public static final String MONGO_COLLECTION = "mongo.collection";
    /**
     * Determines whether aggregation semantics are used in MongoDB.
     * (reserved for future use).
     */
    public static final String MONGO_AGGREGATION = "mongo.aggregation";
    /**
     * Determines whether navigation is possible for an association end in MongoDB.
     */
    public static final String MONGO_NAVIGABLE = "mongo.navigable";
    /**
     * Determines the (override) name that should be used in MongoDB.
     */
    public static final String MONGO_NAME = "mongo.name";
}
