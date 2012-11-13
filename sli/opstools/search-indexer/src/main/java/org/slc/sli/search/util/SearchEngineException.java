package org.slc.sli.search.util;

/**
 * An exception wrapper for Search Engine errors
 *
 */
public class SearchEngineException extends SearchIndexerException {
    private static final long serialVersionUID = 1L;
    private String status;

    public SearchEngineException(String status, String message) {
        super("Status: " + status + ", " + message);
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
