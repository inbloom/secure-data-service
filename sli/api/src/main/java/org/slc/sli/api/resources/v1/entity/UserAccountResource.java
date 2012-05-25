package org.slc.sli.api.resources.v1.entity;

import java.util.HashMap;
import java.util.Map;

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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.DefaultCrudEndpoint;
import org.slc.sli.common.constants.ResourceNames;
import org.slc.sli.common.constants.v1.ParameterConstants;
import org.slc.sli.common.constants.v1.PathConstants;

/**
 * Represents list of $$userAccounts$$ (developer/vendor) and status of the
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

    //Use this to check if we're in sandbox mode
    @Value("${sli.simple-idp.sandboxImpersonationEnabled}")
    protected boolean isSandboxImpersonationEnabled;

    @Value("${sli.useraccount.maximum}")
    private int maximumNumberOfUserAccounts;

    @Autowired
    public UserAccountResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.USER_ACCOUNTS);
    }

    /**
     * Returns all $$userAccounts$$ entities for which the logged in User has permission and context.
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
     * Create a new $$userAccounts$$ entity.
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
        if (isSandboxImpersonationEnabled && maximumNumberOfUserAccounts >= 0) {
            long count = count(ResourceNames.USER_ACCOUNTS);
            if (count >= maximumNumberOfUserAccounts) {
                return Response.status(Status.PRECONDITION_FAILED).entity(createMaximumReachedObject(count)).build();
            }
        }
        return super.create(newEntityBody, headers, uriInfo);
    }

    private Map<String, String> createMaximumReachedObject(long count) {
        Map<String, String> returnObject = new HashMap<String, String>();
        returnObject.put("canCreate", "" + (!isSandboxImpersonationEnabled || maximumNumberOfUserAccounts < 0  || count < maximumNumberOfUserAccounts));
        return returnObject;
    }

    /**
     * Check whether or not we can create more user accounts.
     *
     * @return result
     */
    @GET
    @Path("createCheck")
    public Response createCheck() {
        return Response.status(Status.OK).entity(createMaximumReachedObject(count(ResourceNames.USER_ACCOUNTS))).build();
    }

    /**
     * Put data into the waitingListUserAccount collection.
     *
     * @param newEntityBody
     *          Entity Data
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result
     */
    @POST
    @Path("createWaitingListUser")
    public Response createWaitingListUser(final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.create(ResourceNames.WAITING_LIST_USER_ACCOUNTS, newEntityBody, headers, uriInfo);
    }

    /**
     * Get a single $$userAccounts$$ entity
     *
     * @param userAccountId
     *            The Id of the $$userAccounts$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A single $$userAccounts$$ entity
     */
    @Override
    @GET
    @Path("{" + ParameterConstants.USER_ACCOUNT_ID + "}")
    public Response read(@PathParam(ParameterConstants.USER_ACCOUNT_ID) final String userAccountId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(userAccountId, headers, uriInfo);
    }

    /**
     * Delete a $$userAccounts$$ entity
     *
     * @param userAccountId
     *            The Id of the $$userAccounts$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @Override
    @DELETE
    @Path("{" + ParameterConstants.USER_ACCOUNT_ID + "}")
    public Response delete(@PathParam(ParameterConstants.USER_ACCOUNT_ID) final String userAccountId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.delete(userAccountId, headers, uriInfo);
    }

    /**
     * Update an existing $$userAccounts$$ entity.
     *
     * @param userAccountId
     *            The id of the $$userAccounts$$.
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
    @Path("{" + ParameterConstants.USER_ACCOUNT_ID + "}")
    public Response update(@PathParam(ParameterConstants.USER_ACCOUNT_ID) final String userAccountId,
            final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.update(userAccountId, newEntityBody, headers, uriInfo);
    }
}
