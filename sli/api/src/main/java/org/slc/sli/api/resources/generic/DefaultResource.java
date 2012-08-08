package org.slc.sli.api.resources.generic;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.service.ResourceService;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * @author jstokes
 */
@Scope("request")
@Component
public class DefaultResource {
    @Autowired
    private GetAction getAction;

    @GET
    public Response get(@Context final UriInfo uriInfo) {
        List<EntityBody> results = getAction.get(uriInfo);
        return Response.ok(results).build();
    }
}
