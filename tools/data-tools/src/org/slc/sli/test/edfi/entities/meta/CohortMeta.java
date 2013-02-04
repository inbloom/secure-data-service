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

public class CohortMeta {

    public Set<String> staffIds;
    public Set<String> studentIds;
    public ProgramMeta programMeta;
    public SchoolMeta schoolMeta;

    public final String id;

    /**
     * Constructor for meta data for cohort affiliated with a program
     *
     * @param id
     * @param programMeta
     */
    public CohortMeta(String id, ProgramMeta programMeta) {
        String simplifiedProgramId = programMeta.id.replaceAll("[a-z]", "");
        this.id = simplifiedProgramId + "-c-" + id;

        staffIds = new HashSet<String>();
        studentIds = new HashSet<String>();

        this.programMeta = programMeta;
        this.schoolMeta = null;
    }

    /**
     * Constructor for meta data for cohort not affiliated with a program
     * @param id
     * @param programMeta
     */
    public CohortMeta(String id, SchoolMeta schoolMeta) {
        String simplifiedSchoolId = schoolMeta.id.replaceAll("[a-z]", "");
        this.id = simplifiedSchoolId + "-sch-" + id;

        staffIds = new HashSet<String>();
        studentIds = new HashSet<String>();

        this.programMeta = null;
        this.schoolMeta = schoolMeta;
    }

    @Override
    public String toString() {
        return "CohortMeta [id=" + id + ", staffIds=" + staffIds + ",studentIds=" + studentIds + "]";
    }

}
