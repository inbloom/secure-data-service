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
import org.slc.sli.client.constants.ResourceNames;
import org.slc.sli.client.constants.v1.ParameterConstants;
import org.slc.sli.client.constants.v1.PathConstants;

/**
 * Resource handler for StudentCompetencyObjective entity.
 * Stubbed out for documentation
 *
 * @author chung
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.STUDENT_COMPETENCY_OBJECTIVES)
@Component
@Scope("request")
public class StudentCompetencyObjectiveResource extends DefaultCrudEndpoint {

    @Autowired
    public StudentCompetencyObjectiveResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.STUDENT_COMPETENCY_OBJECTIVES);
    }

    /**
     * Returns all $$studentCompetencyObjectives$$ entities for which the logged in user has permission to see.
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
     * Create a new $$studentCompetencyObjectives$$ entity.
     *
     * @param newEntityBody
     *            studentCompetencyObjective data
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
     * Get a single $$studentCompetencyObjectives$$ entity
     *
     * @param studentCompetencyObjectiveId
     *            The comma separated list of ids of $$studentCompetencyObjectives$$
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A single studentCompetencyObjective entity
     */
    @Override
    @GET
    @Path("{" + ParameterConstants.STUDENT_COMPETENCY_OBJECTIVE_ID + "}")
    public Response read(@PathParam(ParameterConstants.STUDENT_COMPETENCY_OBJECTIVE_ID) final String studentCompetencyObjectiveId,
                         @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return Response.status(Status.NOT_FOUND).build();
    }

    /**
     * Delete an $$studentCompetencyObjectives$$
     *
     * @param studentCompetencyObjectiveId
     *            The id of the $$studentCompetencyObjectives$$
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @Override
    @DELETE
    @Path("{" + ParameterConstants.STUDENT_COMPETENCY_OBJECTIVE_ID + "}")
    public Response delete(@PathParam(ParameterConstants.STUDENT_COMPETENCY_OBJECTIVE_ID) final String studentCompetencyObjectiveId,
                           @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return Response.status(Status.NOT_FOUND).build();
    }

    /**
     * Update an existing $$studentCompetencyObjectives$$
     *
     * @param studentCompetencyObjectiveId
     *            The id of the $$studentCompetencyObjectives$$
     * @param newEntityBody
     *            studentCompetencyObjective data
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Response with a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @Override
    @PUT
    @Path("{" + ParameterConstants.STUDENT_COMPETENCY_OBJECTIVE_ID + "}")
    public Response update(@PathParam(ParameterConstants.STUDENT_COMPETENCY_OBJECTIVE_ID) final String studentCompetencyObjectiveId,
                           final EntityBody newEntityBody, @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return Response.status(Status.NOT_FOUND).build();
    }
}
