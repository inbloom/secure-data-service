package org.slc.sli.api.resources.v1.entity;

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
 * Defines the means to process Session entities,
 * and retrieve their associated school and course entities.
 *
 * Prototype new api end points and versioning
 *
 * @author jstokes
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.SESSIONS)
@Component
@Scope("request")
public class SessionResource extends DefaultCrudEndpoint {

    @Autowired
    public SessionResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.SESSIONS);
    }

    /**
     * Returns all $$sessions$$ entities for which the logged in User has permission and context.
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
     * Create a new $$sessions$$ entity.
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
     *                 item is accessible.}
     */
    @Override
    @POST
    public Response create(final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.create(newEntityBody, headers, uriInfo);
    }

    /**
     * Get a single $$sessions$$ entity
     *
     * @param sessionId
     *            The Id of the $$sessions$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A single session entity
     */
    @Override
    @GET
    @Path("{" + ParameterConstants.SESSION_ID + "}")
    public Response read(@PathParam(ParameterConstants.SESSION_ID) final String sessionId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(sessionId, headers, uriInfo);
    }

    /**
     * Delete a $$sessions$$ entity
     *
     * @param sessionId
     *            The Id of the $$sessions$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @Override
    @DELETE
    @Path("{" + ParameterConstants.SESSION_ID + "}")
    public Response delete(@PathParam(ParameterConstants.SESSION_ID) final String sessionId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.delete(sessionId, headers, uriInfo);
    }

    /**
     * Update an existing $$sessions$$ entity.
     *
     * @param sessionId
     *            The id of the $$sessions$$.
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
    @Path("{" + ParameterConstants.SESSION_ID + "}")
    public Response update(@PathParam(ParameterConstants.SESSION_ID) final String sessionId,
            final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.update(sessionId, newEntityBody, headers, uriInfo);
    }


    /**
     * Returns each $$schoolSessionAssociations$$ that
     * references the given $$schools$$
     *
     * @param sessionId
     *            The id of the $$sessions$$.
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
    @Path("{" + ParameterConstants.SCHOOL_ID + "}" + "/" + PathConstants.SCHOOL_SESSION_ASSOCIATIONS)
    public Response getSchoolSessionAssociations(@PathParam(ParameterConstants.SCHOOL_ID) final String sessionId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.SCHOOL_SESSION_ASSOCIATIONS, "sessionId", sessionId, headers, uriInfo);
    }


    /**
     * Returns each $$schools$$ associated to the given session through
     * a $$schoolSessionAssociations$$
     *
     * @param sessionId
     *            The id of the $$sessions$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     */
    @GET
    @Path("{" + ParameterConstants.SCHOOL_ID + "}" + "/" + PathConstants.SCHOOL_SESSION_ASSOCIATIONS + "/" + PathConstants.SCHOOLS)
    public Response getSchoolSessionAssociationSchools(@PathParam(ParameterConstants.SCHOOL_ID) final String sessionId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.SCHOOL_SESSION_ASSOCIATIONS, "sessionId", sessionId, "schoolId", ResourceNames.SCHOOLS, headers, uriInfo);
    }

    /**
     * Returns each $$courseOfferings$$ that
     * references the given $$sessions$$
     *
     * @param sessionId
     *            The id of the $$sessions$$.
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
    @Path("{" + ParameterConstants.SESSION_ID + "}" + "/" + PathConstants.COURSE_OFFERINGS)
    public Response getCourseOfferings(@PathParam(ParameterConstants.SESSION_ID) final String sessionId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.COURSE_OFFERINGS, "sessionId", sessionId, headers, uriInfo);
    }


    /**
     * Returns each $$courses$$ associated to the given session through
     * a $$courseOfferings$$
     *
     * @param sessionId
     *            The id of the $$sessions$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     */
    @GET
    @Path("{" + ParameterConstants.SESSION_ID + "}" + "/" + PathConstants.COURSE_OFFERINGS + "/" + PathConstants.COURSES)
    public Response getCourseOfferingCourses(@PathParam(ParameterConstants.SESSION_ID) final String sessionId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.COURSE_OFFERINGS, "sessionId", sessionId, "courseId", ResourceNames.COURSES, headers, uriInfo);
    }

}
