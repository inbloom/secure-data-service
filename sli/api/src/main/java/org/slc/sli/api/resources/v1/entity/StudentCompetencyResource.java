package org.slc.sli.api.resources.v1.entity;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
 * Represents the competency assessed or evaluated for the student against a specific Learning Objective.
 *
 * For detailed information, see the schema for $$StudentCompetency$$ resources.
 *
 * @author chung
 */
@Path(PathConstants.V1 + "/" + PathConstants.STUDENT_COMPETENCIES)
@Component
@Scope("request")
public class StudentCompetencyResource extends DefaultCrudResource {

    @Autowired
    public StudentCompetencyResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.STUDENT_COMPETENCIES);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_COMPETENCY_ID + "}" + "/" + PathConstants.REPORT_CARDS)
    public Response getReportCards(@PathParam(ParameterConstants.STUDENT_COMPETENCY_ID) final String studentCompetencyId,
                                  @Context HttpHeaders headers,
                                  @Context final UriInfo uriInfo) {
       return super.read(ResourceNames.REPORT_CARDS, ParameterConstants.STUDENT_COMPETENCY_ID, studentCompetencyId, headers, uriInfo);
    }
}
