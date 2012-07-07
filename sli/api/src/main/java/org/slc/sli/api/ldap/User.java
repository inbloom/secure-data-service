package org.slc.sli.api.ldap;

import java.util.List;

/**
 * Define the class that represents the user entry in ldap directory
 * 
 * @author dliu
 * 
 */
public class User {

    private String uid;
    private List<String> roles;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String tenant;
    private String edorg;
    private String homeDir;
    
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
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getEdorg() {
        return edorg;
    }

    public void setEdorg(String edorg) {
        this.edorg = edorg;
    }

    public String getHomeDir() {
        return homeDir;
    }

    public void setHomeDir(String homeDir) {
        this.homeDir = homeDir;
    }
    
    public void setFullName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    public String getFullName() {
        if (firstName != null && lastName != null && !firstName.equals("") && !lastName.equals(""))
            return this.firstName + " " + this.lastName;
        else
            return "";
    }

}
