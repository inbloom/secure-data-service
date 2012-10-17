package org.slc.sli.search.util;

public class SearchIndexerException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public SearchIndexerException(String message){
        super(message);
    }
    
    public SearchIndexerException(String message, Throwable t){
        super(message, t);
    }
}
