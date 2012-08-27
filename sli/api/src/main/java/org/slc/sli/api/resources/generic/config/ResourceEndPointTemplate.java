package org.slc.sli.api.resources.generic.config;

import java.util.List;

/**
 * Template for holding a resource
 *
 * @author srupasinghe
 *
 */
public class ResourceEndPointTemplate {

    private String path;
    private String doc;
    private String resourceClass;
    private List<ResourceEndPointTemplate> subResources;

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public String getResourceClass() {
        return resourceClass;
    }

    public void setResourceClass(String resourceClass) {
        this.resourceClass = resourceClass;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<ResourceEndPointTemplate> getSubResources() {
        return subResources;
    }

    public void setSubResources(List<ResourceEndPointTemplate> subResources) {
        this.subResources = subResources;
    }
}
