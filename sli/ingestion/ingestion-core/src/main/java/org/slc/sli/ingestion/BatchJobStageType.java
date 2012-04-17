package org.slc.sli.ingestion;

/**
 * Types of stages
 *
 * @author bsuzuki
 *
 */
public enum BatchJobStageType {
    STARTED("Started"),

    ZIP_FILE_PROCESSING("ZipFileProcessing"),
    CONTROL_FILE_PREPROCESSING("ControlFilePreprocessing"),
    CONTROL_FILE_PROCESSING("ControlFileProcessing"),
    PURGE_PROCESSING("PurgeProcessing"),
    XML_FILE_PROCESSING("XmlFileProcessing"),
    EDFI_PROCESSING("EdFiProcessing"),
    TRANSFORMATION_PROCESSING("TransformationProcessing"),
    NR_MERGE_PROCESSING("NeutralRecordMergeProcessing"),
    PERSISTENCE_PROCESSING("PersistenceProcessing"),
    JOB_REPORTING_PROCESSING("JobReportingProcessing"),

    UNDEFINED("Undefined"),

    COMPLETED_WITH_ERRORS("CompletedWithErrors"),
    COMPLETED_SUCCESSFULLY("CompletedSuccessfully");

    private String name;

    BatchJobStageType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
