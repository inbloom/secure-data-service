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
import org.slc.sli.sif.domain.converter.DateConverter;
import org.slc.sli.sif.domain.converter.JobClassificationConverter;
import org.slc.sli.sif.domain.slientity.StaffEducationOrganizationAssociationEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;

/**
 * EmployeeAssignment2StaffEdOrgAssocTranslationTask unit tests
 *
 * @author slee
 *
 */
public class EmployeeAssignment2StaffEdOrgAssocTranslationTaskTest extends AdkTest {

    @InjectMocks
    private final EmployeeAssignment2StaffEdOrgAssocTranslationTask translator = new EmployeeAssignment2StaffEdOrgAssocTranslationTask();

    @Mock
    SifIdResolver mockSifIdResolver;

    @Mock
    DateConverter mockDateConverter;

    @Mock
    JobClassificationConverter mockJobClassificationConverter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNotNull() throws SifTranslationException {
        List<StaffEducationOrganizationAssociationEntity> result = translator.translate(new EmployeeAssignment(), "");
        Assert.assertNotNull("Result was null", result);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testEmptyEmployeePersonalRefId() throws SifTranslationException {
        EmployeeAssignment info = new EmployeeAssignment();
        String zoneId = "zoneId";

        List<StaffEducationOrganizationAssociationEntity> result = translator.translate(info, zoneId);
        Assert.assertEquals(1, result.size());

        StaffEducationOrganizationAssociationEntity entity = result.get(0);
        StaffEducationOrganizationAssociationEntity seoae = entity;
        Assert.assertNull("staffPersonalRefId is expected to be 'null'", seoae.getStaffReference());
        Assert.assertNull("educationOrganizationReference is expected to be 'null'", seoae.getEducationOrganizationReference());
        Assert.assertNull("StaffClassification is expected to be 'null'", seoae.getStaffClassification());
        Assert.assertNull("End Date is expected to be 'null'", seoae.getEndDate());
        Assert.assertNull("Begin Date is expected to be 'null'", seoae.getBeginDate());
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
        JobClassification jobClassification = new JobClassification();
        info.setJobClassification(jobClassification);
        String today = "today";
        String other = "Other";

        Mockito.when(mockSifIdResolver.getSliGuid(employeePersonalRefId, zoneId)).thenReturn(employeePersonalRefId);
        Mockito.when(mockSifIdResolver.getZoneSea(zoneId)).thenReturn(zoneSea);
        Mockito.when(mockDateConverter.convert(calendar)).thenReturn(today);
        Mockito.when(mockJobClassificationConverter.convert(jobClassification)).thenReturn(other);

        List<StaffEducationOrganizationAssociationEntity> result = translator.translate(info, zoneId);
        Assert.assertEquals(1, result.size());

        StaffEducationOrganizationAssociationEntity entity = result.get(0);
        StaffEducationOrganizationAssociationEntity seoae = entity;
        Assert.assertEquals("staffPersonalRefId is expected to be '" + employeePersonalRefId + "'", employeePersonalRefId, seoae.getStaffReference());
        Assert.assertEquals("zoneId is expected to be '" + zoneId + "'", zoneId, seoae.getZoneId());
        Assert.assertEquals("otherSifRefId is expected to be '" + employeePersonalRefId + "'", employeePersonalRefId, seoae.getOtherSifRefId());
        Assert.assertEquals("educationOrganizationReference is expected to be '" + zoneSea + "'", zoneSea, seoae.getEducationOrganizationReference());
        Assert.assertEquals("StaffClassification is expected to be 'Other'", other, seoae.getStaffClassification());
        Assert.assertNotNull("Begin Date is expected to be not 'null'", seoae.getBeginDate());
        Assert.assertEquals("Begin Date is expected to be '" + today + "'", today, seoae.getBeginDate());

    }

}

