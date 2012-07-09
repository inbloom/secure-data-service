package org.slc.sli.api.resources.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.slc.sli.api.init.RoleInitializer;
import org.slc.sli.api.ldap.LdapService;
import org.slc.sli.api.ldap.User;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author dliu
 * 
 */

@Component
@Scope("request")
@Path("/users")
@Produces({ Resource.JSON_MEDIA_TYPE + ";charset=utf-8" })
public class UserResource {

    @Autowired
    LdapService ldapService;

    @Value("${sli.simple-idp.sliAdminRealmName}")
    private String realm;

    @GET
    public Response readAll(
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {

        // TODO add business logic to determine accessible admin accounts based on user rights

        if (!hasRight()) {
            EntityBody body = new EntityBody();
            body.put("response", "You are not authorized to access this resource.");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }
        String tenant = SecurityUtil.getTenantId();
        List<User> users = ldapService.findUserByGroups(realm, RightToGroupMapper.getGroups(getRight()), tenant);
        return Response.status(Status.OK).entity(users).build();
    }
    
    private boolean hasRight() {
        if (!SecurityUtil.hasRight(Right.CRUD_SLC_OPERATOR) && !SecurityUtil.hasRight(Right.CRUD_SEA_ADMIN)
                && !SecurityUtil.hasRight(Right.CRUD_LEA_ADMIN)) {
            return false;
        }
        return true;
    }
    
    private Right getRight() {
        if (SecurityUtil.hasRight(Right.CRUD_SLC_OPERATOR))
            return Right.CRUD_SLC_OPERATOR;
        else if (SecurityUtil.hasRight(Right.CRUD_SEA_ADMIN))
            return Right.CRUD_SEA_ADMIN;
        else if (SecurityUtil.hasRight(Right.CRUD_LEA_ADMIN)) {
            return Right.CRUD_LEA_ADMIN;
        }
        return null;
    }

    public static class RightToGroupMapper {

        private static Map<Right, List<String>> rightToGroupMap;
        
        public static List<String> getGroups(Right right) {
            if (rightToGroupMap == null || rightToGroupMap.size() == 0) {
                init();
            }
            return rightToGroupMap.get(right);
        }
        
        private static void init() {
            rightToGroupMap = new HashMap<Right, List<String>>();
            // define groups that CRUD_SLC_OPERATOR right can access
            List<String> slcoperator_groups=new ArrayList<String>();
            slcoperator_groups.add(RoleInitializer.SLC_OPERATOR);
            slcoperator_groups.add(RoleInitializer.SEA_ADMINISTRATOR);
            slcoperator_groups.add(RoleInitializer.LEA_ADMINISTRATOR);
            slcoperator_groups.add(RoleInitializer.REALM_ADMINISTRATOR);
            slcoperator_groups.add(RoleInitializer.INGESTION_USER);
            rightToGroupMap.put(Right.CRUD_SLC_OPERATOR, slcoperator_groups);
            
            // define groups that CRUD_SEA_ADMIN right can access
            List<String> seaadmin_groups = new ArrayList<String>();
            seaadmin_groups.add(RoleInitializer.SEA_ADMINISTRATOR);
            seaadmin_groups.add(RoleInitializer.LEA_ADMINISTRATOR);
            seaadmin_groups.add(RoleInitializer.REALM_ADMINISTRATOR);
            seaadmin_groups.add(RoleInitializer.INGESTION_USER);
            rightToGroupMap.put(Right.CRUD_SEA_ADMIN, seaadmin_groups);
            
            // define groups that CRUD_LEA_ADMIN right can access
            List<String> leaadmin_groups = new ArrayList<String>();
            leaadmin_groups.add(RoleInitializer.LEA_ADMINISTRATOR);
            leaadmin_groups.add(RoleInitializer.REALM_ADMINISTRATOR);
            leaadmin_groups.add(RoleInitializer.INGESTION_USER);
            rightToGroupMap.put(Right.CRUD_LEA_ADMIN, leaadmin_groups);
        }
    }
}
