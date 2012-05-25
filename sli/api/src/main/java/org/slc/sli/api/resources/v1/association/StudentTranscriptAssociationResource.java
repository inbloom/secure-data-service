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
import org.slc.sli.client.constants.ResourceNames;
import org.slc.sli.client.constants.v1.ParameterConstants;
import org.slc.sli.client.constants.v1.PathConstants;

/**
 * Represents the final record of a student's performance in their courses at the end of semester or
 * school year.
 * 
 * @author kmyers
 * 
 */
@Path(PathConstants.V1 + "/" + PathConstants.COURSE_TRANSCRIPTS)
@Component
@Scope("request")
public class StudentTranscriptAssociationResource extends DefaultCrudResource {
    
    @Autowired
    public StudentTranscriptAssociationResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.STUDENT_TRANSCRIPT_ASSOCIATIONS);
    }
    
    /**
     * Returns each $$Student$$ that references the given $$StudentTranscriptAssociation$$.
     * 
     * @param courseTranscriptId
     *            The Id of the $$StudentTranscriptAssociation$$
     * @param offset
     *            Index of the first result to return
     * @param limit
     *            Maximum number of results to return
     * @param expandDepth
     *            Number of hops (associations) for which to expand entities
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return each $$Student$$ that references the given $$StudentTranscriptAssociation$$
     */
    @GET
    @Path("{" + ParameterConstants.COURSE_TRANSCRIPT_ID + "}" + "/" + PathConstants.STUDENTS)
    public Response getStudents(@PathParam(ParameterConstants.COURSE_TRANSCRIPT_ID) final String courseTranscriptId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_TRANSCRIPT_ASSOCIATIONS, "_id", courseTranscriptId, "studentId",
                ResourceNames.STUDENTS, headers, uriInfo);
    }
    
    /**
     * Returns each $$Course$$ that references the given $$StudentTranscriptAssociation$$.
     * 
     * @param courseTranscriptId
     *            The id of the $$StudentTranscriptAssociation$$
     * @param offset
     *            Index of the first result to return
     * @param limit
     *            Maximum number of results to return
     * @param expandDepth
     *            Number of hops (associations) for which to expand entities
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return each $$Course$$ that references the given $$StudentTranscriptAssociation$$
     */
    @GET
    @Path("{" + ParameterConstants.COURSE_TRANSCRIPT_ID + "}" + "/" + PathConstants.COURSES)
    public Response getCourses(@PathParam(ParameterConstants.COURSE_TRANSCRIPT_ID) final String courseTranscriptId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_TRANSCRIPT_ASSOCIATIONS, "_id", courseTranscriptId, "courseId",
                ResourceNames.COURSES, headers, uriInfo);
    }
    
}
