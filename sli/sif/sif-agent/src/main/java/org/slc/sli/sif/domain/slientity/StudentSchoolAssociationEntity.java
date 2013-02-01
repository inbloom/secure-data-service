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
 * Represents the student school association in SLI datamodel
 *
 * @author syau
 *
 */
public class StudentSchoolAssociationEntity extends SliEntity {

    // mandatory fields
    String studentId;
    String schoolId;
    String entryDate;
    String entryGradeLevel;

    // optional fields
    String schoolYear;
    String entryType;
    boolean repeatGradeIndicator;
    String classOf;
    boolean schoolChoiceTransfer;
    String exitWithdrawDate;
    String exitWithdrawType;
    List<String> educationalPlans;

    // GraduationPlan _graduationPlan; <-- not supported in SIF

    @Override
    public String entityType() {
        return "studentSchoolAssociation";
    }

    // accessors
    public String getStudentId() {
        return studentId;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public String getEntryGradeLevel() {
        return entryGradeLevel;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public String getEntryType() {
        return entryType;
    }

    public boolean getRepeatGradeIndicator() {
        return repeatGradeIndicator;
    }

    public String getClassOf() {
        return classOf;
    }

    public boolean getSchoolChoiceTransfer() {
        return schoolChoiceTransfer;
    }

    public String getExitWithdrawDate() {
        return exitWithdrawDate;
    }

    public String getExitWithdrawType() {
        return exitWithdrawType;
    }

    public List<String> getEducationalPlans() {
        return educationalPlans;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    public void setEntryGradeLevel(String entryGradeLevel) {
        this.entryGradeLevel = entryGradeLevel;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }

    public void setClassOf(String classOf) {
        this.classOf = classOf;
    }

    public void setExitWithdrawDate(String exitWithdrawDate) {
        this.exitWithdrawDate = exitWithdrawDate;
    }

    public void setExitWithdrawType(String exitWithdrawType) {
        this.exitWithdrawType = exitWithdrawType;
    }

    public void setEducationalPlans(List<String> educationalPlans) {
        this.educationalPlans = educationalPlans;
    }
}
