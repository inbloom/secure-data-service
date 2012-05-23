package org.slc.sli.api.resources.v1.association;

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
 * Represents the association between a $$Student$$ and a $$Program$$.
 *
 * @author jtully
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.STUDENT_PROGRAM_ASSOCIATIONS)
@Component
@Scope("request")
public class StudentProgramAssociationResource extends DefaultCrudEndpoint {

    @Autowired
    public StudentProgramAssociationResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.STUDENT_PROGRAM_ASSOCIATIONS);
    }

    /**
     * Returns the requested collection of resource representations.
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
     * Creates a new resource using the given resource data.
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
    @Override
    @POST
    public Response create(final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.create(newEntityBody, headers, uriInfo);
    }

    /**
     * Returns the specified resource representation(s).
     *
     * @param studentProgramAssociationId
     *            The id of the entity
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     */
    @Override
    @GET
    @Path("{" + ParameterConstants.STUDENT_PROGRAM_ASSOCIATION_ID + "}")
    public Response read(@PathParam(ParameterConstants.STUDENT_PROGRAM_ASSOCIATION_ID) final String studentProgramAssociationId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(studentProgramAssociationId, headers, uriInfo);
    }

    /**
     * Deletes the specified resource.
     *
     * @param studentProgramAssociationId
     *            The id of the entity
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @Override
    @DELETE
    @Path("{" + ParameterConstants.PROGRAM_ID + "}")
    public Response delete(@PathParam(ParameterConstants.PROGRAM_ID) final String studentProgramAssociationId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.delete(studentProgramAssociationId, headers, uriInfo);
    }

    /**
     * Updates the specified resource using the given resource data.
     *
     * @param studentProgramAssociationId
     *            The id of the entity
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
    @Path("{" + ParameterConstants.STUDENT_PROGRAM_ASSOCIATION_ID + "}")
    public Response update(@PathParam(ParameterConstants.STUDENT_PROGRAM_ASSOCIATION_ID) final String studentProgramAssociationId,
            final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.update(studentProgramAssociationId, newEntityBody, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     *
     * @param studentProgramAssociationId
     *            The id of the entity
     * @param offset
     *            Index of the first result to return
     * @param limit
     *            Maximum number of results to return
     * @param expandDepth
     *            Number of hops (associations) for which to expand entities
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_PROGRAM_ASSOCIATION_ID + "}" + "/" + PathConstants.STUDENTS)
    public Response getStudentProgramAssociationStudents(@PathParam(ParameterConstants.STUDENT_PROGRAM_ASSOCIATION_ID) final String studentProgramAssociationId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_PROGRAM_ASSOCIATIONS, "_id", studentProgramAssociationId, "studentId", ResourceNames.STUDENTS, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     *
     * @param studentProgramAssociationId
     *            The id of the entity
     * @param offset
     *            Index of the first result to return
     * @param limit
     *            Maximum number of results to return.
     * @param expandDepth
     *            Number of hops (associations) for which to expand entities.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_PROGRAM_ASSOCIATION_ID + "}" + "/" + PathConstants.PROGRAMS)
    public Response getStudentProgramAssociationPrograms(@PathParam(ParameterConstants.STUDENT_PROGRAM_ASSOCIATION_ID) final String studentProgramAssociationId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_PROGRAM_ASSOCIATIONS, "_id", studentProgramAssociationId, "programId", ResourceNames.PROGRAMS, headers, uriInfo);
    }
}
