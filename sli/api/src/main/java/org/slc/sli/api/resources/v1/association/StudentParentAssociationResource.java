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
 * Represents the relationships between students and their parents, guardians, or caretakers.
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.STUDENT_PARENT_ASSOCIATIONS)
@Component
@Scope("request")
public class StudentParentAssociationResource extends DefaultCrudResource {
    /**
     * Logging utility.
     */
//    private static final Logger LOGGER = LoggerFactory.getLogger(StudentParentAssociationResource.class);

    @Autowired
    public StudentParentAssociationResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.STUDENT_PARENT_ASSOCIATIONS);
//        DE260 - Logging of possibly sensitive data
//        LOGGER.debug("New resource handler created {}", this);
    }

    /**
     * Returns each $$Student$$ that references the given $$StudentParentAssociation$$.
     *
     * @param studentParentAssociationId
     *            The Id of the $$StudentParentAssociation$$
     * @param offset
     *            Index of the first result to return
     * @param limit
     *            Maximum number of results to return
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return each $$Student$$ that references the given $$StudentParentAssociation$$
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_PARENT_ASSOCIATION_ID + "}" + "/" + PathConstants.STUDENTS)
    public Response getStudents(@PathParam(ParameterConstants.STUDENT_PARENT_ASSOCIATION_ID) final String studentParentAssociationId,
                                @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
                                @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
                                @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_PARENT_ASSOCIATIONS, "_id", studentParentAssociationId, "studentId", ResourceNames.STUDENTS, headers, uriInfo);
    }

    /**
     * Returns each $$Parent$$ that references the given $$StudentParentAssociation$$.
     *
     * @param studentParentAssociationId
     *            The Id of the $$StudentParentAssociation$$
     * @param offset
     *            Index of the first result to return
     * @param limit
     *            Maximum number of results to return
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return each $$Parent$$ that references the given $$StudentParentAssociation$$
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_PARENT_ASSOCIATION_ID + "}" + "/" + PathConstants.PARENTS)
    public Response getParents(@PathParam(ParameterConstants.STUDENT_PARENT_ASSOCIATION_ID) final String studentParentAssociationId,
                               @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
                               @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
                               @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_PARENT_ASSOCIATIONS, "_id", studentParentAssociationId, "parentId", ResourceNames.PARENTS, headers, uriInfo);
    }
}
