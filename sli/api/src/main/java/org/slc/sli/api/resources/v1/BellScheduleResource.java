package org.slc.sli.api.resources.v1;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.ResourceNames;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;

/**
 * Prototype new api end points and versioning
 * 
 * @author jstokes
 * 
 */
@Path(PathConstants.V1 + "/" + PathConstants.BELL_SCHEDULES)
@Component
@Scope("request")
@Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
public class BellScheduleResource {
    
    /**
     * Logging utility.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BellScheduleResource.class);
    
    /*
     * Interface capable of performing CRUD operations.
     */
    private final CrudEndpoint crudDelegate;

    @Autowired
    public BellScheduleResource(EntityDefinitionStore entityDefs) {
        this.crudDelegate = new DefaultCrudEndpoint(entityDefs, LOGGER);
    }

    /**
     * Returns all $$bellSchedules$$ entities for which the logged in User has permission and context.
     * 
     * @param offset
     *            starting position in results to return to user
     * @param limit
     *            maximum number of results to return to user (starting from offset)
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     */
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    @GET
    public Response readAll(@QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit, 
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return this.crudDelegate.readAll(ResourceNames.BELL_SCHEDULES, offset, limit, headers, uriInfo);
    }

    /**
     * Create a new $$bellSchedules$$ entity.
     * 
     * @param newEntityBody
     *            entity data
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *              URI information including path and query parameters
     * @return result of CRUD operation
     * @response.param {@name Location} {@style header} {@type
     *                 {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI where the created
     *                 item is accessable.}
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response create(final EntityBody newEntityBody, 
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return this.crudDelegate.create(ResourceNames.BELL_SCHEDULES, newEntityBody, headers, uriInfo);
    }

    /**
     * Get a single $$bellSchedules$$ entity
     * 
     * @param bellScheduleId
     *            The Id of the $$bellSchedules$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A single bellSchedule entity
     */
    @GET
    @Path("{" + ParameterConstants.BELL_SCHEDULE_ID + "}")
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    public Response read(@PathParam(ParameterConstants.BELL_SCHEDULE_ID) final String bellScheduleId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return this.crudDelegate.read(ResourceNames.BELL_SCHEDULES, bellScheduleId, headers, uriInfo);
    }

    /**
     * Delete a $$bellSchedules$$ entity
     * 
     * @param bellScheduleId
     *            The Id of the $$bellSchedules$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @DELETE
    @Path("{" + ParameterConstants.BELL_SCHEDULE_ID + "}")
    public Response delete(@PathParam(ParameterConstants.BELL_SCHEDULE_ID) final String bellScheduleId, 
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return this.crudDelegate.delete(ResourceNames.BELL_SCHEDULES, bellScheduleId, headers, uriInfo);
    }

    /**
     * Update an existing $$bellSchedules$$ entity.
     * 
     * @param bellScheduleId
     *            The id of the $$bellSchedules$$.
     * @param newEntityBody
     *            entity data
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Response with a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @PUT
    @Path("{" + ParameterConstants.BELL_SCHEDULE_ID + "}")
    public Response update(@PathParam(ParameterConstants.BELL_SCHEDULE_ID) final String bellScheduleId,
            final EntityBody newEntityBody, 
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return this.crudDelegate.update(ResourceNames.BELL_SCHEDULES, bellScheduleId, newEntityBody, headers, uriInfo);
    }
}
