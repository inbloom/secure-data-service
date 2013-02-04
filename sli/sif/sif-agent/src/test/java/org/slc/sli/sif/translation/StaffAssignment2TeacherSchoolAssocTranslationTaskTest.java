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

package org.slc.sli.sif.translation;

import java.util.ArrayList;
import java.util.List;

import openadk.library.common.GradeLevels;
import openadk.library.student.StaffAssignment;
import openadk.library.student.TeachingAssignment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.impl.GenericEntity;
import org.slc.sli.sif.AdkTest;
import org.slc.sli.sif.domain.converter.DateConverter;
import org.slc.sli.sif.domain.converter.GradeLevelsConverter;
import org.slc.sli.sif.domain.converter.TeachingAssignmentConverter;
import org.slc.sli.sif.domain.slientity.TeacherSchoolAssociationEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;

/**
 * StaffAssignment2TeacherSchoolAssocTranslationTask unit tests
 *
 * @author slee
 *
 */
public class StaffAssignment2TeacherSchoolAssocTranslationTaskTest extends AdkTest {

    @InjectMocks
    private final StaffAssignment2TeacherSchoolAssocTranslationTask translator = new StaffAssignment2TeacherSchoolAssocTranslationTask();

    @Mock
    SifIdResolver mockSifIdResolver;

    @Mock
    DateConverter mockDateConverter;

    @Mock
    GradeLevelsConverter mockGradeLevelsConverter;

    @Mock
    TeachingAssignmentConverter mockTeachingAssignmentConverter;

    private static final String DEFAULT_PROGRAM_ASSIGNMENT = "Regular Education";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNull() throws SifTranslationException {
        List<TeacherSchoolAssociationEntity> result = translator.translate(new StaffAssignment(), "");
        Assert.assertNotNull("Result was null", result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testGradeLevels() throws SifTranslationException {
        StaffAssignment info = new StaffAssignment();
        String staffPersonalRefId = "staffPersonalRefId";
        info.setStaffPersonalRefId(staffPersonalRefId);
        String schoolInfoRefId = "schoolInfoRefId";
        info.setSchoolInfoRefId(schoolInfoRefId);
        String employeePersonalRefId = "employeePersonalRefId";
        info.setEmployeePersonalRefId(employeePersonalRefId);
        GradeLevels gradeLevels = new GradeLevels();
        info.setGradeLevels(gradeLevels);
        List<String> instructionalGradeLevels = new ArrayList<String>();
        String zoneId = "zoneId";
        Entity staffEdOrgAssocEntity = new GenericEntity(null, null);

        Mockito.when(mockSifIdResolver.getSliGuid(staffPersonalRefId, zoneId)).thenReturn(staffPersonalRefId);
        Mockito.when(mockSifIdResolver.getSliGuid(schoolInfoRefId, zoneId)).thenReturn(schoolInfoRefId);
        Mockito.when(mockGradeLevelsConverter.convert(gradeLevels)).thenReturn(instructionalGradeLevels);
        Mockito.when(mockSifIdResolver.getSliEntityByType(employeePersonalRefId, (new TeacherSchoolAssociationEntity()).entityType(), zoneId)).thenReturn(staffEdOrgAssocEntity);

        List<TeacherSchoolAssociationEntity> result = translator.translate(info, zoneId);
        Assert.assertEquals(1, result.size());

        TeacherSchoolAssociationEntity e = result.get(0);
        Assert.assertEquals("schoolId is expected to be '" + schoolInfoRefId + "'", schoolInfoRefId, e.getSchoolId());
        Assert.assertEquals("teacherId is expected to be '" + staffPersonalRefId + "'", staffPersonalRefId, e.getTeacherId());
        Assert.assertEquals(instructionalGradeLevels, e.getInstructionalGradeLevels());
        Assert.assertEquals("ProgramAssignment is expected to be '" + DEFAULT_PROGRAM_ASSIGNMENT + "'",
                DEFAULT_PROGRAM_ASSIGNMENT, e.getProgramAssignment());
        Assert.assertNull("AcademicSubjects is expected to be 'null'", e.getAcademicSubjects());
        Assert.assertEquals("otherSifRefId is expected to be '" + staffPersonalRefId + "'", staffPersonalRefId, e.getOtherSifRefId());
    }

    @Test
    public void testTeachingAssignment() throws SifTranslationException {
        StaffAssignment info = new StaffAssignment();
        TeachingAssignment teachingAssignment = new TeachingAssignment();
        info.setTeachingAssignment(teachingAssignment);
        List<String> academicSubjects = new ArrayList<String>();
        String zoneId = "zoneId";

        Mockito.when(mockTeachingAssignmentConverter.convert(teachingAssignment)).thenReturn(academicSubjects);

        List<TeacherSchoolAssociationEntity> result = translator.translate(info, zoneId);
        Assert.assertEquals(1, result.size());

        TeacherSchoolAssociationEntity e = result.get(0);
        Assert.assertNull("TeacherId is expected to be 'null'", e.getTeacherId());
        Assert.assertNull("SchoolId is expected to be 'null'", e.getSchoolId());
        Assert.assertNull("InstructionalGradeLevels is expected to be 'null'", e.getInstructionalGradeLevels());
        Assert.assertEquals("ProgramAssignment is expected to be '" + DEFAULT_PROGRAM_ASSIGNMENT + "'", DEFAULT_PROGRAM_ASSIGNMENT, e.getProgramAssignment());
        Assert.assertEquals("AcademicSubjects is expected to be '" + academicSubjects + "'",
                academicSubjects, e.getAcademicSubjects());
    }
}


