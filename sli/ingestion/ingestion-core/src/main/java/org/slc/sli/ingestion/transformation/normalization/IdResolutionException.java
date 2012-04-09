package org.slc.sli.ingestion.transformation.normalization;

/**
 * An exception class dedicated to references that could not be resolved
 * to an ID.
 * 
 * @author kmyers
 *
 */
public class IdResolutionException extends Exception {
    
    private String key;
    private String value;
    
    public IdResolutionException(String errorMessage, String key, String value) {
        super(errorMessage);
        this.key = key;
        this.value = value;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public String getValue() {
        return this.value;
    }
}
