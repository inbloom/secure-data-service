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
 * Represents the class sections (see the $$Section$$ schema) to which a teacher (see the
 * $$Teacher$$ schema) is assigned to.
 *
 * For more information, see the schema for $$TeacherSectionAssociation$$ resources.
 *
 * @author srupasinghe
 */
@Path(PathConstants.V1 + "/" + PathConstants.TEACHER_SECTION_ASSOCIATIONS)
@Component
@Scope("request")
public class TeacherSectionAssociationResource extends DefaultCrudResource {

    @Autowired
    public TeacherSectionAssociationResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.TEACHER_SECTION_ASSOCIATIONS);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.TEACHER_SECTION_ASSOCIATION_ID + "}" + "/" + PathConstants.TEACHERS)
    public Response getTeachersForAssociation(
            @PathParam(ParameterConstants.TEACHER_SECTION_ASSOCIATION_ID) final String teacherSectionAssociationId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.TEACHER_SECTION_ASSOCIATIONS, "_id", teacherSectionAssociationId, "teacherId",
                ResourceNames.TEACHERS, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.TEACHER_SECTION_ASSOCIATION_ID + "}" + "/" + PathConstants.SECTIONS)
    public Response getSectionsForAssociation(
            @PathParam(ParameterConstants.TEACHER_SECTION_ASSOCIATION_ID) final String teacherSectionAssociationId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.TEACHER_SECTION_ASSOCIATIONS, "_id", teacherSectionAssociationId, "sectionId",
                ResourceNames.SECTIONS, headers, uriInfo);
    }
}
