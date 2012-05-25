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
 * Represents the link between an education organization and a staff member. Education
 * organizations employ multiple staff and staff are free to work at multiple education
 * organizations.
 *
 * @author kmyers
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS)
@Component
@Scope("request")
public class StaffEducationOrganizationAssociationResource extends DefaultCrudEndpoint {

    public static final String STAFF_REFERENCE = "staffReference";
    public static final String EDUCATION_ORGANIZATION_REFERENCE = "educationOrganizationReference";
    public static final String STAFF_CLASSIFICATION = "staffClassification";
    public static final String BEGIN_DATE = "beginDate";

    /**
     * Logging utility.
     */
//    private static final Logger LOGGER = LoggerFactory.getLogger(StaffEducationOrganizationAssociation.class);

    @Autowired
    public StaffEducationOrganizationAssociationResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS);
//        DE260 - Logging of possibly sensitive data
//        LOGGER.debug("New resource handler created: {}", this);
    }

    /**
     * Returns all $$staffEducationOrganizationAssociations$$ for which the logged in User has permission and context.
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
     * Creates a new $$staffEducationOrganizationAssociations$$.
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
     * Gets a single $$staffEducationOrganizationAssociations$$
     *
     * @param staffEducationOrganizationAssignmentId
     *            The Id of the $$staffEducationOrgAssignmentAssociation$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A single school entity
     */
    @Override
    @GET
    @Path("{" + ParameterConstants.STAFF_EDUCATION_ORGANIZATION_ID + "}")
    public Response read(@PathParam(ParameterConstants.STAFF_EDUCATION_ORGANIZATION_ID) final String staffEducationOrganizationAssociationId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(staffEducationOrganizationAssociationId, headers, uriInfo);
    }

    /**
     * Deletes a $$staffEducationOrganizationAssociations$$
     *
     * @param staffEducationOrganizationAssignmentId
     *            The Id of the $$staffEducationOrgAssignmentAssociation$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @Override
    @DELETE
    @Path("{" + ParameterConstants.STAFF_EDUCATION_ORGANIZATION_ID + "}")
    public Response delete(@PathParam(ParameterConstants.STAFF_EDUCATION_ORGANIZATION_ID) final String staffEducationOrganizationAssociationId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.delete(staffEducationOrganizationAssociationId, headers, uriInfo);
    }

    /**
     * Updates an existing $$staffEducationOrganizationAssociations$$
     *
     * @param staffEducationOrganizationAssignmentId
     *            The id of the $$staffEducationOrgAssignmentAssociation$$.
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
    @Path("{" + ParameterConstants.STAFF_EDUCATION_ORGANIZATION_ID + "}")
    public Response update(@PathParam(ParameterConstants.STAFF_EDUCATION_ORGANIZATION_ID) final String staffEducationOrganizationAssociationId,
            final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.update(staffEducationOrganizationAssociationId, newEntityBody, headers, uriInfo);
    }

    /**
     * Returns each $$staff$$ that
     * references the given $$staffEducationOrganizationAssociations$$
     *
     * @param staffEducationOrganizationAssignmentId
     *            The Id of the $$staffEducationOrganizationAssociations$$.
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
    @Path("{" + ParameterConstants.STAFF_EDUCATION_ORGANIZATION_ID + "}" + "/" + PathConstants.STAFF)
    public Response getStaffEducationOrganizationAssocationStaff(@PathParam(ParameterConstants.STAFF_EDUCATION_ORGANIZATION_ID) final String staffEducationOrganizationAssociationId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
       return super.read(ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS, "_id", staffEducationOrganizationAssociationId,
               "staffReference", ResourceNames.STAFF, headers, uriInfo);
    }

    /**
     * Returns each $$educationalOrganizations$$ that
     * references the given $$staffEducationOrganizationAssociations$$
     *
     * @param staffEducationOrganizationAssignmentId
     *            The Id of the $$staffEducationOrganizationAssociations$$.
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
    @Path("{" + ParameterConstants.STAFF_EDUCATION_ORGANIZATION_ID + "}" + "/" + PathConstants.EDUCATION_ORGANIZATIONS)
    public Response getStaffEducationOrganizationAssocationEducationOrganizations(@PathParam(ParameterConstants.STAFF_EDUCATION_ORGANIZATION_ID) final String staffEducationOrganizationAssociationId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS, "_id", staffEducationOrganizationAssociationId,
                "educationOrganizationReference", ResourceNames.EDUCATION_ORGANIZATIONS, headers, uriInfo);
    }
}
