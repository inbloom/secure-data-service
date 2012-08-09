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
import java.util.GregorianCalendar;
import java.util.List;

import openadk.library.ADK;
import openadk.library.ADKException;
import openadk.library.common.EntryType;
import openadk.library.common.EntryTypeCode;
import openadk.library.common.ExitType;
import openadk.library.common.ExitTypeCode;
import openadk.library.common.GradeLevel;
import openadk.library.common.GradeLevelCode;
import openadk.library.common.StudentLEARelationship;

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
* StudentLEARelationship unit tests
*
*/
public class StudentLEARelationshipTranslationTaskTest {
    @InjectMocks
    private final StudentLEARelationshipTranslationTask translator = new StudentLEARelationshipTranslationTask();

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

    @Before
    public void beforeTests() {
        try {
            ADK.initialize();
        } catch (ADKException e) {
            e.printStackTrace();
        }
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNotNull() throws SifTranslationException {
        List<StudentSchoolAssociationEntity> result = translator.translate(new StudentLEARelationship());
        Assert.assertNotNull("Result was null", result);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testBasicFields() throws SifTranslationException {
        StudentLEARelationship slr = new StudentLEARelationship();

        slr.setStudentPersonalRefId("studentRefID");
        slr.setLEAInfoRefId("LEAInfoRefID");
        slr.setSchoolYear(Integer.valueOf(2001));
        slr.setEntryDate(new GregorianCalendar(2004, Calendar.FEBRUARY, 29));
        slr.setExitDate(new GregorianCalendar(2012, Calendar.DECEMBER, 29));

        Mockito.when(mockSifIdResolver.getSliGuid("studentRefID")).thenReturn("SLI_StudentGUID");
        Mockito.when(mockSifIdResolver.getSliGuid("LEAInfoRefID")).thenReturn("SLI_SchoolGUID");
        Mockito.when(schoolYearConverter.convert(Integer.valueOf(2001))).thenReturn("2001");

        List<StudentSchoolAssociationEntity> result = translator.translate(slr);
        Assert.assertEquals(1, result.size());
        StudentSchoolAssociationEntity entity = result.get(0);
        Assert.assertEquals("student Id is expected to be 'SLI_StudentGUID'", "SLI_StudentGUID", entity.getStudentId());
        Assert.assertEquals("school Id is expected to be 'SLI_SchoolGUID'", "SLI_SchoolGUID", entity.getSchoolId());
        Assert.assertEquals("school yewar is expected to be '2001'", "2001", entity.getSchoolYear());
        Assert.assertEquals("entry date is expected to be '2004-02-29'", "2004-02-29", entity.getEntryDate());
        Assert.assertEquals("exit withdraw date is expected to be '2012-12-29'", "2012-12-29", entity.getExitWithdrawDate());
    }

    @Test
    public void testEntryType() throws SifTranslationException {
        StudentLEARelationship slr = new StudentLEARelationship();
        EntryType entryType = new EntryType(EntryTypeCode._0619_1832);
        slr.setEntryType(entryType);
        Mockito.when(entryTypeConverter.convert(entryType)).thenReturn("Transfer from a charter school");

        List<StudentSchoolAssociationEntity> result = translator.translate(slr);
        Assert.assertEquals(1, result.size());
        StudentSchoolAssociationEntity entity = result.get(0);
        Assert.assertEquals("entry type is expected to be 'Transfer from a charter school'", "Transfer from a charter school", entity.getEntryType());
    }

    @Test
    public void testExitType() throws SifTranslationException {
        StudentLEARelationship slr = new StudentLEARelationship();
        ExitType exitType = new ExitType(ExitTypeCode._1923_DIED_OR_INCAPACITATED);
        slr.setExitType(exitType);
        Mockito.when(exitTypeConverter.convert(exitType)).thenReturn("Died or is permanently incapacitated");

        List<StudentSchoolAssociationEntity> result = translator.translate(slr);
        Assert.assertEquals(1, result.size());
        StudentSchoolAssociationEntity entity = result.get(0);
        Assert.assertEquals("exit withdraw type is expected to be 'Died or is permanently incapacitated'", "Died or is permanently incapacitated", entity.getExitWithdrawType());
    }

    @Test
    public void testGradeLevel() throws SifTranslationException {
        StudentLEARelationship slr = new StudentLEARelationship();
        GradeLevel gradeLevel = new GradeLevel(GradeLevelCode._10);
        slr.setGradeLevel(gradeLevel);
        Mockito.when(gradeLevelsConverter.convert(gradeLevel)).thenReturn("Tenth grade");

        List<StudentSchoolAssociationEntity> result = translator.translate(slr);
        Assert.assertEquals(1, result.size());
        StudentSchoolAssociationEntity entity = result.get(0);
        Assert.assertEquals("entry grade level is expected to be 'Tenth grade'", "Tenth grade", entity.getEntryGradeLevel());
    }
}
