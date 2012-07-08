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
    
    public List<String> getUserGroups(String realm, String uid);
    
    public void removeUser(String realm, String uid);
    
    public String createUser(String realm, User user);
    
    public String updateUser(String realm, User user);
    
    public List<User> findUserByRoles(List<String> roles);
    
    public List<User> findUserByAttributes(List<String> attributes);

}
