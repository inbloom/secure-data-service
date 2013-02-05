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

package org.slc.sli.sif.generator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slc.sli.sif.reporting.EventReporter;

import openadk.library.Event;
import openadk.library.EventAction;

/**
 * Contains valid values for command line scripting
 *
 * @author vmcglaughlin
 *
 */
public final class GeneratorScriptMethod {

    public static final GeneratorScriptMethod LEA_INFO_ADD = new GeneratorScriptMethod("reportLeaInfoEvent", EventAction.ADD);
    public static final GeneratorScriptMethod LEA_INFO_CHANGE = new GeneratorScriptMethod("reportLeaInfoEvent", EventAction.CHANGE);
    public static final GeneratorScriptMethod LEA_INFO_DELETE = new GeneratorScriptMethod("reportLeaInfoEvent", EventAction.DELETE);

    public static final GeneratorScriptMethod SCHOOL_INFO_ADD = new GeneratorScriptMethod("reportSchoolInfoEvent", EventAction.ADD);
    public static final GeneratorScriptMethod SCHOOL_INFO_CHANGE = new GeneratorScriptMethod("reportSchoolInfoEvent", EventAction.CHANGE);
    public static final GeneratorScriptMethod SCHOOL_INFO_DELETE = new GeneratorScriptMethod("reportSchoolInfoEvent", EventAction.DELETE);

    public static final GeneratorScriptMethod STUDENT_PERSONAL_ADD = new GeneratorScriptMethod("reportStudentPersonalEvent", EventAction.ADD);
    public static final GeneratorScriptMethod STUDENT_PERSONAL_CHANGE = new GeneratorScriptMethod("reportStudentPersonalEvent", EventAction.CHANGE);
    public static final GeneratorScriptMethod STUDENT_PERSONAL_DELETE = new GeneratorScriptMethod("reportStudentPersonalEvent", EventAction.DELETE);

    public static final GeneratorScriptMethod STUDENT_LEA_RELATIONSHIP_ADD = new GeneratorScriptMethod("reportStudentLeaRelationshipEvent", EventAction.ADD);
    public static final GeneratorScriptMethod STUDENT_LEA_RELATIONSHIP_CHANGE = new GeneratorScriptMethod("reportStudentLeaRelationshipEvent", EventAction.CHANGE);
    public static final GeneratorScriptMethod STUDENT_LEA_RELATIONSHIP_DELETE = new GeneratorScriptMethod("reportStudentLeaRelationshipEvent", EventAction.DELETE);

    public static final GeneratorScriptMethod STUDENT_SCHOOL_ENROLLMENT_ADD = new GeneratorScriptMethod("reportStudentSchoolEnrollmentEvent", EventAction.ADD);
    public static final GeneratorScriptMethod STUDENT_SCHOOL_ENROLLMENT_CHANGE = new GeneratorScriptMethod("reportStudentSchoolEnrollmentEvent", EventAction.CHANGE);
    public static final GeneratorScriptMethod STUDENT_SCHOOL_ENROLLMENT_DELETE = new GeneratorScriptMethod("reportStudentSchoolEnrollmentEvent", EventAction.DELETE);

    public static final GeneratorScriptMethod STAFF_PERSONAL_ADD = new GeneratorScriptMethod("reportStaffPersonalEvent", EventAction.ADD);
    public static final GeneratorScriptMethod STAFF_PERSONAL_CHANGE = new GeneratorScriptMethod("reportStaffPersonalEvent", EventAction.CHANGE);
    public static final GeneratorScriptMethod STAFF_PERSONAL_DELETE = new GeneratorScriptMethod("reportStaffPersonalEvent", EventAction.DELETE);

    public static final GeneratorScriptMethod EMPLOYEE_PERSONAL_ADD = new GeneratorScriptMethod("reportEmployeePersonalEvent", EventAction.ADD);
    public static final GeneratorScriptMethod EMPLOYEE_PERSONAL_CHANGE = new GeneratorScriptMethod("reportEmployeePersonalEvent", EventAction.CHANGE);
    public static final GeneratorScriptMethod EMPLOYEE_PERSONAL_DELETE = new GeneratorScriptMethod("reportEmployeePersonalEvent", EventAction.DELETE);

    public static final GeneratorScriptMethod STAFF_ASSIGNMENT_ADD = new GeneratorScriptMethod("reportStaffAssignmentEvent", EventAction.ADD);
    public static final GeneratorScriptMethod STAFF_ASSIGNMENT_CHANGE = new GeneratorScriptMethod("reportStaffAssignmentEvent", EventAction.CHANGE);
    public static final GeneratorScriptMethod STAFF_ASSIGNMENT_DELETE = new GeneratorScriptMethod("reportStaffAssignmentEvent", EventAction.DELETE);

    public static final GeneratorScriptMethod EMPLOYMENT_RECORD_ADD = new GeneratorScriptMethod("reportEmploymentRecordEvent", EventAction.ADD);
    public static final GeneratorScriptMethod EMPLOYMENT_RECORD_CHANGE = new GeneratorScriptMethod("reportEmploymentRecordEvent", EventAction.CHANGE);
    public static final GeneratorScriptMethod EMPLOYMENT_RECORD_DELETE = new GeneratorScriptMethod("reportEmploymentRecordEvent", EventAction.DELETE);

    public static final GeneratorScriptMethod EMPLOYEE_ASSIGNMENT_ADD = new GeneratorScriptMethod("reportEmployeeAssignmentEvent", EventAction.ADD);
    public static final GeneratorScriptMethod EMPLOYEE_ASSIGNMENT_CHANGE = new GeneratorScriptMethod("reportEmployeeAssignmentEvent", EventAction.CHANGE);
    public static final GeneratorScriptMethod EMPLOYEE_ASSIGNMENT_DELETE = new GeneratorScriptMethod("reportEmployeeAssignmentEvent", EventAction.DELETE);

    private static Map<String, GeneratorScriptMethod> eventMap = new HashMap<String, GeneratorScriptMethod>();
    static {
        eventMap.put("LEAInfoAdd", LEA_INFO_ADD);
        eventMap.put("LEAInfoChange", LEA_INFO_CHANGE);
        eventMap.put("LEAInfoDelete", LEA_INFO_DELETE);
        eventMap.put("SchoolInfoAdd", SCHOOL_INFO_ADD);
        eventMap.put("SchoolInfoChange", SCHOOL_INFO_CHANGE);
        eventMap.put("SchoolInfoDelete", SCHOOL_INFO_DELETE);
        eventMap.put("StudentPersonalAdd", STUDENT_PERSONAL_ADD);
        eventMap.put("StudentPersonalChange", STUDENT_PERSONAL_CHANGE);
        eventMap.put("StudentPersonalDelete", STUDENT_PERSONAL_DELETE);
        eventMap.put("StudentLEARelationshipAdd", STUDENT_LEA_RELATIONSHIP_ADD);
        eventMap.put("StudentLEARelationshipChange", STUDENT_LEA_RELATIONSHIP_CHANGE);
        eventMap.put("StudentLEARelationshipDelete", STUDENT_LEA_RELATIONSHIP_DELETE);
        eventMap.put("StudentSchoolEnrollmentAdd", STUDENT_SCHOOL_ENROLLMENT_ADD);
        eventMap.put("StudentSchoolEnrollmentChange", STUDENT_SCHOOL_ENROLLMENT_CHANGE);
        eventMap.put("StudentSchoolEnrollmentDelete", STUDENT_SCHOOL_ENROLLMENT_DELETE);
        eventMap.put("StaffPersonalAdd", STAFF_PERSONAL_ADD);
        eventMap.put("StaffPersonalChange", STAFF_PERSONAL_CHANGE);
        eventMap.put("StaffPersonalDelete", STAFF_PERSONAL_DELETE);
        eventMap.put("EmployeePersonalAdd", EMPLOYEE_PERSONAL_ADD);
        eventMap.put("EmployeePersonalChange", EMPLOYEE_PERSONAL_CHANGE);
        eventMap.put("EmployeePersonalDelete", EMPLOYEE_PERSONAL_DELETE);
        eventMap.put("StaffAssignmentAdd", STAFF_ASSIGNMENT_ADD);
        eventMap.put("StaffAssignmentChange", STAFF_ASSIGNMENT_CHANGE);
        eventMap.put("StaffAssignmentDelete", STAFF_ASSIGNMENT_DELETE);
        eventMap.put("EmploymentRecordAdd", EMPLOYMENT_RECORD_ADD);
        eventMap.put("EmploymentRecordChange", EMPLOYMENT_RECORD_CHANGE);
        eventMap.put("EmploymentRecordDelete", EMPLOYMENT_RECORD_DELETE);
        eventMap.put("EmployeeAssignmentAdd", EMPLOYEE_ASSIGNMENT_ADD);
        eventMap.put("EmployeeAssignmentChange", EMPLOYEE_ASSIGNMENT_CHANGE);
        eventMap.put("EmployeeAssignmentDelete", EMPLOYEE_ASSIGNMENT_DELETE);
    }

    private EventAction action;
    private String methodName;

    private GeneratorScriptMethod() {

    }

    private GeneratorScriptMethod(String methodName, EventAction action) {
        this.methodName = methodName;
        this.action = action;
    }

    public Event execute(EventReporter executingClass) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
        Method method = EventReporter.class.getMethod(methodName, EventAction.class);
        return (Event) method.invoke(executingClass, action);
    }

    public static GeneratorScriptMethod get(String descriptor) {
        return eventMap.get(descriptor);
    }

    public String toString() {
        return "Method: " + methodName + "\taction: " + action;
    }

}
