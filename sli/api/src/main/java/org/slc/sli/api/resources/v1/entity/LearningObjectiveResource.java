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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.DefaultCrudEndpoint;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.common.constants.ResourceNames;
import org.slc.sli.common.constants.v1.ParameterConstants;
import org.slc.sli.common.constants.v1.PathConstants;

/**
 * Prototype new api end points and versioning
 * 
 * @author dliu
 * 
 */
@Path(PathConstants.V1 + "/" + PathConstants.LEARNING_OBJECTIVES)
@Component
@Scope("request")
@Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON, MediaType.APPLICATION_XML })
public class LearningObjectiveResource extends DefaultCrudEndpoint {

    @Autowired
    public LearningObjectiveResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.LEARNINGOBJECTIVES);
    }

    /**
     * Returns all $$learningObjectives$$ entities for which the logged in User has permission and
     * context.
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
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON, MediaType.APPLICATION_XML })
    @GET
    public Response readAll(
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.readAll(offset, limit, headers, uriInfo);
    }

    /**
     * Create a new $$learningObjectives$$ entity.
     * 
     * @param newEntityBody
     *            entity data
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     * @response.param {@name Location} {@style header} {@type
     *                 {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI where the created
     *                 item is accessible.}
     */
    @Override
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    public Response create(final EntityBody newEntityBody, @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.create(newEntityBody, headers, uriInfo);
    }

    /**
     * Get a single $$learningObjectives$$ entity
     * 
     * @param learningObjectiveId
     *            The Id of the $$learningObjectives$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A single learningObjective entity
     */
    @Override
    @GET
    @Path("{" + ParameterConstants.LEARNINGOBJECTIVE_ID + "}")
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON, MediaType.APPLICATION_XML })
    public Response read(@PathParam(ParameterConstants.LEARNINGOBJECTIVE_ID) final String learningObjectiveId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(learningObjectiveId, headers, uriInfo);
    }

    /**
     * Get a collection of $$learningStandards$$ entities for which the logged in user has
     * permission and context and directly referenced by learningObjective entity that specified by
     * learningObjective Id
     * 
     * @param learningObjectiveId
     *            The Id of the $$learningObjectives$$.
     * @param offset
     *            Index of the first result to return
     * @param limit
     *            Maximum number of results to return.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A collection of learningStardard entities
     */
    @GET
    @Path("{" + ParameterConstants.LEARNINGOBJECTIVE_ID + "}" + "/" + PathConstants.LEARNING_STANDARDS)
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON, MediaType.APPLICATION_XML })
    public Response getLearningStandards(
            @PathParam(ParameterConstants.LEARNINGOBJECTIVE_ID) final String learningObjectiveId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.LEARNINGOBJECTIVES, "_id", learningObjectiveId,
                ParameterConstants.LEARNING_STANDARDS, ResourceNames.LEARNINGSTANDARDS, headers, uriInfo);
    }

    /**
     * Get a single $$learningObjectives$$ entity is the parent of this resource
     * 
     * @param learningObjectiveId
     *            The Id of the $$learningObjectives$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return the Response containing the parent entity
     */
    @GET
    @Path("{" + ParameterConstants.LEARNINGOBJECTIVE_ID + "}" + "/" + PathConstants.PARENT_LEARNING_OBJECTIVES)
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON, MediaType.APPLICATION_XML })
    public Response getParentLearningObjective(
            @PathParam(ParameterConstants.LEARNINGOBJECTIVE_ID) final String learningObjectiveId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.LEARNINGOBJECTIVES, "_id", learningObjectiveId,
                ParameterConstants.PARENT_LEARNING_OBJECTIVE, ResourceNames.LEARNINGOBJECTIVES, headers, uriInfo);
    }

    /**
     * Get all the $$learningObjectives$$ entities that are children of this resource
     * 
     * @param learningObjectiveId
     *            The Id of the $$learningObjectives$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return the Response contianing the children entities
     */
    @GET
    @Path("{" + ParameterConstants.LEARNINGOBJECTIVE_ID + "}" + "/" + PathConstants.CHILD_LEARNING_OBJECTIVES)
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON, MediaType.APPLICATION_XML })
    public Response getChildrenLearningObjective(
            @PathParam(ParameterConstants.LEARNINGOBJECTIVE_ID) final String learningObjectiveId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.LEARNINGOBJECTIVES, ParameterConstants.PARENT_LEARNING_OBJECTIVE,
                learningObjectiveId, headers, uriInfo);
    }

    /**
     * Delete a $$learningObjectives$$ entity
     * 
     * @param learningObjectiveId
     *            The Id of the $$learningObjectives$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @Override
    @DELETE
    @Path("{" + ParameterConstants.LEARNINGOBJECTIVE_ID + "}")
    public Response delete(@PathParam(ParameterConstants.LEARNINGOBJECTIVE_ID) final String learningObjectiveId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.delete(learningObjectiveId, headers, uriInfo);
    }

    /**
     * Update an existing $$learningObjectives$$ entity.
     * 
     * @param learningObjectiveId
     *            The id of the $$learningObjectives$$.
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
    @Path("{" + ParameterConstants.LEARNINGOBJECTIVE_ID + "}")
    public Response update(@PathParam(ParameterConstants.LEARNINGOBJECTIVE_ID) final String learningObjectiveId,
            final EntityBody newEntityBody, @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.update(learningObjectiveId, newEntityBody, headers, uriInfo);
    }

}
