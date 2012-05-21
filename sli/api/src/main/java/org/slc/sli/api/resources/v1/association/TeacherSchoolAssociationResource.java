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
 * Prototype new api end points and versioning
 *
 * @author srupasinghe
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.TEACHER_SCHOOL_ASSOCIATIONS)
@Component
@Scope("request")
public class TeacherSchoolAssociationResource extends DefaultCrudEndpoint {

    @Autowired
    public TeacherSchoolAssociationResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS);
    }

    /**
     * Returns all $$teacherSchoolAssociations$$ entities for which the logged in User has permission and context.
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
     * Create a new $$teacherSchoolAssociations$$ entity.
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
     * Get a single $$teacherSchoolAssociations$$ entity
     *
     * @param teacherSchoolAssociationId
     *            The Id of the $$teacherSchoolAssociations$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A single school entity
     */
    @Override
    @GET
    @Path("{" + ParameterConstants.TEACHER_SCHOOL_ASSOC_ID + "}")
    public Response read(@PathParam(ParameterConstants.TEACHER_SCHOOL_ASSOC_ID) final String teacherSchoolAssociationId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(teacherSchoolAssociationId, headers, uriInfo);
    }

    /**
     * Delete a $$teacherSchoolAssociations$$ entity
     *
     * @param teacherSchoolAssociationId
     *            The Id of the $$teacherSchoolAssociations$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @Override
    @DELETE
    @Path("{" + ParameterConstants.TEACHER_SCHOOL_ASSOC_ID + "}")
    public Response delete(@PathParam(ParameterConstants.TEACHER_SCHOOL_ASSOC_ID) final String teacherSchoolAssociationId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.delete(teacherSchoolAssociationId, headers, uriInfo);
    }

    /**
     * Update an existing $$teacherSchoolAssociations$$ entity.
     *
     * @param teacherSchoolAssociationId
     *            The id of the $$teacherSchoolAssociations$$.
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
    @Path("{" + ParameterConstants.TEACHER_SCHOOL_ASSOC_ID + "}")
    public Response update(@PathParam(ParameterConstants.TEACHER_SCHOOL_ASSOC_ID) final String teacherSchoolAssociationId,
            final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.update(teacherSchoolAssociationId, newEntityBody, headers, uriInfo);
    }

    /**
     * Returns each $$teachers$$ that
     * references the given $$teacherSchoolAssociations$$
     *
     * @param teacherSchoolAssociationId
     *            The Id of the teacherSchoolAssociation.
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
     * @return
     */
    @GET
    @Path("{" + ParameterConstants.TEACHER_SCHOOL_ASSOC_ID + "}" + "/" + PathConstants.TEACHERS)
    public Response getTeachersForAssociation(@PathParam(ParameterConstants.TEACHER_SCHOOL_ASSOC_ID) final String teacherSchoolAssociationId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS, "_id", teacherSchoolAssociationId, "teacherId", ResourceNames.TEACHERS, headers, uriInfo);
    }

    /**
     * Returns each $$schools$$ that
     * references the given $$teacherSchoolAssociations$$
     *
     * @param teacherSchoolAssociationId
     *            The Id of the teacherSchoolAssociation.
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
     * @return
     */
    @GET
    @Path("{" + ParameterConstants.TEACHER_SCHOOL_ASSOC_ID + "}" + "/" + PathConstants.SCHOOLS)
    public Response getSchoolsForAssociation(@PathParam(ParameterConstants.TEACHER_SCHOOL_ASSOC_ID) final String teacherSchoolAssociationId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS, "_id", teacherSchoolAssociationId, "schoolId", ResourceNames.SCHOOLS, headers, uriInfo);
    }

}
