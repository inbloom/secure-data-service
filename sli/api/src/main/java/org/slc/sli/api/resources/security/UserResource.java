package org.slc.sli.api.resources.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.init.RoleInitializer;
import org.slc.sli.api.ldap.LdapService;
import org.slc.sli.api.ldap.User;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;

/**
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
    @Qualifier("validationRepo")
    private Repository<Entity> repo;


    @POST
    public final Response create(final User newUser, @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        assertEnabled();
        Response result = validateUserCreate(newUser, SecurityUtil.getTenantId());
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
    public final Response readAll(
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {

        assertEnabled();
        String tenant = SecurityUtil.getTenantId();

        Response result = validateAdminRights(SecurityUtil.getAllRights(), tenant);
        if (result != null) {
            return result;
        }

        Collection<String> edorgs = null;
        if (SecurityUtil.hasRole(RoleInitializer.LEA_ADMINISTRATOR)) {
            edorgs = new ArrayList<String>();
            edorgs.add(SecurityUtil.getEdOrg());
        }

        Collection<User> users = ldapService.findUsersByGroups(realm,
                RightToGroupMapper.getInstance().getGroups(SecurityUtil.getAllRights()), SecurityUtil.getTenantId(),
                edorgs);
        if (users != null && users.size() > 0) {
            for (User user : users) {
                user.setGroups((List<String>) (RoleToGroupMapper.getInstance().mapGroupToRoles(user.getGroups())));
            }
        }
        return Response.status(Status.OK).entity(users).build();
    }

    @PUT
    public final Response update(final User updateUser, @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        assertEnabled();
        Response result = validateUserUpdate(updateUser, SecurityUtil.getTenantId());
        if (result != null) {
            return result;
        }
        updateUser.setGroups((List<String>) (RoleToGroupMapper.getInstance().mapRoleToGroups(updateUser.getGroups())));
        ldapService.updateUser(realm, updateUser);
        return Response.status(Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("{uid}")
    public final synchronized Response delete(@PathParam("uid") final String uid, @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        assertEnabled();
        Response result = validateUserDelete(uid, SecurityUtil.getTenantId());
        if (result != null) {
            return result;
        }

        ldapService.removeUser(realm, uid);
        return Response.status(Status.OK).build();
    }

    private void assertEnabled() {
        if (!enableSamt) {
            throw new RuntimeException("This feature is currently disabled via configuration.");
        }
    }

    private Response validateUserCreate(User user, String tenant) {
        Response result = validateAdminRights(SecurityUtil.getAllRights(), tenant);
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

        return null;
    }

    private Response validateUserUpdate(User user, String tenant) {
        Response result = validateAdminRights(SecurityUtil.getAllRights(), tenant);
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

        result = validateCannotUpdateOwnsRoles(user);
        if (result != null) {
            return result;
        }

        return null;
    }

    private Response validateUserDelete(String uid, String tenant) {
        Response result = validateAdminRights(SecurityUtil.getAllRights(), tenant);
        if (result != null) {
            return result;
        }

        User userToDelete = ldapService.getUser(realm, uid);
        if (userToDelete == null) {
            EntityBody body = new EntityBody();
            body.put("response", "user with uid=" + uid + " does not exist");
            return Response.status(Status.NOT_FOUND).entity(body).build();
        }

        result = validateUserGroupsAllowed(getGroupsAllowed(), userToDelete.getGroups());
        if (result != null) {
            return result;
        }

        result = validateCannotOperateOnSelf(uid);
        if (result != null) {
            return result;
        }

        return null;
    }

    private Response validateCannotUpdateOwnsRoles(User user) {
        if (user.getUid().equals(SecurityUtil.getUid())) {
            User currentUser = ldapService.getUser(realm, SecurityUtil.getUid());
            if (!currentUser.getGroups().containsAll(RoleToGroupMapper.getInstance().mapRoleToGroups(user.getGroups()))
                    || !RoleToGroupMapper.getInstance().mapRoleToGroups(user.getGroups())
                            .containsAll(currentUser.getGroups())) {
                EntityBody body = new EntityBody();
                body.put("response", "cannot update own roles");
                return Response.status(Status.FORBIDDEN).entity(body).build();
            }
        }
        return null;
    }

    private Response validateCannotOperateOnSelf(String uid) {
        if (uid.equals(SecurityUtil.getUid())) {
            EntityBody body = new EntityBody();
            body.put("response", "not allowed execute this operation on self");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }
        return null;
    }

    /**
     * Check that the rights contains an admin right. If tenant is null, then also verify the user has operator level rights.
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
            error("Non-operator user {} has null tenant.  Giving up.", new Object[] { SecurityUtil.getUid() });
        }
        if (rightSet.isEmpty() || nullTenant) {
            EntityBody body = new EntityBody();
            body.put("response", "You are not authorized to access this resource.");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }
        return null;
    }

    private static Response composeBadDataResponse(String reason) {
        EntityBody body = new EntityBody();
        body.put("response", reason);
        return Response.status(Status.BAD_REQUEST).entity(body).build();
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
            if (SecurityUtil.getTenantId() != null && !SecurityUtil.getTenantId().equals(user.getTenant())) {
                return composeBadDataResponse("Tenant does not match logged in user's tenant");
            }
        } else if (user.getGroups().contains(RoleInitializer.SEA_ADMINISTRATOR)) {
            // tenant and edorg should not be null for SEA
            if (user.getTenant() == null || user.getEdorg() == null) {
                return composeBadDataResponse("Required tenant/edorg info is missing");
            }
            // if SEA creates SEA, tenant must match
            if (SecurityUtil.getTenantId() != null && !SecurityUtil.getTenantId().equals(user.getTenant())) {
                return composeBadDataResponse("Tenant does not match logged in user's tenant");
            }
        } else if (user.getGroups().contains(RoleInitializer.LEA_ADMINISTRATOR)) {
            // tenant and edorg should not be null for LEA
            if (user.getTenant() == null || user.getEdorg() == null) {
                return composeBadDataResponse("Required tenant/edorg info is missing");
            }
            // if SEA or LEA creates LEA, tenant must match
            if (SecurityUtil.getTenantId() != null && !SecurityUtil.getTenantId().equals(user.getTenant())) {
                return composeBadDataResponse("Tenant does not match logged in user's tenant");
            }
            // LEA's Ed-Org must already exist in the tenant
            String restrictByEdorg = null;
            if (isLeaAdmin()) {
                restrictByEdorg = SecurityUtil.getEdOrg();
            }
            Set<String> allowedEdorgs = getAllowedEdOrgsForLEA(user.getTenant(), restrictByEdorg);
            if (!allowedEdorgs.contains(user.getEdorg())) {
                return composeBadDataResponse("Invalid edorg");
            }
        } else {
            if (user.getTenant() == null) {
                return composeBadDataResponse("Required tenant info is missing");
            }
            if (SecurityUtil.getTenantId() != null && !SecurityUtil.getTenantId().equals(user.getTenant())) {
                return composeBadDataResponse("Tenant does not match logged in user's tenant");
            }
            // if prod mode
            if (SecurityUtil.hasRight(Right.CRUD_LEA_ADMIN) || SecurityUtil.hasRight(Right.CRUD_SEA_ADMIN)
                    || SecurityUtil.hasRight(Right.CRUD_SLC_OPERATOR)) {
                // Ed-Org must already exist in the tenant
                String restrictByEdorg = null;
                if (isLeaAdmin()) {
                    restrictByEdorg = SecurityUtil.getEdOrg();
                }
                Set<String> allowedEdorgs = getAllowedEdOrgsForLEA(user.getTenant(), restrictByEdorg);
                if (!allowedEdorgs.contains(user.getEdorg())) {
                    return composeBadDataResponse("Invalid edorg");
                }
            }
            // if
        }
        return null;
    }

    /*
     * Determines if current logged in user an LEA Admin.
     */
    private boolean isLeaAdmin() {
        // assumption: If the user is able to create LEAs, but not SEAs and OPs, then he is a LEA.
        // note: This is silly.
        Collection<String> roles = RoleToGroupMapper.getInstance().mapGroupToRoles(getGroupsAllowed());
        return roles.contains(RoleInitializer.LEA_ADMINISTRATOR) && !roles.contains(RoleInitializer.SEA_ADMINISTRATOR)
                && !roles.contains(RoleInitializer.SLC_OPERATOR);
    }

    /**
     * Returns a list of possible ed-orgs.
     * @param tenant Tenant in which the ed-orgs reside.
     * @param edOrg if provided, list will be restricted to that ed-org or lower.
     */
    private Set<String> getAllowedEdOrgsForLEA(String tenant, String edOrg) {
        NeutralQuery query = new NeutralQuery();

        if (SecurityUtil.getTenantId() == null && tenant != null) {
            query.addCriteria(new NeutralCriteria("metaData.tenantId", NeutralCriteria.OPERATOR_EQUAL, tenant, false));
        }

        if (edOrg != null) {
            NeutralQuery currentEdOrgQuery = new NeutralQuery(new NeutralCriteria("stateOrganizationId",
                    NeutralCriteria.OPERATOR_EQUAL, edOrg));
            Entity usersEdOrg = this.repo.findOne(EntityNames.EDUCATION_ORGANIZATION, currentEdOrgQuery);
            query.addCriteria(new NeutralCriteria("metaData.edOrgs", NeutralCriteria.OPERATOR_EQUAL, usersEdOrg
                    .getEntityId(), false));
        }

        Set<String> edOrgIds = new HashSet<String>();
        for (Entity e : this.repo.findAll(EntityNames.EDUCATION_ORGANIZATION, query)) {
            edOrgIds.add((String) e.getBody().get("stateOrganizationId"));
        }
        return edOrgIds;
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
        return RightToGroupMapper.getInstance().getGroups(SecurityUtil.getAllRights());
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

    static final class RoleToGroupMapper {
        private static final RoleToGroupMapper INSTANCE = new RoleToGroupMapper();

        public static RoleToGroupMapper getInstance() {
            return INSTANCE;
        }

        private final Map<String, String> ROLETOGROUPMAP;
        private final Map<String, String> GROUPTOROLEMAP;

        private RoleToGroupMapper() {
            ROLETOGROUPMAP = new HashMap<String, String>();
            GROUPTOROLEMAP = new HashMap<String, String>();

            ROLETOGROUPMAP.put(RoleInitializer.SLC_OPERATOR, "SLC Operator");
            ROLETOGROUPMAP.put(RoleInitializer.REALM_ADMINISTRATOR, "Realm Administrator");
            ROLETOGROUPMAP.put(RoleInitializer.SEA_ADMINISTRATOR, "SEA Administrator");
            ROLETOGROUPMAP.put(RoleInitializer.LEA_ADMINISTRATOR, "LEA Administrator");
            ROLETOGROUPMAP.put(RoleInitializer.APP_DEVELOPER, "application_developer");
            ROLETOGROUPMAP.put(RoleInitializer.INGESTION_USER, "ingestion_user");
            ROLETOGROUPMAP.put(RoleInitializer.SANDBOX_SLC_OPERATOR, "Sandbox SLC Operator");
            ROLETOGROUPMAP.put(RoleInitializer.SANDBOX_ADMINISTRATOR, "Sandbox Administrator");

            GROUPTOROLEMAP.put("SLC Operator", RoleInitializer.SLC_OPERATOR);
            GROUPTOROLEMAP.put("Realm Administrator", RoleInitializer.REALM_ADMINISTRATOR);
            GROUPTOROLEMAP.put("SEA Administrator", RoleInitializer.SEA_ADMINISTRATOR);
            GROUPTOROLEMAP.put("LEA Administrator", RoleInitializer.LEA_ADMINISTRATOR);
            GROUPTOROLEMAP.put("application_developer", RoleInitializer.APP_DEVELOPER);
            GROUPTOROLEMAP.put("ingestion_user", RoleInitializer.INGESTION_USER);
            GROUPTOROLEMAP.put("Sandbox SLC Operator", RoleInitializer.SANDBOX_SLC_OPERATOR);
            GROUPTOROLEMAP.put("Sandbox Administrator", RoleInitializer.SANDBOX_ADMINISTRATOR);
        }

        public Collection<String> mapRoleToGroups(Collection<String> roles) {
            Collection<String> groups = new ArrayList<String>();
            if (roles != null) {
                for (String role : roles) {
                    if (this.ROLETOGROUPMAP.containsKey(role)) {
                        groups.add(this.ROLETOGROUPMAP.get(role));
                    }
                }
            }
            return groups;
        }

        public Collection<String> mapGroupToRoles(Collection<String> groups) {
            Collection<String> roles = new ArrayList<String>();
            if (groups != null) {
                for (String group : groups) {
                    if (this.GROUPTOROLEMAP.containsKey(group)) {
                        roles.add(this.GROUPTOROLEMAP.get(group));
                    }
                }
            }
            return roles;
        }

        public String getRole(String group) {
            if (this.GROUPTOROLEMAP.containsKey(group)) {
                return this.GROUPTOROLEMAP.get(group);
            }
            return null;
        }

        public String getGroup(String role) {
            if (this.ROLETOGROUPMAP.containsKey(role)) {
                return this.ROLETOGROUPMAP.get(role);
            }
            return null;
        }
    }

}
