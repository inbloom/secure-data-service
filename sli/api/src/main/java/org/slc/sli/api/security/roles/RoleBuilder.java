package org.slc.sli.api.security.roles;


import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.roles.Right;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple class to help build a Role in terms of their associated rights.
 *
 * Currently this doesn't have much use, but will when we add custom roles.
 *
 */
public final class RoleBuilder {

    Role role;
    List<String> rights;
    
    public static RoleBuilder makeRole(String name) {
        return new RoleBuilder(name);
    }
    
    private RoleBuilder(String name) {
        role = new Role(name);
        rights = new ArrayList<String>();
        
    }
    
    public RoleBuilder addName(String name) {
        role.setName(name);
        return this;
    }

    public RoleBuilder addRight(Right right) {
        role.addRight(right);
        return this;
    }

    public RoleBuilder addRights(Right[] rights) {
        for (Right right : rights) {
            addRight(right);
        }
        return this;
    }

    public RoleBuilder addRight(String right) {
        role.addRight(new Right(right));
        return this;
    }
    
    public RoleBuilder addRights(List<String> rights) {
        for (String right : rights) {
            addRight(new Right(right));
        }
        return this;
    }

    public EntityBody buildEntityBody() {
        return role.getRoleAsEntityBody();
    }
    
    public Role buildRole() {
        return role;
    }

}
