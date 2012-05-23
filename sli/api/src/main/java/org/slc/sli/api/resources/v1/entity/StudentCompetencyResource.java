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
 * Resource handler for StudentCompetency entity.
 * Stubbed out for documentation
 *
 * @author chung
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.STUDENT_COMPETENCIES)
@Component
@Scope("request")
public class StudentCompetencyResource extends DefaultCrudEndpoint {

    @Autowired
    public StudentCompetencyResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.STUDENT_COMPETENCIES);
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
     * @return all $$studentCompetencies$$ entities for which the logged in user has permission to see.
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
     *            studentCompetency data
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
     * @param studentCompetencyId
     *            The comma separated list of ids of $$studentCompetencies$$
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A list of $$studentCompetencies$$ matching the list of ids queried for
     */
    @Override
    @GET
    @Path("{" + ParameterConstants.STUDENT_COMPETENCY_ID + "}")
    public Response read(@PathParam(ParameterConstants.STUDENT_COMPETENCY_ID) final String studentCompetencyId,
                         @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(studentCompetencyId, headers, uriInfo);
    }

    /**
     * delete
     *
     * @param studentCompetencyId
     *            The id of the $$studentCompetencies$$
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @Override
    @DELETE
    @Path("{" + ParameterConstants.STUDENT_COMPETENCY_ID + "}")
    public Response delete(@PathParam(ParameterConstants.STUDENT_COMPETENCY_ID) final String studentCompetencyId,
                           @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.delete(studentCompetencyId, headers, uriInfo);
    }

    /**
     * update
     *
     * @param studentCompetencyId
     *            The id of the $$studentCompetencies$$
     * @param newEntityBody
     *            studentCompetency data
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Response with a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @Override
    @PUT
    @Path("{" + ParameterConstants.STUDENT_COMPETENCY_ID + "}")
    public Response update(@PathParam(ParameterConstants.STUDENT_COMPETENCY_ID) final String studentCompetencyId,
                           final EntityBody newEntityBody, @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.update(studentCompetencyId, newEntityBody, headers, uriInfo);
    }


    /**
     * getReportCards
     *
     * @param studentCompetencyId
     *          The id of the student competency
     * @param headers
     *          HTTP request headers
     * @param uriInfo
     *          URI information including path and query parameters
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_COMPETENCY_ID + "}" + "/" + PathConstants.REPORT_CARDS)
    public Response getReportCards(@PathParam(ParameterConstants.STUDENT_COMPETENCY_ID) final String studentCompetencyId,
                                  @Context HttpHeaders headers,
                                  @Context final UriInfo uriInfo) {
       return super.read(ResourceNames.REPORT_CARDS, ParameterConstants.STUDENT_COMPETENCY_ID, studentCompetencyId, headers, uriInfo);
    }
}
