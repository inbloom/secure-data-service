package org.slc.sli.api.security.roles;

import org.slc.sli.api.representation.EntityBody;

import java.util.HashSet;
import java.util.Set;

/**
 * A simple class to encapsulate a role
 */
public class Role {
    private String name;
    private Set<Right> rights = new HashSet<Right>();

    public Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasRight(Right right) {
        return rights.contains(right);
    }

    public void addRight(Right right) {
        rights.add(right);
    }

    public String getSpringRoleName() {
        return "ROLE_" + name;
    }
    
    public EntityBody getRoleAsEntityBody() {
        EntityBody body = new EntityBody();
        body.put("name", getName());
        body.put("rights", rights);
        return body;
    }
}
