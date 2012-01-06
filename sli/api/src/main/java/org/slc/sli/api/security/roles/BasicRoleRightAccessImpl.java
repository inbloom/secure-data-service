package org.slc.sli.api.security.roles;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.enums.Right;
import org.slc.sli.api.service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * A basic implementation of IRoleRightAccess
 *
 * @author rlatta
 */
@Component
public class BasicRoleRightAccessImpl implements IRoleRightAccess {


    @Autowired
    private EntityDefinitionStore store;

    private EntityService service;
    
    private void init() {
        EntityDefinition def = store.lookupByResourceName("roles");
        setService(def.getService());
    }

    @Override
    public Role findRoleByName(String name) {
        Role temp;
        //TODO find a way to "findAll" from entity service
        Iterable<String> ids = service.list(0, 100);
        for (String id : ids) {
            EntityBody body = service.get(id);
            temp = RoleBuilder.makeRole(body).addId(id).build();
            if (temp.getName().equals(name)) {
                return temp;
            }
        }
        return null;
    }

    @Override
    public Role findRoleBySpringName(String springName) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Role> fetchAllRoles() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Right getRightByName(String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Right> fetchAllRights() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean addRole(Role role) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean deleteRole(Role role) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean updateRole(Role role) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    //Injection method.
    public void setService(EntityService service) {
        this.service = service;
    }

}
