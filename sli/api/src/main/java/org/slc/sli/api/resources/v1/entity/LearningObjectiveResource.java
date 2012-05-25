package org.slc.sli.api.resources.v1.entity;

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
 * Represents identified learning objectives for courses in specific grades.
 *
 * For more information, see the schema for $$LearningObjective$$ resources.
 *
 * @author dliu
 */
@Path(PathConstants.V1 + "/" + PathConstants.LEARNING_OBJECTIVES)
@Component
@Scope("request")
public class LearningObjectiveResource extends DefaultCrudResource {

    @Autowired
    public LearningObjectiveResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.LEARNINGOBJECTIVES);
    }


    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.LEARNINGOBJECTIVE_ID + "}" + "/" + PathConstants.LEARNING_STANDARDS)
    public Response getLearningStandards(
            @PathParam(ParameterConstants.LEARNINGOBJECTIVE_ID) final String learningObjectiveId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.LEARNINGOBJECTIVES, "_id", learningObjectiveId,
                ParameterConstants.LEARNING_STANDARDS, ResourceNames.LEARNINGSTANDARDS, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.LEARNINGOBJECTIVE_ID + "}" + "/" + PathConstants.PARENT_LEARNING_OBJECTIVES)
    public Response getParentLearningObjective(
            @PathParam(ParameterConstants.LEARNINGOBJECTIVE_ID) final String learningObjectiveId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.LEARNINGOBJECTIVES, "_id", learningObjectiveId,
                ParameterConstants.PARENT_LEARNING_OBJECTIVE, ResourceNames.LEARNINGOBJECTIVES, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.LEARNINGOBJECTIVE_ID + "}" + "/" + PathConstants.CHILD_LEARNING_OBJECTIVES)
    public Response getChildrenLearningObjective(
            @PathParam(ParameterConstants.LEARNINGOBJECTIVE_ID) final String learningObjectiveId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.LEARNINGOBJECTIVES, ParameterConstants.PARENT_LEARNING_OBJECTIVE,
                learningObjectiveId, headers, uriInfo);
    }


    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.LEARNINGOBJECTIVE_ID + "}" + "/" + PathConstants.STUDENT_COMPETENCIES)
    public Response getStudentCompetencies(@PathParam(ParameterConstants.LEARNINGOBJECTIVE_ID) final String learningObjectiveId,
                                           @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_COMPETENCIES, ParameterConstants.LEARNINGOBJECTIVE_ID,
                         learningObjectiveId, headers, uriInfo);
    }

}
