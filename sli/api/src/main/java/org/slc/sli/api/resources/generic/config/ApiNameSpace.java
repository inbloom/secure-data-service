package org.slc.sli.api.resources.generic.config;

import java.util.List;

/**
 * Template for holding a name spaced list of resource end points
 *
 * @author srupasinghe
 *
 */
public class ApiNameSpace {

    private String nameSpace;
    private List<ResourceEndPointTemplate> resources;

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public List<ResourceEndPointTemplate> getResources() {
        return resources;
    }

    public void setResources(List<ResourceEndPointTemplate> resources) {
        this.resources = resources;
    }
}
