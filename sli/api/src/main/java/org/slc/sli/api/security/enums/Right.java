package org.slc.sli.api.security.enums;

import org.springframework.security.core.GrantedAuthority;

/**
 * A simple enum describing our basic rights that are required.
 *
 */
public enum Right implements GrantedAuthority {
    READ_GENERAL("READ_GENERAL"), WRITE_GENERAL("WRITE_GENERAL"), READ_RESTRICTED("READ_RESTRICTED"),
    WRITE_RESTRICTED("WRITE_RESTRICTED"), AGGREGATE_READ("AGGREGATE_READ"), AGGREGATE_WRITE("AGGREGATE_WRITE");
    
    private final String rightName;

    public String getRight() {
        return rightName;
    }

    private Right(String right) {
        rightName = right;
    }
    
    public static Right getRightFromString(String right) throws Exception {
        if (right.equalsIgnoreCase(READ_GENERAL.getRight()))
            return READ_GENERAL;
        if (right.equalsIgnoreCase(WRITE_GENERAL.getRight()))
            return WRITE_GENERAL;
        if (right.equalsIgnoreCase(READ_RESTRICTED.getRight()))
            return READ_RESTRICTED;
        if (right.equalsIgnoreCase(WRITE_RESTRICTED.getRight()))
            return WRITE_RESTRICTED;
        if (right.equalsIgnoreCase(AGGREGATE_READ.getRight()))
            return AGGREGATE_READ;
        if (right.equalsIgnoreCase(AGGREGATE_WRITE.getRight()))
            return AGGREGATE_WRITE;
        throw new Exception("No applicable right found.");
    }

    @Override
    public String getAuthority() {
        return this.rightName;
    }
}
