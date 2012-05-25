package org.slc.sli.api.resources.v1.association;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
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

import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.slc.sli.api.client.constants.v1.PathConstants;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.resources.v1.DefaultCrudResource;

/**
 * Represents the association between a $$Section$$ member a $$Assessment$$.
 *
 * For more information, see the schema for $$SectionAssessmentAssociation$$ resources.
 *
 * @author wscott
 */
@Path(PathConstants.V1 + "/" + PathConstants.SECTION_ASSESSMENT_ASSOCIATIONS)
@Component
@Scope("request")
public class SectionAssessmentAssociationResource extends DefaultCrudResource {

    @Autowired
    public SectionAssessmentAssociationResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.SECTION_ASSESSMENT_ASSOCIATIONS);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified
     * resource.
     */
    @GET
    @Path("{" + ParameterConstants.SECTION_ASSESSMENT_ASSOCIATION_ID + "}" + "/" + PathConstants.SECTIONS)
    public Response getSectionsForAssociation(
            @PathParam(ParameterConstants.SECTION_ASSESSMENT_ASSOCIATION_ID) final String sectionAssessmentAssociationId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.SECTION_ASSESSMENT_ASSOCIATIONS, "_id", sectionAssessmentAssociationId,
                "sectionId", ResourceNames.SECTIONS, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified
     * resource.
     */
    @GET
    @Path("{" + ParameterConstants.SECTION_ASSESSMENT_ASSOCIATION_ID + "}" + "/" + PathConstants.ASSESSMENTS)
    public Response getAssessmentsForAssociation(
            @PathParam(ParameterConstants.SECTION_ASSESSMENT_ASSOCIATION_ID) final String sectionAssessmentAssociationId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.SECTION_ASSESSMENT_ASSOCIATIONS, "_id", sectionAssessmentAssociationId,
                "assessmentId", ResourceNames.ASSESSMENTS, headers, uriInfo);
    }

}
