package org.slc.sli.ingestion;

/**
 * Types of batch job status
 *
 * @author bsuzuki
 *
 */
public enum BatchJobStatusType {
    RUNNING("Running"),
    UNDEFINED("Undefined"),
    COMPLETED_WITH_ERRORS("CompletedWithErrors"),
    COMPLETED_SUCCESSFULLY("CompletedSuccessfully");

    private String name;

    BatchJobStatusType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
