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

import java.util.List;

import openadk.library.hrfin.EmployeeAssignment;
import openadk.library.hrfin.HRProgramType;

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
    public void testNull() throws SifTranslationException {
        List<TeacherSchoolAssociationEntity> result = translator.translate(new EmployeeAssignment(), "");
        Assert.assertNotNull("Result was null", result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testEmptyProgramType() throws SifTranslationException {
        EmployeeAssignment info = new EmployeeAssignment();
        HRProgramType hrProgramType = new HRProgramType();
        info.setProgramType(hrProgramType);
        String programType = "programType";
        String zoneId = "zoneId";

        Mockito.when(mockHRProgramTypeConverter.convert(hrProgramType)).thenReturn(programType);

        List<TeacherSchoolAssociationEntity> result = translator.translate(info, zoneId);
        Assert.assertEquals(1, result.size());

        TeacherSchoolAssociationEntity entity = result.get(0);
        TeacherSchoolAssociationEntity e = entity;
        Assert.assertNull("TeacherId is expected to be 'null'", e.getTeacherId());
        Assert.assertNull("SchoolId is expected to be 'null'", e.getSchoolId());
        Assert.assertEquals("ProgramAssignment is expected to be '" + programType + "'", programType, e.getProgramAssignment());
    }

    @Test
    public void testEmployeePersonalRefId() throws SifTranslationException {
        EmployeeAssignment info = new EmployeeAssignment();
        String employeePersonalRefId = "employeePersonalRefId";
        info.setEmployeePersonalRefId(employeePersonalRefId);
        String zoneId = "zoneId";
        String zoneSea = "zoneSea";
        String teacherGuid = "teacherGuid";

        Mockito.when(mockSifIdResolver.getSliGuid(employeePersonalRefId, zoneId)).thenReturn(teacherGuid);
        Mockito.when(mockSifIdResolver.getZoneSea(zoneId)).thenReturn(zoneSea);

        List<TeacherSchoolAssociationEntity> result = translator.translate(info, zoneId);
        Assert.assertEquals(1, result.size());

        TeacherSchoolAssociationEntity entity = result.get(0);
        TeacherSchoolAssociationEntity e = entity;
        Assert.assertEquals("zoneId is expected to be '" + zoneId + "'", zoneId, e.getZoneId());
        Assert.assertEquals("otherSifRefId is expected to be '" + employeePersonalRefId + "'", employeePersonalRefId, e.getOtherSifRefId());
        Assert.assertEquals("SchoolId is expected to be '" + zoneSea + "'", zoneSea, e.getSchoolId());

    }

}

