package org.slc.sli.sandbox.idp.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
public class Users {
    
    @Autowired
    LdapTemplate ldapTemplate;
    
    /**
     * Holds user information
     */
    public static class User {
        String userId;
        String type; //Staff or Teacher
        List<String> roles;
        
        private User(String userId, String tenant) {
            this.userId = userId;
            roles = new ArrayList<String>();
        }

        private User(String userId, String tenant, String type) {
            this.userId = userId;
            this.type = type;
            roles = new ArrayList<String>();
        }
       
        
        public String getUserId() {
            return userId;
        }

        public void addRole(String role){
            roles.add(role);
        }
        public List<String> getRoles() {
            return roles;
        }
    }

    public User authenticate(String tenant, String userId, String password) {
        User user = new User(userId, tenant);
        
        CollectingAuthenticationErrorCallback errorCallback = new CollectingAuthenticationErrorCallback();
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("objectclass", "person")).and(new EqualsFilter("uid", userId));
        boolean result = ldapTemplate.authenticate(DistinguishedName.EMPTY_PATH, filter.toString(), password, errorCallback);
        if (!result) {
          //Exception error = errorCallback.getError();
          return null;
        }
        user.addRole("SLC Operator");
        user.addRole("App Developer");
        return user;
    }

    
    public User getUser(String tenant, String userId) {
        return new User(userId, tenant);
    }

}
