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
import org.slc.sli.client.constants.ResourceNames;
import org.slc.sli.client.constants.v1.ParameterConstants;
import org.slc.sli.client.constants.v1.PathConstants;

/**
 * Represents the association between a $$staff$$ member and a $$programs$$.
 *
 * @author jtully
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.STAFF_PROGRAM_ASSOCIATIONS)
@Component
@Scope("request")
public class StaffProgramAssociationResource extends DefaultCrudEndpoint {

    @Autowired
    public StaffProgramAssociationResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.STAFF_PROGRAM_ASSOCIATIONS);
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
     * @param staffProgramAssociationId
     *            The Id of the staffProgramAssociations.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A single staffProgramAssociation entity
     */
    @Override
    @GET
    @Path("{" + ParameterConstants.STAFF_PROGRAM_ASSOCIATION_ID + "}")
    public Response read(@PathParam(ParameterConstants.STAFF_PROGRAM_ASSOCIATION_ID) final String staffProgramAssociationId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(staffProgramAssociationId, headers, uriInfo);
    }

    /**
     * Deletes the specified resource.
     *
     * @param staffProgramAssociationId
     *            The Id of the staffProgramAssociations.
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
    public Response delete(@PathParam(ParameterConstants.PROGRAM_ID) final String staffProgramAssociationId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.delete(staffProgramAssociationId, headers, uriInfo);
    }

    /**
     * Updates the specified resource using the given resource data.
     *
     * @param staffProgramAssociationId
     *            The id of the staffProgramAssociations.
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
    @Path("{" + ParameterConstants.STAFF_PROGRAM_ASSOCIATION_ID + "}")
    public Response update(@PathParam(ParameterConstants.STAFF_PROGRAM_ASSOCIATION_ID) final String staffProgramAssociationId,
            final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.update(staffProgramAssociationId, newEntityBody, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     *
     * @param staffProgramAssociationId
     *            The Id of the staffProgramAssociation.
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
    @Path("{" + ParameterConstants.STAFF_PROGRAM_ASSOCIATION_ID + "}" + "/" + PathConstants.STAFF)
    public Response getStaffProgramAssociationStaff(@PathParam(ParameterConstants.STAFF_PROGRAM_ASSOCIATION_ID) final String staffProgramAssociationId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STAFF_PROGRAM_ASSOCIATIONS, "_id", staffProgramAssociationId, "staffId", ResourceNames.STAFF, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     *
     * @param staffProgramAssociationId
     *            The Id of the staffProgramAssociation.
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
    @Path("{" + ParameterConstants.STAFF_PROGRAM_ASSOCIATION_ID + "}" + "/" + PathConstants.PROGRAMS)
    public Response getStaffProgramAssociationPrograms(@PathParam(ParameterConstants.STAFF_PROGRAM_ASSOCIATION_ID) final String staffProgramAssociationId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STAFF_PROGRAM_ASSOCIATIONS, "_id", staffProgramAssociationId, "programId", ResourceNames.PROGRAMS, headers, uriInfo);
    }
}
