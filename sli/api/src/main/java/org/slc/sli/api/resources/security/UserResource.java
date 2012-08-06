package org.slc.sli.api.resources.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import org.slc.sli.api.init.RoleInitializer;
import org.slc.sli.api.ldap.LdapService;
import org.slc.sli.api.ldap.User;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.service.SuperAdminService;
import org.slc.sli.api.util.SecurityUtil.SecurityUtilProxy;
import org.slc.sli.domain.enums.Right;

/**
 * Resource for CRUDing Super Admin users (users that exist within the SLC realm).
 *
 * @author dliu
 *
 */

@Component
@Scope("request")
@Path("/users")
@Consumes({ MediaType.APPLICATION_JSON + ";charset=utf-8" })
@Produces({ Resource.JSON_MEDIA_TYPE + ";charset=utf-8" })
public class UserResource {

    @Autowired
    LdapService ldapService;

    @Value("${sli.simple-idp.sliAdminRealmName}")
    private String realm;

    @Value("${sli.feature.enableSamt:false}")
    private boolean enableSamt;

    @Autowired
    private SuperAdminService adminService;

    @Autowired
    private SecurityUtilProxy secUtil;

    @POST
    public final Response create(final User newUser) {
        assertEnabled();
        Response result = validateUserCreate(newUser, secUtil.getTenantId());
        if (result != null) {
            return result;
        }
        newUser.setGroups((List<String>) (RoleToGroupMapper.getInstance().mapRoleToGroups(newUser.getGroups())));
        try {
            ldapService.createUser(realm, newUser);
        } catch (NameAlreadyBoundException e) {
            return Response.status(Status.CONFLICT).build();
        }
        return Response.status(Status.CREATED).build();
    }

    @GET
    public final Response readAll() {

        assertEnabled();
        String tenant = secUtil.getTenantId();
        String edorg = secUtil.getEdOrg();
        String myUid = secUtil.getUid();

        Response result = validateAdminRights(secUtil.getAllRights(), tenant);
        if (result != null) {
            return result;
        }

        Collection<String> edorgs = null;
        if (secUtil.hasRole(RoleInitializer.LEA_ADMINISTRATOR)) {
            edorgs = new ArrayList<String>();
            edorgs.addAll(adminService.getAllowedEdOrgs(tenant, edorg));
        }

        Collection<User> users = ldapService.findUsersByGroups(realm,
                RightToGroupMapper.getInstance().getGroups(secUtil.getAllRights()), secUtil.getTenantId(), edorgs);

        // filtering peer LEAs
        Collection<User> filteredUsers = new LinkedList<User>();
        boolean isLea = isLeaAdmin();

        if (users != null && users.size() > 0) {
            for (User user : users) {
                user.setGroups((List<String>) (RoleToGroupMapper.getInstance().mapGroupToRoles(user.getGroups())));
                if (myUid.equals(user.getUid()) || !(isLea && isUserLeaAdmin(user) && user.getEdorg().equals(edorg))) {
                    filteredUsers.add(user);
                }

            }
        }

        return Response.status(Status.OK).entity(filteredUsers).build();
    }

    @PUT
    public final Response update(final User updateUser) {
        assertEnabled();
        Response result = validateUserUpdate(updateUser, secUtil.getTenantId());
        if (result != null) {
            return result;
        }
        updateUser.setGroups((List<String>) (RoleToGroupMapper.getInstance().mapRoleToGroups(updateUser.getGroups())));
        ldapService.updateUser(realm, updateUser);
        return Response.status(Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("{uid}")
    public final Response delete(@PathParam("uid") final String uid) {
        assertEnabled();

        Response result = validateUserDelete(uid, secUtil.getTenantId());
        if (result != null) {
            return result;
        }

        ldapService.removeUser(realm, uid);
        return Response.status(Status.NO_CONTENT).build();
    }

    /**
     * Finds and returns teh stateOrganizationId for all Ed-Orgs the Admin user has access to.
     * For an SEA Admin, this would be all Ed-Orgs in a tenant.
     * For an LEA Admin, this would be their current Ed-Org or lower in the hierarchy.
     */
    @GET
    @Path("edorgs")
    public final Response getEdOrgs() {
        assertEnabled();

        String tenant = secUtil.getTenantId();

        Response result = validateAdminRights(secUtil.getAllRights(), tenant);
        if (result != null) {
            return result;
        }

        if (tenant == null) {
            List<String> edorgs = new LinkedList<String>();
            return Response.status(Status.OK).entity(edorgs).build();
        }

        String restrictByEdOrg = this.isLeaAdmin() ? secUtil.getEdOrg() : null;
        ArrayList<String> edOrgs = new ArrayList<String>(adminService.getAllowedEdOrgs(tenant, restrictByEdOrg));
        // Sort the edorgs so our response is stable and not super annoying to end users.
        Collections.sort(edOrgs);

        return Response.status(Status.OK).entity(edOrgs).build();
    }

    private void assertEnabled() {
        if (!enableSamt) {
            throw new RuntimeException("This feature is currently disabled via configuration.");
        }
    }

    private Response validateUserCreate(User user, String tenant) {
        Response result = validateAdminRights(secUtil.getAllRights(), tenant);
        if (result != null) {
            return result;
        }

        result = validateUserGroupsAllowed(RoleToGroupMapper.getInstance().mapGroupToRoles(getGroupsAllowed()),
                user.getGroups());
        if (result != null) {
            return result;
        }

        result = validateAtMostOneAdminRole(user.getGroups());
        if (result != null) {
            return result;
        }

        result = validateTenantAndEdorg(RoleToGroupMapper.getInstance().mapGroupToRoles(getGroupsAllowed()), user);
        if (result != null) {
            return result;
        }

        if (user.getEmail() == null) {
            return badRequest("No email address");
        } else if (user.getFirstName() == null) {
            return badRequest("No first name");
        } else if (user.getLastName() == null) {
            return badRequest("No last name");
        } else if (user.getUid() == null) {
            return badRequest("No uid");
        }

        return null;
    }

    private Response badRequest(String message) {
        return Response.status(Status.BAD_REQUEST).entity(message).build();
    }

    private Response validateUserUpdate(User user, String tenant) {
        // create and update shared the same validators
        Response result = validateUserCreate(user, tenant);
        if (result != null) {
            return result;
        }

        result = validateLEACannotUpdateOwnsRolesTenancyEdorg(user);
        if (result != null) {
            return result;
        }

        result = validateCannotOperateOnPeerLEA(user, secUtil.getEdOrg());
        if(result != null) {
            return result;
        }

        return null;
    }

    private Response validateUserDelete(String uid, String tenant) {
        Response result = validateAdminRights(secUtil.getAllRights(), tenant);
        if (result != null) {
            return result;
        }

        User userToDelete = ldapService.getUser(realm, uid);
        if (userToDelete == null) {
            // remove the user from group even user doesnt exist for slc operator
            if (secUtil.hasRole(RoleInitializer.SLC_OPERATOR)) {
                ldapService.removeUser(realm, uid);
            }
            EntityBody body = new EntityBody();
            body.put("response", "user with uid=" + uid + " does not exist");
            return Response.status(Status.NOT_FOUND).entity(body).build();
        }

        // allow the slc operator to remove the user even the user has no groups
        if (secUtil.hasRole(RoleInitializer.SLC_OPERATOR) && userToDelete.getGroups() == null) {
            result = null;
        } else {
            result = validateUserGroupsAllowed(getGroupsAllowed(), userToDelete.getGroups());
        }
        if (result != null) {
            return result;
        }

        result = validateCannotOperateOnSelf(uid);
        if (result != null) {
            return result;
        }

        result = validateCannotOperateOnPeerLEA(userToDelete, secUtil.getEdOrg());
        if (result != null) {
            return result;
        }

        return null;
    }

    private Response validateCannotOperateOnPeerLEA(User userToModify, String adminEdOrg) {
        if (isLeaAdmin() && isUserLeaAdmin(userToModify)
                && !userToModify.getUid().equals(secUtil.getUid())) { //only blocking peer LEA

            if (userToModify.getEdorg() != null && userToModify.getEdorg().equals(adminEdOrg)) {
                EntityBody body = new EntityBody();
                body.put("response", "not allowed to execute this operation on peer admin users");
                return Response.status(Status.FORBIDDEN).entity(body).build();
            }
        }

        return null;
    }

    private Response validateLEACannotUpdateOwnsRolesTenancyEdorg(User user) {

        if (isLeaAdmin() && user.getUid().equals(secUtil.getUid())) {
            User currentUser = ldapService.getUser(realm, secUtil.getUid());

            String error = null;
            if (!currentUser.getGroups().containsAll(RoleToGroupMapper.getInstance().mapRoleToGroups(user.getGroups()))
                    || !RoleToGroupMapper.getInstance().mapRoleToGroups(user.getGroups())
                            .containsAll(currentUser.getGroups())) {
                error = "cannot update own roles";
            }

            if (!currentUser.getTenant().equals(user.getTenant())) {
                error = "cannot update own tenancy";
            }

            if (!currentUser.getEdorg().equals(user.getEdorg())) {
                error = "cannot update own edorg";
            }

            if (error != null) {
                EntityBody body = new EntityBody();
                body.put("response", error);
                return Response.status(Status.FORBIDDEN).entity(body).build();
            }
        }

        return null;
    }

    private Response validateCannotOperateOnSelf(String uid) {
        if (uid.equals(secUtil.getUid())) {
            EntityBody body = new EntityBody();
            body.put("response", "not allowed execute this operation on self");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }
        return null;
    }

    /**
     * Check that the rights contains an admin right. If tenant is null, then also verify the user
     * has operator level rights.
     *
     * @param rights
     * @return null if success, response with error otherwise
     */
    Response validateAdminRights(Collection<GrantedAuthority> rights, String tenant) {
        Collection<GrantedAuthority> rightSet = new HashSet<GrantedAuthority>(rights);
        rightSet.retainAll(Arrays.asList(Right.ALL_ADMIN_CRUD_RIGHTS));
        boolean nullTenant = (tenant == null && !(rights.contains(Right.CRUD_SANDBOX_SLC_OPERATOR) || rights
                .contains(Right.CRUD_SLC_OPERATOR)));
        if (nullTenant) {
            error("Non-operator user {} has null tenant.  Giving up.", new Object[] { secUtil.getUid() });
            throw new RuntimeException("Non-operator user " + secUtil.getUid() + " has null tenant.  Giving up.");
        }
        if (rightSet.isEmpty() || nullTenant) {
            EntityBody body = new EntityBody();
            body.put("response", "You are not authorized to access this resource.");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }
        return null;
    }

    private static Response composeBadDataResponse(String reason) {
        return composeResponse(reason, Status.BAD_REQUEST);
    }

    private static Response composeForbiddenResponse(String reason) {
        return composeResponse(reason, Status.FORBIDDEN);
    }

    private static Response composeResponse(String reason, Status status) {
        EntityBody body = new EntityBody();
        body.put("response", reason);
        return Response.status(status).entity(body).build();
    }

    private Response validateTenantAndEdorg(Collection<String> groupsAllowed, User user) {
        if ("".equals(user.getTenant())) {
            user.setTenant(null);
        }

        if ("".equals(user.getEdorg())) {
            user.setEdorg(null);
        }

        if (user.getGroups().contains(RoleInitializer.SLC_OPERATOR)
                || user.getGroups().contains(RoleInitializer.SANDBOX_SLC_OPERATOR)) {
            // tenant and edorg should be null for SLC OP
            if (user.getTenant() != null || user.getEdorg() != null) {
                return composeBadDataResponse("SLC Operator can not have tenant/edorg");
            }
        } else if (user.getGroups().contains(RoleInitializer.SANDBOX_ADMINISTRATOR)) {
            // tenant should not be null of SB Admin
            if (user.getTenant() == null) {
                return composeBadDataResponse("Required tenant info is missing");
            }
            // if SB Admin creates another SB Admin, then tenant must match existing tenant
            if (secUtil.getTenantId() != null && !secUtil.getTenantId().equals(user.getTenant())) {
                return composeBadDataResponse("Tenant does not match logged in user's tenant");
            }
        } else if (user.getGroups().contains(RoleInitializer.SEA_ADMINISTRATOR)) {
            // tenant and edorg should not be null for SEA
            if (user.getTenant() == null || user.getEdorg() == null) {
                return composeBadDataResponse("Required tenant/edorg info is missing");
            }
            // if SEA creates SEA, tenant must match
            if (secUtil.getTenantId() != null && !secUtil.getTenantId().equals(user.getTenant())) {
                return composeBadDataResponse("Tenant does not match logged in user's tenant");
            }
        } else if (user.getGroups().contains(RoleInitializer.LEA_ADMINISTRATOR)) {
            // tenant and edorg should not be null for LEA
            if (user.getTenant() == null || user.getEdorg() == null) {
                return composeBadDataResponse("Required tenant/edorg info is missing");
            }
            // if SEA or LEA creates LEA, tenant must match
            if (secUtil.getTenantId() != null && !secUtil.getTenantId().equals(user.getTenant())) {
                return composeBadDataResponse("Tenant does not match logged in user's tenant");
            }
            // LEA's Ed-Org must already exist in the tenant
            String restrictByEdorg = null;
            if (isLeaAdmin()) {
                restrictByEdorg = secUtil.getEdOrg();
                // restrict peer level LEA
                if (restrictByEdorg.equals(user.getEdorg()) && !user.getUid().equals(secUtil.getUid())) {
                    return composeForbiddenResponse("Can not operate on peer level LEA");
                }
            }
            Set<String> allowedEdorgs = adminService.getAllowedEdOrgs(user.getTenant(), restrictByEdorg);
            if (!allowedEdorgs.contains(user.getEdorg())) {
                return composeBadDataResponse("Invalid edorg");
            }
        } else {
            if (user.getTenant() == null) {
                return composeBadDataResponse("Required tenant info is missing");
            }
            if (secUtil.getTenantId() != null && !secUtil.getTenantId().equals(user.getTenant())) {
                return composeBadDataResponse("Tenant does not match logged in user's tenant");
            }
            // if prod mode
            if (secUtil.hasRight(Right.CRUD_LEA_ADMIN) || secUtil.hasRight(Right.CRUD_SEA_ADMIN)
                    || secUtil.hasRight(Right.CRUD_SLC_OPERATOR)) {
                // Ed-Org must already exist in the tenant
                String restrictByEdorg = null;
                if (isLeaAdmin()) {
                    restrictByEdorg = secUtil.getEdOrg();
                }
                Set<String> allowedEdorgs = adminService.getAllowedEdOrgs(user.getTenant(), restrictByEdorg);
                if (!allowedEdorgs.contains(user.getEdorg())) {
                    return composeBadDataResponse("Invalid edorg");
                }
            }
        }
        return null;
    }

    /*
     * Determines if current logged in user an LEA Admin.
     */
    private boolean isLeaAdmin() {
        return secUtil.hasRole(RoleInitializer.LEA_ADMINISTRATOR);
    }

    /*
     * Determines if the specified user has LEA permission
     */
    private boolean isUserLeaAdmin(User user) {
        return user.getGroups().contains(RoleInitializer.LEA_ADMINISTRATOR);
    }

    private static final String[] ADMIN_ROLES = new String[] { RoleInitializer.LEA_ADMINISTRATOR,
            RoleInitializer.SEA_ADMINISTRATOR, RoleInitializer.SLC_OPERATOR, RoleInitializer.SANDBOX_SLC_OPERATOR,
            RoleInitializer.SANDBOX_ADMINISTRATOR };

    static Response validateAtMostOneAdminRole(final Collection<String> roles) {
        Collection<String> adminRoles = new ArrayList<String>(Arrays.asList(ADMIN_ROLES));
        adminRoles.retainAll(roles);
        if (adminRoles.size() > 1) {
            EntityBody body = new EntityBody();
            body.put("response", "You cannot assign more than one admin role to a user");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }
        return null;
    }

    static Response validateUserGroupsAllowed(final Collection<String> groupsAllowed,
            final Collection<String> userGroups) {
        if (!groupsAllowed.containsAll(userGroups)) {
            EntityBody body = new EntityBody();
            body.put("response", "You are not allowed to access this resource");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }
        return null;
    }

    private Collection<String> getGroupsAllowed() {
        return RightToGroupMapper.getInstance().getGroups(secUtil.getAllRights());
    }

    /**
     * Map Right to Groups (LDAP's equivalence of Role)
     *
     */
    static final class RightToGroupMapper {
        private static final String[] GROUPS_ALL_ADMINS_ALLOW_TO_READ = new String[] { RoleInitializer.INGESTION_USER };
        private static final String[] GROUPS_ONLY_PROD_ADMINS_ALLOW_TO_READ = new String[] { RoleInitializer.REALM_ADMINISTRATOR };
        private static final String[] GROUPS_ONLY_SANDBOX_ADMINS_ALLOW_TO_READ = new String[] { RoleInitializer.APP_DEVELOPER };

        private final Map<GrantedAuthority, Collection<String>> rightToRoleMap;
        private static final RightToGroupMapper INSTANCE = new RightToGroupMapper();

        private RightToGroupMapper() {
            rightToRoleMap = new HashMap<GrantedAuthority, Collection<String>>();
            Collection<GrantedAuthority> prodAdminCrudRights = Arrays.asList(Right.PROD_ADMIN_CRUD_RIGHTS);
            Collection<GrantedAuthority> sandboxAdminCrudRights = Arrays.asList(Right.SANDBOX_ADMIN_CRUD_RIGHTS);
            Collection<GrantedAuthority> allAdminCrudRights = Arrays.asList(Right.ALL_ADMIN_CRUD_RIGHTS);

            for (GrantedAuthority right : Right.ALL_ADMIN_CRUD_RIGHTS) {
                Collection<String> groups = new HashSet<String>();
                if (allAdminCrudRights.contains(right)) {
                    groups.addAll(Arrays.asList(GROUPS_ALL_ADMINS_ALLOW_TO_READ));
                }
                if (prodAdminCrudRights.contains(right)) {
                    groups.addAll(Arrays.asList(GROUPS_ONLY_PROD_ADMINS_ALLOW_TO_READ));
                }
                if (sandboxAdminCrudRights.contains(right)) {
                    groups.addAll(Arrays.asList(GROUPS_ONLY_SANDBOX_ADMINS_ALLOW_TO_READ));
                }

                if (right instanceof Right) {
                    switch ((Right) right) {
                        case CRUD_SLC_OPERATOR:
                            groups.add(RoleInitializer.SLC_OPERATOR);
                            break;
                        case CRUD_SEA_ADMIN:
                            groups.add(RoleInitializer.SEA_ADMINISTRATOR);
                            break;
                        case CRUD_LEA_ADMIN:
                            groups.add(RoleInitializer.LEA_ADMINISTRATOR);
                            break;
                        case CRUD_SANDBOX_SLC_OPERATOR:
                            groups.add(RoleInitializer.SANDBOX_SLC_OPERATOR);
                            break;
                        case CRUD_SANDBOX_ADMIN:
                            groups.add(RoleInitializer.SANDBOX_ADMINISTRATOR);
                            break;
                    }
                }

                rightToRoleMap.put(right, groups);
            }
        }

        /**
         * Given the user's rights, determine which groups the user can have access to.
         *
         * @param rights
         * @return the groups (AKA roles) the user has access to.
         */
        public Collection<String> getGroups(Collection<GrantedAuthority> rights) {
            Collection<String> groups = new HashSet<String>();
            for (GrantedAuthority right : rights) {
                Collection<String> currentGroups = RoleToGroupMapper.getInstance().mapRoleToGroups(
                        rightToRoleMap.get(right));
                if (currentGroups != null) {
                    groups.addAll(currentGroups);
                }
            }
            return groups;
        }

        public static RightToGroupMapper getInstance() {
            return INSTANCE;
        }
    }

    /**
     * Mappers for Roles to Groups
     *
     * @author nbrown
     *
     */
    static final class RoleToGroupMapper {
        private static final RoleToGroupMapper INSTANCE = new RoleToGroupMapper();

        public static RoleToGroupMapper getInstance() {
            return INSTANCE;
        }

        private final Map<String, String> roleToGroupMap;
        private final Map<String, String> groupToRoleMap;

        private RoleToGroupMapper() {
            roleToGroupMap = new HashMap<String, String>();
            groupToRoleMap = new HashMap<String, String>();

            roleToGroupMap.put(RoleInitializer.SLC_OPERATOR, "SLC Operator");
            roleToGroupMap.put(RoleInitializer.REALM_ADMINISTRATOR, "Realm Administrator");
            roleToGroupMap.put(RoleInitializer.SEA_ADMINISTRATOR, "SEA Administrator");
            roleToGroupMap.put(RoleInitializer.LEA_ADMINISTRATOR, "LEA Administrator");
            roleToGroupMap.put(RoleInitializer.APP_DEVELOPER, "application_developer");
            roleToGroupMap.put(RoleInitializer.INGESTION_USER, "ingestion_user");
            roleToGroupMap.put(RoleInitializer.SANDBOX_SLC_OPERATOR, "Sandbox SLC Operator");
            roleToGroupMap.put(RoleInitializer.SANDBOX_ADMINISTRATOR, "Sandbox Administrator");

            groupToRoleMap.put("SLC Operator", RoleInitializer.SLC_OPERATOR);
            groupToRoleMap.put("Realm Administrator", RoleInitializer.REALM_ADMINISTRATOR);
            groupToRoleMap.put("SEA Administrator", RoleInitializer.SEA_ADMINISTRATOR);
            groupToRoleMap.put("LEA Administrator", RoleInitializer.LEA_ADMINISTRATOR);
            groupToRoleMap.put("application_developer", RoleInitializer.APP_DEVELOPER);
            groupToRoleMap.put("ingestion_user", RoleInitializer.INGESTION_USER);
            groupToRoleMap.put("Sandbox SLC Operator", RoleInitializer.SANDBOX_SLC_OPERATOR);
            groupToRoleMap.put("Sandbox Administrator", RoleInitializer.SANDBOX_ADMINISTRATOR);
        }

        public Collection<String> mapRoleToGroups(Collection<String> roles) {
            Collection<String> groups = new ArrayList<String>();
            if (roles != null) {
                for (String role : roles) {
                    if (this.roleToGroupMap.containsKey(role)) {
                        groups.add(this.roleToGroupMap.get(role));
                    }
                }
            }
            return groups;
        }

        public Collection<String> mapGroupToRoles(Collection<String> groups) {
            Collection<String> roles = new ArrayList<String>();
            if (groups != null) {
                for (String group : groups) {
                    if (this.groupToRoleMap.containsKey(group)) {
                        roles.add(this.groupToRoleMap.get(group));
                    }
                }
            }
            return roles;
        }

        public String getRole(String group) {
            if (this.groupToRoleMap.containsKey(group)) {
                return this.groupToRoleMap.get(group);
            }
            return null;
        }

        public String getGroup(String role) {
            if (this.roleToGroupMap.containsKey(role)) {
                return this.roleToGroupMap.get(role);
            }
            return null;
        }
    }

    public void setSecurityUtilProxy(SecurityUtilProxy proxy) {
        this.secUtil = proxy;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public void setEnableSamt(boolean enableSamt) {
        this.enableSamt = enableSamt;
    }
}
