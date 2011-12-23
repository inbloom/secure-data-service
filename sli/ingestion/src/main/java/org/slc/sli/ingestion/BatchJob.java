package org.slc.sli.ingestion;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.ErrorReportSupport;

/**
 * Batch Job class.
 *
 * @author okrook
 *
 */
public class BatchJob implements ErrorReportSupport {

    /**
     * ID ====================================================================
     */

    /**
     * stores a globally unique ID for the Job
     */
    private String id;

    /**
     * generates a new unique ID
     */
    protected static String createId() {
        return UUID.randomUUID().toString();
    }

    /**
     * @return the jobId
     */
    public String getId() {
        return id;
    }

    /**
     * CREATION DATE =========================================================
     */

    /**
     * stores the date upon which the Job was created
     */
    private Date creationDate;

    /**
     * @return the creationDate
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * PROPERTIES ============================================================
     */

    /**
     * stores configuration properties for the Job
     */
    private Properties configProperties;

    /**
     * @param key
     * @param defaultValue
     * @return
     * @see java.util.Properties#getProperty(java.lang.String, java.lang.String)
     */
    public String getProperty(String key, String defaultValue) {
        return configProperties.getProperty(key, defaultValue);
    }

    /**
     * @param key
     * @return
     * @see java.util.Properties#getProperty(java.lang.String)
     */
    public String getProperty(String key) {
        return configProperties.getProperty(key);
    }

    /**
     * @return
     * @see java.util.Properties#propertyNames()
     */
    public Enumeration<?> propertyNames() {
        return configProperties.propertyNames();
    }

    /**
     * @param key
     * @param value
     * @return
     * @see java.util.Properties#setProperty(java.lang.String, java.lang.String)
     */
    public Object setProperty(String key, String value) {
        return configProperties.setProperty(key, value);
    }

    /**
     * FILES =================================================================
     */

    /**
     * holds references to the files involved in this Job
     */
    private List<IngestionFileEntry> files;

    /**
     * @return the files
     */
    public List<IngestionFileEntry> getFiles() {
        return files;
    }

    /**
     * Adds a file.
     *
     * @param file
     * @return
     * @see java.util.List#add(java.lang.Object)
     */
    public boolean addFile(IngestionFileEntry file) {
        return files.add(file);
    }

    /**
     * FAULTS (errors/warnings) ==============================================
     */

    /**
     * holds references to errors/warnings associated with this job
     */
    private FaultsReport faults;

    /**
     * INSTANTIATION =========================================================
     */

    /**
     * non-public constructor; use factory methods
     */
    protected BatchJob() {
    }

    /**
     * Initialize a BatchJob with default settings for initialization
     *
     * @return BatchJob with default settings
     */
    public static BatchJob createDefault() {
        BatchJob job = new BatchJob();
        job.id = createId();
        job.creationDate = new Date();
        job.configProperties = new Properties();
        job.files = new ArrayList<IngestionFileEntry>();
        job.faults = new FaultsReport();
        return job;
    }

    @Override
    public String toString() {
        return "BatchJob [id=" + id + ", creationDate=" + creationDate + ", configProperties=" + configProperties + "]";
    }

    public FaultsReport getFaultsReport() {
        return faults;
    }

    @Override
    public ErrorReport getValidationReport() {
        return getFaultsReport();
    }

}
