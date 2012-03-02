package org.slc.sli.api.resources.v1.entity;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.ResourceNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.util.ResourceUtil;
import org.slc.sli.api.resources.v1.CrudEndpoint;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.resources.v1.ParameterConstants;
import org.slc.sli.api.resources.v1.PathConstants;

/**
 * Prototype new api end points and versioning
 * 
 * @author jstokes
 * 
 */
@Path(PathConstants.V1 + "/" + PathConstants.STUDENTS)
@Component
@Scope("request")
@Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
public class StudentResource {
    
    /**
     * Logging utility.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StudentResource.class);
    
    /*
     * Interface capable of performing CRUD operations.
     */
    private final CrudEndpoint crudDelegate;

    @Autowired
    public StudentResource(CrudEndpoint crudDelegate) {
        this.crudDelegate = crudDelegate;
    }

    /**
     * Returns all $$students$$ entities for which the logged in User has permission and context.
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
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    @GET
    public Response readAll(@QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit, 
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        ResourceUtil.putValue(headers.getRequestHeaders(), ParameterConstants.LIMIT, limit);
        ResourceUtil.putValue(headers.getRequestHeaders(), ParameterConstants.OFFSET, offset);
        return this.crudDelegate.readAll(ResourceNames.STUDENTS, headers, uriInfo);
    }

    /**
     * Create a new $$students$$ entity.
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
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response create(final EntityBody newEntityBody, 
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return this.crudDelegate.create(ResourceNames.STUDENTS, newEntityBody, headers, uriInfo);
    }

    /**
     * Get a single $$students$$ entity
     * 
     * @param studentId
     *            The Id of the $$students$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A single student entity
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_ID + "}")
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    public Response read(@PathParam(ParameterConstants.STUDENT_ID) final String studentId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return this.crudDelegate.read(ResourceNames.STUDENTS, studentId, headers, uriInfo);
    }

    /**
     * Delete a $$students$$ entity
     * 
     * @param studentId
     *            The Id of the $$students$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @DELETE
    @Path("{" + ParameterConstants.STUDENT_ID + "}")
    public Response delete(@PathParam(ParameterConstants.STUDENT_ID) final String studentId, 
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return this.crudDelegate.delete(ResourceNames.STUDENTS, studentId, headers, uriInfo);
    }

    /**
     * Update an existing $$students$$ entity.
     * 
     * @param studentId
     *            The id of the $$students$$.
     * @param newEntityBody
     *            entity data
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Response with a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @PUT
    @Path("{" + ParameterConstants.STUDENT_ID + "}")
    public Response update(@PathParam(ParameterConstants.STUDENT_ID) final String studentId,
            final EntityBody newEntityBody, 
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return this.crudDelegate.update(ResourceNames.STUDENTS, studentId, newEntityBody, headers, uriInfo);
    }

    /**
     * Returns each $$studentSectionAssociations$$ that
     * references the given $$students$$
     * 
     * @param studentId
     *            The id of the $$students$$.
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
     * @return result of CRUD operation
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    @Path("{" + ParameterConstants.STUDENT_ID + "}" + "/" + PathConstants.STUDENT_SECTION_ASSOCIATIONS)
    public Response getStudentSectionAssociations(@PathParam(ParameterConstants.STUDENT_ID) final String studentId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return this.crudDelegate.read(ResourceNames.STUDENT_SECTION_ASSOCIATIONS, "studentId", studentId, headers,
                uriInfo);
    }

    /**
     * Returns each $$sections$$ associated to the given student through
     * a $$studentSectionAssociations$$
     * 
     * @param studentId
     *            The id of the $$students$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    @Path("{" + ParameterConstants.STUDENT_ID + "}" + "/" + PathConstants.STUDENT_SECTION_ASSOCIATIONS + "/"
            + PathConstants.SECTIONS)
    public Response getStudentSectionAssociationSections(
            @PathParam(ParameterConstants.STUDENT_ID) final String studentId, @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return this.crudDelegate.read(ResourceNames.STUDENT_SECTION_ASSOCIATIONS, "studentId", studentId, "sectionId",
                ResourceNames.SECTIONS, headers, uriInfo);
    }
    
    /**
     * student school associations
     * 
     * @param studentId
     *            The Id of the Student.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    @Path("{" + ParameterConstants.STUDENT_ID + "}" + "/" + PathConstants.STUDENT_SCHOOL_ASSOCIATIONS)
    public Response getStudentSchoolAssociations(@PathParam(ParameterConstants.STUDENT_ID) final String studentId,
            @Context HttpHeaders headers, 
            @Context final UriInfo uriInfo) {
        return this.crudDelegate.read(ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, "studentId", studentId, headers, uriInfo);
    }
    
    /**
     * student school associations - schools lookup
     * 
     * @param studentId
     *            The Id of the Student.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    @Path("{" + ParameterConstants.STUDENT_ID + "}" + "/" + PathConstants.STUDENT_SCHOOL_ASSOCIATIONS + "/" + PathConstants.SCHOOLS)
    public Response getStudentSchoolAssociationSchools(@PathParam(ParameterConstants.STUDENT_ID) final String studentId,
            @Context HttpHeaders headers, 
            @Context final UriInfo uriInfo) {
        return this.crudDelegate.read(ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, "studentId", studentId, "schoolId", ResourceNames.SCHOOLS, headers, uriInfo);
    }

    /**
     * Returns each $$studentAssessmentAssociations$$ that
     * references the given $$students$$
     *
     * @param studentId   The id of the $$students$$.
     * @param offset      Index of the first result to return
     * @param limit       Maximum number of results to return.
     * @param expandDepth Number of hops (associations) for which to expand entities.
     * @param headers     HTTP Request Headers
     * @param uriInfo     URI information including path and query parameters
     * @return result of CRUD operation
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    @Path("{" + ParameterConstants.STUDENT_ID + "}" + "/" + PathConstants.STUDENT_ASSESSMENT_ASSOCIATIONS)
    public Response getStudentAssessmentAssociations(@PathParam(ParameterConstants.STUDENT_ID) final String studentId,
                                                     @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return this.crudDelegate.read(ResourceNames.STUDENT_ASSESSMENT_ASSOCIATIONS, "studentId", studentId, headers,
                uriInfo);
    }

    /**
     * Returns each $$assessments$$ associated to the given section through
     * a $$studentAssessmentAssociations$$
     *
     * @param studentId The id of the $$students$$.
     * @param headers   HTTP Request Headers
     * @param uriInfo   URI information including path and query parameters
     * @return result of CRUD operation
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    @Path("{" + ParameterConstants.STUDENT_ID + "}" + "/" + PathConstants.STUDENT_ASSESSMENT_ASSOCIATIONS + "/"
            + PathConstants.ASSESSMENTS)
    public Response getStudentAssessmentAssociationsAssessments(
            @PathParam(ParameterConstants.STUDENT_ID) final String studentId, @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return this.crudDelegate.read(ResourceNames.STUDENT_ASSESSMENT_ASSOCIATIONS, "studentId", studentId, "assessmentId",
                ResourceNames.ASSESSMENTS, headers, uriInfo);
    }

    /**
     * Returns each $$attendance$$ that
     * references the given $$students$$
     *
     * @param studentId   The id of the $$students$$.
     * @param offset      Index of the first result to return
     * @param limit       Maximum number of results to return.
     * @param expandDepth Number of hops (associations) for which to expand entities.
     * @param headers     HTTP Request Headers
     * @param uriInfo     URI information including path and query parameters
     * @return result of CRUD operation
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    @Path("{" + ParameterConstants.STUDENT_ID + "}" + "/" + PathConstants.ATTENDANCES)
    public Response getStudentsAttendance(@PathParam(ParameterConstants.STUDENT_ID) final String studentId,
                                                     @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return this.crudDelegate.read(ResourceNames.ATTENDANCES, "studentId", studentId, headers,
                uriInfo);
    }

}
