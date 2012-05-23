package org.slc.sli.api.resources.v1.entity;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.DefaultCrudEndpoint;
import org.slc.sli.client.constants.ResourceNames;
import org.slc.sli.client.constants.v1.ParameterConstants;
import org.slc.sli.client.constants.v1.PathConstants;

/**
 * BellScheduleResource
 *
 * A Resource class for accessing a BellSchedule entity.
 *
 * @author jstokes
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.BELL_SCHEDULES)
@Component
@Scope("request")
public class BellScheduleResource extends DefaultCrudEndpoint {

    @Autowired
    public BellScheduleResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.BELL_SCHEDULES);
    }

    /**
     * readAll
     *
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
    @Override
    @GET
    public Response readAll(@QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.readAll(offset, limit, headers, uriInfo);
    }

    /**
     * create
     *
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
     *                 item is accessible.}
     */
    @Override
    @POST
    public Response create(final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.create(newEntityBody, headers, uriInfo);
    }

    /**
     * read
     *
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
    @Override
    @GET
    @Path("{" + ParameterConstants.BELL_SCHEDULE_ID + "}")
    public Response read(@PathParam(ParameterConstants.BELL_SCHEDULE_ID) final String bellScheduleId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(bellScheduleId, headers, uriInfo);
    }

    /**
     * delete
     *
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
    @Override
    @DELETE
    @Path("{" + ParameterConstants.BELL_SCHEDULE_ID + "}")
    public Response delete(@PathParam(ParameterConstants.BELL_SCHEDULE_ID) final String bellScheduleId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.delete(bellScheduleId, headers, uriInfo);
    }

    /**
     * update
     *
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
    @Override
    @PUT
    @Path("{" + ParameterConstants.BELL_SCHEDULE_ID + "}")
    public Response update(@PathParam(ParameterConstants.BELL_SCHEDULE_ID) final String bellScheduleId,
            final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.update(bellScheduleId, newEntityBody, headers, uriInfo);
    }
}
