package org.slc.sli.api.resources;

import java.net.URI;
import java.util.Collection;
import java.util.TreeSet;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.representation.LinkTypes;
import org.slc.sli.api.service.SchoolService;
import org.slc.sli.api.service.StudentSchoolAssociationService;
import org.slc.sli.domain.School;
import org.slc.sli.domain.StudentSchoolAssociation;

/**
 * Provides a RESTful API for accessing and manipulating both School and School
 * <--> Student associations.
 * 
 * The design for this class is described here:
 * 
 * https://thesli.onconfluence.com/display/sli/Student+and+School+API+Design
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */
@Path("/schools")
@Scope("request")
@Component
//TODO add application/xml support
//@Produces({ "application/json", "application/xml" })
@Produces({ "application/json"})
public class SchoolResource {
    
    private Logger log = LoggerFactory.getLogger(SchoolResource.class);
    
    @Context
    private UriInfo uriInfo;
    
    @Autowired
    private SchoolService schoolService;
    
    @Autowired
    private StudentSchoolAssociationService studentSchoolAssociationService;
    
    /**
     * Return all schools in the system.
     * 
     * @return
     * @throws Exception
     */
    @GET
    public Response getAll() throws Exception {
        return Response.ok(schoolService.getAll()).build();
    }
    
    /**
     * Fetch the school with the given Id.
     * 
     * @param schoolId
     *            The school Id.
     * @return
     */
    @GET
    @Path("{schoolId}")
    public Response getSchool(@PathParam("schoolId") int schoolId) {
        School school = schoolService.getSchoolById(schoolId);
        
        if (school != null) {
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            String schoolsUri = builder.path("/students").build().toString();
            return Response.ok(school).header("students", schoolsUri).build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
    }
    
    @DELETE
    @Path("{schoolId}")
    public void delete(@PathParam("schoolId") int schoolId) throws Exception {
        schoolService.deleteById(schoolId);
    }
    
    /**
     * Creates a new school. This is a non-safe, non-idempotent operation and
     * will create new School entries for each call made to this method.
     * 
     * @param school
     * @throws Exception
     */
    @POST
    public Response add(School school) throws Exception {
        log.debug("School being added: {}", school);
        schoolService.addOrUpdate(school);
        ResponseBuilder builder = Response.status(Status.NO_CONTENT);
        URI uri = this.uriInfo.getAbsolutePathBuilder().path(String.valueOf(school.getSchoolId())).build();
        builder.header(ResourceUtilities.LOCATION_HEADER, uri);
        return builder.build();
        
    }
    
    /**
     * Updates the given school. This is an idempotent operation and retries
     * will not change state if a successful operation has already occurred.
     * 
     * @param school
     *            The school
     * @param schoolId
     *            The school ID
     * @throws Exception
     */
    @PUT
    @Path("{schoolId}")
    public void update(School school, @PathParam("schoolId") int schoolId) throws Exception {
        log.debug("School being updated: {}", school);
        school.setSchoolId(schoolId);
        schoolService.addOrUpdate(school);
    }
    
    // Associations
    
    /**
     * Returns information about the associations between the given school and
     * any students that are associated. This method will create hyperlinks that
     * clients can use to retrieve information about the association or
     * information about the student represented by the association.
     * 
     * @param schoolId
     * @return
     */
    @Path("{schoolId}/students")
    @GET
    public Response getSchoolStudentAssociations(@PathParam("schoolId") int schoolId) {
        TreeSet<Integer> students = new TreeSet<Integer>();
        for (StudentSchoolAssociation ssa : studentSchoolAssociationService
                .getStudentSchoolAssociationsForSchool(schoolId)) {
            students.add(ssa.getStudentId());
        }
        
        ResponseBuilder builder = Response.status(Response.Status.NO_CONTENT);
        for (int studentId : students) {
            URI uri = uriInfo.getRequestUriBuilder().path(String.valueOf(studentId)).build();
            ResourceUtilities.buildLinkHeader(builder, uri, LinkTypes.STUDENT_SCHOOL_ASSOCIATION_COLLECTION);
        }
        return builder.build();
    }
    
    /**
     * Returns information about the association between the given school and
     * the given student. This method will return all known attributes about the
     * Student to School association and hyperlinks to the Student and to the
     * parent school.
     * 
     * @param schoolId
     * @return
     */
    @Path("{schoolId}/students/{studentId}")
    @GET
    public Response getAssociationForStudent(@PathParam("schoolId") int schoolId, @PathParam("studentId") int studentId) {
        Collection<StudentSchoolAssociation> association = studentSchoolAssociationService
                .getStudentSchoolAssociationsForStudentAndSchool(studentId, schoolId);
        
        ResponseBuilder builder = Response.status(Response.Status.NO_CONTENT);
        for (StudentSchoolAssociation ssa : association) {
            URI uri = uriInfo.getRequestUriBuilder().path(String.valueOf(ssa.getAssociationId())).build();
            ResourceUtilities.buildLinkHeader(builder, uri, LinkTypes.STUDENT_SCHOOL_ASSOCIATION);
        }
        
        ResourceUtilities.buildLinkHeader(builder,
                uriInfo.getBaseUriBuilder().path(StudentResource.class).path(String.valueOf(studentId)).build(),
                LinkTypes.STUDENT);
        
        ResourceUtilities.buildLinkHeader(builder,
                uriInfo.getBaseUriBuilder().path(SchoolResource.class).path(String.valueOf(schoolId)).build(),
                LinkTypes.SCHOOL);
        
        return builder.build();
    }
    
    @Path("{schoolId}/students/{studentId}/{associationId}")
    @GET
    public Response getAssociation(@PathParam("studentId") int studentId, @PathParam("schoolId") int schoolId,
            @PathParam("associationId") int associationId) {
        StudentSchoolAssociation ssa = studentSchoolAssociationService.getStudentSchoolAssociationById(associationId);
        if (ssa == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(ssa).build();
    }
    
    @Path("{schoolId}/students/{studentId}/{associationId}")
    @PUT
    public Response updateAssociation(StudentSchoolAssociation ssa, @PathParam("associationId") int associationId,
            @PathParam("schoolId") int schoolId, @PathParam("studentId") int studentId) {
        log.debug("StudentSchoolAssociation being updated: {}", ssa);
        ssa.setAssociationId(associationId);
        if (ssa.getStudentId() == null) {
            ssa.setStudentId(studentId);
        }
        if (ssa.getSchoolId() == null) {
            ssa.setSchoolId(schoolId);
        }
//        if (ssa.getStudentId() != studentId || ssa.getSchoolId() != schoolId) {
//            return Response.status(Status.NOT_FOUND).build();
//        }
        this.studentSchoolAssociationService.addOrUpdate(ssa);
        return Response.status(Status.NO_CONTENT).build();
    }
    
    @Path("{schoolId}/students/{studentId}")
    @POST
    public Response createAssociation(StudentSchoolAssociation ssa, @PathParam("studentId") int studentId,
            @PathParam("schoolId") int schoolId) {
        log.debug("StudentSchoolAssociation being created: {}", ssa);
        ssa.setAssociationId(null);
        ssa.setSchoolId(schoolId);
        ssa.setStudentId(studentId);
        Integer associationId = this.studentSchoolAssociationService.addOrUpdate(ssa);
        ResponseBuilder builder = Response.status(Status.NO_CONTENT);
        URI uri = this.uriInfo.getAbsolutePathBuilder().path(String.valueOf(associationId)).build();
        ResourceUtilities.buildLinkHeader(builder, uri, LinkTypes.STUDENT_SCHOOL_ASSOCIATION);
        builder.header(ResourceUtilities.LOCATION_HEADER, uri);
        return builder.build();
    }
    
    @Path("{schoolId}/students/{studentId}/{associationId}")
    @DELETE
    public void deleteAssociation(@PathParam("associationId") int associationId, @PathParam("studentId") int studentId,
            @PathParam("schoolId") int schoolId) {
        this.studentSchoolAssociationService.deleteById(associationId);
    }
    
    public void setUriInfo(UriInfo info) {
        this.uriInfo = info;
    }
}
