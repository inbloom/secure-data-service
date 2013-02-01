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
import openadk.library.hrfin.JobClassification;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.sif.AdkTest;
import org.slc.sli.sif.domain.converter.JobClassificationConverter;
import org.slc.sli.sif.domain.slientity.TeacherEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;

/**
 * EmployeeAssignment2TeacherTranslationTask unit tests
 *
 * @author slee
 *
 */
public class EmployeeAssignment2TeacherTranslationTaskTest extends AdkTest {

    @InjectMocks
    private final EmployeeAssignment2TeacherTranslationTask translator = new EmployeeAssignment2TeacherTranslationTask();

    @Mock
    SifIdResolver mockSifIdResolver;

    @Mock
    JobClassificationConverter mockJobClassificationConverter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNull() throws SifTranslationException {
        List<TeacherEntity> result = translator.translate(new EmployeeAssignment(), "");
        Assert.assertNotNull("Result was null", result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testBasics() throws SifTranslationException {
        EmployeeAssignment info = new EmployeeAssignment();
        String employeePersonalRefId = "employeePersonalRefId";
        info.setEmployeePersonalRefId(employeePersonalRefId);
        JobClassification jobClassification = new JobClassification();
        info.setJobClassification(jobClassification);
        String teacher = "Teacher";
        String zoneId = "zoneId";

        Mockito.when(mockJobClassificationConverter.convert(jobClassification)).thenReturn(teacher);
        Mockito.when(mockSifIdResolver.getSliGuid(employeePersonalRefId, zoneId)).thenReturn(employeePersonalRefId);

        List<TeacherEntity> result = translator.translate(info, zoneId);
        Assert.assertEquals(1, result.size());

        TeacherEntity entity = result.get(0);
        TeacherEntity e = entity;
        Assert.assertTrue("isCreatedByOthers is expected to be 'true'", e.isCreatedByOthers());
        Assert.assertEquals("CreatorRefId is expected to be '" + employeePersonalRefId + "'", employeePersonalRefId, e.getCreatorRefId());

    }

}

