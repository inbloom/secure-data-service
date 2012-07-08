package org.slc.sli.api.ldap;

import java.util.List;

/**
 * define the interface for basic CRUD and search operations on LDAP
 * 
 * @author dliu
 * 
 */
// TODO move the ldap package out from api to common, so to reduce duplicate code with simple idp

public interface LdapService {

    public User getUser(String realm, String uid);
    
    public List<Group> getUserGroups(String realm, String uid);
    
    public Group getGroup(String realm, String groupName);

    public void removeUser(String realm, String uid);
    
    public String createUser(String realm, User user);
    
    public String updateUser(String realm, User user);
    
    public List<User> findUserByGroups(String realm, List<String> groupNames);
    
    public List<User> findUserByAttributes(String realm, List<String> attributes);

}
