package org.slc.sli.ingestion.util;

import java.io.IOException;

/**
 * Generic interface for writing records to file.
 *
 * @author dduran
 *
 * @param <T>
 */
public interface FileRecordWriter<T> {

    void writeRecord(T t) throws IOException;

    void close();
}
