package org.slc.sli.api.resources.v1;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;

/**
 * Prototype new api end points and versioning
 * 
 * @author srupasinghe
 * TODO: remove typepath, use ResourceName constants
 * TODO: remove implements CrudEndpoint (kevin)
 * TODO: make sure @Produces gets picked up from Class to Resource Endpoint (billy)
 * TODO: limit/offset default values documentation, use constants
 * TODO: remove expandDepth from /schools/{schoolId}
 * TODO: association, limit/offset constants, remove expand depth
 * TODO: add uriInfo to every endpoint
 * 
 * TODO: generally remove all strings
 * 
 */
@Path(PathConstants.V1 + "/" + PathConstants.SCHOOLS)
@Component
@Scope("request")
@Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
public class SchoolResource implements CrudEndpoint {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(SchoolResource.class);
    private static final String TYPE_PATH = "schools";
    private final CrudEndpoint crudDelegate;

    @Autowired
    public SchoolResource(EntityDefinitionStore entityDefs) {
        crudDelegate = new DefaultCrudEndpoint(entityDefs, TYPE_PATH);
    }

    /**
     * Returns all $$school$$ entities for which the logged in User has permission and context.
     * 
     * @param uriInfo
     * @param offset
     *            starting position in results to return to user
     * @param limit
     *            maximum number of results to return to user (starting from offset)
     * @return
     */
    @GET
    public Response readAll(@QueryParam(ParameterConstants.OFFSET) @DefaultValue("0") final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue("50") final int limit, @Context final UriInfo uriInfo) {
        return crudDelegate.readAll(offset, limit, uriInfo);
    }

    /**
     * Create a new $$school$$ entity.
     * 
     * @param newEntityBody
     *            entity data
     * @param uriInfo
     * @return (This is the !return place)
     * @response.param {@name Location} {@style header} {@type
     *                 {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI where the created
     *                 item is accessable.}
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response create(final EntityBody newEntityBody, @Context final UriInfo uriInfo) {
        return crudDelegate.create(newEntityBody, uriInfo);
    }

    /**
     * Get a single $$school$$ entity
     * 
     * @param id
     *            The Id of the School.
     * @param expandDepth
     *            whether or not the full entity should be returned or just the link. Defaults to
     *            false
     * @param offset
     *            Index of the first result to return
     * @param limit
     *            Maximum number of results to return.
     * @param expandDepth
     *            Number of hops (associations) for which to expand entities.
     * @param uriInfo
     * @return A single school entity
     * @response.representation.200.mediaType application/json
     * @response.representation.200.qname {http://www.w3.org/2001/XMLSchema}school
     */
    @GET
    @Path("{schoolId}")
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    public Response read(@PathParam(ParameterConstants.SCHOOL_ID) final String schoolId,
            @QueryParam(ParameterConstants.EXPAND_DEPTH) @DefaultValue("false") final boolean expandDepth,
            @Context final UriInfo uriInfo) {
        return crudDelegate.read(schoolId, expandDepth, uriInfo);
    }

    /**
     * Delete a $$school$$ entity
     * 
     * @param id
     *            The Id of the School.
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @DELETE
    @Path("{schoolId}")
    public Response delete(@PathParam(ParameterConstants.SCHOOL_ID) final String schoolId) {
        return crudDelegate.delete(schoolId);
    }

    /**
     * Update an existing $$school$$ entity.
     * 
     * @param schoolId
     *            The Id of the School.
     * @param newEntityBody
     *            entity data that will used to replace the existing entity data
     * @return Response with a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @PUT
    @Path("{schoolId}")
    public Response update(@PathParam(ParameterConstants.SCHOOL_ID) final String schoolId,
            final EntityBody newEntityBody) {
        return crudDelegate.update(schoolId, newEntityBody);
    }

    /**
     * Returns all the student-school-associations that
     * reference the given $$school$$
     * 
     * @param schoolId
     *            The Id of the School.
     * @param offset
     *            Index of the first result to return
     * @param limit
     *            Maximum number of results to return.
     * @param expandDepth
     *            Number of hops (associations) for which to expand entities.
     * 
     * @return
     */
    @GET
    @Path("{schoolId}/student-school-associations")
    public Response getStudentSchoolAssociations(@PathParam(ParameterConstants.SCHOOL_ID) final String schoolId,
            @QueryParam("start-index") @DefaultValue("0") final int offset,
            @QueryParam("max-results") @DefaultValue("50") final int limit,
            @QueryParam("expandDepth") @DefaultValue("0") final int expandDepth) {
        return Response.status(Status.SERVICE_UNAVAILABLE).build();
    }
}
