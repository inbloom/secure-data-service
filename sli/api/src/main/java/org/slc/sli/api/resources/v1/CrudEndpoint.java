package org.slc.sli.api.resources.v1;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.representation.EntityBody;

public interface CrudEndpoint {
    
    Response readAll(UriInfo uriInfo);
    
    Response read(String idList, boolean fullEntities, UriInfo uriInfo);
    
    Response create(EntityBody newEntityBody, UriInfo uriInfo);
    
    Response update(String id, EntityBody newEntityBody);
    
    Response delete(String id);
}
