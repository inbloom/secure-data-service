package org.slc.sli.api.resources.v1.admin;

//
import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slc.sli.api.client.constants.v1.PathConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
@Produces({ MediaType.APPLICATION_JSON + ";charset=utf-8" })
@Path(PathConstants.V1 + "/" + "metrics")
public class MetricsResource {
    
    @Autowired
    protected Repository<Entity> repo;
    
    @GET
    public Response getMetrics(@QueryParam("key") final String fieldKey, @QueryParam("value") final String fieldValue,
            @Context final UriInfo uriInfo) {
        
        SecurityUtil.ensureAuthenticated();
        if (!SecurityUtil.hasRight(Right.ADMIN_ACCESS)) {
            EntityBody body = new EntityBody();
            body.put("message", "You are not authorized to view collection metrics.");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }
        
        CollectionMetrics metrics = MetricsResourceHelper.getAllCollectionMetrics(repo, fieldKey, fieldValue);
        metrics.put("== Totals ==", metrics.getTotals());
        
        try {
            return Response.ok().entity(MetricsResourceHelper.jsonMapper.writeValueAsString(metrics)).build();
        } catch (JsonGenerationException e) {
            return Response.serverError().entity(e.getLocalizedMessage()).build();
        } catch (JsonMappingException e) {
            return Response.serverError().entity(e.getLocalizedMessage()).build();
        } catch (IOException e) {
            return Response.serverError().entity(e.getLocalizedMessage()).build();
        }
        
    }
}
