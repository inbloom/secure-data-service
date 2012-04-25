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
 * Prototype new api end points and versioning
 *
 * @author jstokes
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.TEACHERS)
@Component
@Scope("request")
public class TeacherResource extends DefaultCrudEndpoint {

    @Autowired
    public TeacherResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.TEACHERS);
    }

    /**
     * Returns all $$teachers$$ entities for which the logged in User has permission and context.
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
     * Create a new $$teachers$$ entity.
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
     * Get a single $$teachers$$ entity
     *
     * @param teacherId
     *            The Id of the $$teachers$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A single teacher entity
     */
    @Override
    @GET
    @Path("{" + ParameterConstants.TEACHER_ID + "}")
    public Response read(@PathParam(ParameterConstants.TEACHER_ID) final String teacherId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(teacherId, headers, uriInfo);
    }

    /**
     * Delete a $$teachers$$ entity
     *
     * @param teacherId
     *            The Id of the $$teachers$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @Override
    @DELETE
    @Path("{" + ParameterConstants.TEACHER_ID + "}")
    public Response delete(@PathParam(ParameterConstants.TEACHER_ID) final String teacherId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.delete(teacherId, headers, uriInfo);
    }

    /**
     * Update an existing $$teachers$$ entity.
     *
     * @param teacherId
     *            The id of the $$teachers$$.
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
    @Path("{" + ParameterConstants.TEACHER_ID + "}")
    public Response update(@PathParam(ParameterConstants.TEACHER_ID) final String teacherId,
            final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.update(teacherId, newEntityBody, headers, uriInfo);
    }

    /**
     * Returns each $$teacherSectionAssociations$$ that
     * references the given $$teachers$$
     *
     * @param teacherId
     *            The id of the $$teachers$$.
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
    @Path("{" + ParameterConstants.TEACHER_ID + "}" + "/" + PathConstants.TEACHER_SECTION_ASSOCIATIONS)
    public Response getTeacherSectionAssociations(@PathParam(ParameterConstants.TEACHER_ID) final String teacherId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.TEACHER_SECTION_ASSOCIATIONS, "teacherId", teacherId, headers, uriInfo);
    }

    /**
     * Returns each $$sections$$ associated to the given teacher through
     * a $$teacherSectionAssociations$$
     *
     * @param teacherId
     *            The id of the $$teachers$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     */
    @GET
    @Path("{" + ParameterConstants.TEACHER_ID + "}" + "/" + PathConstants.TEACHER_SECTION_ASSOCIATIONS + "/" + PathConstants.SECTIONS)
    public Response getTeacherSectionAssociationsSections(@PathParam(ParameterConstants.TEACHER_ID) final String teacherId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.TEACHER_SECTION_ASSOCIATIONS, "teacherId", teacherId, "sectionId", ResourceNames.SECTIONS, headers, uriInfo);
    }

    /**
     * Returns each $$teacherSchoolAssociations$$ that
     * references the given $$teachers$$
     *
     * @param teacherId
     *            The id of the $$teachers$$.
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
    @Path("{" + ParameterConstants.TEACHER_ID + "}" + "/" + PathConstants.TEACHER_SCHOOL_ASSOCIATIONS)
    public Response getTeacherSchoolAssociations(@PathParam(ParameterConstants.TEACHER_ID) final String teacherId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS, "teacherId", teacherId, headers, uriInfo);
    }

    /**
     * Returns each $$schools$$ associated to the given teacher through
     * a $$teacherSchoolAssociations$$
     *
     * @param teacherId
     *            The id of the $$teachers$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     */
    @GET
    @Path("{" + ParameterConstants.TEACHER_ID + "}" + "/" + PathConstants.TEACHER_SCHOOL_ASSOCIATIONS + "/" + PathConstants.SCHOOLS)
    public Response getTeacherSchoolAssociationsSchools(@PathParam(ParameterConstants.TEACHER_ID) final String teacherId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS, "teacherId", teacherId, "schoolId", ResourceNames.SCHOOLS, headers, uriInfo);
    }
}
