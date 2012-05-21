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

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.resources.v1.DefaultCrudResource;
import org.slc.sli.common.constants.ResourceNames;
import org.slc.sli.common.constants.v1.ParameterConstants;
import org.slc.sli.common.constants.v1.PathConstants;

/**
 * Prototype new api end points and versioning
 *
 * @author wscott
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.STUDENT_ASSESSMENT_ASSOCIATIONS)
@Component
@Scope("request")
public class StudentAssessmentAssociationResource extends DefaultCrudResource {

    @Autowired
    public StudentAssessmentAssociationResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.STUDENT_ASSESSMENT_ASSOCIATIONS);
    }


    /**
     * Returns each $$students$$ that
     * references the given $$studentAssessments$$
     *
     * @param studentAssessmentId
     *            The Id of the studentAssessmentId.
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
    @Path("{" + ParameterConstants.STUDENT_ASSESSMENT_ID + "}" + "/" + PathConstants.STUDENTS)
    public Response getStudentsForAssociation(@PathParam(ParameterConstants.STUDENT_ASSESSMENT_ID) final String studentAssessmentId,
                                              @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
                                              @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
                                              @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_ASSESSMENT_ASSOCIATIONS, "_id", studentAssessmentId, "studentId", ResourceNames.STUDENTS, headers, uriInfo);
    }

    /**
     * Returns each $$assessments$$ that
     * references the given $$studentAssessments$$
     *
     * @param studentAssessmentId
     *            The Id of the studentAssessmentId.
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
    @Path("{" + ParameterConstants.STUDENT_ASSESSMENT_ID + "}" + "/" + PathConstants.ASSESSMENTS)
    public Response getAssessmentsForAssociation(@PathParam(ParameterConstants.STUDENT_ASSESSMENT_ID) final String studentAssessmentId,
                                                 @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
                                                 @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
                                                 @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_ASSESSMENT_ASSOCIATIONS, "_id", studentAssessmentId, "assessmentId", ResourceNames.ASSESSMENTS, headers, uriInfo);
    }

}
