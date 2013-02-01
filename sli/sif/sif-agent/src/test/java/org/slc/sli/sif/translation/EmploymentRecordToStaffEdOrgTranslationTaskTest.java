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
import org.slc.sli.sif.domain.slientity.StaffEducationOrganizationAssociationEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;
import org.slc.sli.sif.slcinterface.SimpleEntity;

/**
 * Tests for EmploymentRecordToStaffEdOrgTranslationTask
 *
 */
public class EmploymentRecordToStaffEdOrgTranslationTaskTest extends AdkTest {

    @InjectMocks
    EmploymentRecordToStaffEdOrgTranslationTask translator = new EmploymentRecordToStaffEdOrgTranslationTask();

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

        // mock the lookup of the staff/edorg
        Entity entity = new GenericEntity("entityType", new HashMap<String, Object>());
        Mockito.when(sifIdResolver.getSliEntity(Mockito.anyString(), Mockito.anyString())).thenReturn(entity);

        List<StaffEducationOrganizationAssociationEntity> result = translator.doTranslate(new EmploymentRecord(),
                "zone");
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
    }

    /*
     * Since the EmploymentRecord event doesn't populate sifId,
     * generate one so that sifIdResolver works
     */
    @Test
    public void shouldGenerateSifId() {
        EmploymentRecord er = new EmploymentRecord();
        er.setSIF_RefId("staffId");
        er.setSIF_RefObject("EmployeePersonal");
        er.setLEAInfoRefId("leaInfoRefId");

        // mock the lookup of the staff/edorg
        Entity entity = new GenericEntity("entityType", new HashMap<String, Object>());
        Mockito.when(sifIdResolver.getSliEntity(Mockito.anyString(), Mockito.anyString())).thenReturn(entity);

        translator.doTranslate(er, "zone");

        Assert.assertEquals("staffId:EmployeePersonal:leaInfoRefId", er.getRefId());
    }

    @Test
    public void shouldReturnEmptyList() {
        List<StaffEducationOrganizationAssociationEntity> result = translator.doTranslate(null, "zone");
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void shouldReturnNothingWhenMissingStaff() {
        EmploymentRecord er = new EmploymentRecord();
        er.setLEAInfoRefId("schoolId");
        er.setSIF_RefId("teacherId");

        Entity school = new GenericEntity("school", new HashMap<String, Object>());

        Mockito.when(sifIdResolver.getSliEntity("schoolId", "zone")).thenReturn(school);
        Mockito.when(sifIdResolver.getSliEntity("teacherId", "zone")).thenReturn(null);

        List<StaffEducationOrganizationAssociationEntity> result = translator.doTranslate(er, "zone");

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void shouldReturnNothingWhenMissingEdorg() {
        EmploymentRecord er = new EmploymentRecord();
        er.setLEAInfoRefId("schoolId");
        er.setSIF_RefId("teacherId");

        Entity staff = new GenericEntity("teacher", new HashMap<String, Object>());

        Mockito.when(sifIdResolver.getSliEntity("schoolId", "zone")).thenReturn(null);
        Mockito.when(sifIdResolver.getSliEntity("teacherId", "zone")).thenReturn(staff);

        List<StaffEducationOrganizationAssociationEntity> result = translator.doTranslate(er, "zone");

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void shouldTranslateLeaRefId() {
        EmploymentRecord er = new EmploymentRecord();
        er.setLEAInfoRefId("leaSifId");

        // mock the lookup of the staff/edorg
        Entity entity = new SimpleEntity("entityType", "leaSliGuid");
        Mockito.when(sifIdResolver.getSliEntity(Mockito.anyString(), Mockito.anyString())).thenReturn(entity);

        List<StaffEducationOrganizationAssociationEntity> result = translator.doTranslate(er, "zone");

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());

        StaffEducationOrganizationAssociationEntity e = result.get(0);

        Assert.assertEquals("leaSliGuid", e.getEducationOrganizationReference());
    }

    @Test
    public void shouldTranslateStaffId() {
        EmploymentRecord er = new EmploymentRecord();
        er.setSIF_RefId("sifStaffId");

        // mock the lookup of the staff/edorg
        Entity entity = new SimpleEntity("entityType", "staffSliGuid");
        Mockito.when(sifIdResolver.getSliEntity(Mockito.anyString(), Mockito.anyString())).thenReturn(entity);

        List<StaffEducationOrganizationAssociationEntity> result = translator.doTranslate(er, "zone");

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());

        StaffEducationOrganizationAssociationEntity e = result.get(0);

        Assert.assertEquals("staffSliGuid", e.getStaffReference());
    }

    @Test
    public void shouldPopulateDefaultStaffClassification() {
        EmploymentRecord er = new EmploymentRecord();

        // mock the lookup of the staff/edorg
        Entity entity = new GenericEntity("entityType", new HashMap<String, Object>());
        Mockito.when(sifIdResolver.getSliEntity(Mockito.anyString(), Mockito.anyString())).thenReturn(entity);

        List<StaffEducationOrganizationAssociationEntity> result = translator.doTranslate(er, "zone");

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());

        StaffEducationOrganizationAssociationEntity e = result.get(0);

        Assert.assertEquals("Other", e.getStaffClassification());
    }


    @Test
    public void shouldTranslateTitle() {
        EmploymentRecord er = new EmploymentRecord();
        er.setPositionTitle("title");

        // mock the lookup of the staff/edorg
        Entity entity = new GenericEntity("entityType", new HashMap<String, Object>());
        Mockito.when(sifIdResolver.getSliEntity(Mockito.anyString(), Mockito.anyString())).thenReturn(entity);

        List<StaffEducationOrganizationAssociationEntity> result = translator.doTranslate(er, "zone");

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());

        StaffEducationOrganizationAssociationEntity e = result.get(0);

        Assert.assertEquals("title", e.getPositionTitle());

    }

    @Test
    public void shouldTranslateDates() {
        EmploymentRecord er = new EmploymentRecord();

        Calendar hire = new GregorianCalendar(2004, Calendar.FEBRUARY, 1);
        Calendar terminate = new GregorianCalendar(2005, Calendar.DECEMBER, 29);

        er.setHireDate(hire);
        er.setTerminationDate(terminate);

        Mockito.when(mockDateConverter.convert(hire)).thenReturn("hireDate");
        Mockito.when(mockDateConverter.convert(terminate)).thenReturn("terminateDate");

        // mock the lookup of the staff/edorg
        Entity entity = new GenericEntity("entityType", new HashMap<String, Object>());
        Mockito.when(sifIdResolver.getSliEntity(Mockito.anyString(), Mockito.anyString())).thenReturn(entity);

        List<StaffEducationOrganizationAssociationEntity> result = translator.doTranslate(er, "zone");

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());

        StaffEducationOrganizationAssociationEntity e = result.get(0);

        Assert.assertEquals("hireDate", e.getBeginDate());
        Assert.assertEquals("terminateDate", e.getEndDate());

    }

}
