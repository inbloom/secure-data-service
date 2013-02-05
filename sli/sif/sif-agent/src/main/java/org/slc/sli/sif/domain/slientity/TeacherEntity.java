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

package org.slc.sli.sif.domain.slientity;

/**
 * Represents the teacher in SLI datamodel
 *
 * @author slee
 *
 */
public class TeacherEntity extends StaffEntity {
    // optional fields
    private boolean highlyQualifiedTeacher;

    /**
     * Constructor
     */
    public TeacherEntity() {
        super();
    }

    public String getTeacherUniqueStateId() {
        return getStaffUniqueStateId();
    }

    public void setTeacherUniqueStateId(String teacherUniqueStateId) {
        setStaffUniqueStateId(teacherUniqueStateId);
    }

    public boolean getHighlyQualifiedTeacher() {
        return this.highlyQualifiedTeacher;
    }

    public void setHighlyQualifiedTeacher(boolean highlyQualifiedTeacher) {
        this.highlyQualifiedTeacher = highlyQualifiedTeacher;
    }

    @Override
    public String entityType() {
        return "teacher";
    }

}
