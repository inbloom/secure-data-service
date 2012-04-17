package org.slc.sli.ingestion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.ErrorReportSupport;
import org.slc.sli.util.performance.PutResultInContext;

/**
 * Batch Job class.
 *
 * @author okrook
 *
 */
public final class BatchJob implements Serializable, ErrorReportSupport, Job {

    private static final long serialVersionUID = -340538024579162600L;

    /**
     * stores a globally unique ID for the Job
     */
    private final String id;

    /**
     * stores the date upon which the Job was created
     */
    private final Date creationDate;

    /**
     * holds references to the files involved in this Job
     */
    private List<IngestionFileEntry> files;

    /**
     * stores configuration properties for the Job
     */
    private Properties configProperties;

    /**
     * holds references to errors/warnings associated with this job
     */
    private FaultsReport faults;

    /**
     * non-public constructor; use factory methods
     */
    private BatchJob(String id) {
        this.id = id;
        this.creationDate = new Date();
    }

    /**
     * Initialize a BatchJob with default settings for initialization
     *
     * @return BatchJob with default settings
     */
    public static Job createDefault() {
        return BatchJob.createDefault(null);
    }

    /**
     * Initialize a BatchJob with default settings for initialization
     *
     * @param filename
     *            string representation of incoming file
     * @return BatchJob with default settings
     */
    public static Job createDefault(String filename) {
        BatchJob job = new BatchJob(createId(filename));

        job.configProperties = new Properties();
        job.files = new ArrayList<IngestionFileEntry>();
        job.faults = new FaultsReport();
        return job;
    }

    /**
     * generates a new unique ID
     */
    @PutResultInContext(returnName = "ingestionBatchJobId")
    protected static String createId(String filename) {
        if (filename == null)
            return System.currentTimeMillis() + "-" + UUID.randomUUID().toString();
        else
            return filename + "-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString();
    }

    /* (non-Javadoc)
     * @see org.slc.sli.ingestion.Job#addFile(org.slc.sli.ingestion.landingzone.IngestionFileEntry)
     */
    @Override
    public boolean addFile(IngestionFileEntry ingestionFileEntry) {

        ingestionFileEntry.setBatchJobId(id);
        return files.add(ingestionFileEntry);
    }

    /* (non-Javadoc)
     * @see org.slc.sli.ingestion.Job#getCreationDate()
     */
    @Override
    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public ErrorReport getErrorReport() {
        return getFaultsReport();
    }

    /* (non-Javadoc)
     * @see org.slc.sli.ingestion.Job#getFaultsReport()
     */
    @Override
    public FaultsReport getFaultsReport() {
        return faults;
    }

    /* (non-Javadoc)
     * @see org.slc.sli.ingestion.Job#getFiles()
     */
    @Override
    public List<IngestionFileEntry> getFiles() {
        return files;
    }

    /* (non-Javadoc)
     * @see org.slc.sli.ingestion.Job#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.slc.sli.ingestion.Job#getProperty(java.lang.String)
     */
    @Override
    public String getProperty(String key) {
        return configProperties.getProperty(key);
    }

    /* (non-Javadoc)
     * @see org.slc.sli.ingestion.Job#getProperty(java.lang.String, java.lang.String)
     */
    @Override
    public String getProperty(String key, String defaultValue) {
        return configProperties.getProperty(key, defaultValue);
    }

    /* (non-Javadoc)
     * @see org.slc.sli.ingestion.Job#propertyNames()
     */
    @Override
    public Enumeration<?> propertyNames() {
        return configProperties.propertyNames();
    }

    /* (non-Javadoc)
     * @see org.slc.sli.ingestion.Job#setProperty(java.lang.String, java.lang.String)
     */
    @Override
    public Object setProperty(String key, String value) {
        return configProperties.setProperty(key, value);
    }

    @Override
    public String toString() {
        return "BatchJob [id=" + id + ", creationDate=" + creationDate + ", configProperties=" + configProperties + "]";
    }

}
