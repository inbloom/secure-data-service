package org.slc.sli.api.resources.generic.util;

import javax.ws.rs.core.UriInfo;

/**
 * @author jstokes
 */
public interface ResourceHelper {
    @Deprecated
    public String grabResource(String uri, ResourceTemplate template);

    public String getResourceName(UriInfo uriInfo, ResourceTemplate template);
}
