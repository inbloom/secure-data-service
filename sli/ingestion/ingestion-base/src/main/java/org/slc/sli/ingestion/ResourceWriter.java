package org.slc.sli.ingestion;

/**
 * Generic interface for writing resources.
 *
 * @author dduran
 *
 */
public interface ResourceWriter<T> {

    void writeResource(T t, String jobId);

    void insertResource(T t, String jobId);
}
