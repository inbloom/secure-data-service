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

import junit.framework.Assert;

import openadk.library.ADKException;
import openadk.library.EventAction;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slc.sli.sif.EventReporterAdkTest;
import org.slc.sli.sif.reporting.EventReporter;

/**
 * Test functionality of GeneratorScriptMethod
 * @author vmcglaughlin
 *
 */
public class GeneratorScriptMethodTest extends EventReporterAdkTest {

    @Mock
    EventReporter executingClass;

    @Override
    @Before
    public void setup() {
        super.setup();
        executingClass = Mockito.mock(EventReporter.class);
    }

    @Test
    public void testStaticValues() throws ADKException, IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        GeneratorScriptMethod value;
        GeneratorScriptMethod pulledValue;
        String descriptor;

        value = GeneratorScriptMethod.LEA_INFO_ADD;
        descriptor = "LEAInfoAdd";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportLeaInfoEvent(EventAction.ADD);

        value = GeneratorScriptMethod.LEA_INFO_CHANGE;
        descriptor = "LEAInfoChange";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportLeaInfoEvent(EventAction.CHANGE);

        value = GeneratorScriptMethod.LEA_INFO_DELETE;
        descriptor = "LEAInfoDelete";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportLeaInfoEvent(EventAction.DELETE);

        value = GeneratorScriptMethod.SCHOOL_INFO_ADD;
        descriptor = "SchoolInfoAdd";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportSchoolInfoEvent(EventAction.ADD);

        value = GeneratorScriptMethod.SCHOOL_INFO_CHANGE;
        descriptor = "SchoolInfoChange";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportSchoolInfoEvent(EventAction.CHANGE);

        value = GeneratorScriptMethod.SCHOOL_INFO_DELETE;
        descriptor = "SchoolInfoDelete";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportSchoolInfoEvent(EventAction.DELETE);

        value = GeneratorScriptMethod.STUDENT_PERSONAL_ADD;
        descriptor = "StudentPersonalAdd";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportStudentPersonalEvent(EventAction.ADD);

        value = GeneratorScriptMethod.STUDENT_PERSONAL_CHANGE;
        descriptor = "StudentPersonalChange";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportStudentPersonalEvent(EventAction.CHANGE);

        value = GeneratorScriptMethod.STUDENT_PERSONAL_DELETE;
        descriptor = "StudentPersonalDelete";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportStudentPersonalEvent(EventAction.DELETE);

        value = GeneratorScriptMethod.STUDENT_LEA_RELATIONSHIP_ADD;
        descriptor = "StudentLEARelationshipAdd";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportStudentLeaRelationshipEvent(EventAction.ADD);

        value = GeneratorScriptMethod.STUDENT_LEA_RELATIONSHIP_CHANGE;
        descriptor = "StudentLEARelationshipChange";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportStudentLeaRelationshipEvent(EventAction.CHANGE);

        value = GeneratorScriptMethod.STUDENT_LEA_RELATIONSHIP_DELETE;
        descriptor = "StudentLEARelationshipDelete";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportStudentLeaRelationshipEvent(EventAction.DELETE);

        value = GeneratorScriptMethod.STUDENT_SCHOOL_ENROLLMENT_ADD;
        descriptor = "StudentSchoolEnrollmentAdd";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportStudentSchoolEnrollmentEvent(EventAction.ADD);

        value = GeneratorScriptMethod.STUDENT_SCHOOL_ENROLLMENT_CHANGE;
        descriptor = "StudentSchoolEnrollmentChange";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportStudentSchoolEnrollmentEvent(EventAction.CHANGE);

        value = GeneratorScriptMethod.STUDENT_SCHOOL_ENROLLMENT_DELETE;
        descriptor = "StudentSchoolEnrollmentDelete";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportStudentSchoolEnrollmentEvent(EventAction.DELETE);

        value = GeneratorScriptMethod.STAFF_PERSONAL_ADD;
        descriptor = "StaffPersonalAdd";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportStaffPersonalEvent(EventAction.ADD);

        value = GeneratorScriptMethod.STAFF_PERSONAL_CHANGE;
        descriptor = "StaffPersonalChange";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportStaffPersonalEvent(EventAction.CHANGE);

        value = GeneratorScriptMethod.STAFF_PERSONAL_DELETE;
        descriptor = "StaffPersonalDelete";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportStaffPersonalEvent(EventAction.DELETE);

        value = GeneratorScriptMethod.EMPLOYEE_PERSONAL_ADD;
        descriptor = "EmployeePersonalAdd";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportEmployeePersonalEvent(EventAction.ADD);

        value = GeneratorScriptMethod.EMPLOYEE_PERSONAL_CHANGE;
        descriptor = "EmployeePersonalChange";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportEmployeePersonalEvent(EventAction.CHANGE);

        value = GeneratorScriptMethod.EMPLOYEE_PERSONAL_DELETE;
        descriptor = "EmployeePersonalDelete";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportEmployeePersonalEvent(EventAction.DELETE);

        value = GeneratorScriptMethod.STAFF_ASSIGNMENT_ADD;
        descriptor = "StaffAssignmentAdd";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportStaffAssignmentEvent(EventAction.ADD);

        value = GeneratorScriptMethod.STAFF_ASSIGNMENT_CHANGE;
        descriptor = "StaffAssignmentChange";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportStaffAssignmentEvent(EventAction.CHANGE);

        value = GeneratorScriptMethod.STAFF_ASSIGNMENT_DELETE;
        descriptor = "StaffAssignmentDelete";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportStaffAssignmentEvent(EventAction.DELETE);

        value = GeneratorScriptMethod.EMPLOYMENT_RECORD_ADD;
        descriptor = "EmploymentRecordAdd";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportEmploymentRecordEvent(EventAction.ADD);

        value = GeneratorScriptMethod.EMPLOYMENT_RECORD_CHANGE;
        descriptor = "EmploymentRecordChange";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportEmploymentRecordEvent(EventAction.CHANGE);

        value = GeneratorScriptMethod.EMPLOYMENT_RECORD_DELETE;
        descriptor = "EmploymentRecordDelete";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportEmploymentRecordEvent(EventAction.DELETE);

        value = GeneratorScriptMethod.EMPLOYEE_ASSIGNMENT_ADD;
        descriptor = "EmployeeAssignmentAdd";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportEmployeeAssignmentEvent(EventAction.ADD);

        value = GeneratorScriptMethod.EMPLOYEE_ASSIGNMENT_CHANGE;
        descriptor = "EmployeeAssignmentChange";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportEmployeeAssignmentEvent(EventAction.CHANGE);

        value = GeneratorScriptMethod.EMPLOYEE_ASSIGNMENT_DELETE;
        descriptor = "EmployeeAssignmentDelete";
        pulledValue = GeneratorScriptMethod.get(descriptor);
        Assert.assertEquals(value, pulledValue);
        value.execute(executingClass);
        Mockito.verify(executingClass, Mockito.times(1)).reportEmployeeAssignmentEvent(EventAction.DELETE);
    }

}
