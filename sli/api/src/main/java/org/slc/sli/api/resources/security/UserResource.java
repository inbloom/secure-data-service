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

    @GET
    public final Response readAll(
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {

        if (!isAdministrator(SecurityUtil.getAllRights())) {
            EntityBody body = new EntityBody();
            body.put("response", "You are not authorized to access this resource.");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }

        String tenant = null;
        Collection<String> edorgs = null;
        Collection<String> groupsAllowed = RightToGroupMapper.getInstance().getGroups(SecurityUtil.getAllRights());
        if (!groupsAllowed.contains(RoleInitializer.SLC_OPERATOR)) {
            if (groupsAllowed.contains(RoleInitializer.SEA_ADMINISTRATOR)) {
                tenant = SecurityUtil.getTenantId();
            } else if (groupsAllowed.contains(RoleInitializer.LEA_ADMINISTRATOR)) {
                tenant = SecurityUtil.getTenantId();
                edorgs = new ArrayList<String>();
                edorgs.add(SecurityUtil.getEdOrg());
            }
        }

        Collection<User> users = ldapService.findUsersByGroups(realm, RightToGroupMapper.getInstance().getGroups(SecurityUtil.getAllRights()), tenant, edorgs);
        return Response.status(Status.OK).entity(users).build();
    }

    @POST
    public final Response create(final User newUser, @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        if (!isAdministrator(SecurityUtil.getAllRights())) {
            EntityBody body = new EntityBody();
            body.put("response", "You are not authorized to create this resource.");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }

        debug("creating a user {}", newUser.toString());

        Collection<String> groupsAllowed = RightToGroupMapper.getInstance().getGroups(SecurityUtil.getAllRights());
        Collection<String> newUserGroups = new HashSet<String>(newUser.getGroups());
        if (!groupsAllowed.containsAll(newUserGroups)) {
            newUserGroups.removeAll(groupsAllowed);
            debug("the following groups are not allowed to be assigned: {}", User.printGroup(new ArrayList<String>(newUserGroups)));
            EntityBody body = new EntityBody();
            body.put("response", "You are not allowed to create this resource");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }

        Collection<String> adminRoles = Arrays.asList(RoleInitializer.ADMIN_ROLES);
        adminRoles.retainAll(newUserGroups);
        if (adminRoles.size() > 1) {
            EntityBody body = new EntityBody();
            body.put("response", "You cannot assign more than one admin role to a user");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }

        // Production
        if (newUserGroups.contains(RoleInitializer.SLC_OPERATOR)) {
            newUser.setTenant(null);
            newUser.setEdorg(null);
        } else if (groupsAllowed.contains(RoleInitializer.SLC_OPERATOR)) {
            if (newUser.getTenant() == null || newUser.getEdorg() == null) {
                EntityBody body = new EntityBody();
                body.put("response", "Only SLC Operator can have null for tenant/edorg");
                return Response.status(Status.FORBIDDEN).entity(body).build();
            }
        } else if (groupsAllowed.contains(RoleInitializer.SEA_ADMINISTRATOR)) {
            if (newUser.getEdorg() == null) {
                EntityBody body = new EntityBody();
                body.put("response", "SEA Administrators cannot assign empty edorg");
                return Response.status(Status.FORBIDDEN).entity(body).build();
            } else {
                newUser.setTenant(SecurityUtil.getTenantId());
            }
        } else if (groupsAllowed.contains(RoleInitializer.LEA_ADMINISTRATOR)) {
            newUser.setTenant(SecurityUtil.getTenantId());
            newUser.setEdorg(SecurityUtil.getEdOrg());
        }

        // Sandbox
        if (newUserGroups.contains(RoleInitializer.SANDBOX_SLC_OPERATOR)) {
            newUser.setTenant(null);
            newUser.setEdorg(null);
        } else if (groupsAllowed.contains(RoleInitializer.SANDBOX_SLC_OPERATOR)) {
            if (newUser.getTenant() == null || newUser.getEdorg() == null) {
                EntityBody body = new EntityBody();
                body.put("response", "Only Sandbox SLC Operator can have null for tenant/edorg");
                return Response.status(Status.FORBIDDEN).entity(body).build();
            }
        } else if (groupsAllowed.contains(RoleInitializer.SANDBOX_ADMINISTRATOR)) {
            if (newUser.getEdorg() == null) {
                EntityBody body = new EntityBody();
                body.put("response", "Sandbox Administrator cannot assign empty edorg");
                return Response.status(Status.FORBIDDEN).entity(body).build();
            } else {
                newUser.setTenant(SecurityUtil.getTenantId());
            }
        }

        try {
            ldapService.createUser(realm, newUser);
        } catch (NameAlreadyBoundException e) {
            return Response.status(Status.CONFLICT).build();
        }
        return Response.status(Status.CREATED).build();

    }

    @PUT
    public final Response update(final User updateUser, @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        if (!isAdministrator(SecurityUtil.getAllRights())) {
            EntityBody body = new EntityBody();
            body.put("response", "You are not authorized to update this resource.");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }

        Collection<String> groupsAllowed = RightToGroupMapper.getInstance().getGroups(SecurityUtil.getAllRights());
        Collection<String> updateUserGroups = new HashSet<String>(updateUser.getGroups());
        if (!groupsAllowed.containsAll(updateUserGroups)) {
            updateUserGroups.removeAll(groupsAllowed);
            debug("the following groups are not allowed to be assigned: {}", User.printGroup(new ArrayList<String>(updateUserGroups)));
            EntityBody body = new EntityBody();
            body.put("response", "You are not allowed to update this resource");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        } else {
            ldapService.updateUser(realm, updateUser);
            return Response.status(Status.OK).build();
        }
    }

    @DELETE
    public final synchronized Response delete(final String uid, final HttpHeaders headers, final UriInfo uriInfo) {
        if (!isAdministrator(SecurityUtil.getAllRights())) {
            EntityBody body = new EntityBody();
            body.put("response", "You are not authorized to delete this resource.");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        }

        User userToDelete = ldapService.getUser(realm, uid);
        Collection<String> groupsAllowed = RightToGroupMapper.getInstance().getGroups(SecurityUtil.getAllRights());
        Collection<String> deleteUserGroups = new HashSet<String>(userToDelete.getGroups());
        if (!groupsAllowed.containsAll(deleteUserGroups)) {
            deleteUserGroups.removeAll(groupsAllowed);
            debug("the user we are trying to delete is more powerful than the user executing the deletion: {}", User.printGroup(new ArrayList<String>(deleteUserGroups)));
            EntityBody body = new EntityBody();
            body.put("response", "You are not allowed to delete this resource");
            return Response.status(Status.FORBIDDEN).entity(body).build();
        } else {
            Collection<String> groups = userToDelete.getGroups();
            if (groups.contains(RoleInitializer.SLC_OPERATOR)) {
                Collection<User> users = ldapService.findUsersByGroups(realm, Arrays.asList(new String[] {RoleInitializer.SLC_OPERATOR}));
                if (users.size() <= 1) {
                    EntityBody body = new EntityBody();
                    body.put("response", "You are not allowed to delete the last SLC Operator");
                    return Response.status(Status.FORBIDDEN).entity(body).build();
                }
            }
            if (groups.contains(RoleInitializer.SEA_ADMINISTRATOR)) {
                Collection<User> users = ldapService.findUsersByGroups(realm, Arrays.asList(new String[] {RoleInitializer.SEA_ADMINISTRATOR}), SecurityUtil.getTenantId());
                if (users.size() <= 1) {
                    EntityBody body = new EntityBody();
                    body.put("response", "You are not allowed to delete the last SEA Administrator with tenant id = " + SecurityUtil.getTenantId());
                    return Response.status(Status.FORBIDDEN).entity(body).build();
                }
            }
            ldapService.removeUser(realm, uid);
            return Response.status(Status.OK).build();
        }
    }

    /**
     * Given a collection of rights, determine whether or not this user is an administrator.
     * @param rights
     * @return true if user is an administrator, false otherwise
     */
    private boolean isAdministrator(final Collection<GrantedAuthority> rights) {
        Collection<GrantedAuthority> rightSet = new HashSet<GrantedAuthority>(rights);
        rightSet.retainAll(Arrays.asList(Right.ALL_ADMIN_CRUD_RIGHTS));
        return !rightSet.isEmpty();
    }

    /**
     * Map Right to Groups (LDAP's equivalence of Role)
     *
     */
    private static final class RightToGroupMapper {
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
