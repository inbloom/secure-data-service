package org.slc.sli.api.security.roles;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.enums.Rights;

import java.util.ArrayList;
import java.util.List;


public class RoleBuilder {

    EntityBody body;
    List<String> rights;
    public RoleBuilder(String name) {
        body = new EntityBody();
        rights = new ArrayList<String>();
        body.put("name", name);
    }
    
    public void addName(String name) {
        body.put("name", name);
    }

    public void addRight(Rights right) {
        rights.add(right.getRight());
    }

    public void addRights(Rights[] rights) {
        checkAndClearRights();
        for (Rights right : rights) {
            addRight(right);
        }
    }

    private void checkAndClearRights() {
        if(this.rights.size() != 0) {
            this.rights.clear();
        }
    }

    public void addRight (String right) {
        rights.add(right);
    }
    
    public void addRights(List<String> rights) {
        checkAndClearRights();
        for (String right : rights) {
            addRight(right);
        }
    }
    
    public void addRights(Object rights) {
        checkAndClearRights();
        body.put("rights", rights);
    }

    public EntityBody build() {
        if(!body.containsKey("rights"))
            body.put("rights", rights);
        return body;
    }

}
