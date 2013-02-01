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

import java.util.List;

/**
 * Represents the teacherSchoolAssociation in SLI datamodel
 *
 * @author slee
 *
 */
public class TeacherSchoolAssociationEntity extends SliEntity {
    // mandatory fields
    private String teacherId;
    private String schoolId;
    private String programAssignment;

    // optional fields
    private List<String> instructionalGradeLevels;
    private List<String> academicSubjects;

    /**
     * Constructor
     */
    public TeacherSchoolAssociationEntity() {
        super();
    }

    public String getTeacherId() {
        return this.teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getSchoolId() {
        return this.schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getProgramAssignment() {
        return this.programAssignment;
    }

    public void setProgramAssignment(String programAssignment) {
        this.programAssignment = programAssignment;
    }

    public List<String> getInstructionalGradeLevels() {
        return this.instructionalGradeLevels;
    }

    public void setInstructionalGradeLevels(List<String> instructionalGradeLevels) {
        this.instructionalGradeLevels = instructionalGradeLevels;
    }

    public List<String> getAcademicSubjects() {
        return this.academicSubjects;
    }

    public void setAcademicSubjects(List<String> academicSubjects) {
        this.academicSubjects = academicSubjects;
    }

    @Override
    public String entityType() {
        return "teacherSchoolAssociation";
    }

}

