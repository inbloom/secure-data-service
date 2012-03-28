package org.slc.sli.api.resources.v1.entity;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.ResourceNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.util.ResourceUtil;
import org.slc.sli.api.resources.v1.CrudEndpoint;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.resources.v1.ParameterConstants;
import org.slc.sli.api.resources.v1.PathConstants;

/**
 * Prototype new api end points and versioning
 * 
 * @author kmyers
 * 
 */
@Path(PathConstants.V1 + "/" + PathConstants.EDUCATION_ORGANIZATIONS)
@Component
@Scope("request")
@Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
public class EducationOrganizationResource {
    
    /**
     * Logging utility.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EducationOrganizationResource.class);
    
    /*
     * Interface capable of performing CRUD operations.
     */
    private final CrudEndpoint crudDelegate;

    @Autowired
    public EducationOrganizationResource(CrudEndpoint crudDelegate) {
        this.crudDelegate = crudDelegate;
    }

    /**
     * Returns all $$educationalOrganizations$$ entities for which the logged in User has permission and context.
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
        ResourceUtil.putValue(headers.getRequestHeaders(), ParameterConstants.LIMIT, limit);
        ResourceUtil.putValue(headers.getRequestHeaders(), ParameterConstants.OFFSET, offset);
        return this.crudDelegate.readAll(ResourceNames.EDUCATION_ORGANIZATIONS, headers, uriInfo);
    }

    /**
     * Create a new $$educationalOrganizations$$ entity.
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
    @Consumes({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    public Response create(final EntityBody newEntityBody, 
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return this.crudDelegate.create(ResourceNames.EDUCATION_ORGANIZATIONS, newEntityBody, headers, uriInfo);
    }

    /**
     * Get a single $$educationalOrganizations$$ entity
     * 
     * @param educationOrganizationId
     *            The Id of the $$educationalOrganizations$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A single $$educationalOrganizations$$ entity
     */
    @GET
    @Path("{" + ParameterConstants.EDUCATION_ORGANIZATION_ID + "}")
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    public Response read(@PathParam(ParameterConstants.EDUCATION_ORGANIZATION_ID) final String educationOrganizationId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return this.crudDelegate.read(ResourceNames.EDUCATION_ORGANIZATIONS, educationOrganizationId, headers, uriInfo);
    }

    /**
     * Delete a $$educationalOrganizations$$ entity
     * 
     * @param educationOrganizationId
     *            The Id of the $$educationalOrganizations$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @DELETE
    @Path("{" + ParameterConstants.EDUCATION_ORGANIZATION_ID + "}")
    public Response delete(@PathParam(ParameterConstants.EDUCATION_ORGANIZATION_ID) final String educationOrganizationId, 
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return this.crudDelegate.delete(ResourceNames.EDUCATION_ORGANIZATIONS, educationOrganizationId, headers, uriInfo);
    }

    /**
     * Update an existing $$educationalOrganizations$$ entity.
     * 
     * @param educationOrganizationId
     *            The id of the $$educationalOrganizations$$.
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
    @Path("{" + ParameterConstants.EDUCATION_ORGANIZATION_ID + "}")
    public Response update(@PathParam(ParameterConstants.EDUCATION_ORGANIZATION_ID) final String educationOrganizationId,
            final EntityBody newEntityBody, 
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return this.crudDelegate.update(ResourceNames.EDUCATION_ORGANIZATIONS, educationOrganizationId, newEntityBody, headers, uriInfo);
    }
    

    /**
     * Returns each $$staffEducationOrganizationAssociations$$ that
     * references the given $$educationalOrganizations$$
     * 
     * @param educationOrganizationId
     *            The Id of the School.
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
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    @Path("{" + ParameterConstants.EDUCATION_ORGANIZATION_ID + "}" + "/" + PathConstants.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS)
    public Response getStaffEducationOrganizationAssociations(@PathParam(ParameterConstants.EDUCATION_ORGANIZATION_ID) final String educationOrganizationId,
            @Context HttpHeaders headers, 
            @Context final UriInfo uriInfo) {
        return this.crudDelegate.read(ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS, "educationOrganizationReference", educationOrganizationId, headers, uriInfo);
    }
    

    /**
     * Returns each $$staff$$ associated to the given education organization through
     * a $$staffEducationOrganizationAssociations$$ 
     * 
     * @param educationOrganizationId
     *            The Id of the School.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    @Path("{" + ParameterConstants.EDUCATION_ORGANIZATION_ID + "}" + "/" + PathConstants.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS + "/" + PathConstants.STAFF)
    public Response getStaffEducationOrganizationAssociationStaff(@PathParam(ParameterConstants.EDUCATION_ORGANIZATION_ID) final String educationOrganizationId,
            @Context HttpHeaders headers, 
            @Context final UriInfo uriInfo) {
        return this.crudDelegate.read(ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS, "educationOrganizationReference", educationOrganizationId, 
                "staffReference", ResourceNames.STAFF, headers, uriInfo);
    }
}
