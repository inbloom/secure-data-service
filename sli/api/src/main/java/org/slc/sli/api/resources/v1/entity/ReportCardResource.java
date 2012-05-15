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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.DefaultCrudEndpoint;
import org.slc.sli.common.constants.ResourceNames;
import org.slc.sli.common.constants.v1.ParameterConstants;
import org.slc.sli.common.constants.v1.PathConstants;

/**
 * Resource handler for ReportCard entity.
 * Stubbed out for documentation
 *
 * @author chung
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.REPORT_CARDS)
@Component
@Scope("request")
public class ReportCardResource extends DefaultCrudEndpoint {

    @Autowired
    public ReportCardResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.REPORT_CARDS);
    }

    /**
     * Returns all $$reportCards$$ entities for which the logged in user has permission to see.
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
    public Response readAll(
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return Response.status(Status.NOT_FOUND).build();
    }

    /**
     * Create a new $$reportCards$$ entity.
     *
     * @param newEntityBody
     *            reportCard data
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     * @response.param {@name Location} {@style header} {@type
     *                 {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI where the created
     *                 item is accessible.}
     */
    @Override
    @POST
    public Response create(final EntityBody newEntityBody, @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return Response.status(Status.NOT_FOUND).build();
    }

    /**
     * Get a single $$reportCards$$ entity
     *
     * @param reportCardId
     *            The comma separated list of ids of $$reportCards$$
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A single reportCard entity
     */
    @Override
    @GET
    @Path("{" + ParameterConstants.REPORT_CARD_ID + "}")
    public Response read(@PathParam(ParameterConstants.REPORT_CARD_ID) final String reportCardId,
                         @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return Response.status(Status.NOT_FOUND).build();
    }

    /**
     * Delete an $$reportCards$$
     *
     * @param reportCardId
     *            The id of the $$reportCards$$
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @Override
    @DELETE
    @Path("{" + ParameterConstants.REPORT_CARD_ID + "}")
    public Response delete(@PathParam(ParameterConstants.REPORT_CARD_ID) final String reportCardId,
                           @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return Response.status(Status.NOT_FOUND).build();
    }

    /**
     * Update an existing $$reportCards$$
     *
     * @param reportCardId
     *            The id of the $$reportCards$$
     * @param newEntityBody
     *            reportCard data
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Response with a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @Override
    @PUT
    @Path("{" + ParameterConstants.REPORT_CARD_ID + "}")
    public Response update(@PathParam(ParameterConstants.REPORT_CARD_ID) final String reportCardId,
                           final EntityBody newEntityBody, @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return Response.status(Status.NOT_FOUND).build();
    }
}
