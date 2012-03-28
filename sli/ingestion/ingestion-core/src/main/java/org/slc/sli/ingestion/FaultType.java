package org.slc.sli.ingestion;

/**
 * Types of faults
 *
 * @author okrook
 *
 */
public enum FaultType {
    TYPE_WARNING("WARNING"),
    TYPE_ERROR("ERROR");

    private String name;

    FaultType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
