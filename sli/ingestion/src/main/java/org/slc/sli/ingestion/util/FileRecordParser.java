package org.slc.sli.ingestion.util;

import java.io.IOException;

/**
 * Interface that extends Iterator to provide file level functionality.
 *
 * @author dduran
 *
 * @param <T>
 */
public interface FileRecordParser<T> {

    T parseRecord() throws IOException;

    void close();
}
