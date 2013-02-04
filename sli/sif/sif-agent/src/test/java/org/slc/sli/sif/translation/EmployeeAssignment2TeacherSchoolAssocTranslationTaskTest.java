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

import java.util.List;

import openadk.library.hrfin.EmployeeAssignment;
import openadk.library.hrfin.HRProgramType;
import openadk.library.hrfin.JobClassification;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.api.client.Entity;
import org.slc.sli.sif.AdkTest;
import org.slc.sli.sif.domain.converter.HRProgramTypeConverter;
import org.slc.sli.sif.domain.converter.JobClassificationConverter;
import org.slc.sli.sif.domain.slientity.TeacherSchoolAssociationEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;

/**
 * EmployeeAssignment2TeacherSchoolAssocTranslationTask unit tests
 *
 * @author slee
 *
 */
public class EmployeeAssignment2TeacherSchoolAssocTranslationTaskTest extends AdkTest {

    @InjectMocks
    private final EmployeeAssignment2TeacherSchoolAssocTranslationTask translator = new EmployeeAssignment2TeacherSchoolAssocTranslationTask();

    @Mock
    SifIdResolver mockSifIdResolver;

    @Mock
    JobClassificationConverter mockJobClassificationConverter;

    @Mock
    HRProgramTypeConverter mockHRProgramTypeConverter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNull() throws SifTranslationException {
        List<TeacherSchoolAssociationEntity> result = translator.translate(new EmployeeAssignment(), "");
        Assert.assertNotNull("Result was null", result);
        Assert.assertEquals(0, result.size());
    }

    /**
     * Happy path EmployeeAssignment translation
     */
    @Test
    public void testEmployeeAssignmentTranslation() throws SifTranslationException {
        EmployeeAssignment info = new EmployeeAssignment();
        String employeePersonalRefId = "employeePersonalRefId";
        info.setEmployeePersonalRefId(employeePersonalRefId);
        String zoneId = "zoneId";

        HRProgramType hrProgramType = new HRProgramType();
        info.setProgramType(hrProgramType);
        String programType = "programType";
        Mockito.when(mockHRProgramTypeConverter.convert(hrProgramType)).thenReturn(programType);

        JobClassification jobClassification = new JobClassification();
        info.setJobClassification(jobClassification);
        Mockito.when(mockJobClassificationConverter.convert(jobClassification)).thenReturn("Teacher");

        String zoneSea = "zoneSea";
        String teacherGuid = "teacherGuid";
        Entity mockStaffEntity = Mockito.mock(Entity.class);
        Mockito.when(mockStaffEntity.getEntityType()).thenReturn("teacher");
        Mockito.when(mockStaffEntity.getId()).thenReturn(teacherGuid);

        Mockito.when(mockSifIdResolver.getSliEntity(employeePersonalRefId, zoneId)).thenReturn(mockStaffEntity);
        Mockito.when(mockSifIdResolver.getZoneSea(zoneId)).thenReturn(zoneSea);

        List<TeacherSchoolAssociationEntity> result = translator.translate(info, zoneId);
        Assert.assertEquals(1, result.size());

        TeacherSchoolAssociationEntity entity = result.get(0);

        Assert.assertEquals("zoneId is expected to be '" + zoneId + "'", zoneId, entity.getZoneId());
        Assert.assertEquals("otherSifRefId is expected to be '" + employeePersonalRefId + "'", employeePersonalRefId,
                entity.getOtherSifRefId());
        Assert.assertEquals("SchoolId is expected to be '" + zoneSea + "'", zoneSea, entity.getSchoolId());
        Assert.assertEquals("TeacherId is expected to be '" + teacherGuid + "'", teacherGuid, entity.getTeacherId());
        Assert.assertEquals("ProgramAssignment is expected to be '" + programType + "'", programType,
                entity.getProgramAssignment());
    }

    /**
     * Test that when a non-teacher entity is referenced no teacherSchoolAssociation is created
     */
    @Test
    public void testNonTeacherReferenceTranslation() throws SifTranslationException {
        EmployeeAssignment info = new EmployeeAssignment();
        String employeePersonalRefId = "employeePersonalRefId";
        info.setEmployeePersonalRefId(employeePersonalRefId);
        String zoneId = "zoneId";

        String zoneSea = "zoneSea";
        String teacherGuid = "teacherGuid";
        Entity mockStaffEntity = Mockito.mock(Entity.class);
        //referenced entity type is not teacher
        Mockito.when(mockStaffEntity.getEntityType()).thenReturn("staff");
        Mockito.when(mockStaffEntity.getId()).thenReturn(teacherGuid);

        Mockito.when(mockSifIdResolver.getSliEntity(employeePersonalRefId, zoneId)).thenReturn(mockStaffEntity);
        Mockito.when(mockSifIdResolver.getZoneSea(zoneId)).thenReturn(zoneSea);

        List<TeacherSchoolAssociationEntity> result = translator.translate(info, zoneId);
        Assert.assertNotNull("Result was null", result);
        Assert.assertEquals(0, result.size());
    }

    /**
     * Test that when job classification is not "Teacher" no teacherSchoolAssociation is created
     */
    @Test
    public void testNonTeacherJobClassificationTranslation() throws SifTranslationException {
        EmployeeAssignment info = new EmployeeAssignment();
        String employeePersonalRefId = "employeePersonalRefId";
        info.setEmployeePersonalRefId(employeePersonalRefId);
        String zoneId = "zoneId";

        String zoneSea = "zoneSea";
        String teacherGuid = "teacherGuid";
        Entity mockStaffEntity = Mockito.mock(Entity.class);
        Mockito.when(mockStaffEntity.getEntityType()).thenReturn("teacher");
        Mockito.when(mockStaffEntity.getId()).thenReturn(teacherGuid);

        //job classification is not teacher
        JobClassification jobClassification = new JobClassification();
        info.setJobClassification(jobClassification);
        Mockito.when(mockJobClassificationConverter.convert(jobClassification)).thenReturn("Not Teacher");

        Mockito.when(mockSifIdResolver.getSliEntity(employeePersonalRefId, zoneId)).thenReturn(mockStaffEntity);
        Mockito.when(mockSifIdResolver.getZoneSea(zoneId)).thenReturn(zoneSea);

        List<TeacherSchoolAssociationEntity> result = translator.translate(info, zoneId);
        Assert.assertNotNull("Result was null", result);
        Assert.assertEquals(0, result.size());
    }

}
