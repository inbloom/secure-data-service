/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slc.sli.api.init.RoleInitializer;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.security.RightsAllowed;
import org.slc.sli.api.security.SecurityEventBuilder;
import org.slc.sli.api.service.SuperAdminService;
import org.slc.sli.api.util.SecurityUtil.SecurityUtilProxy;
import org.slc.sli.common.ldap.LdapService;
import org.slc.sli.common.ldap.User;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * Resource for CRUDing Super Admin users (users that exist within the SLC realm).
 *
 * @author dliu
 *
 */

@Component
@Scope("request")
@Path("/users")
@Consumes({ HypermediaType.JSON + ";charset=utf-8" })
@Produces({ HypermediaType.JSON + ";charset=utf-8" })
public class UserResource {
    @Autowired
    private LdapService ldapService;

    @Value("${sli.simple-idp.sliAdminRealmName}")
    private String realm;

    @Autowired
    private SuperAdminService adminService;

    @Autowired
    private SecurityUtilProxy secUtil;

    @Autowired
    private SecurityEventBuilder securityEventBuilder;

    SecurityEvent createSecurityEvent(String logMessage, String tenantId, String edorg) {
        SecurityEvent securityEvent = securityEventBuilder.createSecurityEvent(UserResource.class.getName(), null, logMessage);
        securityEvent.setTenantId(tenantId);
        securityEvent.setTargetEdOrg(edorg);
        return securityEvent;
    }

    private static String rolesToString(User user) {
        StringBuilder buf = new StringBuilder();
        buf.append('[');
        if (user.getGroups().size() > 0) {
            buf.append(user.getGroups().get(0));
            for (int i = 1; i < user.getGroups().size(); i++) {
                buf.append(',').append(user.getGroups().get(i));
            }
        }
        buf.append(']');
        return buf.toString();
    }

    
    @POST
    @RightsAllowed({Right.CRUD_LEA_ADMIN, Right.CRUD_SEA_ADMIN, Right.CRUD_SLC_OPERATOR, Right.CRUD_SANDBOX_ADMIN, Right.CRUD_SANDBOX_SLC_OPERATOR })
    public final Response create(final User newUser) {
        Response result = validateUserCreate(newUser, secUtil.getTenantId());
        if (result != null) {
            return result;
        }
        newUser.setGroups((List<String>) (RoleToGroupMapper.getInstance().mapRoleToGroups(newUser.getGroups())));

        newUser.setStatus(User.Status.SUBMITTED);

        try {
            ldapService.createUser(realm, newUser);
        } catch (NameAlreadyBoundException e) {
            return Response.status(Status.CONFLICT).build();
        }

        audit(createSecurityEvent("Created user " + newUser.getUid() + " with roles " + rolesToString(newUser), newUser.getTenant(), newUser.getEdorg()));
        return Response.status(Status.CREATED).build();
    }

    @GET
    @RightsAllowed({Right.CRUD_LEA_ADMIN, Right.CRUD_SEA_ADMIN, Right.CRUD_SLC_OPERATOR, Right.CRUD_SANDBOX_ADMIN, Right.CRUD_SANDBOX_SLC_OPERATOR })
    public final Response readAll() {
        String tenant = secUtil.getTenantId();
        String edorg = secUtil.getEdOrg();

        Response result = validateAdminRights(secUtil.getAllRights(), tenant);
        if (result != null) {
            return result;
        }

        Collection<String> edorgs = null;
        if (secUtil.hasRole(RoleInitializer.LEA_ADMINISTRATOR)) {
            edorgs = new ArrayList<String>();
            edorgs.addAll(adminService.getAllowedEdOrgs(tenant, edorg));
        }

        Set<String> groupsToIgnore = new HashSet<String>();
        if (isLeaAdmin()) {
            groupsToIgnore.add(RoleInitializer.SLC_OPERATOR);
            groupsToIgnore.add(RoleInitializer.SEA_ADMINISTRATOR);
        }

        Collection<User> users = ldapService.findUsersByGroups(realm,
                RightToGroupMapper.getInstance().getGroups(secUtil.getAllRights()), groupsToIgnore, secUtil.getTenantId(), edorgs);

        if (users != null && users.size() > 0) {
            for (User user : users) {
                user.setGroups((List<String>) (RoleToGroupMapper.getInstance().mapGroupToRoles(user.getGroups())));
            }
        }

        return Response.status(Status.OK).entity(users).build();
    }

    @PUT
    @RightsAllowed({Right.CRUD_LEA_ADMIN, Right.CRUD_SEA_ADMIN, Right.CRUD_SLC_OPERATOR, Right.CRUD_SANDBOX_ADMIN, Right.CRUD_SANDBOX_SLC_OPERATOR })
    public final Response update(final User updateUser) {
        Response result = validateUserUpdate(updateUser, secUtil.getTenantId());
        if (result != null) {
            return result;
        }
        updateUser.setGroups((List<String>) (RoleToGroupMapper.getInstance().mapRoleToGroups(updateUser.getGroups())));
        ldapService.updateUser(realm, updateUser);

        audit(createSecurityEvent("Updated user " + updateUser.getUid() + " with roles " + rolesToString(updateUser), updateUser.getTenant(), updateUser.getEdorg()));
        return Response.status(Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("{uid}")
    @RightsAllowed({Right.CRUD_LEA_ADMIN, Right.CRUD_SEA_ADMIN, Right.CRUD_SLC_OPERATOR, Right.CRUD_SANDBOX_ADMIN, Right.CRUD_SANDBOX_SLC_OPERATOR })
    public final Response delete(@PathParam("uid") final String uid) {
        Response result = validateUserDelete(uid, secUtil.getTenantId());
        if (result != null) {
            return result;
        }

        User userToDelete = ldapService.getUser(realm, uid);
        ldapService.removeUser(realm, uid);

        audit(createSecurityEvent("Deleted user " + uid + " with roles " + rolesToString(userToDelete), userToDelete.getTenant(), userToDelete.getEdorg()));
        return Response.status(Status.NO_CONTENT).build();
    }

    /**
     * Finds and returns teh stateOrganizationId for all Ed-Orgs the Admin user has access to.
     * For an SEA Admin, this would be all Ed-Orgs in a tenant.
     * For an LEA Admin, this would be their current Ed-Org or lower in the hierarchy.
     */
    @GET
    @Path("edorgs")
    @RightsAllowed({Right.CRUD_LEA_ADMIN, Right.CRUD_SEA_ADMIN, Right.CRUD_SLC_OPERATOR, Right.CRUD_SANDBOX_ADMIN, Right.CRUD_SANDBOX_SLC_OPERATOR })
    public final Response getEdOrgs() {
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

        result = validateLEAExistForDistrict(user);
        if (result != null) {
            return result;
        }

        if (user.getEmail() == null) {
            return badRequest("No email address");
        } else if (user.getFullName() == null) {
            return badRequest("No name");
        } else if (user.getUid() == null) {
            return badRequest("No uid");
        }

        if (isSandboxAdministrator()) {
        	user.setVendor(secUtil.getVendor());
        }
        
        updateUnmodifiableFields(user, null);

        return null;
    }

    private Response validateLEAExistForDistrict(User user) {
        //only care about realm admin and ingestion user in prod mode
        if (isProdMode() && user.getGroups() != null) {
            Collection<String> adminRoles = Arrays.asList(ADMIN_ROLES);
            Set<String> userGroups = new HashSet<String>(user.getGroups());
            userGroups.retainAll(adminRoles);

            if (userGroups.size() != 0) {
                //user is an admin, not realm / ingestion user
                return null;
            }

            String userEdorg = user.getEdorg();
            Set<String> districtEdorgs = adminService.getAllowedEdOrgs(user.getTenant(), secUtil.getEdOrg(),
                    Arrays.asList(SuperAdminService.LOCAL_EDUCATION_AGENCY), true);

            if (districtEdorgs.contains(userEdorg)) {
                //only care about ingestion user and realm admin in district level
                boolean foundLEA = false;
                Collection<User> users = ldapService.findUsersByGroups(realm,
                        RightToGroupMapper.getInstance().getGroups(secUtil.getAllRights()), user.getTenant(), Arrays.asList(userEdorg));
                for (User userInLdap : users) {
                    if (isUserLeaAdmin(userInLdap)) {
                        foundLEA = true;
                        break;
                    }
                }

                if (!foundLEA) {
                    return composeBadDataResponse("Can not create Realm Administrator or Ingestion User because there is no LEA Administrator in " + user.getEdorg());
                }
            }
        }

        return null;
    }

    private Response badRequest(String message) {
        return Response.status(Status.BAD_REQUEST).entity(message).build();
    }

    private Response validateUserUpdate(User user, String tenant) {

        Response result = validateAdminRights(secUtil.getAllRights(), tenant);
        if (result != null) {
            return result;
        }

        User userInLdap = ldapService.getUser(realm, user.getUid());
        if (userInLdap == null) {
            return composeBadDataResponse("can not update user that does not exist");
        }

        updateUnmodifiableFields(user, userInLdap);

        if (userInLdap.getGroups() != null) {
            result = validateUserGroupsAllowed(getGroupsAllowed(), userInLdap.getGroups());
            if (result != null) {
                return result;
            }
        }

        result = validateAtMostOneAdminRole(user.getGroups());
        if (result != null) {
            return result;
        }

        result = validateTenantAndEdorg(RoleToGroupMapper.getInstance().mapGroupToRoles(getGroupsAllowed()), user);
        if (result != null) {
            return result;
        }

        result = validateCannotChangeSelfPrimaryAdminRole(user);
        if (result != null) {
            return result;
        }

        result = validateCannotRemoveLastSuperAdmin(user, userInLdap);
        if (result != null) {
            return result;
        }

        result = validateLEAExistForDistrict(user);
        if (result != null) {
            return result;
        }

        return null;
    }

    private void updateUnmodifiableFields(User user, User userInLdap) {
        //home directory should not be updatable from API
        user.setHomeDir(userInLdap == null ? "/dev/null" : userInLdap.getHomeDir());
    }

    private Response validateCannotRemoveLastSuperAdmin(User updateTo, User userInLdap) {

        if (userInLdap.getGroups() == null || userInLdap.getGroups().size() == 0) {
            //this user does not belong to any group, can't be the last super admin
            return null;
        }

        String groupToVerify = null;
        Collection<User> users = null;

        if (!isUserLeaAdmin(updateTo) && userInLdap.getGroups().contains(RoleToGroupMapper.GROUP_LEA_ADMINISTRATOR)) {

            groupToVerify = RoleToGroupMapper.GROUP_LEA_ADMINISTRATOR;
            List<String> edorgs = new ArrayList<String>();
            edorgs.add(userInLdap.getEdorg());

            users = ldapService.findUsersByGroups(realm,
                    RightToGroupMapper.getInstance().getGroups(secUtil.getAllRights()), userInLdap.getTenant(), edorgs);

        } else if (!updateTo.getGroups().contains(RoleInitializer.SEA_ADMINISTRATOR)
                && userInLdap.getGroups().contains(RoleToGroupMapper.GROUP_SEA_ADMINISTRATOR)) {

            groupToVerify = RoleToGroupMapper.GROUP_SEA_ADMINISTRATOR;
            users = ldapService.findUsersByGroups(realm,
                    RightToGroupMapper.getInstance().getGroups(secUtil.getAllRights()), userInLdap.getTenant());
        } else {
            //no need to check
            return null;
        }

        if (users == null) {
            return composeBadDataResponse("Can not remove the last administrator");
        }

        boolean otherSuperAdminExists = false;
        for (User user : users) {
            if (user.getGroups().contains(groupToVerify) && !user.getUid().equals(updateTo.getUid())) {
                otherSuperAdminExists = true;
                break;
            }
        }

        if (!otherSuperAdminExists) {
            return composeBadDataResponse("Can not remove the last administrator");
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
        if ((secUtil.hasRole(RoleInitializer.SANDBOX_SLC_OPERATOR) || secUtil.hasRole(RoleInitializer.SLC_OPERATOR))
                && userToDelete.getGroups() == null) {
            result = null;
        } else if (!(secUtil.hasRole(RoleInitializer.SANDBOX_SLC_OPERATOR) || secUtil
                .hasRole(RoleInitializer.SLC_OPERATOR)) && !(tenant.equals(userToDelete.getTenant()))) {
            // verify user tenant match up with userToDelete
            return composeForbiddenResponse("You are not authorized to access this resource.");
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

        return null;
    }


    private Response validateCannotChangeSelfPrimaryAdminRole(User user) {
        if (secUtil.getUid().equals(user.getUid())) {
            User currentUser = ldapService.getUser(realm, secUtil.getUid());

            Set<String> currentRoles = new HashSet<String>(Arrays.asList(ADMIN_ROLES));
            currentRoles.retainAll(currentUser.getGroups());
            Set<String> toBeAssignedRoles = new HashSet<String>(Arrays.asList(ADMIN_ROLES));
            toBeAssignedRoles.retainAll(user.getGroups());

            if (!currentRoles.equals(toBeAssignedRoles)) {
                return composeBadDataResponse("not allowed to change primary admin roles");
            }
        }

        return null;
    }


    private Response validateCannotOperateOnSelf(String uid) {
        if (uid.equals(secUtil.getUid())) {
            return composeBadDataResponse("not allowed to execute this operation on self");
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
            throw new IllegalArgumentException("Non-operator user " + secUtil.getUid() + " has null tenant.  Giving up.");
        }
        if (rightSet.isEmpty() || nullTenant) {
            return composeForbiddenResponse("You are not authorized to access this resource.");
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
            //explicitly set tenancy and edorg to empty string as they are already null
            user.setTenant("");
            user.setEdorg("");
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
            //can't enforce SLC operator create SEA in invalid edorg
        } else if (user.getGroups().contains(RoleInitializer.LEA_ADMINISTRATOR)) {
            // tenant and edorg should not be null for LEA
            if (user.getTenant() == null || user.getEdorg() == null) {
                return composeBadDataResponse("Required tenant/edorg info is missing");
            }
            // if SEA or LEA creates LEA, tenant must match
            if (secUtil.getTenantId() != null && !secUtil.getTenantId().equals(user.getTenant())) {
                return composeBadDataResponse("Tenant does not match logged in user's tenant");
            }

            //SLC can create LEA anywhere
            if (!getGroupsAllowed().contains(RoleToGroupMapper.GROUP_SLC_OPERATOR)) {
                // LEA's Ed-Org must already exist in the tenant
                String restrictByEdorg = null;
                if (isLeaAdmin()) {
                    restrictByEdorg = secUtil.getEdOrg();
                }
                //edorgs must already exist in db
                Set<String> allowedEdorgs = adminService.getAllowedEdOrgs(user.getTenant(), restrictByEdorg, Arrays.asList(SuperAdminService.LOCAL_EDUCATION_AGENCY), true);
                if (!allowedEdorgs.contains(user.getEdorg())) {
                    return composeBadDataResponse("Can not change or create LEA in this Education Organization");
                }
            }
        } else {
            if (user.getTenant() == null) {
                return composeBadDataResponse("Required tenant info is missing");
            }
            if (secUtil.getTenantId() != null && !secUtil.getTenantId().equals(user.getTenant())) {
                return composeBadDataResponse("Tenant does not match logged in user's tenant");
            }
            // if prod mode
            if (isProdMode()) {
                // Ed-Org must already exist in the tenant
                Set<String> allowedEdorgs = adminService.getAllowedEdOrgs(user.getTenant(), secUtil.getEdOrg());
                if (!allowedEdorgs.contains(user.getEdorg())) {
                    return composeBadDataResponse("Not allowed to create or modify users in this Education Organization");
                }
            }
        }
        return null;
    }

    /*
     *  Determine if this is in ProdMode
     */
    private boolean isProdMode() {
        return (secUtil.hasRight(Right.CRUD_LEA_ADMIN) || secUtil.hasRight(Right.CRUD_SEA_ADMIN)
                    || secUtil.hasRight(Right.CRUD_SLC_OPERATOR));
    }

    /*
     * Determines if current logged in user an LEA Admin.
     */
    private boolean isLeaAdmin() {
        return secUtil.hasRole(RoleInitializer.LEA_ADMINISTRATOR);
    }
    
    /*
     * Determine if current logged in user is a Sandbox Administrator
     */
    private boolean isSandboxAdministrator() {
    	return secUtil.hasRole(RoleInitializer.SANDBOX_ADMINISTRATOR);
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
            return composeForbiddenResponse("You cannot assign more than one admin role to a user");
        }
        return null;
    }

    static Response validateUserGroupsAllowed(final Collection<String> groupsAllowed,
            final Collection<String> userGroups) {
        if (!groupsAllowed.containsAll(userGroups)) {
            return composeForbiddenResponse("You are not allowed to access this resource");
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

        private final Map<Right, Collection<String>> rightToRoleMap;
        private static final RightToGroupMapper INSTANCE = new RightToGroupMapper();

        private RightToGroupMapper() {
            rightToRoleMap = new HashMap<Right, Collection<String>>();
            Collection<Right> prodAdminCrudRights = Arrays.asList(Right.PROD_ADMIN_CRUD_RIGHTS);
            Collection<Right> sandboxAdminCrudRights = Arrays.asList(Right.SANDBOX_ADMIN_CRUD_RIGHTS);
            Collection<Right> allAdminCrudRights = Arrays.asList(Right.ALL_ADMIN_CRUD_RIGHTS);

            for (Right right : Right.ALL_ADMIN_CRUD_RIGHTS) {
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

        //Groups in LDAP
        private static final String GROUP_SLC_OPERATOR = "SLC Operator";
        private static final String GROUP_LEA_ADMINISTRATOR = "LEA Administrator";
        private static final String GROUP_SEA_ADMINISTRATOR = "SEA Administrator";
        private static final String GROUP_REALM_ADMINISTRATOR = "Realm Administrator";
        private static final String GROUP_APP_DEVELOPER = "application_developer";
        private static final String GROUP_INGESTION_USER = "ingestion_user";
        private static final String GROUP_SANDBOX_SLC_OPERATOR = "Sandbox SLC Operator";
        private static final String GROUP_SANDBOX_ADMINISTRATOR = "Sandbox Administrator";

        private static final RoleToGroupMapper INSTANCE = new RoleToGroupMapper();

        public static RoleToGroupMapper getInstance() {
            return INSTANCE;
        }

        private final Map<String, String> roleToGroupMap;
        private final Map<String, String> groupToRoleMap;

        private RoleToGroupMapper() {
            roleToGroupMap = new HashMap<String, String>();
            groupToRoleMap = new HashMap<String, String>();

            roleToGroupMap.put(RoleInitializer.SLC_OPERATOR, GROUP_SLC_OPERATOR);
            roleToGroupMap.put(RoleInitializer.REALM_ADMINISTRATOR, GROUP_REALM_ADMINISTRATOR);
            roleToGroupMap.put(RoleInitializer.SEA_ADMINISTRATOR, GROUP_SEA_ADMINISTRATOR);
            roleToGroupMap.put(RoleInitializer.LEA_ADMINISTRATOR, GROUP_LEA_ADMINISTRATOR);
            roleToGroupMap.put(RoleInitializer.APP_DEVELOPER, GROUP_APP_DEVELOPER);
            roleToGroupMap.put(RoleInitializer.INGESTION_USER, GROUP_INGESTION_USER);
            roleToGroupMap.put(RoleInitializer.SANDBOX_SLC_OPERATOR, GROUP_SANDBOX_SLC_OPERATOR);
            roleToGroupMap.put(RoleInitializer.SANDBOX_ADMINISTRATOR, GROUP_SANDBOX_ADMINISTRATOR);

            groupToRoleMap.put(GROUP_SLC_OPERATOR, RoleInitializer.SLC_OPERATOR);
            groupToRoleMap.put(GROUP_REALM_ADMINISTRATOR, RoleInitializer.REALM_ADMINISTRATOR);
            groupToRoleMap.put(GROUP_SEA_ADMINISTRATOR, RoleInitializer.SEA_ADMINISTRATOR);
            groupToRoleMap.put(GROUP_LEA_ADMINISTRATOR, RoleInitializer.LEA_ADMINISTRATOR);
            groupToRoleMap.put(GROUP_APP_DEVELOPER, RoleInitializer.APP_DEVELOPER);
            groupToRoleMap.put(GROUP_INGESTION_USER, RoleInitializer.INGESTION_USER);
            groupToRoleMap.put(GROUP_SANDBOX_SLC_OPERATOR, RoleInitializer.SANDBOX_SLC_OPERATOR);
            groupToRoleMap.put(GROUP_SANDBOX_ADMINISTRATOR, RoleInitializer.SANDBOX_ADMINISTRATOR);
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

    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

}
