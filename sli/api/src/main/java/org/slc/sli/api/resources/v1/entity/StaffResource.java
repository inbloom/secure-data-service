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
 * StaffResource
 *
 * This entity represents an individual who performs specified activities for any public
 * or private education institution or agency that provides instructional and/or support
 * services to students or staff at the early childhood level through high school
 * completion.
 *
 * @author jstokes
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.STAFF)
@Component
@Scope("request")
public class StaffResource extends DefaultCrudEndpoint {

    public static final String UNIQUE_STATE_ID = "staffUniqueStateId";
    public static final String NAME = "name";
    public static final String SEX = "sex";
    public static final String HISPANIC_LATINO_ETHNICITY = "hispanicLatinoEthnicity";
    public static final String EDUCATION_LEVEL = "highestLevelOfEducationCompleted";

    @Autowired
    public StaffResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.STAFF);
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
     * @return Returns all $$staff$$ entities for which the logged in User has permission and context.
     */
    @Override
    @GET
    public Response readAll(@QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.readAll(offset, limit, headers, uriInfo);
    }

    /**
     * create
     *
     * @param newEntityBody
     *            entity data
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *              URI information including path and query parameters
     * @return A 201 response on successfully created entity with the ID of the entity
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
     * read
     *
     * @param staffId
     *            The id (or list of ids) of the $$staff$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A list of entities matching the list of ids queried for
     */
    @Override
    @GET
    @Path("{" + ParameterConstants.STAFF_ID + "}")
    public Response read(@PathParam(ParameterConstants.STAFF_ID) final String staffId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(staffId, headers, uriInfo);
    }

    /**
     * delete
     *
     * @param staffId
     *            The Id of the $$staff$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @Override
    @DELETE
    @Path("{" + ParameterConstants.STAFF_ID + "}")
    public Response delete(@PathParam(ParameterConstants.STAFF_ID) final String staffId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.delete(staffId, headers, uriInfo);
    }

    /**
     * update
     *
     * @param staffId
     *            The id of the $$staff$$.
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
    @Path("{" + ParameterConstants.STAFF_ID + "}")
    public Response update(@PathParam(ParameterConstants.STAFF_ID) final String staffId,
            final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.update(staffId, newEntityBody, headers, uriInfo);
    }

    /**
     * getStaffEducationOrganizationAssociations
     *
     * @param staffId
     *            The Id of the School.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns each $$staffEducationOrganizationAssociations$$ that references the given $$staff$$
     */
    @GET
    @Path("{" + ParameterConstants.STAFF_ID + "}" + "/" + PathConstants.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS)
    public Response getStaffEducationOrganizationAssociations(@PathParam(ParameterConstants.STAFF_ID) final String staffId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS, "staffReference", staffId, headers, uriInfo);
    }


    /**
     * getStaffEducationOrganizationAssociationEducationOrganizations
     *
     * @param staffId
     *            The Id of the School.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns each $$staff$$ associated to the given school
     * through a $$staffEducationOrganizationAssociations$$
     */
    @GET
    @Path("{" + ParameterConstants.STAFF_ID + "}" + "/" + PathConstants.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS + "/" + PathConstants.EDUCATION_ORGANIZATIONS)
    public Response getStaffEducationOrganizationAssociationEducationOrganizations(@PathParam(ParameterConstants.STAFF_ID) final String staffId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS, "staffReference", staffId,
                "educationOrganizationReference", ResourceNames.EDUCATION_ORGANIZATIONS, headers, uriInfo);
    }

    /**
     * getStaffCohortAssociations
     *
     * @param staffId
     *            The Id of the Staff.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns each $$staffCohortAssociations$$ that references the given $$staff$$
     */
    @GET
    @Path("{" + ParameterConstants.STAFF_ID + "}" + "/" + PathConstants.STAFF_COHORT_ASSOCIATIONS)
    public Response getStaffCohortAssociations(@PathParam(ParameterConstants.STAFF_ID) final String staffId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STAFF_COHORT_ASSOCIATIONS, ParameterConstants.STAFF_ID, staffId, headers, uriInfo);
    }


    /**
     * getStaffCohortAssociationCohorts
     *
     * @param staffId
     *            The Id of the Staff.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns each $$cohorts$$ associated to the given staff through a $$staffCohortAssociations$$
     */
    @GET
    @Path("{" + ParameterConstants.STAFF_ID + "}" + "/" + PathConstants.STAFF_COHORT_ASSOCIATIONS + "/" + PathConstants.COHORTS)
    public Response getStaffCohortAssociationCohorts(@PathParam(ParameterConstants.STAFF_ID) final String staffId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STAFF_COHORT_ASSOCIATIONS, ParameterConstants.STAFF_ID, staffId,
                ParameterConstants.COHORT_ID, ResourceNames.COHORTS, headers, uriInfo);
    }
    /**
     * getStaffProgramAssociations
     * Returns each $$staffProgramAssociations$$ that
     * references the given $$staff$$
     *
     * @param staffId
     *            The Id of the $$staff$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns each $$staffProgramAssociations$$ that references the given $$staff$$
     */
    @GET
    @Path("{" + ParameterConstants.STAFF_ID + "}" + "/" + PathConstants.STAFF_PROGRAM_ASSOCIATIONS)
    public Response getStaffProgramAssociations(@PathParam(ParameterConstants.STAFF_ID) final String staffId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STAFF_PROGRAM_ASSOCIATIONS, "staffId", staffId, headers, uriInfo);
    }


    /**
     * getStaffProgramAssociationPrograms
     * Returns the $$programs$$ that are referenced from the $$staffProgramAssociations$$
     * that references the given $$staff$$.
     *
     * @param staffId
     *            The Id of the $$staff$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns the $$programs$$ that are referenced from the $$staffProgramAssociations$$
     * that references the given $$staff$$.
     */
    @GET
    @Path("{" + ParameterConstants.STAFF_ID + "}" + "/" + PathConstants.STAFF_PROGRAM_ASSOCIATIONS + "/" + PathConstants.PROGRAMS)
    public Response getStaffProgramAssociationPrograms(@PathParam(ParameterConstants.STAFF_ID) final String staffId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STAFF_PROGRAM_ASSOCIATIONS, "staffId", staffId,
                "programId", ResourceNames.PROGRAMS, headers, uriInfo);
    }

}
