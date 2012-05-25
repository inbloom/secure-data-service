package org.slc.sli.api.resources.v1.association;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.DefaultCrudEndpoint;
import org.slc.sli.client.constants.ResourceNames;
import org.slc.sli.client.constants.v1.ParameterConstants;
import org.slc.sli.client.constants.v1.PathConstants;

/**
 * Represents the link between a school and a session of instruction. A school will likely
 * be associated to multiple sessions over its operation and sessions can be shared by
 * multiple schools (perhaps in the same district).
 *
 * @author kmyers
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.SCHOOL_SESSION_ASSOCIATIONS)
@Component
@Scope("request")
public class SchoolSessionAssociationResource extends DefaultCrudEndpoint {
    /**
     * Logging utility.
     */
//    private static final Logger LOGGER = LoggerFactory.getLogger(SchoolSessionAssociationResource.class);

    @Autowired
    public SchoolSessionAssociationResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.SCHOOL_SESSION_ASSOCIATIONS);

//        DE260 - Logging of possibly sensitive data
//        LOGGER.debug("New resource handler created ", this);
    }

    /**
     * Returns all $$schoolSessionAssociations$$ entities for which the logged in User has permission and context.
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
    @Override
    @GET
    public Response readAll(@QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.readAll(offset, limit, headers, uriInfo);
    }

    /**
     * Creates a new $$schoolSessionAssociations$$.
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
    @Override
    @POST
    public Response create(final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.create(newEntityBody, headers, uriInfo);
    }

    /**
     * Gets a specified $$schoolSessionAssociations$$.
     *
     * @param schoolSessionAssociationId
     *            The Id of the $$schoolSessionAssociations$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A single school session association entity
     */
    @Override
    @GET
    @Path("{" + ParameterConstants.SCHOOL_SESSION_ASSOCIATION_ID + "}")
    public Response read(@PathParam(ParameterConstants.SCHOOL_SESSION_ASSOCIATION_ID) final String schoolSessionAssociationId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(schoolSessionAssociationId, headers, uriInfo);
    }

    /**
     * Deletes a specified $$schoolSessionAssociations$$.
     *
     * @param schoolSessionAssociationId
     *            The Id of the $$schoolSessionAssociations$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @Override
    @DELETE
    @Path("{" + ParameterConstants.SCHOOL_SESSION_ASSOCIATION_ID + "}")
    public Response delete(@PathParam(ParameterConstants.SCHOOL_SESSION_ASSOCIATION_ID) final String schoolSessionAssociationId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.delete(schoolSessionAssociationId, headers, uriInfo);
    }

    /**
     * Updates the specified $$schoolSessionAssociations$$.
     *
     * @param schoolSessionAssociationId
     *            The id of the $$schoolSessionAssociations$$.
     * @param newEntityBody
     *            entity data
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Response with a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @Override
    @PUT
    @Path("{" + ParameterConstants.SCHOOL_SESSION_ASSOCIATION_ID + "}")
    public Response update(@PathParam(ParameterConstants.SCHOOL_SESSION_ASSOCIATION_ID) final String schoolSessionAssociationId,
            final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.update(schoolSessionAssociationId, newEntityBody, headers, uriInfo);
    }

    /**
     * Returns each $$teachers$$ that
     * references the given $$schoolSessionAssociations$$
     *
     * @param schoolSessionAssociationId
     *            The Id of the teacherSchoolAssociation.
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
     * @return
     */
    @GET
    @Path("{" + ParameterConstants.SCHOOL_SESSION_ASSOCIATION_ID + "}" + "/" + PathConstants.SESSIONS)
    public Response getSessions(@PathParam(ParameterConstants.SCHOOL_SESSION_ASSOCIATION_ID) final String schoolSessionAssociationId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
       return super.read(ResourceNames.SCHOOL_SESSION_ASSOCIATIONS, "_id", schoolSessionAssociationId, "sessionId", ResourceNames.SESSIONS, headers, uriInfo);
    }

    /**
     * Returns each $$schools$$ that
     * references the given $$schoolSessionAssociations$$
     *
     * @param schoolSessionAssociationId
     *            The Id of the teacherSchoolAssociation.
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
     * @return
     */
    @GET
    @Path("{" + ParameterConstants.SCHOOL_SESSION_ASSOCIATION_ID + "}" + "/" + PathConstants.SCHOOLS)
    public Response getSchools(@PathParam(ParameterConstants.SCHOOL_SESSION_ASSOCIATION_ID) final String schoolSessionAssociationId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.SCHOOL_SESSION_ASSOCIATIONS, "_id", schoolSessionAssociationId, "schoolId", ResourceNames.SCHOOLS, headers, uriInfo);
    }
}
