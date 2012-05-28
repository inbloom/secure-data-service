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

import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.slc.sli.api.client.constants.v1.PathConstants;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.DefaultCrudEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Represents the association between a $$Student$$ and a $$Cohort$$.
 * 
 * For more information, see the schema for $$StudentCohortAssociation$$ resources.
 * 
 * @author kmyers
 * @author srichards
 */
@Path(PathConstants.V1 + "/" + PathConstants.STUDENT_COHORT_ASSOCIATIONS)
@Component
@Scope("request")
public class StudentCohortAssociationResource extends DefaultCrudEndpoint {
    
    public static final String BEGIN_DATE = "beginDate";
    
    /**
     * Logging utility.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StudentCohortAssociationResource.class);
    
    @Autowired
    public StudentCohortAssociationResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.STUDENT_COHORT_ASSOCIATIONS);
        LOGGER.debug("New resource handler created: {}", this);
    }
    
    /**
     * Returns the requested collection of resource representations.
     */
    @Override
    @GET
    public Response readAll(
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.readAll(offset, limit, headers, uriInfo);
    }
    
    /**
     * Creates a new resource using the given resource data.
     */
    @Override
    @POST
    public Response create(final EntityBody newEntityBody, @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.create(newEntityBody, headers, uriInfo);
    }
    
    /**
     * Returns the specified resource representation(s).
     */
    @Override
    @GET
    @Path("{" + ParameterConstants.STUDENT_COHORT_ASSOCIATION_ID + "}")
    public Response read(
            @PathParam(ParameterConstants.STUDENT_COHORT_ASSOCIATION_ID) final String studentCohortAssociationId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(studentCohortAssociationId, headers, uriInfo);
    }
    
    /**
     * Deletes the specified resource.
     */
    @Override
    @DELETE
    @Path("{" + ParameterConstants.STUDENT_COHORT_ASSOCIATION_ID + "}")
    public Response delete(
            @PathParam(ParameterConstants.STUDENT_COHORT_ASSOCIATION_ID) final String studentCohortAssociationId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.delete(studentCohortAssociationId, headers, uriInfo);
    }
    
    /**
     * Updates the specified resource using the given resource data.
     */
    @Override
    @PUT
    @Path("{" + ParameterConstants.STUDENT_COHORT_ASSOCIATION_ID + "}")
    public Response update(
            @PathParam(ParameterConstants.STUDENT_COHORT_ASSOCIATION_ID) final String studentCohortAssociationId,
            final EntityBody newEntityBody, @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.update(studentCohortAssociationId, newEntityBody, headers, uriInfo);
    }
    
    /**
     * Returns the requested collection of resources that are associated with the specified
     * resource.
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_COHORT_ASSOCIATION_ID + "}" + "/" + PathConstants.STUDENTS)
    public Response getStudentCohortAssocationStudents(
            @PathParam(ParameterConstants.STUDENT_COHORT_ASSOCIATION_ID) final String studentCohortAssociationId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_COHORT_ASSOCIATIONS, "_id", studentCohortAssociationId,
                ParameterConstants.STUDENT_ID, ResourceNames.STUDENTS, headers, uriInfo);
    }
    
    /**
     * Returns the requested collection of resources that are associated with the specified
     * resource.
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_COHORT_ASSOCIATION_ID + "}" + "/" + PathConstants.COHORTS)
    public Response getStudentCohortAssocationCohorts(
            @PathParam(ParameterConstants.STUDENT_COHORT_ASSOCIATION_ID) final String studentCohortAssociationId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_COHORT_ASSOCIATIONS, "_id", studentCohortAssociationId,
                ParameterConstants.COHORT_ID, ResourceNames.COHORTS, headers, uriInfo);
    }
}