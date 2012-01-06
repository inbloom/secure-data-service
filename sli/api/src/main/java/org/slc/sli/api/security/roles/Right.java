package org.slc.sli.api.security.roles;

import org.springframework.security.core.GrantedAuthority;

/**
 * A class to encapsulate a right.
 */
public class Right implements GrantedAuthority {
    private String name;

    public Right(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getAuthority() {
        return getName();
    }

    @Override
    public int hashCode() {
        return getAuthority().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof GrantedAuthority && ((GrantedAuthority) o).getAuthority().equals(getAuthority());
    }
}
