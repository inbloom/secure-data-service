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

public class SectionMeta {
    public final String id;
    public final String schoolId;
    public final CourseOfferingMeta courseOffering;
    public final String sessionId;
    public final String programId;
    //public final String studentId;

    public final String simpleId;

    public SectionMeta(String id, SchoolMeta schoolMeta, CourseOfferingMeta courseOfferingMeta, SessionMeta sessionMeta,
                       ProgramMeta programMeta) {

        String schoolIdNoAlpha = schoolMeta.id.replaceAll("[a-z]", "");
        String sessionIdNoAlpha = courseOfferingMeta.simpleId.replaceAll("[a-z]", "");
        String courseIdNoAlpha = sessionMeta.simpleId.replaceAll("[a-z]", "");

        this.id = schoolIdNoAlpha + MetaRelations.ID_DELIMITER + sessionIdNoAlpha + MetaRelations.ID_DELIMITER + courseIdNoAlpha + MetaRelations.ID_DELIMITER + id;
        this.schoolId = schoolMeta.id;
        this.courseOffering = courseOfferingMeta;
        this.sessionId = sessionMeta.id;
        this.programId = programMeta == null ? null : programMeta.id;
       // this.studentId = studentMeta.id;

        this.simpleId = id;
    }

    @Override
    public String toString() {
        return "SectionMeta [id=" + id + ", schoolId=" + schoolId + ", courseOffering=[" + courseOffering.toString() + "], sessionId="
                + sessionId + ", programId=" + programId + "]";
    }

}
