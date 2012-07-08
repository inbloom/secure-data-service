package org.slc.sli.api.resources.security;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.ldap.LdapService;
import org.slc.sli.api.resources.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author dliu
 * 
 */

@Component
@Scope("request")
@Path("/adminAccounts")
@Produces({ Resource.JSON_MEDIA_TYPE + ";charset=utf-8" })
public class AdminAccountResource {

    @Autowired
    LdapService ldapservice;
    
    @SuppressWarnings("rawtypes")
    @GET
    public Response getUsers(
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        // TODO
        Response resp = null;
        return resp;
    }

}
