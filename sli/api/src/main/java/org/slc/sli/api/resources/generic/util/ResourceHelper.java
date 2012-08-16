package org.slc.sli.api.resources.generic.util;

import org.slc.sli.api.service.query.UriInfoToApiQueryConverter;

import javax.ws.rs.core.UriInfo;

/**
 * @author jstokes
 */
public interface ResourceHelper {
    public String grabResource(String uri, ResourceTemplate template);
    public String getResourceName(UriInfo uriInfo, ResourceTemplate template);
    public ArrayList<String> getIds(UriInfoToApiQueryConverter uriInfo,ResourceTemplate template);
}
