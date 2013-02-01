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

import java.util.Set;
import java.util.HashSet;

import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;

public class ProgramMeta {

    public Set<String> staffIds;
    public Set<String> studentIds;

    public Set<String> cohortIds;

    public String orgId; // this is used in generating studentProgram associations
    
    public final String id;
    private static int uniquer = 0;

    public ProgramMeta(String id, SchoolMeta schoolMeta) {
        this.id = id + MetaRelations.ID_DELIMITER + uniquer++;

        staffIds = new HashSet<String>();
        studentIds = new HashSet<String>();

        cohortIds = new HashSet<String>();

        orgId = schoolMeta.id;
    }
    
    
    public ProgramMeta(String id, SeaMeta seaMeta) {
        this.id = id + MetaRelations.ID_DELIMITER + uniquer++;

        staffIds = new HashSet<String>();
        studentIds = new HashSet<String>();

        cohortIds = new HashSet<String>();

        orgId = seaMeta.id;
    }
    
    public ProgramMeta(String id, LeaMeta leaMeta) {
        this.id = id + MetaRelations.ID_DELIMITER + uniquer++;

        staffIds = new HashSet<String>();
        studentIds = new HashSet<String>();

        cohortIds = new HashSet<String>();

        orgId = leaMeta.id;
    }

    @Override
    public String toString() {
        return "ProgramMeta [id=" + id + ", staffIds=" + staffIds + ",studentIds=" + studentIds + 
                             ",cohortIds=" + cohortIds + "]";
    }

}
