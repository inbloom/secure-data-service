package org.slc.sli.api.resources.v1.entity;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.DefaultCrudEndpoint;
import org.slc.sli.common.constants.ResourceNames;
import org.slc.sli.common.constants.v1.ParameterConstants;
import org.slc.sli.common.constants.v1.PathConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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

/**
 * This education entity represents an overall score or assessment tied to a course over a period
 * of time (i.e., the grading period). Student grades are usually a compilation of marks and
 * other scores.
 * @author jstokes
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.GRADES)
@Component
@Scope("request")
public class GradeResource extends DefaultCrudEndpoint {

    @Autowired
    public GradeResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.GRADES);
    }

    /**
     * readAll
     *
     * @param offset
     *            starting position in results to return to user
     * @param limit
     *            maximum number of results to return to user (starting from offset)
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return all grade entities for which the logged in user has permission and context.
     * @response.representation.200.mediaType
     */
    @Override
    @GET
    public Response readAll(@QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.readAll(offset, limit, headers, uriInfo);
    }

    /**
     * Create a new $$grades$$ entity.
     *
     * @param newEntityBody
     *            entity data
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *              URI information including path and query parameters
     * @return result of CRUD operation
     * @response.param {@name Location} {@style header} {@type
     * {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI where the created
     * item is accessible.}
     * @response.representation.201.mediaType
     */
    @Override
    @POST
    public Response create(final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.create(newEntityBody, headers, uriInfo);
    }

    /**
     * Get a single $$grades$$ entity
     *
     * @param gradeId
     *            The Id of the $$grades$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A single $$grades$$ entity
     * @response.representation.200.mediaType
     */
    @Override
    @GET
    @Path("{" + ParameterConstants.GRADE_ID + "}")
    public Response read(@PathParam(ParameterConstants.GRADE_ID) final String gradeId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(gradeId, headers, uriInfo);
    }

    /**
     * Delete a $$grades$$ entity
     *
     * @param gradeId
     *            The Id of the $$grades$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @Override
    @DELETE
    @Path("{" + ParameterConstants.GRADE_ID + "}")
    public Response delete(@PathParam(ParameterConstants.GRADE_ID) final String gradeId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.delete(gradeId, headers, uriInfo);
    }

    /**
     * Update an existing $$grades$$ entity.
     *
     * @param gradeId
     *            The id of the $$grades$$.
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
    @Path("{" + ParameterConstants.GRADE_ID + "}")
    public Response update(@PathParam(ParameterConstants.GRADE_ID) final String gradeId,
            final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.update(gradeId, newEntityBody, headers, uriInfo);
    }
}
