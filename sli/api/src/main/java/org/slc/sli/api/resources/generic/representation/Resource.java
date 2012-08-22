package org.slc.sli.api.resources.generic.representation;

/**
 * Resource container.
 *
 * @author srupasinghe
 */
public class Resource {

    private String namespace;
    private String resourceType;

    public Resource(String namespace, String resourceType) {
        this.namespace = namespace;
        this.resourceType = resourceType;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getResourceType() {
        return resourceType;
    }
}
