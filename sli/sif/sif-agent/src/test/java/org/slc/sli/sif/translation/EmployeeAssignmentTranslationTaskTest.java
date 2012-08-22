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

import java.util.Calendar;
import java.util.List;

import openadk.library.hrfin.EmployeeAssignment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.sif.AdkTest;
import org.slc.sli.sif.domain.converter.DateConverter;
import org.slc.sli.sif.domain.converter.HRProgramTypeConverter;
import org.slc.sli.sif.domain.converter.JobClassificationConverter;
import org.slc.sli.sif.domain.slientity.SliEntity;
import org.slc.sli.sif.domain.slientity.StaffEducationOrganizationAssociationEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;

/**
 * EmployeeAssignmentTranslationTask unit tests
 *
 * @author slee
 *
 */
public class EmployeeAssignmentTranslationTaskTest extends AdkTest {

    @InjectMocks
    private final EmployeeAssignmentTranslationTask translator = new EmployeeAssignmentTranslationTask();

    @Mock
    SifIdResolver mockSifIdResolver;

    @Mock
    DateConverter mockDateConverter;

    @Mock
    JobClassificationConverter mockJobClassificationConverter;

    @Mock
    HRProgramTypeConverter mockHRProgramTypeConverter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNotNull() throws SifTranslationException {
        List<SliEntity> result = translator.translate(new EmployeeAssignment(), "");
        Assert.assertNotNull("Result was null", result);
        Assert.assertEquals(3, result.size());
    }

    @Test
    public void testBasics() throws SifTranslationException {
        EmployeeAssignment info = new EmployeeAssignment();
        String employeePersonalRefId = "employeePersonalRefId";
        info.setEmployeePersonalRefId(employeePersonalRefId);
        String zoneId = "zoneId";
        String zoneSea = "zoneSea";
        Calendar calendar = Calendar.getInstance();
        info.setJobStartDate(calendar);
        String today = "today";

        Mockito.when(mockSifIdResolver.getSliGuid(employeePersonalRefId, zoneId)).thenReturn(employeePersonalRefId);
        Mockito.when(mockSifIdResolver.getZoneSea(zoneId)).thenReturn(zoneSea);
        Mockito.when(mockDateConverter.convert(calendar)).thenReturn(today);

        List<SliEntity> result = translator.translate(info, zoneId);
        Assert.assertEquals(3, result.size());

        for (SliEntity entity : result) {
            if (entity.entityType().equals("staffEducationOrganizationAssociation")) {
                StaffEducationOrganizationAssociationEntity seoae = (StaffEducationOrganizationAssociationEntity) entity;
                Assert.assertEquals("staffPersonalRefId is expected to be '" + employeePersonalRefId + "'", employeePersonalRefId, seoae.getStaffReference());
                Assert.assertEquals("zoneId is expected to be '" + zoneId + "'", zoneId, seoae.getZoneId());
                Assert.assertEquals("otherSifRefId is expected to be '" + employeePersonalRefId + "'", employeePersonalRefId, seoae.getOtherSifRefId());
                Assert.assertEquals("educationOrganizationReference is expected to be '" + zoneSea + "'", zoneSea, seoae.getEducationOrganizationReference());
                Assert.assertNull("StaffClassification is expected to be 'null'", seoae.getStaffClassification());
                Assert.assertNull("End Date is expected to be 'null'", seoae.getEndDate());
                Assert.assertNotNull("Begin Date is expected to be not 'null'", seoae.getBeginDate());
                Assert.assertEquals("Begin Date is expected to be '" + today + "'", today, seoae.getBeginDate());
            }
//            if (entity.entityType().equals("teacherSchoolAssociation")) {
//                TeacherSchoolAssociationEntity tsae = (TeacherSchoolAssociationEntity) entity;
//                Assert.assertNull("TeacherId is expected to be 'null'", tsae.getTeacherId());
//                Assert.assertNull("SchoolId is expected to be 'null'", tsae.getSchoolId());
//            }
        }
    }

    @Test
    public void testBasicsAA() throws SifTranslationException {
        EmployeeAssignment info = new EmployeeAssignment();
        String employeePersonalRefId = "employeePersonalRefId";
        info.setEmployeePersonalRefId(employeePersonalRefId);
        String zoneId = "zoneId";
        Calendar calendar = Calendar.getInstance();
        info.setJobStartDate(calendar);
        String today = "today";

        Mockito.when(mockSifIdResolver.getSliGuid(employeePersonalRefId, zoneId)).thenReturn(employeePersonalRefId);
        Mockito.when(mockDateConverter.convert(calendar)).thenReturn(today);

        List<SliEntity> result = translator.translate(info, zoneId);
        Assert.assertEquals(3, result.size());

        for (SliEntity entity : result) {
            if (entity.entityType().equals("staffEducationOrganizationAssociation")) {
                StaffEducationOrganizationAssociationEntity seoae = (StaffEducationOrganizationAssociationEntity) entity;
//                Assert.assertEquals("staffPersonalRefId is expected to be '" + staffPersonalRefId + "'", staffPersonalRefId, seoae.getStaffReference());
//                Assert.assertEquals("educationOrganizationReference is expected to be '" + schoolInfoRefId + "'", schoolInfoRefId, seoae.getEducationOrganizationReference());
                Assert.assertNull("StaffClassification is expected to be 'null'", seoae.getStaffClassification());
                Assert.assertNull("End Date is expected to be 'null'", seoae.getEndDate());
                Assert.assertNotNull("Begin Date is expected to be not 'null'", seoae.getBeginDate());
                Assert.assertEquals("Begin Date is expected to be '" + today + "'", today, seoae.getBeginDate());
//                Assert.assertEquals("StaffClassification is expected to be 'Other'", "Other", seoae.getStaffClassification());
            }
//            if (entity.entityType().equals("teacherSchoolAssociation")) {
//                TeacherSchoolAssociationEntity tsae = (TeacherSchoolAssociationEntity) entity;
//                Assert.assertNull("TeacherId is expected to be 'null'", tsae.getTeacherId());
//                Assert.assertNull("SchoolId is expected to be 'null'", tsae.getSchoolId());
//            }
        }
    }



}

