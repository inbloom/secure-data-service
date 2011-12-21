package org.slc.sli.api.security.roles;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slc.sli.api.security.enums.DefaultRoles;
import org.slc.sli.api.service.EntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;



/**
 *  A RESTful class to return the roles and their configured rights.
 *
 *  This is meant to be a read-only operation, but contains a convenience post
 *  method to create new roles.
 *
 *  @see Rights
 */
@Path("/admin/roles")
@Component
@Scope("request")
@Produces("application/json")

public class RolesAndPermissionsResource {

    @Autowired
    private EntityDefinitionStore store;

    private static final Logger LOG = LoggerFactory.getLogger(RolesAndPermissionsResource.class);


    /**
     * Fetches the first 100 roles listed in the system to be serialized to json
     * This is intended to be a restful API call.
     * 
     * @return an object that is technically a list of maps that are the roles
     */
    @GET
    @Path("/")
    public List<Map<String, Object>> getRolesAndPermissions() {
        Map<String, Object> roles = new HashMap<String, Object>();
        List<Map<String, Object>> roleList = new ArrayList<Map<String, Object>>();
        EntityService service = getEntityService();
        
        //Add default roles
        roleList.add(getDefaultRole(DefaultRoles.EDUCATOR));
        roleList.add(getDefaultRole(DefaultRoles.LEADER));
        roleList.add(getDefaultRole(DefaultRoles.AGGREGATOR));
        roleList.add(getDefaultRole(DefaultRoles.ADMINISTRATOR));

        //TODO get some way to findAll.
        Iterable<String> names = service.list(0, 100);
        Iterable<EntityBody> entities = service.get(names);
        for (EntityBody body : entities) {
            roleList.add(body);
        }
        return roleList;
    }
    
    private Map<String, Object> getDefaultRole(DefaultRoles role) {
        RoleBuilder builder = new RoleBuilder(role.getRoleName());
        builder.addRights(role.getRights());
        return builder.build();
    }

    /**
     * A simple method to add a new role to the database.
     * @param name the name of the new role (eg: Educator)
     * @param rights some list of rights to be added
     * @see Rights
     */
    @POST
    @Path("/")
    public void createRoleWithPermission(String name, Object rights) {
        //Make sure we aren't creating a duplicate of a default role
        if (name.equalsIgnoreCase(DefaultRoles.EDUCATOR.getRoleName())
                || name.equalsIgnoreCase(DefaultRoles.ADMINISTRATOR.getRoleName())
                || name.equalsIgnoreCase(DefaultRoles.AGGREGATOR.getRoleName())
                || name.equalsIgnoreCase(DefaultRoles.LEADER.getRoleName())) {
            return;
        }
        RoleBuilder builder = new RoleBuilder(name);
        builder.addRights(rights);
        
        EntityService service = getEntityService();
        service.create(builder.build());
    }

    private EntityService getEntityService() {
        EntityDefinition def = store.lookupByResourceName("roles");
        return def.getService();
    }

}
