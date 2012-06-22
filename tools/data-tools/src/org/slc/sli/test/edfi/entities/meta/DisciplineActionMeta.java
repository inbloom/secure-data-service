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


package org.slc.sli.test.edfi.entities.meta;

import java.util.Set;
import java.util.HashSet;

public class DisciplineActionMeta {

    public Set<String> incidentIds;
    public Set<String> studentIds;
    public Set<String> staffIds;

    public String schoolId; 
    
    public final String id;

    public DisciplineActionMeta(String id, SchoolMeta schoolMeta) {
        this.id = schoolMeta.id + "-" + id;

        incidentIds = new HashSet<String>();
        staffIds = new HashSet<String>();
        studentIds = new HashSet<String>();

        schoolId = schoolMeta.id;
    }

    @Override
    public String toString() {
        return "DisciplineActionMeta [id=" + id + ",incidentIds=" + incidentIds + ",staffIds=" + staffIds + ",studentIds=" + studentIds + "]";
    }

}
