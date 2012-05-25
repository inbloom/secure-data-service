package org.slc.sli.api.resources.v1.entity;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.resources.v1.DefaultCrudResource;
import org.slc.sli.common.constants.ResourceNames;
import org.slc.sli.common.constants.v1.ParameterConstants;
import org.slc.sli.common.constants.v1.PathConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * This entity represents an educational organization that includes staff and students who
 * participate in classes and educational activity groups.
 * 
 * For more information, see the schema for $$School$$ resources.
 * 
 * @author srupasinghe
 * @author kmyers
 * 
 */
@Path(PathConstants.V1 + "/" + PathConstants.SCHOOLS)
@Component
@Scope("request")
public class SchoolResource extends DefaultCrudResource {
    
    @Autowired
    public SchoolResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.SCHOOLS);
    }
    
    /**
     * Returns the requested collection of resources that are associated with the specified
     * resource.
     */
    @GET
    @Path("{" + ParameterConstants.SCHOOL_ID + "}" + "/" + PathConstants.TEACHER_SCHOOL_ASSOCIATIONS)
    public Response getTeacherSchoolAssociations(@PathParam(ParameterConstants.SCHOOL_ID) final String schoolId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS, "schoolId", schoolId, headers, uriInfo);
    }
    
    /**
     * Returns the requested collection of resources that are associated with the specified
     * resource.
     */
    @GET
    @Path("{" + ParameterConstants.SCHOOL_ID + "}" + "/" + PathConstants.TEACHER_SCHOOL_ASSOCIATIONS + "/"
            + PathConstants.TEACHERS)
    public Response getTeacherSchoolAssociationTeachers(@PathParam(ParameterConstants.SCHOOL_ID) final String schoolId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS, "schoolId", schoolId, "teacherId",
                ResourceNames.TEACHERS, headers, uriInfo);
    }
    
    /**
     * Returns the requested collection of resources that are associated with the specified
     * resource.
     */
    @GET
    @Path("{" + ParameterConstants.SCHOOL_ID + "}" + "/" + PathConstants.STUDENT_SCHOOL_ASSOCIATIONS)
    public Response getStudentSchoolAssociations(@PathParam(ParameterConstants.SCHOOL_ID) final String schoolId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, "schoolId", schoolId, headers, uriInfo);
    }
    
    /**
     * Returns the requested collection of resources that are associated with the specified
     * resource.
     */
    @GET
    @Path("{" + ParameterConstants.SCHOOL_ID + "}" + "/" + PathConstants.STUDENT_SCHOOL_ASSOCIATIONS + "/"
            + PathConstants.STUDENTS)
    public Response getStudentSchoolAssociationStudents(@PathParam(ParameterConstants.SCHOOL_ID) final String schoolId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, "schoolId", schoolId, "studentId",
                ResourceNames.STUDENTS, headers, uriInfo);
    }
    
    /**
     * Returns the requested collection of resources that are associated with the specified
     * resource.
     */
    @GET
    @Path("{" + ParameterConstants.SCHOOL_ID + "}" + "/" + PathConstants.SECTIONS)
    public Response getSectionsForSchool(@PathParam(ParameterConstants.SCHOOL_ID) final String schoolId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.SECTIONS, "schoolId", schoolId, headers, uriInfo);
    }
    
    /**
     * Returns the requested collection of resources that are associated with the specified
     * resource.
     */
    @GET
    @Path("{" + ParameterConstants.SCHOOL_ID + "}" + "/" + PathConstants.SESSIONS)
    public Response getSessionsForSchool(@PathParam(ParameterConstants.SCHOOL_ID) final String schoolId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.SESSIONS, "schoolId", schoolId, headers, uriInfo);
    }
}
