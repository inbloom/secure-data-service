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
import org.slc.sli.common.constants.ResourceNames;
import org.slc.sli.common.constants.v1.ParameterConstants;
import org.slc.sli.common.constants.v1.PathConstants;

/**
 * Represents list of $$UserAccount$$ (developer/vendor) and status of the
 * account requested by them.
 *
 * @author jstokes
 * @author srichards
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.USER_ACCOUNTS)
@Component
@Scope("request")
public class UserAccountResource extends DefaultCrudEndpoint {

    @Autowired
    public UserAccountResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.USER_ACCOUNTS);
    }

    /**
     * Returns all $$UserAccount$$ entities for which the logged in User has permission and context.
     */
    @Override
    @GET
    public Response readAll(@QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.readAll(offset, limit, headers, uriInfo);
    }

    /**
     * Create a new $$UserAccount$$ entity.
     */
    @Override
    @POST
    public Response create(final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.create(newEntityBody, headers, uriInfo);
    }

    /**
     * Get a single $$UserAccount$$ entity
     */
    @Override
    @GET
    @Path("{" + ParameterConstants.USER_ACCOUNT_ID + "}")
    public Response read(@PathParam(ParameterConstants.USER_ACCOUNT_ID) final String userAccountId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(userAccountId, headers, uriInfo);
    }

    /**
     * Delete a $$UserAccount$$ entity
     */
    @Override
    @DELETE
    @Path("{" + ParameterConstants.USER_ACCOUNT_ID + "}")
    public Response delete(@PathParam(ParameterConstants.USER_ACCOUNT_ID) final String userAccountId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.delete(userAccountId, headers, uriInfo);
    }

    /**
     * Update an existing $$UserAccount$$ entity.
     */
    @Override
    @PUT
    @Path("{" + ParameterConstants.USER_ACCOUNT_ID + "}")
    public Response update(@PathParam(ParameterConstants.USER_ACCOUNT_ID) final String userAccountId,
            final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.update(userAccountId, newEntityBody, headers, uriInfo);
    }
}
