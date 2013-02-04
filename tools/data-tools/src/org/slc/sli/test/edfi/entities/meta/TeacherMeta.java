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

import java.util.ArrayList;
import java.util.List;

import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;

public final class TeacherMeta {
    public final String id;
    public final List<String> schoolIds;
    public final List<String> sectionIds;

    public final String simpleId;

    private TeacherMeta(String id, String schoolId) {
        this.id = id;
        this.schoolIds = new ArrayList<String>();
        this.schoolIds.add(schoolId);
        this.sectionIds = new ArrayList<String>();

        this.simpleId = id;
    }

    public static TeacherMeta createWithChainedId(String id, SchoolMeta schoolMeta) {
        return new TeacherMeta(schoolMeta.id + MetaRelations.ID_DELIMITER + id, schoolMeta.id);
    }

    public static TeacherMeta create(String id, SchoolMeta schoolMeta) {
        return new TeacherMeta(id, schoolMeta.id);
    }

    @Override
    public String toString() {
        return "TeacherMeta [id=" + id + ", schoolIds=" + schoolIds + ", sectionIds=" + sectionIds + "]";
    }

}
