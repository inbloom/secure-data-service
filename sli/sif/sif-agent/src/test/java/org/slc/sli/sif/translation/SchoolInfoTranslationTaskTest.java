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

import java.util.ArrayList;
import java.util.List;

import openadk.library.common.AddressList;
import openadk.library.common.GradeLevels;
import openadk.library.common.PhoneNumberList;
import openadk.library.student.OperationalStatus;
import openadk.library.student.SchoolFocusList;
import openadk.library.student.SchoolInfo;
import openadk.library.student.SchoolLevelType;
import openadk.library.student.Title1Status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.sif.AdkTest;
import org.slc.sli.sif.domain.converter.AddressListConverter;
import org.slc.sli.sif.domain.converter.GradeLevelsConverter;
import org.slc.sli.sif.domain.converter.OperationalStatusConverter;
import org.slc.sli.sif.domain.converter.PhoneNumberListConverter;
import org.slc.sli.sif.domain.converter.SchoolFocusConverter;
import org.slc.sli.sif.domain.converter.SchoolLevelTypeConverter;
import org.slc.sli.sif.domain.converter.TitleIPartASchoolDesignationConverter;
import org.slc.sli.sif.domain.slientity.Address;
import org.slc.sli.sif.domain.slientity.InstitutionTelephone;
import org.slc.sli.sif.domain.slientity.SchoolEntity;
import org.slc.sli.sif.domain.slientity.TitleIPartASchoolDesignation;
import org.slc.sli.sif.slcinterface.SifIdResolver;

/**
 *
 * SchoolInfoTranslationTask unit tests
 *
 */
public class SchoolInfoTranslationTaskTest extends AdkTest {

    @InjectMocks
    private final SchoolInfoTranslationTask translator = new SchoolInfoTranslationTask();

    @Mock
    SifIdResolver mockSifIdResolver;

    @Mock
    AddressListConverter mockAddressConverter;

    @Mock
    SchoolFocusConverter mockSchoolFocusConverter;

    @Mock
    GradeLevelsConverter mockGradeLevelsConverter;

    @Mock
    OperationalStatusConverter mockOperationalStatusConverter;

    @Mock
    SchoolLevelTypeConverter mockSchoolTypeConverter;

    @Mock
    TitleIPartASchoolDesignationConverter mockTitleIPartASchoolDesignationConverter;

    @Mock
    PhoneNumberListConverter mockPhoneNumberListConverter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNotNull() throws SifTranslationException {
        List<SchoolEntity> result = translator.translate(new SchoolInfo(), null);
        Assert.assertNotNull("Result was null", result);
    }

    @Test
    public void testBasicFields() throws SifTranslationException {
        SchoolInfo info = new SchoolInfo();

        String leaRefId = "SIF_LEAREFID";
        String leaGuid = "SLI_LEAGUID";
        String zoneId = "zoneId";
        info.setLEAInfoRefId(leaRefId);
        Mockito.when(mockSifIdResolver.getSliGuid(leaRefId, zoneId)).thenReturn(leaGuid);

        String stateOrgId = "stateOrgId";
        info.setStateProvinceId(stateOrgId);

        String schoolName = "schoolName";
        info.setSchoolName(schoolName);

        String schoolUrl = "schoolUrl";
        info.setSchoolURL(schoolUrl);

        info.setTitle1Status(Title1Status.SCHOOLWIDE);

        List<SchoolEntity> result = translator.translate(info, zoneId);
        Assert.assertEquals(1, result.size());
        SchoolEntity entity = result.get(0);

        Assert.assertEquals(stateOrgId, entity.getStateOrganizationId());
        Assert.assertEquals(schoolName, entity.getNameOfInstitution());
        Assert.assertEquals(schoolUrl, entity.getWebSite());
        Assert.assertEquals("School", entity.getOrganizationCategories().get(0));
        Assert.assertEquals(schoolUrl, entity.getWebSite());

        Assert.assertEquals(leaGuid, entity.getParentEducationAgencyReference());
    }

    @Test
    public void testAddressList() throws SifTranslationException {

        AddressList addressList = new AddressList();
        SchoolInfo info = new SchoolInfo();
        info.setAddressList(addressList);

        List<Address> address = new ArrayList<Address>();

        Mockito.when(mockAddressConverter.convert(Mockito.eq(addressList))).thenReturn(address);

        List<SchoolEntity> result = translator.translate(info, null);
        Assert.assertEquals(1, result.size());
        SchoolEntity entity = result.get(0);

        Mockito.verify(mockAddressConverter).convert(Mockito.eq(addressList));
        Assert.assertEquals(address, entity.getAddress());

    }

    @Test
    public void testSchoolFocus() throws SifTranslationException {
        SchoolFocusList focusList = new SchoolFocusList();
        SchoolInfo info = new SchoolInfo();
        info.setSchoolFocusList(focusList);

        Mockito.when(mockSchoolFocusConverter.convert(focusList)).thenReturn("schoolType");

        List<SchoolEntity> result = translator.translate(info, null);
        Assert.assertEquals(1, result.size());
        SchoolEntity entity = result.get(0);

        Mockito.verify(mockSchoolFocusConverter).convert(Mockito.eq(focusList));
        Assert.assertEquals("schoolType", entity.getSchoolType());
    }

    @Test
    public void testGradeLevels() throws SifTranslationException {
        GradeLevels sifGradeLevels = new GradeLevels();
        SchoolInfo info = new SchoolInfo();
        info.setGradeLevels(sifGradeLevels);

        List<String> convertedGrades = new ArrayList<String>();

        Mockito.when(mockGradeLevelsConverter.convert(sifGradeLevels)).thenReturn(convertedGrades);

        List<SchoolEntity> result = translator.translate(info, null);
        Assert.assertEquals(1, result.size());
        SchoolEntity entity = result.get(0);

        Mockito.verify(mockGradeLevelsConverter).convert(Mockito.eq(sifGradeLevels));
        Assert.assertEquals(convertedGrades, entity.getGradesOffered());
    }

    @Test
    public void testSchoolTypes() throws SifTranslationException {
        SchoolInfo info = new SchoolInfo();
        info.setSchoolType(SchoolLevelType.ELEMENTARY);

        List<String> schoolTypes = new ArrayList<String>();

        Mockito.when(mockSchoolTypeConverter.convertAsList(SchoolLevelType.ELEMENTARY)).thenReturn(schoolTypes);

        List<SchoolEntity> result = translator.translate(info, null);
        Assert.assertEquals(1, result.size());
        SchoolEntity entity = result.get(0);

        Mockito.verify(mockSchoolTypeConverter).convertAsList(SchoolLevelType.ELEMENTARY);
        Assert.assertEquals(schoolTypes, entity.getSchoolCategories());
    }

    @Test
    public void testPhoneNumbers() throws SifTranslationException {
        PhoneNumberList phoneNumberList = new PhoneNumberList();
        SchoolInfo info = new SchoolInfo();
        info.setPhoneNumberList(phoneNumberList);

        List<InstitutionTelephone> telephones = new ArrayList<InstitutionTelephone>();

        Mockito.when(mockPhoneNumberListConverter.convertInstitutionTelephone(phoneNumberList)).thenReturn(telephones);

        List<SchoolEntity> result = translator.translate(info, null);
        Assert.assertEquals(1, result.size());
        SchoolEntity entity = result.get(0);

        Mockito.verify(mockPhoneNumberListConverter).convertInstitutionTelephone(phoneNumberList);
        Assert.assertEquals(telephones, entity.getTelephone());
    }

    @Test
    public void testTitleIDesignation() throws SifTranslationException {
        SchoolInfo info = new SchoolInfo();
        info.setTitle1Status(Title1Status.SCHOOLWIDE);

        Mockito.when(mockTitleIPartASchoolDesignationConverter.convert(Title1Status.SCHOOLWIDE)).thenReturn(
                TitleIPartASchoolDesignation.PART_A_SCHOOLWIDE);

        List<SchoolEntity> result = translator.translate(info, null);
        Assert.assertEquals(1, result.size());
        SchoolEntity entity = result.get(0);

        Mockito.verify(mockTitleIPartASchoolDesignationConverter).convert(Title1Status.SCHOOLWIDE);
        Assert.assertEquals(TitleIPartASchoolDesignation.PART_A_SCHOOLWIDE.getText(), entity.getSchoolType());
    }

    @Test
    public void testOperationalStatus() throws SifTranslationException {
        SchoolInfo info = new SchoolInfo();
        info.setOperationalStatus(OperationalStatus.AGENCY_CLOSED);
        Mockito.when(mockOperationalStatusConverter.convert(OperationalStatus.wrap(info.getOperationalStatus())))
                .thenReturn("Closed");
        List<SchoolEntity> result = translator.translate(info, null);
        Assert.assertEquals(1, result.size());
        SchoolEntity entity = result.get(0);

        Mockito.verify(mockOperationalStatusConverter).convert(OperationalStatus.wrap(info.getOperationalStatus()));
        Assert.assertEquals("Closed", entity.getOperationalStatus());
    }

}
