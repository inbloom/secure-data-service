package org.slc.sli.api.security.roles;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * A basic implementation of RoleRightAccess
 * 
 * @author rlatta
 */
@Component
public class InsecureRoleRightAccessImpl implements RoleRightAccess {
    
    @Autowired
    private EntityDefinitionStore store;
    
    @Autowired
    private EntityRepository repo;
    
    private EntityService service;
    
    public static final String EDUCATOR = "Educator";
    public static final String LEADER = "Leader";
    public static final String AGGREGATOR = "Aggregate Viewer";
    public static final String IT_ADMINISTRATOR = "IT Administrator";
    
    @PostConstruct
    private void init() {
        EntityDefinition def = store.lookupByResourceName("roles");
        setService(def.getService());
    }
    
    @Override
    public Role findRoleByName(String name) {
        Role found = null;
        Iterable<Entity> results = repo.findByQuery("roles", new Query(Criteria.where("body.name").is(name)), 0, 1);
        
        if (results.iterator().hasNext()) {
            Entity e = results.iterator().next();
            found = getRoleWithBodyAndID(e.getEntityId(), new EntityBody(e.getBody()));
        }
        
        return found;
    }
    
    @Override
    public Role findRoleBySpringName(String springName) {
        Iterable<String> ids = service.list(0, 100);
        for (String id : ids) {
            EntityBody body = service.get(id);
            Role tempRole = getRoleWithBodyAndID(id, body);
            if (tempRole.getSpringRoleName().equals(springName))
                return tempRole;
        }
        return null;
    }
    
    private Role getRoleWithBodyAndID(String id, EntityBody body) {
        return RoleBuilder.makeRole(body).addId(id).build();
    }
    
    @Override
    public List<Role> fetchAllRoles() {
        List<Role> allRoles = new ArrayList<Role>();
        Iterable<Entity> results = repo.findAll("roles");
        for (Entity entity : results) {
            allRoles.add(getRoleWithBodyAndID(entity.getEntityId(), new EntityBody(entity.getBody())));
        }
        return allRoles;
    }
    
    @Override
    public boolean addRole(Role role) {
        return service.create(role.getRoleAsEntityBody()) != null;
    }
    
    @Override
    public boolean deleteRole(Role role) {
        if (role.getId().length() > 0) {
            try {
                service.delete(role.getId());
                return true;
            } catch (Exception e) {
                return false;
            }
            
        }
        return false;
    }
    
    @Override
    public boolean updateRole(Role role) {
        if (role.getId().length() > 0) {
            try {
                service.update(role.getId(), role.getRoleAsEntityBody());
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
    
    // Injection method.
    public void setStore(EntityDefinitionStore store) {
        this.store = store;
    }
    
    // Injection method.
    public void setService(EntityService service) {
        this.service = service;
    }
    
    public Role getDefaultRole(String name) {
        return findRoleByName(name);
    }
    
}
