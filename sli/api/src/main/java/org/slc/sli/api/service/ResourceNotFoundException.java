package org.slc.sli.api.service;

/**
 * 404
 *
 */
public class ResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String resource;
    private String typePath;

    public ResourceNotFoundException(String typePath, String resource) {
        this.resource = resource;
        this.typePath = typePath;
    }

    public String getResource() {
        return resource;
    }

    public String getTypePath() {
        return typePath;
    }
}
