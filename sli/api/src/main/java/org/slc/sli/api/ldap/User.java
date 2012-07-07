package org.slc.sli.api.ldap;

import java.util.List;
import java.util.Map;

/**
 * Define the class that represents the user entry in ldap directory
 * 
 * @author dliu
 * 
 */
public class User {

    private String uid;
    private List<String> roles;
    private Map<String, String> attributes;
    
    public String getUid() {
        return uid;
    }
    
    public void setUid(String uid) {
        this.uid = uid;
    }
    
    public List<String> getRoles() {
        return roles;
    }
    
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
    
    public Map<String, String> getAttributes() {
        return attributes;
    }
    
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

}
