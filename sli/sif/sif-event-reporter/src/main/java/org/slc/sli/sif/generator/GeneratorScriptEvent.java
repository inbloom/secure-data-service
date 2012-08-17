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

package org.slc.sli.sif.generator;

/**
 * Contains valid values for command line scripting
 *
 * @author vmcglaughlin
 *
 */
public class GeneratorScriptEvent {

    public static final String KEY_LEA_INFO_ADD = "LEAInfoAdd";
    public static final String KEY_LEA_INFO_CHANGE = "LEAInfoChange";
    public static final String KEY_LEA_INFO_DELETE = "LEAInfoDelete";

    public static final String KEY_SCHOOL_INFO_ADD = "SchoolInfoAdd";
    public static final String KEY_SCHOOL_INFO_CHANGE = "SchoolInfoChange";
    public static final String KEY_SCHOOL_INFO_DELETE = "SchoolInfoDelete";

    public static final String KEY_STUDENT_PERSONAL_ADD = "StudentPersonalAdd";
    public static final String KEY_STUDENT_PERSONAL_CHANGE = "StudentPersonalChange";
    public static final String KEY_STUDENT_PERSONAL_DELETE = "StudentPersonalDelete";

    public static final String KEY_STUDENT_LEA_RELATIONSHIP_ADD = "StudentLEARelationshipAdd";
    public static final String KEY_STUDENT_LEA_RELATIONSHIP_CHANGE = "StudentLEARelationshipChange";
    public static final String KEY_STUDENT_LEA_RELATIONSHIP_DELETE = "StudentLEARelationshipDelete";

    public static final String KEY_STUDENT_SCHOOL_ENROLLMENT_ADD = "StudentSchoolEnrollmentAdd";
    public static final String KEY_STUDENT_SCHOOL_ENROLLMENT_CHANGE = "StudentSchoolEnrollmentChange";
    public static final String KEY_STUDENT_SCHOOL_ENROLLMENT_DELETE = "StudentSchoolEnrollmentDelete";

    public static final String KEY_STAFF_PERSONAL_ADD = "StaffPersonalAdd";
    public static final String KEY_STAFF_PERSONAL_CHANGE = "StaffPersonalChange";
    public static final String KEY_STAFF_PERSONAL_DELETE = "StaffPersonalDelete";

    public static final String KEY_EMPLOYEE_PERSONAL_ADD = "EmployeePersonalAdd";
    public static final String KEY_EMPLOYEE_PERSONAL_CHANGE = "EmployeePersonalChange";
    public static final String KEY_EMPLOYEE_PERSONAL_DELETE = "EmployeePersonalDelete";

    public static final String KEY_STAFF_ASSIGNMENT_ADD = "StaffAssignmentAdd";
    public static final String KEY_STAFF_ASSIGNMENT_CHANGE = "StaffAssignmentChange";
    public static final String KEY_STAFF_ASSIGNMENT_DELETE = "StaffAssignmentDelete";

    public static final String KEY_EMPLOYMENT_RECORD_ADD = "EmploymentRecordAdd";
    public static final String KEY_EMPLOYMENT_RECORD_CHANGE = "EmploymentRecordChange";
    public static final String KEY_EMPLOYMENT_RECORD_DELETE = "EmploymentRecordDelete";
}
