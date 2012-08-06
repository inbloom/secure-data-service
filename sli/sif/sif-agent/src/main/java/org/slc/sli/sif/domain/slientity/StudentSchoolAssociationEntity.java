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

package org.slc.sli.sif.domain.slientity;

import java.util.List;

/**
 * Represents the student school association in SLI datamodel
 * @author syau
 *
 */
public class StudentSchoolAssociationEntity extends SliEntity {

    // mandatory fields
    String _studentId;
    String _schoolId;
    String _entryDate;
    String _entryGradeLevel;
    
    // optional fields
    String _schoolYear; 
    String _entryType;
    boolean _repeatGradeIndicator;
    String _classOf;
    boolean _schoolChoiceTransfer;
    String _exitWithdrawDate;
    String _exitWithdrawType;
    List<String> _educationalPlans;
    // GraduationPlan _graduationPlan; <-- not supported in SIF
    
    @Override
    public String entityType() {
        return "studentSchoolAssociation";
    }

    // accessors
    public String getStudentId() { return  _studentId; }
    public String getSchoolId() { return  _schoolId; }
    public String getEntryDate() { return  _entryDate; }
    public String getEntryGradeLevel() { return  _entryGradeLevel; }
    public String getSchoolYear() { return  _schoolYear; }
    public String getEntryType() { return  _entryType; }
    public boolean getRepeatGradeIndicator() { return  _repeatGradeIndicator; }
    public String getClassOf() { return  _classOf; }
    public boolean getSchoolChoiceTransfer() { return  _schoolChoiceTransfer; }
    public String getExitWithdrawDate() { return  _exitWithdrawDate; }
    public String getExitWithdrawType() { return  _exitWithdrawType; }
    public List<String> getEducationalPlans() { return _educationalPlans; }
    
    public void setStudentId(String s) { _studentId = s; } 
    public void setSchoolId(String s) { _schoolId = s; }
    public void setEntryDate(String s) { _entryDate = s; }
    public void setEntryGradeLevel(String s) {  _entryGradeLevel = s; }
    public void setSchoolYear(String s) {  _schoolYear = s; }
    public void setEntryType(String s) { _entryType = s; }
    public void setClassOf(String s) { _classOf = s; }
    public void setExitWithdrawDate(String s) { _exitWithdrawDate = s; }
    public void setExitWithdrawType(String s) { _exitWithdrawType = s; }
    public void setEducationalPlans(List<String> l) { _educationalPlans = l; }
}
