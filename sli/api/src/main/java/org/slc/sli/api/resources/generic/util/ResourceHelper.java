package org.slc.sli.api.resources.generic.util;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.resources.generic.representation.Resource;

import javax.ws.rs.core.UriInfo;

/**
 * @author jstokes
 */
public interface ResourceHelper {
    public Resource getResourceName(UriInfo uriInfo, ResourceTemplate template);
    public Resource getBaseName(UriInfo uriInfo, ResourceTemplate template);
    public String getResourcePath(UriInfo uriInfo, ResourceTemplate template);
    public Resource getAssociationName(UriInfo uriInfo, ResourceTemplate template);
    public EntityDefinition getEntityDefinition(final Resource resource);
    public EntityDefinition getEntityDefinition(final String resource);
    public boolean resolveResourcePath(final String URI, final ResourceTemplate template);
}
