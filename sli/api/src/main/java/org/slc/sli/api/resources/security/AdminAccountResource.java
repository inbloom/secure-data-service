package org.slc.sli.api.resources.security;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.ldap.LdapService;
import org.slc.sli.api.ldap.User;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.enums.Right;
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
    LdapService ldapService;
    
    @GET
    public Response getUsers(
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        
        // TODO add business logic to determine accessible admin accounts based on user rights
        Right requiredRight = Right.ADMIN_ACCESS;

        if (!SecurityUtil.hasRight(requiredRight)) {
            EntityBody body = new EntityBody();
            body.put("response", "You are not authorized to access this resource.");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }
        String[] groups = new String[] { "SLC Operator", "SEA Administrator", "LEA Administrator" };
        List<User> users = ldapService.findUserByGroups("SLIAdmin", Arrays.asList(groups));
        return Response.status(Status.OK).entity(users).build();
    }

}
