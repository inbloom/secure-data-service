package org.slc.sli.ingestion;

//import java.io.File;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.common.util.performance.PutResultInContext;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.ErrorReportSupport;

/**
 * Database Batch Job class.
 *
 * @author bsuzuki
 *
 */
public final class DatabaseBatchJob implements Serializable, ErrorReportSupport {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseBatchJob.class);

    // TODO need to change this?
    private static final long serialVersionUID = -340538024579162600L;

    /**
     * stores a globally unique ID for the Job
     */
    private String id;

    /**
     * stage type cache
     */
    private BatchJobStageType stage = null;

    /**
     * holds references to the files involved in this Job
     */
//    private List<IngestionFileEntry> files;

    /**
     * stores the date upon which the Job was created
     */
//    private Date creationDate;

    /**
     * stores configuration properties for the Job
     */
//    private Properties configProperties;

    /**
     * holds references to errors/warnings associated with this job
     */
//    private FaultsReport faults;

    /**
     * Initialize a DatabaseBatchJob with default settings for initialization
     *
     * @return DatabaseBatchJob with default settings
     */
    public static DatabaseBatchJob createDefault() {
        return DatabaseBatchJob.createDefault(null);
    }

    /**
     * Initialize a BatchJob with default settings for initialization
     *
     * @param filename string representation of incoming file
     * @return BatchJob with default settings
     */
    public static DatabaseBatchJob createDefault(String filename) {
        DatabaseBatchJob job = new DatabaseBatchJob();
        job.id = createId(filename);
        // TODO BatchJobUtils JobLog.createJob(job.id);
        return job;
    }

    /**
     * generates a new unique ID
     */
    @PutResultInContext(returnName = "databaseIngestionBatchJobId")
    protected static String createId(String filename) {
        if (filename == null) {
            return System.currentTimeMillis() + "-" + UUID.randomUUID().toString();
        } else {
            return filename + "-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString();
        }
    }

    /**
     * Adds a file.
     *
     * @param file
     * @return
     * @see java.util.List#add(java.lang.Object)
     */
    public boolean addFile(IngestionFileEntry file) {

        // TODO BatchJobUtils - refactor IngestionFileEntry to be per File
        // Create the original file field
        // JobLog.createOrUpdateFile(id, file.getFormat(), file.getType(), file.getFileName(), file.getChecksum());
        return true;
    }

    @Override
    public ErrorReport getErrorReport() {
        return getFaultsReport();
    }

    public FaultsReport getFaultsReport() {
        // TODO BatchJobUtils
        // Get the faults from the db
//        JobLogStatus status = JobLog.getFaults(id);
//        if (status.getSuccess())
//            return status.getResult();
//
//        log(status.getMessage());
        return null;
    }

    // TODO: enumerate severity for errors

    /**
     * Write a batch job error using state manager system properties.
     * This should be refactored to be an interface with different implementations.
     *
     * @param batchJobId
     * @param stageName
     * @param fileId
     * @param severity  // TODO enumerate this
     * // TODO determine if we need @param errorType
     * @param errorDetail
     */
    public void logError(BatchJobStageType stage, String fileId, String severity, String errorDetail) {
        // TODO: create an error document in the db
//        JobLogStatus status = JobLog.log(id, stage, fileId, severity, errorDetail);
//        if (!status.getSuccess())
//          LOG.error("Job {} unable to log error \"{}\"", id, status.getMessage());
    }
    public void logStageError(BatchJobStageType stage, String severity, String errorDetail) {
        logError(stage, null, severity, errorDetail);
    }
    public void logBatchError(String severity, String errorDetail) {
        logError(null, null, severity, errorDetail);
    }

    /**
     * Should be called after beginStage(), but before endStage()
     *
     * @return the files
     */
    public List<IngestionFileEntry> getResources() {
        // TODO BatchJobUtils
        // Get the files from the db
//        JobLogStatus status = JobLog.getFiles(id);
//        if (status.getSuccess())
//            return status.getResult();
//
//        log(status.getMessage());

        // set the jobId and stage for each resource
//        for (IngestionFileEntry resource : resources) {
//            resource.setBatchJobId(id);
//            resource.getDatabaseErrorsReport().setBatchJobId(stage);
//            if (stage != null)
//                resource.getDatabaseErrorsReport().setBatchJobStage(stage);
//        }

        return null;
    }

    /**
     * @return the jobId
     */
    public String getId() {
        return id;
    }

    /**
     * @param key
     * @return
     * @see java.util.Properties#getProperty(java.lang.String)
     */
    public String getProperty(String key) {
        // TODO BatchJobUtils
        // Get job properties from the database
//        Properties configProperties = JobLog.getProperties(id);
//        return configProperties.getProperty(key);
        return null;
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     * @see java.util.Properties#getProperty(java.lang.String, java.lang.String)
     */
    public String getProperty(String key, String defaultValue) {
        // TODO BatchJobUtils
        // Get job properties from the database
//        Properties configProperties = JobLog.getProperties(id);
//        return configProperties.getProperty(key, defaultValue);
        return null;
    }

    /**
     * @return
     * @see java.util.Properties#propertyNames()
     */
    public Enumeration<?> propertyNames() {
        // TODO BatchJobUtils
        // Get job properties from the database - use iterator?
//        Properties configProperties = JobLog.getProperties(id);
//        return configProperties.propertyNames();
        return null;
    }

    /**
     * @param key
     * @param value
     * @return
     * @see java.util.Properties#setProperty(java.lang.String, java.lang.String)
     */
    public void setProperty(String key, String value) {
        // TODO BatchJobUtils
//      JobLogStatus status = JobLog.setProperty(id, key, value);
//      if (!status.getSuccess())
//        logBatchError("ERROR", status.getMessage());
    }

    @Override
    public String toString() {
        // TODO get job static data from the db as well
        return "BatchJob [id=" + id + "]";
    }

    /**
     * Set the status of a batch job  // TODO enumerate status values
     *
     * @param status
     */
    public void setStatus(BatchJobStageType status) {
        // TODO update the job status
//      JobLogStatus jobLogStatus = JobLog.setJobStatus(id, status);
//      if (!jobLogStatus.getSuccess())
//        logBatchError("ERROR", jobLogStatus.getMessage());
    }

    /**
     * Create a stage in the db with startTimeStamp of now
     *
     * @param stage
     */
    public void beginStage(BatchJobStageType batchJobStage) {
            LOG.info("started a stage in the db managed batch job");

            // TODO: create a stage field with status "running" and startTime of now in the db
            long startTime = System.currentTimeMillis();
            // TODO update the job status
//          status = JobLog.createOrUpdateStage(id, stage, "started", startTime, null);
//          if (!status.getSuccess())
//            logBatchError("ERROR", status.getMessage());

            this.stage = batchJobStage;  // cache the stage
    }

    /**
     * Update the stage field in the db to have stopTimeStamp of now
     *
     * @param stage     // TODO determine stage from existing job state
     */
    public void endStage(BatchJobStageType stage) {
        LOG.info("stopped the stage in the db managed batch job");
        // TODO update the stage status to "completed" and the stopTimeStamp to now in the db
        long stopTime = System.currentTimeMillis();
//      status = JobLog.createOrUpdateStage(id, stage, "completed", null, stopTime);
//      if (!status.getSuccess())
//        logBatchError("ERROR", status.getMessage());

        this.stage = null;
    }

    /**
     * Create a metric with startTime of current system time
     *
     * @param stage
     * @param fileId
     */
    public void startMetric(BatchJobStageType stage, String resourceId) {
        LOG.info("creating a metric");
        // TODO: create a metric with startTimeStamp of now in the db
        long startTime = System.currentTimeMillis();
//      status = JobLog.createOrUpdateMetric(id, stage, resourceId, startTime, null, null, null);
//      if (!status.getSuccess())
//        logBatchError("ERROR", status.getMessage());
   }

    /**
     * Update the metric in the db with the current time as stopTime and the specified record and
     * error counts
     *
     * @param stage   // TODO determine stage from existing job state
     * @param fileId
     * @param recordCount
     * @param errorCount
     */
    public void stopMetric(BatchJobStageType stage, String resourceId, int recordCount, int errorCount) {
        LOG.info("stopping a metric");

        long stopTime = System.currentTimeMillis();
        // status = JobLog.createOrUpdateMetric(id, stage, resourceId, null, stopTime, recordCount, errorCount);
        // if (!status.getSuccess())
        // logBatchError("ERROR", status.getMessage());
    }

}
