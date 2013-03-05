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


package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.SLCStaffReferenceType;
import org.slc.sli.test.edfi.entities.StateAbbreviationType;
import org.slc.sli.test.edfi.entities.Teacher;

public class TeacherGenerator extends StaffGenerator {
    public static int counter = 0;

    public TeacherGenerator(StateAbbreviationType state, boolean optional) {
        super(state, optional);
    }

    public Teacher generate(String teacherId) throws Exception {
        Teacher teacher = new Teacher();
        populateFields(teacher, teacherId);
        return teacher;
    }

    protected void populateFields(Teacher teacher, String teacherId) throws Exception {
        super.populateFields(teacher, teacherId);
        teacher.setHighlyQualifiedTeacher(random.nextBoolean());
    }

    public static Teacher generateLowFi(String teacherId) {
        Teacher teacher = new Teacher();
        populateFieldsLowFi(teacher, teacherId);
        return teacher;
    }

    public static Teacher generateMediumFi (String teacherId) throws Exception{
        Teacher teacher = new Teacher();
        StaffGenerator.populateFields(teacher, teacherId);
         teacher.setHighlyQualifiedTeacher(random.nextBoolean());
        return teacher;

    }

    public static SLCStaffReferenceType getTeacherReference(String staffId) {
        return getStaffReference(staffId);
    }
}
