/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

import java.util.HashSet;
import java.util.Set;

import org.slc.sli.domain.enums.Right;
import org.springframework.security.core.GrantedAuthority;

/**
 * A simple class to encapsulate a role
 */
public class Role {
    private String name;
    private Set<GrantedAuthority> rights = new HashSet<GrantedAuthority>();
    private boolean admin = false;

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

    public Set<GrantedAuthority> getRights() {
        return rights;
    }
    
    public Set<String> getRightsAsStrings() {
        Set<String> strings = new HashSet<String>();
        for (GrantedAuthority authority : rights) {
            strings.add(authority.getAuthority());
        }
        return strings;
    }

    public void addRight(GrantedAuthority right) {
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

    public String toString() {
        return name;
    }
}
