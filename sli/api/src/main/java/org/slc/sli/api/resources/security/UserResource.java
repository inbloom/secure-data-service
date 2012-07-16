package org.slc.sli.api.resources.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.init.RoleInitializer;
import org.slc.sli.api.ldap.LdapService;
import org.slc.sli.api.ldap.User;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.util.SecurityUtil;
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

    @POST
    public final Response create(final User newUser, @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        Collection<String> groupsAllowed = RightToGroupMapper.getInstance().getGroups(SecurityUtil.getAllRights());
        Collection<String> newUserGroups = new HashSet<String>(newUser.getGroups());

        Response result = validateUserCreateOrUpdate(groupsAllowed, newUserGroups, newUser);
        if (result != null) {
            return result;
        }

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

        Response result = validAdminRights(SecurityUtil.getAllRights());
        if (result != null) {
            return result;
        }

        Collection<String> edorgs = null;
        if (SecurityUtil.hasRole(RoleInitializer.LEA_ADMINISTRATOR)) {
            edorgs = new ArrayList<String>();
            edorgs.add(SecurityUtil.getEdOrg());
        }

        Collection<User> users = ldapService.findUsersByGroups(realm, RightToGroupMapper.getInstance().getGroups(SecurityUtil.getAllRights()), SecurityUtil.getTenantId(), edorgs);
        return Response.status(Status.OK).entity(users).build();
    }

    @PUT
    public final Response update(final User updateUser, @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        Collection<String> groupsAllowed = RightToGroupMapper.getInstance().getGroups(SecurityUtil.getAllRights());
        Collection<String> updateUserGroups = new HashSet<String>(updateUser.getGroups());

        Response result = validateUserCreateOrUpdate(groupsAllowed, updateUserGroups, updateUser);
        if (result != null) {
            return result;
        }
        result = validateCannotUpdateOwnsRoles(updateUser);
        if (result != null) {
            return result;
        }

        ldapService.updateUser(realm, updateUser);
        return Response.status(Status.OK).build();
    }

    @DELETE
    public final synchronized Response delete(final String uid, final HttpHeaders headers, final UriInfo uriInfo) {
        Response result = validAdminRights(SecurityUtil.getAllRights());
        if (result != null) {
            return result;
        }

        User userToDelete = ldapService.getUser(realm, uid);
        Collection<String> groupsAllowed = RightToGroupMapper.getInstance().getGroups(SecurityUtil.getAllRights());
        result = validateUserGroupsAllowed(groupsAllowed, userToDelete.getGroups());
        if (result != null) {
            return result;
        }

        result = validateCannotOperateOfSelf(uid);
        if (result != null) {
            return result;
        }

        ldapService.removeUser(realm, uid);
        return Response.status(Status.OK).build();
    }

    private Response validateCannotUpdateOwnsRoles(User user) {
        if (user.getUid().equals(SecurityUtil.getUid())) {
            User currentUser = ldapService.getUser(realm, SecurityUtil.getUid());
            if (!currentUser.getGroups().containsAll(user.getGroups()) || !user.getGroups().containsAll(currentUser.getGroups())) {
                EntityBody body = new EntityBody();
                body.put("response", "you cannot change your own roles");
                return Response.status(Status.FORBIDDEN).entity(body).build();
            }
        }
        return null;
    }

    private Response validateCannotOperateOfSelf(String uid) {
        if (uid.equals(SecurityUtil.getUid())) {
            EntityBody body = new EntityBody();
            body.put("response", "You are not allowed to delete yourself");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }
        return null;
    }

    /**
     * Check that the rights contains an admin right.
     * @param rights
     * @return null if success, response with error otherwise
     */
    static Response validAdminRights(Collection<GrantedAuthority> rights) {
        if (!isAdministrator(rights)) {
            EntityBody body = new EntityBody();
            body.put("response", "You are not authorized to access this resource.");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }
        return null;
    }

    private Response validateTenantAndEdorg(Collection<String> userGroups, Collection<String> groupsAllowed, User user) {
        // Production
        if (userGroups.contains(RoleInitializer.SLC_OPERATOR)) {
            user.setTenant(null);
            user.setEdorg(null);
        } else if (groupsAllowed.contains(RoleInitializer.SLC_OPERATOR)) {
            if (user.getTenant() == null || user.getEdorg() == null) {
                EntityBody body = new EntityBody();
                body.put("response", "Only SLC Operator can have null for tenant/edorg");
                return Response.status(Status.FORBIDDEN).entity(body).build();
            }
        } else if (groupsAllowed.contains(RoleInitializer.SEA_ADMINISTRATOR)) {
            if (user.getEdorg() == null) {
                EntityBody body = new EntityBody();
                body.put("response", "SEA Administrators cannot assign empty edorg");
                return Response.status(Status.FORBIDDEN).entity(body).build();
            } else {
                user.setTenant(SecurityUtil.getTenantId());
            }
        } else if (groupsAllowed.contains(RoleInitializer.LEA_ADMINISTRATOR)) {
            user.setTenant(SecurityUtil.getTenantId());
            user.setEdorg(SecurityUtil.getEdOrg());
        }

        // Sandbox
        if (userGroups.contains(RoleInitializer.SANDBOX_SLC_OPERATOR)) {
            user.setTenant(null);
            user.setEdorg(null);
        } else if (groupsAllowed.contains(RoleInitializer.SANDBOX_SLC_OPERATOR)) {
            if (user.getTenant() == null || user.getEdorg() == null) {
                EntityBody body = new EntityBody();
                body.put("response", "Only Sandbox SLC Operator can have null for tenant/edorg");
                return Response.status(Status.FORBIDDEN).entity(body).build();
            }
        } else if (groupsAllowed.contains(RoleInitializer.SANDBOX_ADMINISTRATOR)) {
            if (user.getEdorg() == null) {
                EntityBody body = new EntityBody();
                body.put("response", "Sandbox Administrator cannot assign empty edorg");
                return Response.status(Status.FORBIDDEN).entity(body).build();
            } else {
                user.setTenant(SecurityUtil.getTenantId());
            }
        }
        return null;
    }

    private Response validateAtMostOneAdminRole(final Collection<String> groups) {
        Collection<String> adminRoles = Arrays.asList(RoleInitializer.ADMIN_ROLES);
        adminRoles.retainAll(groups);
        if (adminRoles.size() > 1) {
            EntityBody body = new EntityBody();
            body.put("response", "You cannot assign more than one admin role to a user");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }
        return null;
    }

    static Response validateUserGroupsAllowed(final Collection<String> groupsAllowed, final Collection<String> userGroups) {
        if (!groupsAllowed.containsAll(userGroups)) {
            EntityBody body = new EntityBody();
            body.put("response", "You are not allowed to access this resource");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }
        return null;
    }

    private Response validateUserCreateOrUpdate(Collection<String> groupsAllowed, Collection<String> userGroups, User user) {
        Response result = validAdminRights(SecurityUtil.getAllRights());
        if (result != null) {
            return result;
        }

        result = validateUserGroupsAllowed(groupsAllowed, userGroups);
        if (result != null) {
            return result;
        }

        result = validateAtMostOneAdminRole(userGroups);
        if (result != null) {
            return result;
        }

        result = validateTenantAndEdorg(userGroups, groupsAllowed, user);
        if (result != null) {
            return result;
        }
        return null;
    }

    /**
     * Given a collection of rights, determine whether or not this user is an administrator.
     * @param rights
     * @return true if user is an administrator, false otherwise
     */
    private static boolean isAdministrator(final Collection<GrantedAuthority> rights) {
        Collection<GrantedAuthority> rightSet = new HashSet<GrantedAuthority>(rights);
        rightSet.retainAll(Arrays.asList(Right.ALL_ADMIN_CRUD_RIGHTS));
        return !rightSet.isEmpty();
    }

    /**
     * Map Right to Groups (LDAP's equivalence of Role)
     *
     */
    static final class RightToGroupMapper {
        private static final String[] GROUPS_ALL_ADMINS_ALLOW_TO_READ = new String[] {RoleInitializer.INGESTION_USER};
        private static final String[] GROUPS_ONLY_PROD_ADMINS_ALLOW_TO_READ = new String[] {RoleInitializer.REALM_ADMINISTRATOR};
        private static final String[] GROUPS_ONLY_SANDBOX_ADMINS_ALLOW_TO_READ = new String[] {RoleInitializer.APP_DEVELOPER};

        private final Map<GrantedAuthority, Collection<String>> rightToGroupMap;
        private static final RightToGroupMapper INSTANCE = new RightToGroupMapper();

        private RightToGroupMapper() {
            rightToGroupMap = new HashMap<GrantedAuthority, Collection<String>>();
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
                    switch((Right) right) {
                    case CRUD_SLC_OPERATOR: groups.add(RoleInitializer.SLC_OPERATOR); break;
                    case CRUD_SEA_ADMIN: groups.add(RoleInitializer.SEA_ADMINISTRATOR); break;
                    case CRUD_LEA_ADMIN: groups.add(RoleInitializer.LEA_ADMINISTRATOR); break;
                    case CRUD_SANDBOX_SLC_OPERATOR: groups.add(RoleInitializer.SANDBOX_SLC_OPERATOR); break;
                    case CRUD_SANDBOX_ADMIN: groups.add(RoleInitializer.SANDBOX_ADMINISTRATOR); break;
                    }
                }

                rightToGroupMap.put(right, groups);
            }
        }

        /**
         * Given the user's rights, determine which groups the user can have access to.
         * @param rights
         * @return the groups (AKA roles) the user has access to.
         */
        public Collection<String> getGroups(Collection<GrantedAuthority> rights) {
            Collection<String> groups = new HashSet<String>();
            for (GrantedAuthority right : rights) {
                Collection<String> currentGroups = rightToGroupMap.get(right);
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
}
