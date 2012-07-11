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
    
    // Use this to check if we're in sandbox mode
    @Value("${sli.simple-idp.sandboxImpersonationEnabled}")
    protected boolean isSandboxImpersonationEnabled;

    @Value("${sli.simple-idp.sliAdminRealmName}")
    private String realm;

    @GET
    public Response readAll(
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {

        if (!hasRight()) {
            EntityBody body = new EntityBody();
            body.put("response", "You are not authorized to access this resource.");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }
        String tenant = SecurityUtil.getTenantId();
        List<String> edorgs = null;
        if (!this.isSandboxImpersonationEnabled && SecurityUtil.hasRight(Right.CRUD_LEA_ADMIN)) {
            edorgs = new ArrayList<String>();
            edorgs.add(SecurityUtil.getEdOrg());
        }
        List<User> users = ldapService.findUserByGroups(realm,
                RightToGroupMapper.getGroups(getRights(), this.isSandboxImpersonationEnabled), tenant, edorgs);
        return Response.status(Status.OK).entity(users).build();
    }

    private boolean hasRight() {
        if (this.isSandboxImpersonationEnabled) {
            if (!SecurityUtil.hasRight(Right.CRUD_SLC_OPERATOR) && !SecurityUtil.hasRight(Right.CRUD_SEA_ADMIN)) {
                return false;
            }
        } else {
            if (!SecurityUtil.hasRight(Right.CRUD_SLC_OPERATOR) && !SecurityUtil.hasRight(Right.CRUD_SEA_ADMIN)
                    && !SecurityUtil.hasRight(Right.CRUD_LEA_ADMIN)) {
                return false;
            }
        }
        return true;
    }
    
    private List<Right> getRights() {
        List<Right> rights = new ArrayList<Right>();
        if (SecurityUtil.hasRight(Right.CRUD_SLC_OPERATOR))
            rights.add(Right.CRUD_SLC_OPERATOR);
        if (SecurityUtil.hasRight(Right.CRUD_SEA_ADMIN))
            rights.add(Right.CRUD_SEA_ADMIN);
        if (!this.isSandboxImpersonationEnabled && SecurityUtil.hasRight(Right.CRUD_LEA_ADMIN))
            rights.add(Right.CRUD_LEA_ADMIN);
        if (this.isSandboxImpersonationEnabled && SecurityUtil.hasRight(Right.CRUD_APP_DEVELOPER))
            rights.add(Right.CRUD_APP_DEVELOPER);

        return rights;
    }

    /**
     * Map Right to Groups (LDAP's equivalence of Role)
     * 
     */
    
    // TODO need configured to be environment specific
    public static class RightToGroupMapper {
        
        private static Map<Right, List<String>> rightToGroupMap;

        public static List<String> getGroups(Right right, boolean isSandbox) {
            if (rightToGroupMap == null || rightToGroupMap.size() == 0) {
                init(isSandbox);
            }
            return rightToGroupMap.get(right);
        }
        
        public static List<String> getGroups(List<Right> rights, boolean isSandbox) {
            if (rights != null && rights.size() > 0) {
                List<String> combinedGroupNames = new ArrayList<String>();
                for (Right right : rights) {
                    List<String> groupNames = getGroups(right, isSandbox);
                    if (groupNames != null && groupNames.size() > 0) {
                        for (String groupName : groupNames) {
                            if (!combinedGroupNames.contains(groupName)) {
                                combinedGroupNames.add(groupName);
                            }
                        }
                    }
                }
                return combinedGroupNames;
            }
            return null;
        }

        private static void init(boolean isSandbox) {
            rightToGroupMap = new HashMap<Right, List<String>>();

            // define groups that CRUD_SLC_OPERATOR right can access
            List<String> slcoperatorGroups = new ArrayList<String>();
            slcoperatorGroups.add(RoleInitializer.SLC_OPERATOR);
            if (!isSandbox) {
                slcoperatorGroups.add(RoleInitializer.REALM_ADMINISTRATOR);
            }
            slcoperatorGroups.add(RoleInitializer.INGESTION_USER);
            rightToGroupMap.put(Right.CRUD_SLC_OPERATOR, slcoperatorGroups);

            // define groups that CRUD_SEA_ADMIN right can access
            List<String> seaadmiGroups = new ArrayList<String>();
            if (!isSandbox) {
                seaadmiGroups.add(RoleInitializer.SEA_ADMINISTRATOR);
                seaadmiGroups.add(RoleInitializer.REALM_ADMINISTRATOR);
            } else {
                seaadmiGroups.add(RoleInitializer.SANDBOX_ADMINISTRATOR);
            }
            seaadmiGroups.add(RoleInitializer.INGESTION_USER);
            rightToGroupMap.put(Right.CRUD_SEA_ADMIN, seaadmiGroups);

            // define groups that CRUD_LEA_ADMIN right can access for production env
            if (!isSandbox) {
                List<String> leaadminGroups = new ArrayList<String>();
                leaadminGroups.add(RoleInitializer.LEA_ADMINISTRATOR);
                leaadminGroups.add(RoleInitializer.REALM_ADMINISTRATOR);
                leaadminGroups.add(RoleInitializer.INGESTION_USER);
                rightToGroupMap.put(Right.CRUD_LEA_ADMIN, leaadminGroups);
            }
            
            // define groups that CRUD_APP_DEVELOPER right can access for sandbox env
            if (isSandbox) {
                List<String> appadminGroups = new ArrayList<String>();
                appadminGroups.add(RoleInitializer.APP_DEVELOPER);
                rightToGroupMap.put(Right.CRUD_APP_DEVELOPER, appadminGroups);
            }
        }
    }
}
