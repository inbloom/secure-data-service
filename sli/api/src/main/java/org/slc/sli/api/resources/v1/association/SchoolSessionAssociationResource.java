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
import org.slc.sli.common.constants.ResourceNames;
import org.slc.sli.common.constants.v1.ParameterConstants;
import org.slc.sli.common.constants.v1.PathConstants;

/**
 * Represents the link between a school and a session of instruction. A school will likely
 * be associated to multiple sessions over its operation and sessions can be shared by
 * multiple schools (perhaps in the same district).
 *
 * For more information, see the schema for $$SchoolSessionAssociation$$ resources.
 *
 * @author kmyers
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.SCHOOL_SESSION_ASSOCIATIONS)
@Component
@Scope("request")
public class SchoolSessionAssociationResource extends DefaultCrudEndpoint {

    @Autowired
    public SchoolSessionAssociationResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.SCHOOL_SESSION_ASSOCIATIONS);
    }

    /**
     * Returns the requested collection of resource representations.
     */
    @Override
    @GET
    public Response readAll(@QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.readAll(offset, limit, headers, uriInfo);
    }

    /**
     * Creates a new resource using the given resource data.
     */
    @Override
    @POST
    public Response create(final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.create(newEntityBody, headers, uriInfo);
    }

    /**
     * Returns the specified resource representation(s).
     */
    @Override
    @GET
    @Path("{" + ParameterConstants.SCHOOL_SESSION_ASSOCIATION_ID + "}")
    public Response read(@PathParam(ParameterConstants.SCHOOL_SESSION_ASSOCIATION_ID) final String schoolSessionAssociationId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(schoolSessionAssociationId, headers, uriInfo);
    }

    /**
     * Deletes the specified resource.
     */
    @Override
    @DELETE
    @Path("{" + ParameterConstants.SCHOOL_SESSION_ASSOCIATION_ID + "}")
    public Response delete(@PathParam(ParameterConstants.SCHOOL_SESSION_ASSOCIATION_ID) final String schoolSessionAssociationId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.delete(schoolSessionAssociationId, headers, uriInfo);
    }

    /**
     * Updates the specified resource using the given resource data.
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
     * Returns the requested collection of resources that are associated with the specified resource.
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
     * Returns the requested collection of resources that are associated with the specified resource.
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
