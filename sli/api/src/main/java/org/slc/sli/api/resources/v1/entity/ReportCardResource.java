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
import org.slc.sli.common.constants.ResourceNames;
import org.slc.sli.common.constants.v1.ParameterConstants;
import org.slc.sli.common.constants.v1.PathConstants;

/**
 * This educational entity represents the collection of student grades for
 * courses taken during a grading period.
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
     * readAll
     * @param offset
     *            starting position in results to return to user
     * @param limit
     *            maximum number of results to return to user (starting from offset)
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return all $$reportCards$$ entities for which the logged in user has permission to see.
     */
    @Override
    @GET
    public Response readAll(
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.readAll(offset, limit, headers, uriInfo);
    }

    /**
     * create
     *
     * @param newEntityBody
     *            reportCard data
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A 201 response on successfully created entity with the ID of the entity
     */
    @Override
    @POST
    public Response create(final EntityBody newEntityBody, @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.create(newEntityBody, headers, uriInfo);
    }

    /**
     * read
     *
     * @param reportCardId
     *            The comma separated list of ids of $$reportCards$$
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A single reportCard entity
     * @response.representation.200.mediaType
     */
    @Override
    @GET
    @Path("{" + ParameterConstants.REPORT_CARD_ID + "}")
    public Response read(@PathParam(ParameterConstants.REPORT_CARD_ID) final String reportCardId,
                         @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(reportCardId, headers, uriInfo);
    }

    /**
     * delete
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
        return super.delete(reportCardId, headers, uriInfo);
    }

    /**
     * update
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
        return super.update(reportCardId, newEntityBody, headers, uriInfo);
    }
}
