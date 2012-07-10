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

package org.slc.sli.domain.enums;

import org.springframework.security.core.GrantedAuthority;

/**
 * A simple enum describing our basic rights that are required.
 * ADMIN_ACCESS -> allows operations on entities in Admin Sphere
 * FULL_ACCESS -> allows operations on all entities everywhere without regard for associations
 */
public enum Right implements GrantedAuthority {
    ANONYMOUS_ACCESS, READ_GENERAL, WRITE_GENERAL, READ_RESTRICTED, WRITE_RESTRICTED, AGGREGATE_READ, AGGREGATE_WRITE, ADMIN_ACCESS, FULL_ACCESS, CRUD_REALM_ROLES, ROLE_CRUD, SLC_APP_APPROVE, EDORG_APP_AUTHZ, EDORG_DELEGATE, DEV_APP_CRUD, INGEST_DATA, CRUD_SLC_OPERATOR, CRUD_SEA_ADMIN, CRUD_LEA_ADMIN, CRUD_APP_DEVELOPER, READ_PUBLIC;

    @Override
    public String getAuthority() {
        return this.toString();
    }

}
