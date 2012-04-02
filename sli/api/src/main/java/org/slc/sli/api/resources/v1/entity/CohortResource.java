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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.config.ResourceNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.DefaultCrudEndpoint;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.resources.v1.ParameterConstants;
import org.slc.sli.api.resources.v1.PathConstants;

/**
 * Prototype new api end points and versioning
 * 
 * @author jstokes
 * 
 */
@Path(PathConstants.V1 + "/" + PathConstants.COHORTS)
@Component
@Scope("request")
@Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
public class CohortResource extends DefaultCrudEndpoint {
    
    public static final String COHORT_IDENTIFIER = "cohortIdentifier";
    public static final String COHORT_DESCRIPTION = "cohortDescription";
    public static final String COHORT_TYPE = "cohortType";
    public static final String COHORT_SCOPE = "cohortScope";
    public static final String ACADEMIC_SUBJECT = "academicSubject";
    public static final String EDUCATION_ORGANIZATION_ID = "educationOrgId";
    
    @Autowired
    public CohortResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.COHORTS);
    }

    /**
     * Returns all $$cohorts$$ entities for which the logged in User has permission and context.
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
        return super.readAll(offset, limit, headers, uriInfo);
    }

    /**
     * Create a new $$cohorts$$ entity.
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
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response create(final EntityBody newEntityBody, 
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.create(newEntityBody, headers, uriInfo);
    }

    /**
     * Get a single $$cohorts$$ entity
     * 
     * @param cohortId
     *            The Id of the $$cohorts$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A single $$cohorts$$ entity
     */
    @GET
    @Path("{" + COHORT_IDENTIFIER + "}")
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    public Response read(@PathParam(COHORT_IDENTIFIER) final String cohortId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(cohortId, headers, uriInfo);
    }

    /**
     * Delete a $$cohorts$$ entity
     * 
     * @param cohortId
     *            The Id of the $$cohorts$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @DELETE
    @Path("{" + COHORT_IDENTIFIER + "}")
    public Response delete(@PathParam(COHORT_IDENTIFIER) final String cohortId, 
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.delete(cohortId, headers, uriInfo);
    }

    /**
     * Update an existing $$cohorts$$ entity.
     * 
     * @param cohortId
     *            The id of the $$cohorts$$.
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
    @Path("{" + COHORT_IDENTIFIER + "}")
    public Response update(@PathParam(COHORT_IDENTIFIER) final String cohortId,
            final EntityBody newEntityBody, 
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.update(cohortId, newEntityBody, headers, uriInfo);
    }

    /**
     * Returns each $$staffCohortAssociations$$ that
     * references the given $$cohorts$$
     * 
     * @param cohortId
     *            The Id of the $$cohorts$$.
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
    @Path("{" + COHORT_IDENTIFIER + "}" + "/" + PathConstants.STAFF_COHORT_ASSOCIATIONS)
    public Response getStaffCohortAssociations(@PathParam(COHORT_IDENTIFIER) final String cohortId,
            @Context HttpHeaders headers, 
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STAFF_COHORT_ASSOCIATIONS, ParameterConstants.COHORT_ID, cohortId, headers, uriInfo);
    }
    

    /**
     * Returns each $$staff$$ associated to the given $$cohorts$$ through
     * a $$staffCohortAssociations$$ 
     * 
     * @param cohortId
     *            The Id of the $$cohorts$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    @Path("{" + COHORT_IDENTIFIER + "}" + "/" + PathConstants.STAFF_COHORT_ASSOCIATIONS + "/" + PathConstants.STAFF)
    public Response getStaffCohortAssociationStaff(@PathParam(COHORT_IDENTIFIER) final String cohortId,
            @Context HttpHeaders headers, 
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STAFF_COHORT_ASSOCIATIONS, ParameterConstants.COHORT_ID, cohortId, 
                ParameterConstants.STAFF_ID, ResourceNames.STAFF, headers, uriInfo);
    }

    /**
     * Returns each $$studentCohortAssociations$$ that
     * references the given $$cohorts$$
     * 
     * @param cohortId
     *            The Id of the $$cohorts$$.
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
    @Path("{" + COHORT_IDENTIFIER + "}" + "/" + PathConstants.STUDENT_COHORT_ASSOCIATIONS)
    public Response getStudentCohortAssociations(@PathParam(COHORT_IDENTIFIER) final String cohortId,
            @Context HttpHeaders headers, 
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_COHORT_ASSOCIATIONS, ParameterConstants.COHORT_ID, cohortId, headers, uriInfo);
    }
    

    /**
     * Returns each $$students$$ associated to the given $$cohorts$$ through
     * a $$studentCohortAssociations$$ 
     * 
     * @param cohortId
     *            The Id of the $$cohorts$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    @Path("{" + COHORT_IDENTIFIER + "}" + "/" + PathConstants.STUDENT_COHORT_ASSOCIATIONS + "/" + PathConstants.STUDENTS)
    public Response getStudentCohortAssociationStudents(@PathParam(COHORT_IDENTIFIER) final String cohortId,
            @Context HttpHeaders headers, 
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_COHORT_ASSOCIATIONS, ParameterConstants.COHORT_ID, cohortId, 
                ParameterConstants.STUDENT_ID, ResourceNames.STUDENTS, headers, uriInfo);
    }
}
