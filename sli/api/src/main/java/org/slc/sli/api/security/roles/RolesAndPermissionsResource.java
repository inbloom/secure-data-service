package org.slc.sli.api.security.roles;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.security.enums.Rights;
import org.slc.sli.api.service.EntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;

import java.security.PublicKey;
import java.util.*;

@Path("/admin")
@Component
@Scope("request")
@Produces("application/json")

public class RolesAndPermissionsResource {

    @Autowired
    private EntityDefinitionStore store;

    private static final Logger LOG = LoggerFactory.getLogger(RolesAndPermissionsResource.class);

    private Map<String, Object> createRolesAndPermissions() {
        Map<String, Object> role = new HashMap<String, Object>();
        List<String> permissions = new ArrayList<String>();
        permissions.add(Rights.READ_GENERAL.getRight());
        permissions.add(Rights.WRITE_GENERAL.getRight());
        role.put("name", "Role1");
        role.put("permissions", permissions);
        return role;
    }
    @GET
    @Path("roles")
    public Object getRolesAndPermissions() {
        Map<String, Object> roles = new TreeMap<String, Object>();
        EntityDefinition def = store.lookupByResourceName("roles");
        EntityService service = def.getService();

        //Add some data here.
        service.create(new EntityBody(createRolesAndPermissions()));

        Iterable<String> names = service.list(0, 100);
        Iterable<EntityBody> entities = service.get(names);
        for(EntityBody body : entities) {
            roles.put("name", body.get("name"));
            roles.put("permissions", body.get("permissions"));
        }
        return roles;
    }
    
    @POST
    @Path("roles")
    public void createRoleWithPermission(String name, Object permissions) {
        Map<String, Object> role = new TreeMap<String, Object>();
        role.put("name", name);
        role.put("permissions", permissions);
        EntityBody body = new EntityBody(role);
        EntityDefinition def = store.lookupByResourceName("roles");
        EntityService service = def.getService();
        service.create(body);
    }
}
