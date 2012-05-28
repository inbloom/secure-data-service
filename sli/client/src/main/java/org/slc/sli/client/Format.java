package org.slc.sli.client;

/**
 * Enum for supported media type formats
 * 
 * @author nbrown
 * 
 */
public enum Format {
    XML("application/xml"), JSON("application/json");
    private final String mediaType;
    
    public String getMediaType() {
        return mediaType;
    }
    
    private Format(String type) {
        mediaType = type;
    }
}
