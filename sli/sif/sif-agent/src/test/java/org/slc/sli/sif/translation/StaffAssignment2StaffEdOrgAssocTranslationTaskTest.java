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

import openadk.library.student.StaffAssignment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.sif.AdkTest;
import org.slc.sli.sif.domain.converter.DateConverter;
import org.slc.sli.sif.domain.converter.GradeLevelsConverter;
import org.slc.sli.sif.domain.converter.TeachingAssignmentConverter;
import org.slc.sli.sif.domain.slientity.StaffEducationOrganizationAssociationEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;

/**
 * StaffAssignment2StaffEdOrgAssocTranslationTask unit tests
 *
 * @author slee
 *
 */
public class StaffAssignment2StaffEdOrgAssocTranslationTaskTest extends AdkTest {

    @InjectMocks
    private final StaffAssignment2StaffEdOrgAssocTranslationTask translator = new StaffAssignment2StaffEdOrgAssocTranslationTask();

    @Mock
    SifIdResolver mockSifIdResolver;

    @Mock
    DateConverter mockDateConverter;

    @Mock
    GradeLevelsConverter mockGradeLevelsConverter;

    @Mock
    TeachingAssignmentConverter mockTeachingAssignmentConverter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNotNull() throws SifTranslationException {
        List<StaffEducationOrganizationAssociationEntity> result = translator.translate(new StaffAssignment(), "");
        Assert.assertNotNull("Result was null", result);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testBasics() throws SifTranslationException {
        StaffAssignment info = new StaffAssignment();
        String staffPersonalRefId = "staffPersonalRefId";
        info.setStaffPersonalRefId(staffPersonalRefId);
        String schoolInfoRefId = "schoolInfoRefId";
        info.setSchoolInfoRefId(schoolInfoRefId);
        String zoneId = "zoneId";
        Calendar calendar = Calendar.getInstance();
        info.setJobStartDate(calendar);
        String today = "today";

        Mockito.when(mockSifIdResolver.getSliGuid(staffPersonalRefId, zoneId)).thenReturn(staffPersonalRefId);
        Mockito.when(mockSifIdResolver.getSliGuid(schoolInfoRefId, zoneId)).thenReturn(schoolInfoRefId);
        Mockito.when(mockDateConverter.convert(calendar)).thenReturn(today);

        List<StaffEducationOrganizationAssociationEntity> result = translator.translate(info, zoneId);
        Assert.assertEquals(1, result.size());

        StaffEducationOrganizationAssociationEntity e = result.get(0);
        Assert.assertEquals("staffReference is expected to be '" + staffPersonalRefId + "'", staffPersonalRefId, e.getStaffReference());
        Assert.assertEquals("educationOrganizationReference is expected to be '" + schoolInfoRefId + "'", schoolInfoRefId, e.getEducationOrganizationReference());
        Assert.assertNull("End Date is expected to be 'null'", e.getEndDate());
        Assert.assertNotNull("Begin Date is expected to be not 'null'", e.getBeginDate());
        Assert.assertEquals("Begin Date is expected to be '" + today + "'", today, e.getBeginDate());
        Assert.assertEquals("StaffClassification is expected to be 'Other'", "Other", e.getStaffClassification());
        Assert.assertEquals("otherSifRefId is expected to be '" + staffPersonalRefId + "'", staffPersonalRefId, e.getOtherSifRefId());

    }
}

