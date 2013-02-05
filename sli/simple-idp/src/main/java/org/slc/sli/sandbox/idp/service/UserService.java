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

package org.slc.sli.sandbox.idp.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.CollectingAuthenticationErrorCallback;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.stereotype.Component;

/**
 * Returns users that can be logged in as
 *
 */
@Component
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    LdapTemplate ldapTemplate;

    @Value("${sli.simple-idp.userSearchAttribute}")
    private String userSearchAttribute;

    @Value("${sli.simple-idp.userObjectClass}")
    private String userObjectClass;

    @Value("${sli.simple-idp.groupSearchAttribute}")
    private String groupSearchAttribute;

    @Value("${sli.simple-idp.groupObjectClass}")
    private String groupObjectClass;

    @Value("${sli.simple-idp.sliAdminRealmName}")
    private String sliAdminRealmName;

    private static final String USER_TYPE = "userType";
    private static final String SIMPLE_IDP_EXCEPTION = "SimpleIDP Authentication Exception";
    private static final Map<String, String> LDAP_ROLE_MAPPING = new HashMap<String, String>();
    static {
        // Mapping from roles in LDAP which comply with requirements of POSIX systems
        // to roles understood by the API
        LDAP_ROLE_MAPPING.put("educator", "Educator");
        LDAP_ROLE_MAPPING.put("aggregate_viewer", "Aggregate Viewer");
        LDAP_ROLE_MAPPING.put("it_administrator", "IT Administrator");
        LDAP_ROLE_MAPPING.put("leader", "Leader");
        LDAP_ROLE_MAPPING.put("lea_administrator", "LEA Administrator");
        LDAP_ROLE_MAPPING.put("sea_administrator", "SEA Administrator");
        LDAP_ROLE_MAPPING.put("application_developer", "Application Developer");
        LDAP_ROLE_MAPPING.put("slc_operator", "SLC Operator");
        LDAP_ROLE_MAPPING.put("realm_administrator", "Realm Administrator");
        LDAP_ROLE_MAPPING.put("ingestion_user", "Ingestion User");
        LDAP_ROLE_MAPPING.put("acceptance_test_user", "Acceptance Test User");
        LDAP_ROLE_MAPPING.put("sandbox_administrator", "Sandbox Administrator");
        LDAP_ROLE_MAPPING.put("sandbox_slc_operator", "Sandbox SLC Operator");
    }

    public UserService() {
        super();
    }

    UserService(String userSearchAttribute, String userObjectClass, String groupSearchAttribute, String groupObjectClass) {
        this.userSearchAttribute = userSearchAttribute;
        this.userObjectClass = userObjectClass;
        this.groupSearchAttribute = groupSearchAttribute;
        this.groupObjectClass = groupObjectClass;
    }

    /**
     * Holds user information
     */
    public static class User {
        String userId;
        List<String> roles;
        Map<String, String> attributes;
        User impersonationUser;

        public User() {
            attributes = new HashMap<String, String>();
        }

        public User(String userId, List<String> roles, Map<String, String> attributes) {
            this.userId = userId;
            this.roles = roles;
            this.attributes = attributes;
        }

        public String getUserId() {
            return userId;
        }

        public List<String> getRoles() {
            return roles;
        }

        public Map<String, String> getAttributes() {
            return attributes;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public void setImpersonationUser(User impersonationUser) {
            this.impersonationUser = impersonationUser;
        }

        public User getImpersonationUser() {
            return impersonationUser;
        }
        /**
         * Enumerates the User object's ldap id, roles, and attributes.
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("User = { ");
            builder.append("[id:").append(this.userId).append("], ");
            builder.append("[roles:").append(this.roles).append("],");
            builder.append("[attributes:").append(this.attributes).append("]");
            builder.append(" }");
            return builder.toString();
        }
    }

    /**
     * Authenticate the user for the given realm and return back a User object with
     * all user attributes and roles loaded. If sandbox mode then it ensures the realm provided
     * matches with the realm stored in the users LDAP description attribute (Realm=xxx)
     *
     * @param realm
     * @param userId
     * @param password
     * @return
     * @throws AuthenticationException
     */
    public User authenticate(String realm, String userId, String password) throws AuthenticationException {
        CollectingAuthenticationErrorCallback errorCallback = new CollectingAuthenticationErrorCallback();
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("objectclass", userObjectClass)).and(new EqualsFilter(userSearchAttribute, userId));
        DistinguishedName dn = new DistinguishedName("ou=" + realm);
        boolean result = ldapTemplate.authenticate(dn, filter.toString(), password, errorCallback);
        if (!result) {
            Exception error = errorCallback.getError();
            if (error == null) {
                LOG.error(SIMPLE_IDP_EXCEPTION);
            } else {
                LOG.error(SIMPLE_IDP_EXCEPTION, error);
            }
            throw new AuthenticationException(error);
        }

        User user = getUser(realm, userId);

        if (user.getUserId() == null || !user.getUserId().equals(userId)) {
            String error = "Username does not match LDAP User ID.";
            LOG.error(SIMPLE_IDP_EXCEPTION, error);
            throw new AuthenticationException(error);
        }

        user.roles = getUserGroups(realm, userId);

        // check if userId needs to be updated (based on groups/roles)
        if (user.roles.size() == 1 && (user.roles.contains("Student") || user.roles.contains("Parent"))) {
            if (user.getAttributes().containsKey("employeeNumber")) {
                String newUid = user.getAttributes().remove("employeeNumber");
                LOG.info("Updating user id: {} --> {}", user.getUserId(), newUid);
                user.setUserId(newUid);
            }
        }

        user.getAttributes().put(USER_TYPE, "staff");
        if (user.roles.contains("Student")) {
            user.getAttributes().put(USER_TYPE, "student");
        } else if (user.roles.contains("Parent")) {
            user.getAttributes().put(USER_TYPE, "parent");
        } else {
            if (user.getAttributes().containsKey("isAdmin")) {
                boolean isAdmin = Boolean.valueOf(user.getAttributes().get("isAdmin"));
                if (isAdmin) {
                    user.getAttributes().put(USER_TYPE, "admin");
                }
            }
        }

        return user;
    }

    /**
     *
     * @param realm
     *            The realm under which the user exists
     * @param userId
     *            The id of the user
     * @return
     */
    public User getUser(String realm, String userId) {
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("objectclass", userObjectClass)).and(new EqualsFilter(userSearchAttribute, userId));
        DistinguishedName dn = new DistinguishedName("ou=" + realm);
        PersonContextMapper pcm = new PersonContextMapper();
        boolean needAdditionalAttributes = (realm != null && realm.equals(sliAdminRealmName));
        pcm.setAddAttributes(needAdditionalAttributes);
        return (User) ldapTemplate.searchForObject(dn, filter.toString(), pcm);
    }

    /**
     *
     * @param realm
     *            The realm under which the user exists
     * @param userId
     *            The id of the user
     * @return List of roles assigned to this user
     */
    public List<String> getUserGroups(String realm, String userId) {
        DistinguishedName dn = new DistinguishedName("ou=" + realm);
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("objectclass", groupObjectClass)).and(
                new EqualsFilter(groupSearchAttribute, userId));
        @SuppressWarnings("unchecked")
        List<String> groups = ldapTemplate.search(dn, filter.toString(), new GroupContextMapper());

        // map the roles in LDAP which are better suited for Posix systems to
        // the roles used by the API
        List<String> result = new LinkedList<String>();
        for (String group : groups) {
            result.add(LDAP_ROLE_MAPPING.containsKey(group) ? LDAP_ROLE_MAPPING.get(group) : group);
        }
        return result;
    }

    public void updateUser(String realm, User user, String resetKey, String password) {
        LOG.info("Update User with resetKey");

        user.attributes.put("resetKey", resetKey);

        DirContextAdapter context = (DirContextAdapter) ldapTemplate.lookupContext(buildUserDN(realm, user
                .getAttributes().get("userName")));

        mapUserToContext(context, user);
        ldapTemplate.modifyAttributes(context);
    }

    private DistinguishedName buildUserDN(String realm, String uid) {
        DistinguishedName dn = new DistinguishedName("cn=" + uid + ",ou=people,ou=" + realm);
        return dn;
    }

    private void mapUserToContext(DirContextAdapter context, User user) {
        context.setAttributeValues("objectclass", new String[] { "inetOrgPerson", "posixAccount", "top" });
        context.setAttributeValue("givenName", user.getAttributes().get("givenName"));
        context.setAttributeValue("sn", user.getAttributes().get("sn"));
        context.setAttributeValue("uid", user.getAttributes().get("uid"));
        context.setAttributeValue("uidNumber", user.getAttributes().get("uidNumber"));
        context.setAttributeValue("gidNumber", user.getAttributes().get("gidNumber"));
        context.setAttributeValue("cn", user.getAttributes().get("userName"));
        context.setAttributeValue("mail", user.getAttributes().get("mail"));
        context.setAttributeValue("homeDirectory", user.getAttributes().get("homeDirectory"));

        context.setAttributeValue("gecos", user.getAttributes().get("resetKey"));
        context.setAttributeValue("userPassword", user.getAttributes().get("userPassword"));

        String description = "";
        if (user.getAttributes().get("tenant") != null) {
            description += "tenant=" + user.getAttributes().get("tenant");
        }
        if (user.getAttributes().get("edOrg") != null) {
            description += ",edOrg=" + user.getAttributes().get("edOrg");
        }
        if (!"".equals(description)) {
            context.setAttributeValue("description", "tenant=" + user.getAttributes().get("tenant") + "," + "edOrg="
                    + user.getAttributes().get("edOrg"));
        }

        if (user.getAttributes().containsKey("employeeNumber")) {
            context.setAttributeValue("employeeNumber", user.getAttributes().get("employeeNumber"));
        }
    }

    /**
     * LDAPTemplate mapper for getting attributes from the person context. Retrieves cn and
     * description,
     * parsing the value of description by line and then by key=value.
     *
     * @author scole
     *
     */
    static class PersonContextMapper implements ContextMapper {
        boolean needAdditionalAttributes = false;

        public void setAddAttributes(boolean needAdditionalAttributes) {
            this.needAdditionalAttributes = needAdditionalAttributes;
        }

        @Override
        public Object mapFromContext(Object ctx) {
            DirContextAdapter context = (DirContextAdapter) ctx;
            User user = new User();
            Map<String, String> attributes = new HashMap<String, String>();

            attributes.put("userName", context.getStringAttribute("cn"));
            attributes.put("vendor", context.getStringAttribute("o"));
            attributes.put("givenName", context.getStringAttribute("givenName"));
            attributes.put("sn", context.getStringAttribute("sn"));

            if (needAdditionalAttributes) {
                attributes.put("uid", context.getStringAttribute("uid"));
                attributes.put("uidNumber", context.getStringAttribute("uidNumber"));
                attributes.put("gidNumber", context.getStringAttribute("gidNumber"));
                attributes.put("homeDirectory", context.getStringAttribute("homeDirectory"));
                attributes.put("mail", context.getStringAttribute("mail"));

                String emailToken = context.getStringAttribute("displayName");
                if (emailToken == null || emailToken.trim().length() == 0) {
                    emailToken = "";
                }
                attributes.put("emailToken", emailToken);
            }

            String description = context.getStringAttribute("description");
            if (description != null && description.length() > 0) {
                String[] pairs;
                if (description.indexOf("\n") > 0) {
                    pairs = description.split("\n");
                } else if (description.indexOf(",") > 0) {
                    pairs = description.split(",");
                } else {
                    pairs = description.split(" ");
                }
                for (String pair : pairs) {
                    pair = pair.trim();
                    String[] pairArray = pair.split("=", 2);
                    if (pairArray.length == 2) {
                        String value = pairArray[1].trim();
                        if (value.length() > 0) {
                            attributes.put(pairArray[0].trim(), value);
                        }
                    }
                }
            }

            String employeeNumber = context.getStringAttribute("employeeNumber");
            if (employeeNumber != null && employeeNumber.length() > 0) {
                attributes.put("employeeNumber", employeeNumber);
            }
            user.setUserId(context.getStringAttribute("uid"));
            user.attributes = attributes;
            return user;
        }
    }

    /**
     * LDAPTemplate mapper for getting group names.
     *
     * @author scole
     *
     */
    static class GroupContextMapper implements ContextMapper {
        @Override
        public Object mapFromContext(Object ctx) {
            DirContextAdapter context = (DirContextAdapter) ctx;
            String group = context.getStringAttribute("cn");
            return group;
        }

    }
}
