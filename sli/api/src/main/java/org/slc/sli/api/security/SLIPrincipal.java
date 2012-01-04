package org.slc.sli.api.security;

import org.slc.sli.api.security.enums.DefaultRoles;
import org.slc.sli.api.security.enums.Rights;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Attribute holder for SLI Principal
 *
 * @author dkornishev
 */
public class SLIPrincipal implements Principal {

    private String id;
    private String name;
    private String state;
    private List<String> theirRoles;
    private Set<Rights> rights = new HashSet<Rights>();

    @Override
    public String getName() {
        return this.name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTheirRoles(List<String> theirRoles) {
        this.theirRoles = theirRoles;
        setRights(theirRoles);
    }

    public List<String> getTheirRoles() {
        return theirRoles;
    }

    private void setRights(List<String> roleNames) {
        rights.clear();
        for (String roleName : roleNames) {
            DefaultRoles role = DefaultRoles.find(roleName);
            if (role != null) {
                rights.addAll(role.getRights());
            }
        }
    }

    public Set<Rights> getRights() {
        return rights;
    }
}
