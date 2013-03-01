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

package org.slc.sli.common.ldap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.stereotype.Component;

import javax.naming.directory.SearchControls;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * implementation of LdapService interface for basic CRUD and search operations on LDAP directory
 *
 * @author dliu
 *
 */

@Component
public class LdapServiceImpl implements LdapService {

    public static final String OBJECTCLASS = "objectclass";
    @Autowired
    private LdapTemplate ldapTemplate;

    @Value("${sli.simple-idp.userSearchAttribute}")
    private String userSearchAttribute;

    @Value("${sli.simple-idp.userObjectClass}")
    private String userObjectClass;

    @Value("${sli.simple-idp.groupSearchAttribute}")
    private String groupSearchAttribute;

    @Value("${sli.simple-idp.groupObjectClass}")
    private String groupObjectClass;

    private static final String USER_ID_NUMBER = "500";
    private static final String GROUP_ID_NUMBER = "113";
    private static final String LOGIN_SHELL = "/sbin/nologin";

    @SuppressWarnings("rawtypes")
    @Override
    public User getUser(String realm, String uid) {
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter(OBJECTCLASS, userObjectClass)).and(new EqualsFilter(userSearchAttribute, uid));
        DistinguishedName dn = new DistinguishedName("ou=" + realm);
        User user;
        try {
            List userList = ldapTemplate.search(dn, filter.toString(), SearchControls.SUBTREE_SCOPE, new String[] {
                "*", CREATE_TIMESTAMP, MODIFY_TIMESTAMP }, new UserContextMapper());
            if (userList == null || userList.size() == 0) {
                throw new EmptyResultDataAccessException(1);
            } else if (userList.size() > 1) {
                throw new IncorrectResultSizeDataAccessException("User must be unique", 1);
            }
            user = (User) userList.get(0);
            user.setUid(uid);
            user.setGroups(getGroupNames(getUserGroups(realm, uid)));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return user;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Group> getUserGroups(String realm, String uid) {
        DistinguishedName dn = new DistinguishedName("ou=" + realm);
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter(OBJECTCLASS, groupObjectClass)).and(new EqualsFilter(groupSearchAttribute, uid));
        List<Group> groups = ldapTemplate.search(dn, filter.toString(), new GroupContextMapper());
        return groups;
    }

    @Override
    public void removeUser(String realm, String uid) {
        Collection<Group> groups = getUserGroups(realm, uid);
        User oldUser = getUser(realm, uid);
        if (oldUser != null) {
            ldapTemplate.unbind(buildUserDN(realm, oldUser.getCn()));
        }
        if (groups != null && groups.size() > 0) {

            for (Group group : groups) {
                group.removeMemberUid(uid);
                updateGroup(realm, group);
            }
        }
    }

    @Override
    public String createUser(String realm, User user) throws NameAlreadyBoundException {
        user.setCn(user.getUid());
        ldapTemplate.bind(createUserContext(realm, user));
        List<String> groupNames = user.getGroups();
        if (groupNames != null && groupNames.size() > 0) {
            for (String groupName : groupNames) {
                Group group = getGroup(realm, groupName);
                group.addMemberUid(user.getUid());
                updateGroup(realm, group);
            }
        }
        return user.getUid();
    }

    @Override
    public boolean updateUser(String realm, User user) {
        User oldUser = getUser(realm, user.getUid());
        if (oldUser == null) {
            return false;
        }
        Collection<Group> oldGroups = getUserGroups(realm, user.getUid());
        DirContextAdapter context = (DirContextAdapter) ldapTemplate.lookupContext(buildUserDN(realm, oldUser.getCn()));
        mapUserToContext(context, user);
        ldapTemplate.modifyAttributes(context);
        Collection<String> newGroupNames = user.getGroups();
        Collection<String> oldGroupNames = getGroupNames(oldGroups);

        if (oldGroups != null && oldGroups.size() > 0) {
            for (Group oldGroup : oldGroups) {
                if (!newGroupNames.contains(oldGroup.getGroupName())) {
                    oldGroup.removeMemberUid(user.getUid());
                    updateGroup(realm, oldGroup);
                }
            }
        }

        if (newGroupNames != null && newGroupNames.size() > 0) {
            for (String newGroupName : newGroupNames) {
                if (!oldGroupNames.contains(newGroupName)) {
                    Group newGroup = getGroup(realm, newGroupName);
                    newGroup.addMemberUid(user.getUid());
                    updateGroup(realm, newGroup);
                }
            }
        }
        return true;
    }

    @Override
    public Collection<User> findUsersByGroups(String realm, final Collection<String> allowedGroupNames,
            final Collection<String> disallowedGroupNames, String tenant, Collection<String> edorgs) {

        Collection<String> allowed = allowedGroupNames;
        Collection<String> disallowed = disallowedGroupNames;

        if (allowed == null) {
            allowed = new LinkedList<String>();
        }
        if (disallowed == null) {
            disallowed = new LinkedList<String>();
        }

        Set<String> allowedUsers = new HashSet<String>();

        Map<String, List<String>> uidToGroupsMap = new HashMap<String, List<String>>();
        for (String groupName : allowed) {
            Group group = getGroup(realm, groupName);
            if (group != null) {
                List<String> memberUids = group.getMemberUids();

                if (memberUids != null && memberUids.size() > 0) {
                    for (String memberUid : memberUids) {
                        if (uidToGroupsMap.containsKey(memberUid)) {
                            uidToGroupsMap.get(memberUid).add(groupName);
                        } else {
                            List<String> uidGroupNames = new ArrayList<String>();
                            uidGroupNames.add(groupName);
                            uidToGroupsMap.put(memberUid, uidGroupNames);
                        }
                        allowedUsers.add(memberUid);
                    }
                }
            }
        }

        for (String groupName : disallowed) {
            Group group = getGroup(realm, groupName);
            if (group != null) {
                allowedUsers.removeAll(group.getMemberUids());
            }
        }

        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter(OBJECTCLASS, userObjectClass));
        OrFilter orFilter = new OrFilter();
        for (String uid : allowedUsers) {
            orFilter.or(new EqualsFilter(userSearchAttribute, uid));
        }
        filter.and(orFilter);
        DistinguishedName dn = new DistinguishedName("ou=" + realm);
        @SuppressWarnings("unchecked")
        Collection<User> users = (ldapTemplate.search(dn, filter.toString(), SearchControls.SUBTREE_SCOPE,
                new String[] { "*", CREATE_TIMESTAMP, MODIFY_TIMESTAMP }, new UserContextMapper()));
        for (User user : users) {
            user.setGroups(uidToGroupsMap.get(user.getUid()));
        }

        if (tenant != null) {
            users = filterByTenant(users, tenant);
        }

        if (edorgs != null) {
            users = filterByEdorgs(users, edorgs);
        }

        return users;
    }

    @Override
    public Collection<User> findUsersByGroups(String realm, Collection<String> groupNames) {
        return findUsersByGroups(realm, groupNames, null, null, null);
    }

    @Override
    public Collection<User> findUsersByGroups(String realm, Collection<String> groupNames, String tenant) {
        return findUsersByGroups(realm, groupNames, null, tenant, null);
    }

    @Override
    public Collection<User> findUsersByAttributes(String realm, Collection<String> attributes) {
        return null;
    }

    @Override
    public Group getGroup(String realm, String groupName) {
        DistinguishedName dn = new DistinguishedName("ou=" + realm);
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter(OBJECTCLASS, groupObjectClass)).and(new EqualsFilter("cn", groupName));
        try {
            return (Group) ldapTemplate.searchForObject(dn, filter.toString(), new GroupContextMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Collection<User> findUsersByGroups(String realm, Collection<String> groupNames, String tenant,
            Collection<String> edorgs) {
        return findUsersByGroups(realm, groupNames, null, tenant, edorgs);
    }

    @Override
    public boolean updateGroup(String realm, Group group) {
        DirContextAdapter context = (DirContextAdapter) ldapTemplate.lookupContext(buildGroupDN(realm,
                group.getGroupName()));
        mapGroupToContext(context, group);
        ldapTemplate.modifyAttributes(context);
        return true;
    }

    private Collection<User> filterByTenant(Collection<User> users, String tenant) {
        if (tenant == null || users == null) {
            return users;
        }

        List<User> filteredUsers = new ArrayList<User>();
        for (User user : users) {
            if (tenant.equals(user.getTenant())) {
                filteredUsers.add(user);
            }
        }
        return filteredUsers;
    }

    private Collection<User> filterByEdorgs(Collection<User> users, Collection<String> edorgs) {
        if (edorgs == null || edorgs.size() == 0 || users == null) {
            return users;
        }
        Collection<User> filteredUsers = new ArrayList<User>();
        for (User user : users) {
            if (edorgs.contains(user.getEdorg())) {
                filteredUsers.add(user);
            }
        }
        return filteredUsers;
    }

    private DirContextAdapter createUserContext(String realm, User user) {
        DirContextAdapter context = new DirContextAdapter(buildUserDN(realm, user));
        mapUserToContext(context, user);
        context.setAttributeValue("cn", user.getCn());
        return context;
    }

    private DistinguishedName buildUserDN(String realm, User user) {
        return buildUserDN(realm, user.getCn());
    }

    private DistinguishedName buildUserDN(String realm, String cn) {
        return new DistinguishedName("cn=" + cn + ",ou=people,ou=" + realm);
    }

    private DistinguishedName buildGroupDN(String realm, String groupName) {
        return new DistinguishedName("cn=" + groupName + ",ou=groups,ou=" + realm);
    }

    private void mapUserToContext(DirContextAdapter context, User user) {
        context.setAttributeValues(OBJECTCLASS, new String[] { "inetOrgPerson", "posixAccount", "top" });
        context.setAttributeValue("givenName", user.getGivenName());
        String surName = user.getSn();
        context.setAttributeValue("sn", surName == null ? " " : surName);
        context.setAttributeValue("uid", user.getUid());
        context.setAttributeValue("uidNumber", USER_ID_NUMBER);
        context.setAttributeValue("gidNumber", GROUP_ID_NUMBER);
        context.setAttributeValue("loginShell", LOGIN_SHELL);
        context.setAttributeValue("mail", user.getEmail());
        context.setAttributeValue("homeDirectory", user.getHomeDir());
        if (user.getStatus() != null && user.getStatus().getStatusString() != null) {
            context.setAttributeValue("destinationindicator", user.getStatus().getStatusString());
        }
        String description = "";
        if (user.getTenant() != null) {
            description += "tenant=" + user.getTenant();
        }
        if (user.getEdorg() != null) {
            description += ",edOrg=" + user.getEdorg();
        }
        if (!"".equals(description)) {
            context.setAttributeValue("description", "tenant=" + user.getTenant() + "," + "edOrg=" + user.getEdorg());
        }
        if (user.getVendor() != null) {
            context.setAttributeValue("o", user.getVendor());
        }

    }

    private void mapGroupToContext(DirContextAdapter context, Group group) {
        context.setAttributeValues("memberUid", group.getMemberUids().toArray());
    }

    private List<String> getGroupNames(Collection<Group> groups) {
        if (groups != null && groups.size() > 0) {
            List<String> groupNames = new ArrayList<String>();
            for (Group group : groups) {
                groupNames.add(group.getGroupName());
            }
            return groupNames;
        }
        return null;
    }

    @Override
    public void setLdapTemplate(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }
}
