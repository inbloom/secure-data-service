package org.slc.sli.api.security.enums;


public enum Rights {
    READ_GENERAL("READ_GENERAL"), WRITE_GENERAL("WRITE_GENERAL"), READ_RESTRICTED("READ_RESTRICTED"),
    WRITE_RESTRICTED("WRITE_RESTRICTED"), AGGREGATE_READ("AGGREGATE_READ"), AGGREGATE_WRITE("AGGREGATE_WRITE");
    
    private final String rightName;

    public String getRight() {
        return rightName;
    }

    private Rights(String right) {
        rightName = right;
    }
}
