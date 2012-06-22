package org.slc.sli.ingestion;

import java.util.List;

/**
 * Generic interface for writing resources.
 *
 * @author dduran
 *
 */
public interface ResourceWriter<T> {

    /**
     * Write an entity to the data store (using 'upsert').
     * 
     * @param t Entity to be written.
     * @param jobId Current batch job id.
     */
    void writeResource(T t, String jobId);

    /**
     * Inserts an entity to the data store (using 'insert').
     * 
     * @param t Entity to be written.
     * @param jobId Current batch job id.
     */
    void insertResource(T t, String jobId);

    /**
     * Inserts multiple entities to the data store (using 'insert').
     * 
     * @param t Entities to be written.
     * @param collectionName Name of collection to write entities to.
     * @param jobId Current batch job id.
     */
    void insertResources(List<T> neutralRecords, String collectionName, String jobId);
}
