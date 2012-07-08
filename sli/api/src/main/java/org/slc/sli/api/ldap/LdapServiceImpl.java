package org.slc.sli.api.ldap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
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
            user = (User) ldapTemplate.searchForObject(dn, filter.toString(), new UserContextMapper());
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
    
    @Override
    public List<User> findUserByGroups(String realm, List<String> groupNames) {
        List<User> users = new ArrayList<User>();
        List<String> uids = new ArrayList<String>();
        for (String groupName : groupNames) {
            Group group = getGroup(realm, groupName);
            List<String> memberUids = group.getMemberUids();
            if (memberUids != null && memberUids.size() > 0) {
                for (String memberUid : memberUids) {
                    if (!uids.contains(memberUid)) {
                        uids.add(memberUid);
                        User user = getUser(realm, memberUid);
                        if (user != null)
                            users.add(user);
                    }
                }
            }
        }
        return users;
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
        Group group = (Group) ldapTemplate.searchForObject(dn, filter.toString(), new GroupContextMapper());
        return group;
    }
    
}
