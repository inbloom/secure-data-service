package org.slc.sli.ingestion.landingzone;

/**
 *
 * @author npandey
 *
 */
public enum AttributeType {
    PURGE("purge"),
    DRYRUN("dry-run");

    private String name;

    AttributeType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}