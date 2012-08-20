package org.slc.sli.api.resources.generic.representation;

/**
 * Created with IntelliJ IDEA.
 * User: srupasinghe
 * Date: 8/20/12
 * Time: 10:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class Resource {

    private String namespace;
    private String resourceType;

    public Resource(final String resourcePath) {
        populate(resourcePath);
    }

    protected void populate(final String resourcePath) {
        this.namespace = resourcePath.split("/")[0];
        this.resourceType = resourcePath.split("/")[1];
    }

    public String getNamespace() {
        return namespace;
    }

    public String getResourceType() {
        return resourceType;
    }
}
