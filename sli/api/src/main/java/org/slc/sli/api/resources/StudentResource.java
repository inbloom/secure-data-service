package org.slc.sli.api.resources;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import org.slc.sli.api.service.StudentSchoolAssociationService;
import org.slc.sli.api.service.StudentService;
import org.slc.sli.domain.Student;
import org.slc.sli.domain.StudentSchoolAssociation;

/**
 * RESTful Service for exposing Student resources.
 * 
 * @author smelody
 * 
 */
@Path("/students")
@Scope("request")
@Component
// TODO add application/xml support
//@Produces({ "application/json", "application/xml" })
@Produces({ "application/json"})
public class StudentResource {
    
    /** Logger */
    private Logger log = LoggerFactory.getLogger(StudentResource.class);
    
    @Context
    private UriInfo uriInfo;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private StudentSchoolAssociationService studentSchoolAssociationService;
    
    /**
     * Returns all students.
     * 
     * @return
     * @throws Exception
     */
    @GET
    public Response getAll() throws Exception {
        Collection<Student> students = studentService.getAll();
        List<Student> listStudents = new ArrayList<Student>();
        listStudents.addAll(students);
        
        Response response = Response.ok(listStudents).build();
        return response;
    }
    
    /**
     * Returns a student with the given ID. This is a Safe operation which does not change system
     * state.
     * 
     * @param studentId
     * @return
     */
    @GET
    @Path("{studentId}")
    public Response getStudent(@PathParam("studentId") int studentId) {
        
        Student student = studentService.getStudentById(studentId);
        Response response = null;
        
        if (student != null) {
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            String schoolsUri = builder.path("/schools").build().toString();
            response = Response.ok(student).header("schools", schoolsUri).build();
        } else {
            response = Response.status(Status.NOT_FOUND).build();
        }
        return response;
        
    }
    
    @DELETE
    @Path("{studentId}")
    public void delete(@PathParam("studentId") int id) throws Exception {
        studentService.deleteById(id);
        
    }
    
    /**
     * Creates a new student. This is a non-safe, non-idempotent operation and will create new
     * Student entries for each call made to this method.
     * 
     * @param student
     * @throws Exception
     */
    @POST
    public Response add(Student student) throws Exception {
        
        log.debug("Student being added: {}", student);
        studentService.addOrUpdate(student);
        ResponseBuilder builder = Response.status(Status.NO_CONTENT);
        URI uri = this.uriInfo.getAbsolutePathBuilder().path(String.valueOf(student.getStudentId())).build();
        builder.header(ResourceUtilities.LOCATION_HEADER, uri);
        return builder.build();
    }
    
    /**
     * Updates the given student. This is an idempotent operation and retries will not change state
     * if a successful operation has
     * already occurred.
     * 
     * @param student
     *            The student
     * @param studentId
     *            The student ID
     * @throws Exception
     */
    @PUT
    @Path("{studentId}")
    public void update(Student student, @PathParam("studentId") int studentId) throws Exception {
        
        log.debug("Student being added: {}", student);
        student.setStudentId(studentId);
        studentService.addOrUpdate(student);
    }
    
    // Associations
    
    /**
     * Returns information about the associations between the given student and
     * any schools that are associated. This method will create hyperlinks that
     * clients can use to retrieve information about the association or
     * information about the student represented by the association.
     * 
     * @param studentId
     * @return
     */
    @Path("{studentId}/schools")
    @GET
    public Response getSchoolStudentAssociations(@PathParam("studentId") int studentId) {
        TreeSet<Integer> schools = new TreeSet<Integer>();
        for (StudentSchoolAssociation ssa : studentSchoolAssociationService
                .getStudentSchoolAssociationsForStudent(studentId)) {
            schools.add(ssa.getSchoolId());
        }
        
        ResponseBuilder builder = Response.status(Response.Status.NO_CONTENT);
        for (int schoolId : schools) {
            URI uri = uriInfo.getRequestUriBuilder().path(String.valueOf(schoolId)).build();
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
    @Path("{studentId}/schools/{schoolId}")
    @GET
    public Response getAssociationForStudent(@PathParam("schoolId") int schoolId, @PathParam("studentId") int studentId) {
        Collection<StudentSchoolAssociation> association = studentSchoolAssociationService
                .getStudentSchoolAssociationsForStudentAndSchool(studentId, schoolId);
        
        ResponseBuilder builder = Response.status(Status.NO_CONTENT);
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
    
    @Path("{studentId}/schools/{schoolId}/{associationId}")
    @GET
    public Response getAssociation(@PathParam("studentId") int studentId, @PathParam("schoolId") int schoolId,
            @PathParam("associationId") int associationId) {
        StudentSchoolAssociation ssa = studentSchoolAssociationService.getStudentSchoolAssociationById(associationId);
        if (ssa == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(ssa).build();
    }
    
    @Path("{studentId}/schools/{schoolId}/{associationId}")
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
    
    @Path("{studentId}/schools/{schoolId}")
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
    
    @Path("{studentId}/schools/{schoolId}/{associationId}")
    @DELETE
    public void deleteAssociation(@PathParam("associationId") int associationId, @PathParam("studentId") int studentId,
            @PathParam("schoolId") int schoolId) {
        this.studentSchoolAssociationService.deleteById(associationId);
    }
    
    public void setStudentService(StudentService studentService) {
        this.studentService = studentService;
    }
    
    public void setUriInfo(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }
}
