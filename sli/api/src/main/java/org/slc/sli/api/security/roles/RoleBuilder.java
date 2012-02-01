package org.slc.sli.api.security.roles;

import java.util.List;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.domain.enums.Right;

/**
 * A simple class to help build a Role in terms of their associated rights.
 *
 * Currently this doesn't have much use, but will when we add custom roles.
 */
public final class RoleBuilder {

    Role role;

    public static RoleBuilder makeRole(String name) {
        return new RoleBuilder(name);
    }

    private RoleBuilder(String name) {
        role = new Role(name);

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
            role.addRight(right);
        }
        return this;
    }

    public RoleBuilder addRight(String right) {
        role.addRight(Right.valueOf(right));
        return this;
    }

    public RoleBuilder addRights(List<String> rights) {
        for (String right : rights) {
            role.addRight(Right.valueOf(right));
        }
        return this;
    }

    public EntityBody buildEntityBody() {
        return role.getRoleAsEntityBody();
    }

    public Role build() {
        return role;
    }

    public void addRight(Object right) {
        if (right instanceof String) {
            addRight(Right.valueOf((String) right));
        }
    }


    @SuppressWarnings("unchecked")
    public static RoleBuilder makeRole(EntityBody entityBody) {
        RoleBuilder resultRole = new RoleBuilder((String) entityBody.get("name"));
        resultRole.addRights((List<String>) entityBody.get("rights"));
        return resultRole;

    }

    public RoleBuilder addId(String id) {
        role.setId(id);
        return this;
    }
}
