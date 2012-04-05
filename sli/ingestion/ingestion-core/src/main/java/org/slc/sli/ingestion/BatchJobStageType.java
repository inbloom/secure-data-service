package org.slc.sli.ingestion;

/**
 * Types of stages
 *
 * @author bsuzuki
 *
 */
public enum BatchJobStageType {
    TYPE_STARTED("Started"),
    TYPE_ZIPFILEPROCESSOR("ZipfileProcessor"),
    TYPE_CONTROLFILEPREPROCESSOR("ControlFilePreprocessor"),
    TYPE_CONTROLFILEPROCESSOR("ControlFileProcessor"),
    TYPE_NEUTRALRECORDSMERGE("NeutralRecordMergeProcessor"),
    TYPE_EDFIPROCESSOR("EdFiProcessor"),
    TYPE_PERSISTENCEPROCESSOR("PersistenceProcessor"),
    TYPE_TRANSFORMATIONPROCESSOR("TransformationProcessor"),
    TYPE_COMPLETEDWITHERRORS("CompletedWithErrors"),
    TYPE_COMPLETEDSUCCESSFULLY("CompletedSuccessfully");

    private String name;

    BatchJobStageType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
