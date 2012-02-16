package org.slc.sli.api.resources.v1;

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
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;

//import org.slc.sli.api.resources.Resource;

/**
 * Prototype new api end points and versioning
 * 
 * @author srupasinghe
 * 
 */
@Path(PathConstants.V1 + "/" + PathConstants.TEACHER_SCHOOL_ASSOCIATIONS)
@Component
@Scope("request")
@Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
public class TeacherSchoolAssociationResource implements CrudEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeacherSchoolAssociationResource.class);
    
    private static final String TYPE_PATH = "teacher-school-associations";
    private final CrudEndpoint crudDelegate;
    
    @Autowired
    public TeacherSchoolAssociationResource(final EntityDefinitionStore entityDefs) {
        crudDelegate = new DefaultCrudEndpoint(entityDefs, TYPE_PATH, LOGGER);
    }
    
    /**
     * Returns all teacher-school-association entities for which the logged in User has permission
     * and context.
     * 
     * @param uriInfo
     * @param offset
     *            starting position in results to return to user
     * @param limit
     *            maximum number of results to return to user (starting from offset)
     * @return
     */
    @GET
    public Response readAll(@QueryParam("offset") @DefaultValue("0") final int offset,
            @QueryParam("limit") @DefaultValue("50") final int limit, @Context final UriInfo uriInfo) {
        return crudDelegate.readAll(offset, limit, uriInfo);
    }
    
    /**
     * Create a new teacher-school-association entity.
     * 
     * @param newEntityBody
     *            entity data
     * @param uriInfo
     * @return Response with a status of CREATED and a Location header set pointing to where the new
     *         entity lives
     * @response.representation.201.mediaType HTTP headers with a Created status code and a Location
     *                                        value.
     */
    @POST
    public Response create(final EntityBody newEntityBody, @Context final UriInfo uriInfo) {
        return crudDelegate.create(newEntityBody, uriInfo);
    }
    
    /**
     * Get a single teacher-school-association entity
     * 
     * @param id
     *            teacher-school-association id
     * @param expandDepth
     *            whether or not the full entity should be returned or just the link. Defaults to
     *            false
     * @param uriInfo
     * @return A single student entity
     * @response.representation.200.mediaType application/json
     * @response.representation.200.qname {http://www.w3.org/2001/XMLSchema}student
     */
    @GET
    @Path("{id}")
    public Response read(@PathParam("id") final String id,
            @QueryParam(ParameterConstants.EXPAND_DEPTH) @DefaultValue("false") final boolean expandDepth,
            @Context final UriInfo uriInfo) {
        return crudDelegate.read(id, expandDepth, uriInfo);
    }
    
    /**
     * Delete a teacher-school-association entity
     * 
     * @param typePath
     *            resourceUri of the entity
     * @param id
     *            id of the entity
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") final String id) {
        return crudDelegate.delete(id);
    }
    
    /**
     * Update an existing teacher-school-association entity.
     * 
     * @param typePath
     *            resourceUri for the entity
     * @param id
     *            id of the entity
     * @param newEntityBody
     *            entity data that will used to replace the existing entity data
     * @return Response with a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") final String id, final EntityBody newEntityBody) {
        return crudDelegate.update(id, newEntityBody);
    }
}
