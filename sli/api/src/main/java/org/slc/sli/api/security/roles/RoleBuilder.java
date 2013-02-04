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

import java.util.List;

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
    
    public RoleBuilder addSelfRight(Right right) {
        role.addSelfRight(right);
        return this;
    }

    public RoleBuilder addSelfRights(Right[] rights) {
        for (Right right : rights) {
            role.addSelfRight(right);
        }
        return this;
    }


    public RoleBuilder addGrantedAuthorities(List<String> auths) {
		if (auths != null) {
			for (String rightName : auths) {
				try {
					role.addRight(Right.valueOf(rightName));
				} catch (IllegalArgumentException e) {
					warn("No such right: {}", rightName);
				}
			}
		}
        return this;
    }
    
    public RoleBuilder addSelfGrantedAuthorities(List<String> auths) {
		if (auths != null) {
			for (String rightName : auths) {
				try {
					role.addRight(Right.valueOf(rightName));
				} catch (IllegalArgumentException e) {
					warn("No such right: {}", rightName);
				}
			}
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

    public RoleBuilder setAdmin(boolean admin) {
        role.setAdmin(admin);
        return this;
    }

    public Role build() {
        return role;
    }

}
