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

package org.slc.sli.sif.translation;

import java.util.ArrayList;
import java.util.Calendar;
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
import org.slc.sli.sif.domain.slientity.SliEntity;
import org.slc.sli.sif.domain.slientity.StaffEducationOrganizationAssociationEntity;
import org.slc.sli.sif.domain.slientity.TeacherSchoolAssociationEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;

/**
 * StaffAssignmentTranslationTask unit tests
 *
 * @author slee
 *
 */
public class StaffAssignmentTranslationTaskTest extends AdkTest {

    @InjectMocks
    private final StaffAssignmentTranslationTask translator = new StaffAssignmentTranslationTask();

    @Mock
    SifIdResolver mockSifIdResolver;

    @Mock
    DateConverter mockDateConverter;

    @Mock
    GradeLevelsConverter mockGradeLevelsConverter;

    @Mock
    TeachingAssignmentConverter mockTeachingAssignmentConverter;

    @Override
    @Before
    public void setup() {
        super.setup();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNotNull() throws SifTranslationException {
        List<SliEntity> result = translator.translate(new StaffAssignment(), "");
        Assert.assertNotNull("Result was null", result);
        Assert.assertEquals(2, result.size());
    }

    @Test
    public void testBasics() throws SifTranslationException {
        StaffAssignment info = new StaffAssignment();
        String staffPersonalRefId = "staffPersonalRefId";
        info.setStaffPersonalRefId(staffPersonalRefId);
        String schoolInfoRefId = "schoolInfoRefId";
        info.setSchoolInfoRefId(schoolInfoRefId);
        String zoneId = "zoneId";
        Calendar calendar = Calendar.getInstance();
        info.setJobStartDate(calendar);
        String today = "today";

        Mockito.when(mockSifIdResolver.getSliGuid(staffPersonalRefId, zoneId)).thenReturn(staffPersonalRefId);
        Mockito.when(mockSifIdResolver.getSliGuid(schoolInfoRefId, zoneId)).thenReturn(schoolInfoRefId);
        Mockito.when(mockDateConverter.convert(calendar)).thenReturn(today);

        List<SliEntity> result = translator.translate(info, zoneId);
        Assert.assertEquals(2, result.size());

        for (SliEntity entity : result) {
            if (entity.entityType().equals("staffEducationOrganizationAssociation")) {
                StaffEducationOrganizationAssociationEntity seoae = (StaffEducationOrganizationAssociationEntity) entity;
                Assert.assertEquals("staffPersonalRefId is expected to be '" + staffPersonalRefId + "'", staffPersonalRefId, seoae.getStaffReference());
                Assert.assertEquals("educationOrganizationReference is expected to be '" + schoolInfoRefId + "'", schoolInfoRefId, seoae.getEducationOrganizationReference());
                Assert.assertNull("End Date is expected to be 'null'", seoae.getEndDate());
                Assert.assertNotNull("Begin Date is expected to be not 'null'", seoae.getBeginDate());
                Assert.assertEquals("Begin Date is expected to be '" + today + "'", today, seoae.getBeginDate());
                Assert.assertEquals("StaffClassification is expected to be 'Other'", "Other", seoae.getStaffClassification());
            }
            if (entity.entityType().equals("teacherSchoolAssociation")) {
                TeacherSchoolAssociationEntity tsae = (TeacherSchoolAssociationEntity) entity;
                Assert.assertNull("TeacherId is expected to be 'null'", tsae.getTeacherId());
                Assert.assertNull("SchoolId is expected to be 'null'", tsae.getSchoolId());
            }
        }
    }

    @Test
    public void testEmployeePersonalRefId() throws SifTranslationException {
        StaffAssignment info = new StaffAssignment();
        String employeePersonalRefId = "employeePersonalRefId";
        info.setEmployeePersonalRefId(employeePersonalRefId);
        String zoneId = "zoneId";
        Entity staffEdOrgAssocEntity = new GenericEntity(null, null);

        Mockito.when(mockSifIdResolver.getSliEntityByType(employeePersonalRefId, "staffEducationOrganizationAssociation", zoneId)).thenReturn(staffEdOrgAssocEntity);

        List<SliEntity> result = translator.translate(info, zoneId);
        Assert.assertEquals(2, result.size());

        for (SliEntity entity : result) {
            if (entity.entityType().equals("staffEducationOrganizationAssociation")) {
                StaffEducationOrganizationAssociationEntity seoae = (StaffEducationOrganizationAssociationEntity)entity;
                Assert.assertEquals("staffEdOrgAssocEntity is expected to be '" + staffEdOrgAssocEntity + "'", staffEdOrgAssocEntity, seoae.getMatchedEntity());
                Assert.assertNull("staffPersonalRefId is expected to be 'null'", seoae.getStaffReference());
                Assert.assertNull("educationOrganizationReference is expected to be 'null'", seoae.getEducationOrganizationReference());
                Assert.assertNull("End Date is expected to be 'null'", seoae.getBeginDate());
                Assert.assertNull("Begin Date is expected to be 'null'", seoae.getEndDate());
                Assert.assertNull("StaffClassification is expected to be 'null'", seoae.getStaffClassification());
            }
            if (entity.entityType().equals("teacherSchoolAssociation")) {
                TeacherSchoolAssociationEntity tsae = (TeacherSchoolAssociationEntity)entity;
                Assert.assertNull("TeacherId is expected to be 'null'", tsae.getTeacherId());
                Assert.assertNull("SchoolId is expected to be 'null'", tsae.getSchoolId());
                Assert.assertNull("ProgramAssignment is expected to be 'null'", tsae.getProgramAssignment());
                Assert.assertNull("InstructionalGradeLevels is expected to be 'null'", tsae.getInstructionalGradeLevels());
                Assert.assertNull("AcademicSubjects is expected to be 'null'", tsae.getAcademicSubjects());
            }
        }
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
        Mockito.when(mockSifIdResolver.getSliEntityByType(employeePersonalRefId, "teacherSchoolAssociation", zoneId)).thenReturn(staffEdOrgAssocEntity);

        List<SliEntity> result = translator.translate(info, zoneId);
        Assert.assertEquals(2, result.size());

        for (SliEntity entity : result) {
            if (entity.entityType().equals("teacherSchoolAssociation")) {
                TeacherSchoolAssociationEntity tsae = (TeacherSchoolAssociationEntity)entity;
                Assert.assertEquals("schoolInfoRefId is expected to be '" + schoolInfoRefId + "'", schoolInfoRefId, tsae.getSchoolId());
                Assert.assertEquals("staffPersonalRefId is expected to be '" + staffPersonalRefId + "'", staffPersonalRefId, tsae.getTeacherId());
                Assert.assertEquals(instructionalGradeLevels, tsae.getInstructionalGradeLevels());
                Assert.assertNull("ProgramAssignment is expected to be 'null'", tsae.getProgramAssignment());
                Assert.assertNull("AcademicSubjects is expected to be 'null'", tsae.getAcademicSubjects());
            }
        }
    }

    @Test
    public void testTeachingAssignment() throws SifTranslationException {
        StaffAssignment info = new StaffAssignment();
        TeachingAssignment teachingAssignment = new TeachingAssignment();
        info.setTeachingAssignment(teachingAssignment);
        List<String> academicSubjects = new ArrayList<String>();
        String zoneId = "zoneId";

        Mockito.when(mockTeachingAssignmentConverter.convert(teachingAssignment)).thenReturn(academicSubjects);

        List<SliEntity> result = translator.translate(info, zoneId);
        Assert.assertEquals(2, result.size());

        for (SliEntity entity : result) {
            if (entity.entityType().equals("teacherSchoolAssociation")) {
                TeacherSchoolAssociationEntity tsae = (TeacherSchoolAssociationEntity)entity;
                Assert.assertNull("TeacherId is expected to be 'null'", tsae.getTeacherId());
                Assert.assertNull("SchoolId is expected to be 'null'", tsae.getSchoolId());
                Assert.assertNull("InstructionalGradeLevels is expected to be 'null'", tsae.getInstructionalGradeLevels());
                Assert.assertEquals("ProgramAssignment is expected to be 'Regular Education'", "Regular Education", tsae.getProgramAssignment());
                Assert.assertEquals(academicSubjects, tsae.getAcademicSubjects());
            }
        }
    }
}


