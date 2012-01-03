package org.slc.sli.api.security.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple enum that describes the default roles in terms of their rights.
 * 
 * Also has a few utility functions to see if a role contains a right.
 */
public enum DefaultRoles {
    EDUCATOR("Educator", new Right[] { Right.AGGREGATE_READ, Right.READ_GENERAL }), LEADER("Leader", new Right[] { Right.AGGREGATE_READ, Right.READ_GENERAL, Right.READ_RESTRICTED }), AGGREGATOR("Aggregate Viewer",
            new Right[] { Right.AGGREGATE_READ }), ADMINISTRATOR("IT Administrator", new Right[] { Right.AGGREGATE_READ, Right.READ_GENERAL, Right.READ_RESTRICTED, Right.WRITE_GENERAL, Right.WRITE_RESTRICTED }), NONE("None",
            new Right[] {});
    private final String   name;
    private final Right[] rights;
    
    public String getRoleName() {
        return name;
    }

    public String getSpringRoleName() {
        return "ROLE_" + name.toUpperCase().replace(' ', '_');
    }

    public List<Right> getRights() {
        return Arrays.asList(rights);
    }

    public boolean hasRight(Right right) {
        for (Right checkedRight : rights) {
            if (checkedRight == right) {
                return true;
            }
        }
        return false;
    }

    private DefaultRoles(String role, Right[] rights) {
        name = role;
        this.rights = rights;
    }

    public static List<String> getDefaultRoleNames() {
        List<String> names = new ArrayList<String>();
        for (DefaultRoles role : DefaultRoles.values()) {
            names.add(role.getRoleName());
        }
        return names;
    }
    
    public static DefaultRoles getDefaultRoleByName(String name) {
        if (name.equalsIgnoreCase(DefaultRoles.EDUCATOR.getRoleName()))
            return DefaultRoles.EDUCATOR;
        if (name.equalsIgnoreCase(DefaultRoles.ADMINISTRATOR.getRoleName()))
            return DefaultRoles.ADMINISTRATOR;
        if (name.equalsIgnoreCase(DefaultRoles.AGGREGATOR.getRoleName()))
            return DefaultRoles.AGGREGATOR;
        if (name.equalsIgnoreCase(DefaultRoles.LEADER.getRoleName()))
            return DefaultRoles.LEADER;
        return DefaultRoles.NONE;
    }
}
