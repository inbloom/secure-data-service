package org.slc.sli.api.resources.v1;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;

/**
 * Prototype new api end points and versioning
 * 
 * @author srupasinghe
 * 
 */
@Path(PathConstants.V1 + "/" + PathConstants.TEACHER_SCHOOL_ASSOCIATIONS)
@Component
@Scope("request")
@Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
public class TeacherSchoolAssociationResource {
    
    /**
     * Logging utility.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TeacherSchoolAssociationResource.class);
    
    /*
     * Interface capable of performing CRUD operations.
     */
    private final CrudEndpoint crudDelegate;

    @Autowired
    public TeacherSchoolAssociationResource(EntityDefinitionStore entityDefs) {
        this.crudDelegate = new DefaultCrudEndpoint(entityDefs, LOGGER);
    }

    /**
     * Returns all $$teacherSchoolAssociations$$ entities for which the logged in User has permission and context.
     * 
     * @param offset
     *            starting position in results to return to user
     * @param limit
     *            maximum number of results to return to user (starting from offset)
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     */
    @GET
    public Response readAll(@QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit, 
            @Context final UriInfo uriInfo) {
        return this.crudDelegate.readAll(PathConstants.TEACHER_SCHOOL_ASSOCIATIONS, offset, limit, uriInfo);
    }

    /**
     * Create a new $$teacherSchoolAssociations$$ entity.
     * 
     * @param newEntityBody
     *            entity data
     * @param uriInfo
     *              URI information including path and query parameters
     * @return result of CRUD operation
     * @response.param {@name Location} {@style header} {@type
     *                 {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI where the created
     *                 item is accessable.}
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response create(final EntityBody newEntityBody, 
            @Context final UriInfo uriInfo) {
        return this.crudDelegate.create(PathConstants.TEACHER_SCHOOL_ASSOCIATIONS, newEntityBody, uriInfo);
    }

    /**
     * Get a single $$teacherSchoolAssociations$$ entity
     * 
     * @param teacherSchoolAssociationId
     *            The Id of the teacher school association.
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A single school entity
     */
    @GET
    @Path("{" + ParameterConstants.TEACHER_SCHOOL_ASSOCIATION_ID + "}")
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    public Response read(@PathParam(ParameterConstants.TEACHER_SCHOOL_ASSOCIATION_ID) final String teacherSchoolAssociationId,
            @Context final UriInfo uriInfo) {
        return this.crudDelegate.read(PathConstants.TEACHER_SCHOOL_ASSOCIATIONS, teacherSchoolAssociationId, uriInfo);
    }

    /**
     * Delete a $$teacherSchoolAssociations$$ entity
     * 
     * @param teacherSchoolAssociationId
     *            The Id of the teacher school association.
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @DELETE
    @Path("{" + ParameterConstants.TEACHER_SCHOOL_ASSOCIATION_ID + "}")
    public Response delete(@PathParam(ParameterConstants.TEACHER_SCHOOL_ASSOCIATION_ID) final String teacherSchoolAssociationId, 
            @Context final UriInfo uriInfo) {
        return this.crudDelegate.delete(PathConstants.TEACHER_SCHOOL_ASSOCIATIONS, teacherSchoolAssociationId, uriInfo);
    }

    /**
     * Update an existing $$teacherSchoolAssociations$$ entity.
     * 
     * @param teacherSchoolAssociationId
     *            The Id of the teacher school association.
     * @param newEntityBody
     *            entity data
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Response with a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @PUT
    @Path("{" + ParameterConstants.TEACHER_SCHOOL_ASSOCIATION_ID + "}")
    public Response update(@PathParam(ParameterConstants.TEACHER_SCHOOL_ASSOCIATION_ID) final String teacherSchoolAssociationId,
            final EntityBody newEntityBody, 
            @Context final UriInfo uriInfo) {
        return this.crudDelegate.update(PathConstants.TEACHER_SCHOOL_ASSOCIATIONS, teacherSchoolAssociationId, newEntityBody, uriInfo);
    }
}
