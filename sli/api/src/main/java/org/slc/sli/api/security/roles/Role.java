/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.api.security.roles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.domain.enums.Right;

/**
 * A simple class to encapsulate a role
 */
public class Role {
    private String name;
    private Set<Right> rights = new HashSet<Right>();
    private String id = "";
    private boolean admin = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Set<Right> getRights() {
        return rights;
    }

    public void addRight(Right right) {
        rights.add(right);
    }
    
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
    
    /**
     * Determines whether this is an admin role, which means it's
     * only applicable to user of the SLI IDP.
     * @return
     */
    public boolean isAdmin() {
        return admin;
    }

    public String getSpringRoleName() {
        return "ROLE_" + getName().toUpperCase().replace(' ', '_');
    }

    public EntityBody getRoleAsEntityBody() {
        EntityBody body = new EntityBody();
        body.put("name", getName());
        List<String> rightStrings = new ArrayList<String>();
        for (Right right : rights) {
            rightStrings.add(right.toString());
        }
        body.put("rights", rightStrings);
        body.put("admin", admin);
        return body;
    }
}
