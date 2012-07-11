/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

    private static final Map<String, String> LDAP_ROLE_MAPPING = new HashMap<String,String>();
    static {
        // Mapping from roles in LDAP which comply with requirements of POSIX systems
        // to roles understood by the API
        LDAP_ROLE_MAPPING.put("educator",              "Educator");
        LDAP_ROLE_MAPPING.put("aggregate_viewer",      "Aggregate Viewer");
        LDAP_ROLE_MAPPING.put("it_administrator",      "IT Administrator");
        LDAP_ROLE_MAPPING.put("leader",                "Leader");
        LDAP_ROLE_MAPPING.put("lea_administrator",     "LEA Administrator");
        LDAP_ROLE_MAPPING.put("sea_administrator",     "SEA Administrator");
        LDAP_ROLE_MAPPING.put("application_developer", "Application Developer");
        LDAP_ROLE_MAPPING.put("slc_operator",          "SLC Operator");
        LDAP_ROLE_MAPPING.put("realm_administrator",   "Realm Administrator");
        LDAP_ROLE_MAPPING.put("ingestion_user",        "Ingestion User");
        LDAP_ROLE_MAPPING.put("acceptance_test_user",  "Acceptance Test User");
    }

    public UserService() {
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

        public User() {
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
                LOG.error("SimpleIDP Authentication Eception");
            } else {
                LOG.error("SimpleIDP Authentication Exception", error);
            }
            throw new AuthenticationException(error);
        }

        User user = getUser(realm, userId);
        user.roles = getUserGroups(realm, userId);
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
        User user = (User) ldapTemplate.searchForObject(dn, filter.toString(), new PersonContextMapper());
        user.userId = userId;
        return user;
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

    /**
     * LDAPTemplate mapper for getting attributes from the person context. Retrieves cn and
     * description,
     * parsing the value of description by line and then by key=value.
     *
     * @author scole
     *
     */
    static class PersonContextMapper implements ContextMapper {
        @Override
        public Object mapFromContext(Object ctx) {
            DirContextAdapter context = (DirContextAdapter) ctx;
            User user = new User();
            Map<String, String> attributes = new HashMap<String, String>();
            attributes.put("userName", context.getStringAttribute("cn"));
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
