package org.slc.sli.api.ldap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.SearchControls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.stereotype.Component;

/**
 * implementation of LdapService interface for basic CRUD and search operations on LDAP directory
 * 
 * @author dliu
 * 
 */

@Component
public class LdapServiceImpl implements LdapService {

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
    
    private static final String USER_ID_NUMBER = "500";
    private static final String GROUP_ID_NUMBER = "113";

    public LdapServiceImpl() {
    }

    @SuppressWarnings("rawtypes")
    @Override
    public User getUser(String realm, String uid) {
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("objectclass", userObjectClass)).and(new EqualsFilter(userSearchAttribute, uid));
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
    public List<Group> getUserGroups(String realm, String uid) {
        DistinguishedName dn = new DistinguishedName("ou=" + realm);
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("objectclass", groupObjectClass)).and(new EqualsFilter(groupSearchAttribute, uid));
        List<Group> groups = ldapTemplate.search(dn, filter.toString(), new GroupContextMapper());
        return groups;
    }

    @Override
    public void removeUser(String realm, String uid) {
        List<Group> groups = getUserGroups(realm, uid);
        ldapTemplate.unbind(buildUserDN(realm, uid));
        if (groups != null && groups.size() > 0) {
            
            for (Group group : groups) {
                group.removeMemberUid(uid);
                updateGroup(realm, group);
            }
        }
    }

    @Override
    public String createUser(String realm, User user) {
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
        List<Group> oldGroups = getUserGroups(realm, user.getUid());
        DirContextAdapter context = (DirContextAdapter) ldapTemplate.lookupContext(buildUserDN(realm, user.getUid()));
        mapUserToContext(context, user);
        ldapTemplate.modifyAttributes(context);
        List<String> newGroupNames = user.getGroups();
        List<String> oldGroupNames = getGroupNames(oldGroups);

        if (oldGroups != null && oldGroups.size() > 0)
            for (Group oldGroup : oldGroups) {
                if (!newGroupNames.contains(oldGroup.getGroupName())) {
                    oldGroup.removeMemberUid(user.getUid());
                    updateGroup(realm, oldGroup);
                }
            }

        if (newGroupNames != null && newGroupNames.size() > 0)
            for (String newGroupName : newGroupNames) {
                if (!oldGroupNames.contains(newGroupName)) {
                    Group newGroup = getGroup(realm, newGroupName);
                    newGroup.addMemberUid(user.getUid());
                    updateGroup(realm, newGroup);
                }
            }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<User> findUserByGroups(String realm, List<String> groupNames) {
        List<User> users = new ArrayList<User>();
        List<String> uids = new ArrayList<String>();
        Map<String, List<String>> uidToGroupsMap = new HashMap<String, List<String>>();
        if (groupNames != null && groupNames.size() > 0) {
            for (String groupName : groupNames) {
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
                            if (!uids.contains(memberUid)) {
                                uids.add(memberUid);
                            }
                        }
                    }
                }
            }
            AndFilter filter = new AndFilter();
            filter.and(new EqualsFilter("objectclass", userObjectClass));
            OrFilter orFilter = new OrFilter();
            for (String uid : uids) {
                orFilter.or(new EqualsFilter(userSearchAttribute, uid));
            }
            filter.and(orFilter);
            DistinguishedName dn = new DistinguishedName("ou=" + realm);
            users = (List<User>) (ldapTemplate.search(dn, filter.toString(), SearchControls.SUBTREE_SCOPE,
                    new String[] { "*", CREATE_TIMESTAMP, MODIFY_TIMESTAMP }, new UserContextMapper()));
            for (User user : users) {
                user.setGroups(uidToGroupsMap.get(user.getUid()));
            }
            return users;
        }
        return null;
    }

    @Override
    public List<User> findUserByGroups(String realm, List<String> groupNames, String tenant) {
        return filterByTenant(findUserByGroups(realm, groupNames), tenant);
    }

    @Override
    public List<User> findUserByAttributes(String realm, List<String> attributes) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Group getGroup(String realm, String groupName) {
        DistinguishedName dn = new DistinguishedName("ou=" + realm);
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("objectclass", groupObjectClass)).and(new EqualsFilter("cn", groupName));
        try {
            Group group = (Group) ldapTemplate.searchForObject(dn, filter.toString(), new GroupContextMapper());
            return group;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    @Override
    public List<User> findUserByGroups(String realm, List<String> groupNames, String tenant, List<String> edorgs) {
        return filterByEdorgs(filterByTenant(findUserByGroups(realm, groupNames), tenant), edorgs);
    }
    
    @Override
    public boolean updateGroup(String realm, Group group) {
        DirContextAdapter context = (DirContextAdapter) ldapTemplate.lookupContext(buildGroupDN(realm,
                group.getGroupName()));
        mapGroupToContext(context, group);
        ldapTemplate.modifyAttributes(context);
        return true;
    }

    private List<User> filterByTenant(List<User> users, String tenant) {
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
    
    private List<User> filterByEdorgs(List<User> users, List<String> edorgs) {
        if (edorgs == null || edorgs.size() == 0 || users == null) {
            return users;
        }
        List<User> filteredUsers = new ArrayList<User>();
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
        return context;
    }
    
    private DistinguishedName buildUserDN(String realm, User user) {
        return buildUserDN(realm, user.getUid());
    }
    
    private DistinguishedName buildUserDN(String realm, String uid) {
        DistinguishedName dn = new DistinguishedName("cn=" + uid + ",ou=people,ou=" + realm);
        return dn;
    }
    
    private DistinguishedName buildGroupDN(String realm, String groupName) {
        DistinguishedName dn = new DistinguishedName("cn=" + groupName + ",ou=groups,ou=" + realm);
        return dn;
    }

    private void mapUserToContext(DirContextAdapter context, User user) {
        context.setAttributeValues("objectclass", new String[] { "inetOrgPerson", "posixAccount", "top" });
        context.setAttributeValue("givenName", user.getFirstName());
        context.setAttributeValue("sn", user.getLastName());
        context.setAttributeValue("uid", user.getUid());
        context.setAttributeValue("uidNumber", USER_ID_NUMBER);
        context.setAttributeValue("gidNumber", GROUP_ID_NUMBER);
        context.setAttributeValue("cn", user.getUid());
        context.setAttributeValue("mail", user.getEmail());
        context.setAttributeValue("homeDirectory", user.getHomeDir());
        context.setAttributeValue("description", "tenant=" + user.getTenant() + "," + "edOrg=" + user.getEdorg());
        
    }
    
    private void mapGroupToContext(DirContextAdapter context, Group group) {
        context.setAttributeValues("memberUid", group.getMemberUids().toArray());
    }

    private List<String> getGroupNames(List<Group> groups) {
        if (groups != null && groups.size() > 0) {
            List<String> groupNames = new ArrayList<String>();
            for (Group group : groups) {
                groupNames.add(group.getGroupName());
            }
            return groupNames;
        }
        return null;
    }

}
