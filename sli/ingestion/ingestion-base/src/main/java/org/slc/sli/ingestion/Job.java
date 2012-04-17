package org.slc.sli.ingestion;

import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Job {
    
    /**
     * Adds a file.
     *
     * @param ingestionFileEntry
     * @return
     * @see java.util.List#add(java.lang.Object)
     */
    public abstract boolean addFile(IngestionFileEntry ingestionFileEntry);
    
    /**
     * @return the creationDate
     */
    public abstract Date getCreationDate();
    
    public abstract FaultsReport getFaultsReport();
    
    /**
     * @return the files
     */
    public abstract List<IngestionFileEntry> getFiles();
    
    /**
     * @return the jobId
     */
    public abstract String getId();
    
    /**
     * @param key
     * @return
     * @see java.util.Properties#getProperty(java.lang.String)
     */
    public abstract String getProperty(String key);
    
    /**
     * @param key
     * @param defaultValue
     * @return
     * @see java.util.Properties#getProperty(java.lang.String, java.lang.String)
     */
    public abstract String getProperty(String key, String defaultValue);
    
    /**
     * @return
     * @see java.util.Properties#propertyNames()
     */
    public abstract Enumeration<?> propertyNames();
    
    /**
     * @param key
     * @param value
     * @return
     * @see java.util.Properties#setProperty(java.lang.String, java.lang.String)
     */
    public abstract Object setProperty(String key, String value);
    
}