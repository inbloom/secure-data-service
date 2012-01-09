package org.slc.sli.api.security.roles;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.enums.Right;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple class to help build a Role in terms of their associated rights.
 *
 * Currently this doesn't have much use, but will when we add custom roles.
 *
 */
public final class RoleBuilder {

    EntityBody body;
    List<String> rights;
    
    public static RoleBuilder makeRole(String name) {
        return new RoleBuilder(name);
    }
    
    private RoleBuilder(String name) {
        body = new EntityBody();
        rights = new ArrayList<String>();
        body.put("name", name);
    }
    
    public RoleBuilder addName(String name) {
        body.put("name", name);
        return this;
    }

    public RoleBuilder addRight(Right right) {
        rights.add(right.toString());
        return this;
    }

    public RoleBuilder addRights(Right[] rights) {
        checkAndClearRights();
        for (Right right : rights) {
            addRight(right);
        }
        return this;
    }

    private void checkAndClearRights() {
        if (this.rights.size() != 0) {
            this.rights.clear();
        }
    }

    public RoleBuilder addRight(String right) {
        rights.add(right);
        return this;
    }
    
    public RoleBuilder addRights(List<String> rights) {
        checkAndClearRights();
        for (String right : rights) {
            addRight(right);
        }
        return this;
    }
    
    public RoleBuilder addRights(Object rights) {
        checkAndClearRights();
        body.put("rights", rights);
        return this;
    }

    public EntityBody build() {
        if (!body.containsKey("rights"))
            body.put("rights", rights);
        return body;
    }

}
