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


package org.slc.sli.test.edfi.entities.meta;

import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;

public class StaffMeta {
    public final String id;
    public final String edOrgId;

    public final String simpleId;

    private StaffMeta(String id, String edOrgId) {
        this.id = id;
        this.edOrgId = edOrgId;
        this.simpleId = id;
    }

    public static StaffMeta createWithChainedId(String id, SeaMeta seaMeta) {
        return new StaffMeta(seaMeta.id + MetaRelations.ID_DELIMITER + id, seaMeta.id);
    }

    @Override
    public String toString() {
        return "StaffMeta [id=" + id + ", edOrgId=" + edOrgId + "]";
    }

}
