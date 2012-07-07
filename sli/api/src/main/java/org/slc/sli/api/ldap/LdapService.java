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

    User getUser(String realm, String uid);
    
    List<String> getUserGroups(String realm, String uid);

}
