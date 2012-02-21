package org.slc.sli.api.resources.v1.association;

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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.config.ResourceNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.CrudEndpoint;
import org.slc.sli.api.resources.v1.DefaultCrudEndpoint;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.resources.v1.ParameterConstants;
import org.slc.sli.api.resources.v1.PathConstants;

/**
 * Prototype new api end points and versioning
 * 
 * @author srupasinghe
 * 
 */
@Path(PathConstants.V1 + "/" + PathConstants.TEACHER_SECTION_ASSOCIATIONS)
@Component
@Scope("request")
@Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
public class TeacherSectionAssociationResource {
    /**
     * Logging utility.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TeacherSectionAssociationResource.class);
    
    /*
     * Interface capable of performing CRUD operations.
     */
    private final CrudEndpoint crudDelegate;

    @Autowired
    public TeacherSectionAssociationResource(EntityDefinitionStore entityDefs) {
        this.crudDelegate = new DefaultCrudEndpoint(entityDefs, LOGGER);
    }

    /**
     * Returns all $$teacherSectionAssociations$$ entities for which the logged in User has permission and context.
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
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    @GET
    public Response readAll(@QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit, 
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return this.crudDelegate.readAll(ResourceNames.TEACHER_SECTION_ASSOCIATIONS, offset, limit, headers, uriInfo);
    }

    /**
     * Create a new $$teacherSectionAssociations$$ entity.
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
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response create(final EntityBody newEntityBody, 
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return this.crudDelegate.create(ResourceNames.TEACHER_SECTION_ASSOCIATIONS, newEntityBody, headers, uriInfo);
    }

    /**
     * Get a single $$teacherSectionAssociations$$ entity
     * 
     * @param teacherSectionAssociationId
     *            The Id of the $$teacherSectionAssociations$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A single $$teacherSectionAssociations$$ entity
     */
    @GET
    @Path("{" + ParameterConstants.TEACHER_SECTION_ASSOCIATION_ID + "}")
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    public Response read(@PathParam(ParameterConstants.TEACHER_SECTION_ASSOCIATION_ID) final String teacherSectionAssociationId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return this.crudDelegate.read(ResourceNames.TEACHER_SECTION_ASSOCIATIONS, teacherSectionAssociationId, headers, uriInfo);
    }

    /**
     * Delete a $$teacherSectionAssociations$$ entity
     * 
     * @param teacherSectionAssociationId
     *            The Id of the $$teacherSectionAssociations$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @DELETE
    @Path("{" + ParameterConstants.TEACHER_SECTION_ASSOCIATION_ID + "}")
    public Response delete(@PathParam(ParameterConstants.TEACHER_SECTION_ASSOCIATION_ID) final String teacherSectionAssociationId, 
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return this.crudDelegate.delete(ResourceNames.TEACHER_SECTION_ASSOCIATIONS, teacherSectionAssociationId, headers, uriInfo);
    }

    /**
     * Update an existing $$teacherSectionAssociations$$ entity.
     * 
     * @param teacherSectionAssociationId
     *            The id of the $$teacherSectionAssociations$$.
     * @param newEntityBody
     *            entity data
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Response with a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @PUT
    @Path("{" + ParameterConstants.TEACHER_SECTION_ASSOCIATION_ID + "}")
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response update(@PathParam(ParameterConstants.TEACHER_SECTION_ASSOCIATION_ID) final String teacherSectionAssociationId,
            final EntityBody newEntityBody, 
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return this.crudDelegate.update(ResourceNames.TEACHER_SECTION_ASSOCIATIONS, teacherSectionAssociationId, newEntityBody, headers, uriInfo);
    }

    /**
     * Returns each $$teachers$$ that
     * references the given $$teacherSectionAssociations$$
     * 
     * @param teacherSectionAssociationId
     *            The Id of the $$teacherSectionAssociations$$.
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
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    @Path("{" + ParameterConstants.TEACHER_SECTION_ASSOCIATION_ID + "}" + "/" + PathConstants.TEACHERS)
    public Response getTeachersForAssociation(@PathParam(ParameterConstants.TEACHER_SECTION_ASSOCIATION_ID) final String teacherSectionAssociatonId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return Response.status(Status.SERVICE_UNAVAILABLE).build();
    }
    
    /**
     * Returns each $$sections$$ that
     * references the given $$teacherSectionAssociations$$
     * 
     * @param teacherSectionAssociationId
     *            The Id of the $$teacherSectionAssociations$$.
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
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    @Path("{" + ParameterConstants.TEACHER_SECTION_ASSOCIATION_ID + "}" + "/" + PathConstants.SECTIONS)
    public Response getSectionsForAssociation(@PathParam(ParameterConstants.TEACHER_SECTION_ASSOCIATION_ID) final String teacherSectionAssociationId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return Response.status(Status.SERVICE_UNAVAILABLE).build();
    }
}
