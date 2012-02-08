package org.slc.sli.api.security.roles;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.service.EntityService;

/**
 * A basic implementation of RoleRightAccess
 * 
 * @author rlatta
 */
@Component
public class SecureRoleRightAccessImpl implements RoleRightAccess {
    private static final Logger   LOG              = LoggerFactory.getLogger(SecureRoleRightAccessImpl.class);
    
    @Autowired
    private EntityDefinitionStore store;
    
    private EntityService         service;
    
    public static final String    EDUCATOR         = "Educator";
    public static final String    LEADER           = "Leader";
    public static final String    AGGREGATOR       = "Aggregate Viewer";
    public static final String    IT_ADMINISTRATOR = "IT Administrator";
    
    @PostConstruct
    private void init() {
        EntityDefinition def = store.lookupByResourceName("roles");
        setService(def.getService());
    }
    
    @Override
    public Role findRoleByName(String name) {
        // TODO find a way to "findAll" from entity service
        Iterable<String> ids = service.list(0, 100);
        for (String id : ids) {
            EntityBody body;
            try {
                body = service.get(id); // FIXME massive hack for roles disappearing sporadically
            } catch (EntityNotFoundException e) {
                LOG.error("Error reading role.  Will try again", e);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    LOG.error("Thread go boom", e);
                }
                body = service.get(id);
            }
            
            if (body.get("name").equals(name)) {
                return getRoleWithBodyAndID(id, body);
            }
        }
        return null;
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
        List<Role> roles = new ArrayList<Role>();
        Iterable<String> ids = service.list(0, 100);
        for (String id : ids) {
            EntityBody body = service.get(id);
            roles.add(getRoleWithBodyAndID(id, body));
        }
        return roles;
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
