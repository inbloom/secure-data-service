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

/**
 * Batch Job class.
 *
 * @author okrook
 *
 */
public class BatchJob implements  Serializable, ErrorReportSupport{

    private static final long serialVersionUID = 1L;

    /**
     * holds references to the files involved in this Job
     */
    private List<IngestionFileEntry> files;

    /**
     * stores the date upon which the Job was created
     */
    private Date creationDate;

    /**
     * stores a globally unique ID for the Job
     */
    private String id;

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
    private BatchJob() {
    }

    /**
     * Initialize a BatchJob with default settings for initialization
     *
     * @return BatchJob with default settings
     */
    public static BatchJob createDefault() {
        return BatchJob.createDefault(null);
    }

    
    /**
     * Initialize a BatchJob with default settings for initialization
     *
     * @param filename string representation of incoming file
     * @return BatchJob with default settings
     */
    public static BatchJob createDefault(String filename) {
        BatchJob job = new BatchJob();
        job.id = createId(filename);
        job.creationDate = new Date();
        job.configProperties = new Properties();
        job.files = new ArrayList<IngestionFileEntry>();
        job.faults = new FaultsReport();
        return job;
    }

    /**
     * generates a new unique ID
     */
    protected static String createId(String filename) {
    	if (filename == null)
    		return System.currentTimeMillis() + "-" + UUID.randomUUID().toString();
    	else
    		return filename + "-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString();
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
     * @return the creationDate
     */
    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public ErrorReport getErrorReport() {
        return getFaultsReport();
    }

    public FaultsReport getFaultsReport() {
        return faults;
    }

    /**
     * @return the files
     */
    public List<IngestionFileEntry> getFiles() {
        return files;
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
        return configProperties.getProperty(key);
    }

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

    @Override
    public String toString() {
        return "BatchJob [id=" + id + ", creationDate=" + creationDate + ", configProperties=" + configProperties + "]";
    }

}
