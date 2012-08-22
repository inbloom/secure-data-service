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

import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;
import openadk.library.hrfin.EmploymentRecord;

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
import org.slc.sli.sif.domain.slientity.TeacherSchoolAssociationEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;
import org.slc.sli.sif.slcinterface.SimpleEntity;

public class EmploymentRecordToTeacherSchoolTranslationTaskTest extends AdkTest {

    @InjectMocks
    EmploymentRecordToTeacherSchoolTranslationTask translator = new EmploymentRecordToTeacherSchoolTranslationTask();

    @Mock
    SifIdResolver sifIdResolver;

    @Mock
    DateConverter mockDateConverter;

    @Before
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldNotReturnNull() {
        EmploymentRecord er = createEmploymentRecord();

        List<TeacherSchoolAssociationEntity> result = translator.doTranslate(er, "zone");
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void shouldReturnEmptyList() {
        List<TeacherSchoolAssociationEntity> result = translator.doTranslate(null, "zone");
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void shouldTranslateLeaRefId() {
        EmploymentRecord er = createEmploymentRecord();

        List<TeacherSchoolAssociationEntity> result = translator.doTranslate(er, "zone");

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());

        TeacherSchoolAssociationEntity e = result.get(0);

        Assert.assertEquals("schoolSliGuid", e.getSchoolId());
    }

    @Test
    public void shouldTranslateTeacherId() {
        EmploymentRecord er = createEmploymentRecord();

        Mockito.when(sifIdResolver.getSliGuid("teacherId", "zone")).thenReturn("teacherSliGuid");

        List<TeacherSchoolAssociationEntity> result = translator.doTranslate(er, "zone");

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());

        TeacherSchoolAssociationEntity e = result.get(0);

        Assert.assertEquals("teacherSliGuid", e.getTeacherId());
    }

    @Test
    public void shouldPopulateDefaultProgramAssingment() {
        EmploymentRecord er = createEmploymentRecord();

        List<TeacherSchoolAssociationEntity> result = translator.doTranslate(er, "zone");

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());

        TeacherSchoolAssociationEntity e = result.get(0);

        Assert.assertEquals("Regular Education", e.getProgramAssignment());
    }

    @Test
    public void shouldCreateNotResultForStaffReference() {
        EmploymentRecord er = new EmploymentRecord();
        er.setLEAInfoRefId("schoolId");
        er.setSIF_RefId("teacherId");

        Entity teacher = new GenericEntity("staff", new HashMap<String, Object>());
        Entity school = new GenericEntity("school", new HashMap<String, Object>());

        Mockito.when(sifIdResolver.getSliEntity("schoolId", "zone")).thenReturn(school);
        Mockito.when(sifIdResolver.getSliEntity("teacherId", "zone")).thenReturn(teacher);

        List<TeacherSchoolAssociationEntity> result = translator.doTranslate(er, "zone");

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void shouldCreateNotResultForLeaReference() {
        EmploymentRecord er = new EmploymentRecord();
        er.setLEAInfoRefId("schoolId");
        er.setSIF_RefId("teacherId");

        Entity teacher = new GenericEntity("teacher", new HashMap<String, Object>());
        Entity school = new GenericEntity("localEducationAgency", new HashMap<String, Object>());

        Mockito.when(sifIdResolver.getSliEntity("schoolId", "zone")).thenReturn(school);
        Mockito.when(sifIdResolver.getSliEntity("teacherId", "zone")).thenReturn(teacher);

        List<TeacherSchoolAssociationEntity> result = translator.doTranslate(er, "zone");

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    private EmploymentRecord createEmploymentRecord() {
        EmploymentRecord er = new EmploymentRecord();
        er.setLEAInfoRefId("schoolId");
        er.setSIF_RefId("teacherId");

        // handle the lookup of staff/edOrg type
        Entity teacher = new SimpleEntity("teacher", "teacherSliGuid");
        Entity school = new SimpleEntity("school", "schoolSliGuid");
        Mockito.when(sifIdResolver.getSliEntity("schoolId", "zone")).thenReturn(school);
        Mockito.when(sifIdResolver.getSliEntity("teacherId", "zone")).thenReturn(teacher);

        return er;
    }


    @Test
    public void shouldReturnNothingWhenMissingEdorg() {
        EmploymentRecord er = new EmploymentRecord();
        er.setLEAInfoRefId("schoolId");
        er.setSIF_RefId("teacherId");

        Entity teacher = new GenericEntity("teacher", new HashMap<String, Object>());

        Mockito.when(sifIdResolver.getSliEntity("schoolId", "zone")).thenReturn(null);
        Mockito.when(sifIdResolver.getSliEntity("teacherId", "zone")).thenReturn(teacher);

        List<TeacherSchoolAssociationEntity> result = translator.doTranslate(er, "zone");

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void shouldReturnNothingWhenMissingTeacher() {
        EmploymentRecord er = new EmploymentRecord();
        er.setLEAInfoRefId("schoolId");
        er.setSIF_RefId("teacherId");

        Entity school = new GenericEntity("school", new HashMap<String, Object>());

        Mockito.when(sifIdResolver.getSliEntity("schoolId", "zone")).thenReturn(school);
        Mockito.when(sifIdResolver.getSliEntity("teacherId", "zone")).thenReturn(null);

        List<TeacherSchoolAssociationEntity> result = translator.doTranslate(er, "zone");

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }



}
