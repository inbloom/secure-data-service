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

    public LdapServiceImpl() {
    }

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
            List<Group> groups = getUserGroups(realm, uid);
            if (groups != null && groups.size() > 0) {
                for (Group group : groups) {
                    user.addGroup(group.getGroupName());
                }
            }
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return user;
    }

    @Override
    public List<Group> getUserGroups(String realm, String uid) {
        DistinguishedName dn = new DistinguishedName("ou=" + realm);
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("objectclass", groupObjectClass)).and(new EqualsFilter(groupSearchAttribute, uid));
        @SuppressWarnings("unchecked")
        List<Group> groups = ldapTemplate.search(dn, filter.toString(), new GroupContextMapper());
        return groups;
    }

    @Override
    public void removeUser(String realm, String uid) {
        // TODO Auto-generated method stub

    }

    @Override
    public String createUser(String realm, User user) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String updateUser(String realm, User user) {
        // TODO Auto-generated method stub
        return null;
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
        if (edorgs == null || users == null) {
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

}
