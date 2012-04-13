package org.slc.sli.sandbox.idp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Returns users that can be logged in as
 * 
 */
@Component
public class Users {
    
    @Autowired
    Repository<Entity> repo;
    
    /**
     * Holds user information
     */
    public static class User {
        String firstName;
        String lastName;
        String id;
        String type;
        
        public User(String firstName, String lastName, String id, String type) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.id = id;
            this.type = type;
        }
        
        public String getFirstName() {
            return firstName;
        }
        
        public String getLastName() {
            return lastName;
        }
        
        public String getId() {
            return id;
        }
        
        public String getType() {
            return type;
        }
        
        public String getUserName() {
            return getFirstName() + " " + getLastName();
        }
    }
    
    /**
     * Loads available users from the staff and teacher collections
     * 
     * @param tenant
     *            only staff and teachers with a tenantId that match this value will be included
     * @return List of users
     */
    @SuppressWarnings("unchecked")
    public List<User> getAvailableUsers(String tenant) {
        
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("metaData.tenantId", "=", tenant, false));
        
        List<User> users = new ArrayList<User>();
        for (Entity e : repo.findAll("staff", query)) {
            users.add(getUserFromEntity(e, "Staff"));
        }
        for (Entity e : repo.findAll("teacher", query)) {
            users.add(getUserFromEntity(e, "Teacher"));
        }
        return users;
    }
    
    public User getUser(String tenant, String id) {
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("metaData.tenantId", "=", tenant, false));
        query.addCriteria(new NeutralCriteria("staffUniqueStateId", "=", id));
        
        Entity entity = repo.findOne("staff", query);
        String type = "Staff";
        if (entity == null) {
            entity = repo.findOne("teacher", query);
            type = "Teacher";
        }
        if (entity == null) {
            throw new RuntimeException("Unable to find user in tenant: " + tenant + " with id: " + id);
        }
        return getUserFromEntity(entity, type);
    }
    
    @SuppressWarnings("unchecked")
    private static User getUserFromEntity(Entity entity, String type) {
        Map<String, Object> b = entity.getBody();
        Map<String, Object> name = (Map<String, Object>) b.get("name");
        return new User((String) name.get("firstName"), (String) name.get("lastSurname"),
                (String) b.get("staffUniqueStateId"), type);
    }
}
