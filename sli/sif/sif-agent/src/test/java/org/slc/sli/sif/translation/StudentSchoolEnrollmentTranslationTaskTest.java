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

import java.util.GregorianCalendar;
import java.util.List;

import openadk.library.ADK;
import openadk.library.ADKException;
import openadk.library.common.EntryTypeCode;
import openadk.library.common.ExitTypeCode;
import openadk.library.common.GradeLevelCode;
import openadk.library.student.StudentSchoolEnrollment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.sif.domain.converter.EntryTypeConverter;
import org.slc.sli.sif.domain.converter.ExitTypeConverter;
import org.slc.sli.sif.domain.converter.GradeLevelsConverter;
import org.slc.sli.sif.domain.converter.SchoolYearConverter;
import org.slc.sli.sif.domain.slientity.StudentSchoolAssociationEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;

/**
 *
 * StudentSchoolEnrollmentTranslationTask unit tests
 *
 */
public class StudentSchoolEnrollmentTranslationTaskTest {
    @InjectMocks
    private final StudentSchoolEnrollmentTranslationTask translator = new StudentSchoolEnrollmentTranslationTask();

    @Mock
    SifIdResolver mockSifIdResolver;

    @Before
    public void beforeTests() {
        try {
            ADK.initialize();
        } catch (ADKException e) {
            e.printStackTrace();
        }
        MockitoAnnotations.initMocks(this);
        // inject implementation
        translator.entryTypeConverter = new EntryTypeConverter();
        translator.exitTypeConverter = new ExitTypeConverter();
        translator.schoolYearConverter = new SchoolYearConverter();
        translator.gradeLevelsConverter = new GradeLevelsConverter();
    }

    @Test
    public void testNotNull() throws SifTranslationException {
        List<StudentSchoolAssociationEntity> result = translator.translate(new StudentSchoolEnrollment());
        Assert.assertNotNull("Result was null", result);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testBasicFields() throws SifTranslationException {
        StudentSchoolEnrollment sse = new StudentSchoolEnrollment();

        sse.setStudentPersonalRefId("studentRefID");
        sse.setSchoolInfoRefId("schoolInfoRefID");
        sse.setSchoolYear(2001);
        sse.setEntryDate(new GregorianCalendar(2004, 2, 29));
        sse.setGradeLevel(GradeLevelCode._01);
        sse.setEntryType(EntryTypeCode._0619_1832);
        sse.setExitDate(new GregorianCalendar(2012, 2, 29));
        sse.setExitType(ExitTypeCode._1923_DIED_OR_INCAPACITATED);

        Mockito.when(mockSifIdResolver.getSliGuid("studentRefID")).thenReturn("SLI_StudentGUID");
        Mockito.when(mockSifIdResolver.getSliGuid("schoolInfoRefID")).thenReturn("SLI_SchoolGUID");

        List<StudentSchoolAssociationEntity> result = translator.translate(sse);
        Assert.assertEquals(1, result.size());
        StudentSchoolAssociationEntity entity = result.get(0);
    }

}
