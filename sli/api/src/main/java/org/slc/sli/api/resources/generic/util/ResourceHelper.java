package org.slc.sli.api.resources.generic.util;

import javax.ws.rs.core.UriInfo;

/**
 * @author jstokes
 */
public interface ResourceHelper {
    public String getResourceName(UriInfo uriInfo, ResourceTemplate template);
    public String getBaseName(UriInfo uriInfo, ResourceTemplate template);
    public String getResourcePath(UriInfo uriInfo, ResourceTemplate template);
    public String getAssociationName(UriInfo uriInfo, ResourceTemplate template);
}
