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
 * Represents the course sections a student is assigned to.
 *
 * @author kmyers
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.STUDENT_SECTION_ASSOCIATIONS)
@Component
@Scope("request")
public class StudentSectionAssociationResource extends DefaultCrudResource {
    /**
     * Logging utility.
     */
//    private static final Logger LOGGER = LoggerFactory.getLogger(StudentSectionAssociationResource.class);

    @Autowired
    public StudentSectionAssociationResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.STUDENT_SECTION_ASSOCIATIONS);
//        DE260 - Logging of possibly sensitive data
//        LOGGER.debug("New resource handler created {}", this);
    }

    /**
     * Returns each $$Student$$ that references the given $$StudentSectionAssociation$$.
     *
     * @param studentSectionAssociationId
     *            The Id of the $$StudentSectionAssociation$$
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
     * @return each $$Student$$ that references the given $$StudentSectionAssociation$$
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID + "}" + "/" + PathConstants.STUDENTS)
    public Response getStudents(@PathParam(ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID) final String studentSectionAssociationId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
       return super.read(ResourceNames.STUDENT_SECTION_ASSOCIATIONS, "_id", studentSectionAssociationId, "studentId", ResourceNames.STUDENTS, headers, uriInfo);
    }

    /**
     * Returns each $$Section$$ that references the given $$StudentSectionAssociation$$.
     *
     * @param studentSectionAssociationId
     *            The Id of the $$StudentSectionAssociation$$
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
     * @return each $$Section$$ that references the given $$StudentSectionAssociation$$
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID + "}" + "/" + PathConstants.SECTIONS)
    public Response getSections(@PathParam(ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID) final String studentSectionAssociationId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_SECTION_ASSOCIATIONS, "_id", studentSectionAssociationId, "sectionId", ResourceNames.SECTIONS, headers, uriInfo);
    }

    /**
     * Returns each $$StudentCompetency$$ that references the given $$StudentSectionAssociation$$.
     *
     * @param studentSectionAssociationId
     *            The Id of the $$StudentSectionAssociation$$
     * @param offset
     *            Index of the first result to return
     * @param limit
     *            Maximum number of results to return
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return each $$StudentCompetency$$ that references the given $$StudentSectionAssociation$$
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID + "}" + "/" + PathConstants.STUDENT_COMPETENCIES)
    public Response getStudentCompetencies(@PathParam(ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID) final String studentSectionAssociationId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_COMPETENCIES, ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID,
                studentSectionAssociationId, headers, uriInfo);
    }
}
