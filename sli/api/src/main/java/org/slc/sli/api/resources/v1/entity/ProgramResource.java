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
 * This entity represents any program designed to work
 * in conjunction with or to supplement the main
 * academic program.
 *
 * @author jstokes
 * @author jtully
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.PROGRAMS)
@Component
@Scope("request")
public class ProgramResource extends DefaultCrudEndpoint {

    @Autowired
    public ProgramResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.PROGRAMS);
    }

    /**
     * Returns all $$programs$$ entities for which the logged in User has permission and context.
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
     * Create a new $$programs$$ entity.
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
     * Get a single $$programs$$ entity.
     *
     * @param programId
     *            The Id of the $$programs$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A single program entity
     */
    @Override
    @GET
    @Path("{" + ParameterConstants.PROGRAM_ID + "}")
    public Response read(@PathParam(ParameterConstants.PROGRAM_ID) final String programId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(programId, headers, uriInfo);
    }

    /**
     * Delete a $$programs$$ entity.
     *
     * @param programId
     *            The Id of the $$programs$$.
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
    public Response delete(@PathParam(ParameterConstants.PROGRAM_ID) final String programId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.delete(programId, headers, uriInfo);
    }

    /**
     * Update an existing $$programs$$ entity.
     *
     * @param programId
     *            The id of the $$programs$$.
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
    @Path("{" + ParameterConstants.PROGRAM_ID + "}")
    public Response update(@PathParam(ParameterConstants.PROGRAM_ID) final String programId,
            final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.update(programId, newEntityBody, headers, uriInfo);
    }

    /**
     * Returns the $$studentProgramAssociations$$ that
     * reference the given $$programs$$
     *
     * @param programId
     *            The Id of the Program.
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
    @Path("{" + ParameterConstants.PROGRAM_ID + "}" + "/" + PathConstants.STUDENT_PROGRAM_ASSOCIATIONS)
    public Response getStudentProgramAssociations(@PathParam(ParameterConstants.PROGRAM_ID) final String programId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_PROGRAM_ASSOCIATIONS, "programId", programId, headers, uriInfo);
    }


    /**
     * Returns the $$students$$ that are referenced from the $$studentProgramAssociations$$
     * that references the given $$programs$$.
     *
     * @param programId
     *            The Id of the program.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     */
    @GET
    @Path("{" + ParameterConstants.PROGRAM_ID + "}" + "/" + PathConstants.STUDENT_PROGRAM_ASSOCIATIONS + "/" + PathConstants.STUDENTS)
    public Response getStudentProgramAssociationStudent(@PathParam(ParameterConstants.PROGRAM_ID) final String programId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_PROGRAM_ASSOCIATIONS, "programId", programId,
                "studentId", ResourceNames.STUDENTS, headers, uriInfo);
    }

    /**
     * Returns the $$staffProgramAssociations$$ that
     * reference the given $$programs$$
     *
     * @param programId
     *            The Id of the program.
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
    @Path("{" + ParameterConstants.PROGRAM_ID + "}" + "/" + PathConstants.STAFF_PROGRAM_ASSOCIATIONS)
    public Response getStaffProgramAssociations(@PathParam(ParameterConstants.PROGRAM_ID) final String programId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STAFF_PROGRAM_ASSOCIATIONS, "programId", programId, headers, uriInfo);
    }


    /**
     * Returns the $$staff$$ that are referenced from the $$staffProgramAssociations$$
     * that references the given $$programs$$.
     *
     * @param programId
     *            The Id of the program.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     */
    @GET
    @Path("{" + ParameterConstants.PROGRAM_ID + "}" + "/" + PathConstants.STAFF_PROGRAM_ASSOCIATIONS + "/" + PathConstants.STAFF)
    public Response getStaffProgramAssociationStaff(@PathParam(ParameterConstants.PROGRAM_ID) final String programId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STAFF_PROGRAM_ASSOCIATIONS, "programId", programId,
                "staffId", ResourceNames.STAFF, headers, uriInfo);
    }


}
