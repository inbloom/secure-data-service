package org.slc.sli.ingestion;

import java.util.List;
import java.util.Set;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;

/**
 * Interface for batch jobs
 *
 * @author nbrown
 *
 */
public interface Job {

    /**
     * @return the jobId
     */
    String getId();

    /**
     * @param key
     * @return
     */
    String getProperty(String key);

    /**
     * @param key
     * @param defaultValue
     * @return
     */
    String getProperty(String key, String defaultValue);

    /**
     *
     * @return
     */
    Set<String> propertyNames();

    /**
     *
     * @param name
     * @param value
     */
    void setProperty(String name, String value);

    /**
     *
     * @return
     */
    List<IngestionFileEntry> getFiles();

    /**
     *
     * @param ingestionFileEntry
     * @return
     */
    boolean addFile(IngestionFileEntry ingestionFileEntry);

    /**
     *
     * @return
     */
    FaultsReport getFaultsReport();

}
