package org.slc.sli.api.security.enums;

/**
 * A simple enum describing our basic rights that are required.
 *
 */
public enum Rights {
    READ_AGGREGATE("READ_AGGREGATE"),
    READ_GENERAL("READ_GENERAL"),
    READ_RESTRICTED("READ_RESTRICTED"),

    WRITE_AGGREGATE("WRITE_AGGREGATE"),
    WRITE_GENERAL("WRITE_GENERAL"),
    WRITE_RESTRICTED("WRITE_RESTRICTED");

    private final String rightName;

    public String getRight() {
        return rightName;
    }

    private Rights(String right) {
        rightName = right;
    }
}
