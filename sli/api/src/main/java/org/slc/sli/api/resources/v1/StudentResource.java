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

@Path(PathConstants.V1 + "/" + PathConstants.STUDENTS)
@Component
@Scope("request")
@Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
public class StudentResource implements CrudEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(StudentResource.class);
    
    private static final String RESOURCE_NAME_STUDENTS = "students";
    private final CrudEndpoint crudDelegate;
    
    @Autowired
    public StudentResource(final EntityDefinitionStore entityDefs) {
        crudDelegate = new DefaultCrudEndpoint(entityDefs, RESOURCE_NAME_STUDENTS, LOGGER);
    }
    
    /**
     * Returns all Student entities.
     * 
     * @param uriInfo
     *            The URI context.
     */
    @GET
    public Response readAll(@QueryParam(ParameterConstants.OFFSET) @DefaultValue("0") final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue("50") final int limit, @Context final UriInfo uriInfo) {
        return crudDelegate.readAll(offset, limit, uriInfo);
    }
    
    /**
     * Get a single student entity
     * 
     * @param idList
     *            school id
     * @param skip
     *            number of results to skip
     * @param max
     *            maximum number of results to return
     * @param fullEntities
     *            whether or not the full entity should be returned or just the link. Defaults to
     *            false
     * @param uriInfo
     * @return A single school entity
     * @response.representation.200.mediaType application/json
     * @response.representation.200.qname {http://www.w3.org/2001/XMLSchema}school
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_ID + "}")
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    public Response read(@PathParam(ParameterConstants.STUDENT_ID) final String idList,
            @QueryParam(ParameterConstants.EXPAND_DEPTH) @DefaultValue("false") final boolean fullEntities,
            @Context final UriInfo uriInfo) {
        return crudDelegate.read(idList, fullEntities, uriInfo);
    }
    
    /**
     * Returns all the student-school-associations in the context of the specified student.
     * 
     * @param id
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_ID + "}/" + PathConstants.STUDENT_SCHOOL_ASSOCIATIONS)
    public Response getStudentSchoolAssociations(@PathParam(ParameterConstants.STUDENT_ID) final String id,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue("0") final int skip,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue("50") final int max,
            @QueryParam(ParameterConstants.EXPAND_DEPTH) @DefaultValue("false") final boolean fullEntities,
            @Context final UriInfo uriInfo) {
        return null;
        /*
         * return handle(RESOURCE_NAME_STUDENTS, entityDefs, new ResourceLogic() {
         * 
         * @Override
         * public Response run(final EntityDefinition entityDef) {
         * // FIXME: Why do we effectively read twice? (The isOfType is an exists test).
         * if (entityDef.isOfType(id)) {
         * final EntityBody entityBody = entityDef.getService().get(id);
         * entityBody.put(ResourceConstants.LINKS, getLinks(uriInfo, entityDef, id, entityBody,
         * entityDefs));
         * return Response.ok(entityBody).build();
         * } else if (entityDef instanceof AssociationDefinition) {
         * AssociationDefinition associationDefinition = (AssociationDefinition) entityDef;
         * Iterable<String> associationIds = null;
         * 
         * boolean checkAgainstSourceEntity = associationDefinition.getSourceEntity().isOfType(id);
         * boolean checkAgainstTargetEntity = associationDefinition.getTargetEntity().isOfType(id);
         * 
         * if (checkAgainstSourceEntity && checkAgainstTargetEntity) {
         * associationIds = associationDefinition.getService().getAssociationsFor(id, skip, max,
         * uriInfo.getRequestUri().getQuery());
         * } else if (checkAgainstSourceEntity) {
         * associationIds = associationDefinition.getService().getAssociationsWith(id, skip, max,
         * uriInfo.getRequestUri().getQuery());
         * } else if (checkAgainstTargetEntity) {
         * associationIds = associationDefinition.getService().getAssociationsTo(id, skip, max,
         * uriInfo.getRequestUri().getQuery());
         * } else {
         * return Response.status(Status.NOT_FOUND).build();
         * }
         * 
         * if (fullEntities) {
         * return Response.ok(getFullEntities(associationIds, entityDef)).build();
         * } else {
         * CollectionResponse collection = getShortEntities(uriInfo, entityDef, associationIds);
         * return Response.ok(collection).build();
         * }
         * }
         * return Response.status(Status.NOT_FOUND).build();
         * }
         * });
         */
    }
    
    /**
     * Create a new student resource.
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
     * Delete a student resource.
     * 
     * @param typePath
     *            resourceUri of the entity
     * @param id
     *            id of the entity
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @DELETE
    @Path("{" + ParameterConstants.STUDENT_ID + "}")
    public Response delete(@PathParam(ParameterConstants.STUDENT_ID) final String id) {
        return crudDelegate.delete(id);
    }
    
    /**
     * Update an existing student resource.
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
    @Path("{" + ParameterConstants.STUDENT_ID + "}")
    public Response update(@PathParam(ParameterConstants.STUDENT_ID) final String id, final EntityBody newEntityBody) {
        return crudDelegate.update(id, newEntityBody);
    }
}
