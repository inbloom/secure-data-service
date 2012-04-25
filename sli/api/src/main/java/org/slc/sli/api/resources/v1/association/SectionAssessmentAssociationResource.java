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
 * @author wscott
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.SECTION_ASSESSMENT_ASSOCIATIONS)
@Component
@Scope("request")
public class SectionAssessmentAssociationResource extends DefaultCrudEndpoint {

    @Autowired
    public SectionAssessmentAssociationResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.SECTION_ASSESSMENT_ASSOCIATIONS);
    }

    /**
     * Returns all $$sectionAssessmentAssociations$$ entities for which the logged in User has permission and context.
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
     * Create a new $$sectionAssessmentAssociations$$ entity.
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
     * Get a single $$sectionAssessmentAssociations$$ entity
     *
     * @param sectionAssessmentAssociationId
     *            The Id of the $$sectionAssessmentAssociations$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A single $$sectionAssessmentAssociations$$ entity
     */
    @Override
    @GET
    @Path("{" + ParameterConstants.SECTION_ASSESSMENT_ASSOCIATION_ID + "}")
    public Response read(@PathParam(ParameterConstants.SECTION_ASSESSMENT_ASSOCIATION_ID) final String sectionAssessmentAssociationId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(sectionAssessmentAssociationId, headers, uriInfo);
    }

    /**
     * Delete a $$sectionAssessmentAssociations$$ entity
     *
     * @param sectionAssessmentAssociationId
     *            The Id of the $$sectionAssessmentAssociations$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @Override
    @DELETE
    @Path("{" + ParameterConstants.SECTION_ASSESSMENT_ASSOCIATION_ID + "}")
    public Response delete(@PathParam(ParameterConstants.SECTION_ASSESSMENT_ASSOCIATION_ID) final String sectionAssessmentAssociationId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.delete(sectionAssessmentAssociationId, headers, uriInfo);
    }

    /**
     * Update an existing $$sectionAssessmentAssociations$$ entity.
     *
     * @param sectionAssessmentAssociationId
     *            The id of the $$sectionAssessmentAssociations$$.
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
    @Path("{" + ParameterConstants.SECTION_ASSESSMENT_ASSOCIATION_ID + "}")
    public Response update(@PathParam(ParameterConstants.SECTION_ASSESSMENT_ASSOCIATION_ID) final String sectionAssessmentAssociationId,
            final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.update(sectionAssessmentAssociationId, newEntityBody, headers, uriInfo);
    }

    /**
     * Returns each $$sections$$ that
     * references the given $$sectionAssessmentAssociations$$
     *
     * @param sectionAssessmentAssociationId
     *            The Id of the sectionAssessmentAssociationId.
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
    @Path("{" + ParameterConstants.SECTION_ASSESSMENT_ASSOCIATION_ID + "}" + "/" + PathConstants.SECTIONS)
    public Response getSectionsForAssociation(@PathParam(ParameterConstants.SECTION_ASSESSMENT_ASSOCIATION_ID) final String sectionAssessmentAssociationId,
                                              @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
                                              @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
                                              @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.SECTION_ASSESSMENT_ASSOCIATIONS, "_id", sectionAssessmentAssociationId, "sectionId", ResourceNames.SECTIONS, headers, uriInfo);
    }

    /**
     * Returns each $$assessments$$ that
     * references the given $$sectionAssessmentAssociations$$
     *
     * @param sectionAssessmentAssociationId
     *            The Id of the sectionAssessmentAssociationId.
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
    @Path("{" + ParameterConstants.SECTION_ASSESSMENT_ASSOCIATION_ID + "}" + "/" + PathConstants.ASSESSMENTS)
    public Response getAssessmentsForAssociation(@PathParam(ParameterConstants.SECTION_ASSESSMENT_ASSOCIATION_ID) final String sectionAssessmentAssociationId,
                                                 @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
                                                 @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
                                                 @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.SECTION_ASSESSMENT_ASSOCIATIONS, "_id", sectionAssessmentAssociationId, "assessmentId", ResourceNames.ASSESSMENTS, headers, uriInfo);
    }

}
