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

package org.slc.sli.domain.enums;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.security.core.GrantedAuthority;

/**
 * A simple enum describing our basic rights that are required.
 * ADMIN_ACCESS -> allows operations on entities in Admin Sphere
 * FULL_ACCESS -> allows operations on all entities everywhere without regard for associations
 */
public enum Right implements GrantedAuthority {
    ANONYMOUS_ACCESS, WRITE_PUBLIC, READ_GENERAL, WRITE_GENERAL, READ_RESTRICTED, WRITE_RESTRICTED, AGGREGATE_READ, AGGREGATE_WRITE, 
    ADMIN_ACCESS, FULL_ACCESS, CRUD_REALM, CRUD_ROLE, SLC_APP_APPROVE, EDORG_APP_AUTHZ, EDORG_DELEGATE, DEV_APP_CRUD, 
    INGEST_DATA, CRUD_SLC_OPERATOR, CRUD_SANDBOX_SLC_OPERATOR, CRUD_SANDBOX_ADMIN, CRUD_SEA_ADMIN, CRUD_LEA_ADMIN, READ_PUBLIC, 
    SECURITY_EVENT_VIEW, PRODUCTION_LOGIN, ACCOUNT_APPROVAL;

    public static final Right[] PROD_ADMIN_CRUD_RIGHTS = new Right[] {CRUD_LEA_ADMIN, CRUD_SEA_ADMIN, CRUD_SLC_OPERATOR};

    public static final Right[] SANDBOX_ADMIN_CRUD_RIGHTS = new Right[] {CRUD_SANDBOX_ADMIN, CRUD_SANDBOX_SLC_OPERATOR};

    public static final Right[] ALL_ADMIN_CRUD_RIGHTS = ArrayUtils.addAll(PROD_ADMIN_CRUD_RIGHTS, SANDBOX_ADMIN_CRUD_RIGHTS);
    
    public static final Right[] DEFAULT_RIGHTS = new Right[] { READ_GENERAL, WRITE_GENERAL, READ_RESTRICTED, WRITE_RESTRICTED, 
        AGGREGATE_READ, AGGREGATE_WRITE };

    @Override
    public String getAuthority() {
        return this.toString();
    }

}
