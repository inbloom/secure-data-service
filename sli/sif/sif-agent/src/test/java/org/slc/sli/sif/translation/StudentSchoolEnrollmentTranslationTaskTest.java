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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import openadk.library.common.EntryType;
import openadk.library.common.EntryTypeCode;
import openadk.library.common.ExitType;
import openadk.library.common.ExitTypeCode;
import openadk.library.common.GradeLevel;
import openadk.library.common.GradeLevelCode;
import openadk.library.student.StudentSchoolEnrollment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.sif.domain.converter.DateConverter;
import org.slc.sli.sif.AdkTest;
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
public class StudentSchoolEnrollmentTranslationTaskTest extends AdkTest {
    @InjectMocks
    private final StudentSchoolEnrollmentTranslationTask translator = new StudentSchoolEnrollmentTranslationTask();

    @Mock
    SifIdResolver mockSifIdResolver;

    @Mock
    SchoolYearConverter schoolYearConverter;

    @Mock
    GradeLevelsConverter gradeLevelsConverter;

    @Mock
    EntryTypeConverter entryTypeConverter;

    @Mock
    ExitTypeConverter exitTypeConverter;

    @Mock
    DateConverter dateConverter;

    @Before
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNotNull() throws SifTranslationException {
        List<StudentSchoolAssociationEntity> result = translator.translate(new StudentSchoolEnrollment(), null);
        Assert.assertNotNull("Result was null", result);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testBasicFields() throws SifTranslationException {
        StudentSchoolEnrollment sse = new StudentSchoolEnrollment();

        sse.setStudentPersonalRefId("studentRefID");
        sse.setSchoolInfoRefId("SchoolInfoRefID");
        sse.setSchoolYear(Integer.valueOf(2001));

        Mockito.when(mockSifIdResolver.getSliGuid("studentRefID", null)).thenReturn("SLI_StudentGUID");
        Mockito.when(mockSifIdResolver.getSliGuid("SchoolInfoRefID", null)).thenReturn("SLI_SchoolGUID");
        Mockito.when(schoolYearConverter.convert(Integer.valueOf(2001))).thenReturn("2001");

        List<StudentSchoolAssociationEntity> result = translator.translate(sse, null);
        Assert.assertEquals(1, result.size());
        StudentSchoolAssociationEntity entity = result.get(0);
        Assert.assertEquals("student Id is expected to be 'SLI_StudentGUID'", "SLI_StudentGUID", entity.getStudentId());
        Assert.assertEquals("school Id is expected to be 'SLI_SchoolGUID'", "SLI_SchoolGUID", entity.getSchoolId());
        Assert.assertEquals("school yewar is expected to be '2001'", "2001", entity.getSchoolYear());
    }

    @Test
    public void testEntryType() throws SifTranslationException {
        StudentSchoolEnrollment sse = new StudentSchoolEnrollment();
        EntryType entryType = new EntryType(EntryTypeCode._0619_1832);
        sse.setEntryType(entryType);
        Mockito.when(entryTypeConverter.convert(entryType)).thenReturn("Transfer from a charter school");

        List<StudentSchoolAssociationEntity> result = translator.translate(sse, null);
        Assert.assertEquals(1, result.size());
        StudentSchoolAssociationEntity entity = result.get(0);
        Assert.assertEquals("entry type is expected to be 'Transfer from a charter school'",
                "Transfer from a charter school", entity.getEntryType());
    }

    @Test
    public void testExitType() throws SifTranslationException {
        StudentSchoolEnrollment sse = new StudentSchoolEnrollment();
        ExitType exitType = new ExitType(ExitTypeCode._1923_DIED_OR_INCAPACITATED);
        sse.setExitType(exitType);
        Mockito.when(exitTypeConverter.convert(exitType)).thenReturn("Died or is permanently incapacitated");

        List<StudentSchoolAssociationEntity> result = translator.translate(sse, null);
        Assert.assertEquals(1, result.size());
        StudentSchoolAssociationEntity entity = result.get(0);
        Assert.assertEquals("exit withdraw type is expected to be 'Died or is permanently incapacitated'",
                "Died or is permanently incapacitated", entity.getExitWithdrawType());
    }

    @Test
    public void testGradeLevel() throws SifTranslationException {
        StudentSchoolEnrollment sse = new StudentSchoolEnrollment();
        GradeLevel gradeLevel = new GradeLevel(GradeLevelCode._10);
        sse.setGradeLevel(gradeLevel);
        Mockito.when(gradeLevelsConverter.convert(gradeLevel)).thenReturn("Tenth grade");

        List<StudentSchoolAssociationEntity> result = translator.translate(sse, null);
        Assert.assertEquals(1, result.size());
        StudentSchoolAssociationEntity entity = result.get(0);
        Assert.assertEquals("entry grade level is expected to be 'Tenth grade'", "Tenth grade",
                entity.getEntryGradeLevel());
    }

    @Test
    public void testDates() throws SifTranslationException {
        StudentSchoolEnrollment sse = new StudentSchoolEnrollment();

        Calendar hire = new GregorianCalendar(2004, Calendar.FEBRUARY, 1);
        Calendar terminate = new GregorianCalendar(2005, Calendar.DECEMBER, 29);

        sse.setEntryDate(hire);
        sse.setExitDate(terminate);

        Mockito.when(dateConverter.convert(hire)).thenReturn("hireDate");
        Mockito.when(dateConverter.convert(terminate)).thenReturn("terminateDate");

        List<StudentSchoolAssociationEntity> result = translator.translate(sse, null);
        Assert.assertEquals(1, result.size());
        StudentSchoolAssociationEntity entity = result.get(0);
        Assert.assertEquals("hireDate", entity.getEntryDate());
        Assert.assertEquals("terminateDate", entity.getExitWithdrawDate());
    }
}
