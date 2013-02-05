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
import openadk.library.common.Demographics;
import openadk.library.common.EmailList;
import openadk.library.common.Gender;
import openadk.library.common.NameType;
import openadk.library.common.PhoneNumberList;
import openadk.library.common.RaceList;
import openadk.library.common.YesNo;
import openadk.library.hrfin.EmployeePersonal;
import openadk.library.hrfin.HrOtherIdList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.sif.AdkTest;
import org.slc.sli.sif.domain.converter.AddressListConverter;
import org.slc.sli.sif.domain.converter.DemographicsToBirthDataConverter;
import org.slc.sli.sif.domain.converter.EmailListConverter;
import org.slc.sli.sif.domain.converter.GenderConverter;
import org.slc.sli.sif.domain.converter.HrOtherIdListConverter;
import org.slc.sli.sif.domain.converter.NameConverter;
import org.slc.sli.sif.domain.converter.PhoneNumberListConverter;
import org.slc.sli.sif.domain.converter.RaceListConverter;
import org.slc.sli.sif.domain.converter.YesNoUnknownConverter;
import org.slc.sli.sif.domain.slientity.Address;
import org.slc.sli.sif.domain.slientity.BirthData;
import org.slc.sli.sif.domain.slientity.ElectronicMail;
import org.slc.sli.sif.domain.slientity.PersonalTelephone;
import org.slc.sli.sif.domain.slientity.StaffEntity;
import org.slc.sli.sif.domain.slientity.StaffIdentificationCode;

/**
 * EmployeePersonal to StaffEntity unit tests
 *
 * @author slee
 *
 */
public class EmployeePersonalTranslationTaskTest extends AdkTest {

    @InjectMocks
    private final EmployeePersonalTranslationTask translator = new EmployeePersonalTranslationTask();

    @Mock
    YesNoUnknownConverter mockYesNoUnknownConverter;

    @Mock
    GenderConverter mockGenderConverter;

    @Mock
    RaceListConverter mockRaceListConverter;

    @Mock
    DemographicsToBirthDataConverter mockBirthDataConverter;

    @Mock
    NameConverter mockNameConverter;

    @Mock
    HrOtherIdListConverter mockHrOtherIdListConverter;

    @Mock
    EmailListConverter mockEmailListConverter;

    @Mock
    AddressListConverter mockAddressConverter;

    @Mock
    PhoneNumberListConverter mockPhoneNumberListConverter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNotNull() throws SifTranslationException {
        List<StaffEntity> result = translator.translate(new EmployeePersonal(), "");
        Assert.assertNotNull("Result was null", result);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testBasics() throws SifTranslationException {
        EmployeePersonal info = new EmployeePersonal();
        Demographics demographics = new Demographics();
        demographics.setHispanicLatino(YesNo.NO);
        demographics.setGender(Gender.FEMALE);

        String stateProvinceId = "stateProvinceId";
        info.setStateProvinceId(stateProvinceId);
        info.setDemographics(demographics);

        Mockito.when(mockYesNoUnknownConverter.convert("Yes")).thenReturn(true);
        Mockito.when(mockYesNoUnknownConverter.convert("No")).thenReturn(false);
        Mockito.when(mockGenderConverter.convert("F")).thenReturn("Female");

        List<StaffEntity> result = translator.translate(info, "");
        Assert.assertEquals(1, result.size());
        StaffEntity entity = result.get(0);

        Assert.assertEquals("StaffUniqueStateId is expected to be '" + stateProvinceId + "'", stateProvinceId,
                entity.getStaffUniqueStateId());
        Assert.assertEquals("HispanicLatinoEthnicity is expected to be 'false'", false,
                entity.getHispanicLatinoEthnicity());
        Assert.assertEquals("SexType is expected to be 'Female'", "Female", entity.getSex());
        Assert.assertFalse("isCreatedByOthers() must be 'false'", entity.isCreatedByOthers());
    }

    @Test
    public void testRaceList() throws SifTranslationException {
        EmployeePersonal info = new EmployeePersonal();
        Demographics demographics = new Demographics();
        RaceList raceList = new RaceList();
        demographics.setRaceList(raceList);
        info.setDemographics(demographics);

        List<String> race = new ArrayList<String>();
        Mockito.when(mockRaceListConverter.convert(raceList)).thenReturn(race);

        List<StaffEntity> result = translator.translate(info, "");
        Assert.assertEquals(1, result.size());
        StaffEntity entity = result.get(0);
        Assert.assertEquals(race, entity.getRace());
    }

    @Test
    public void testBirthData() throws SifTranslationException {
        EmployeePersonal info = new EmployeePersonal();
        Demographics demographics = new Demographics();
        info.setDemographics(demographics);

        BirthData birthData = new BirthData();
        Mockito.when(mockBirthDataConverter.convert(demographics)).thenReturn(birthData);

        List<StaffEntity> result = translator.translate(info, "");
        Assert.assertEquals(1, result.size());
        StaffEntity entity = result.get(0);
        Assert.assertEquals(birthData, entity.getBirthData());
    }

    @Test
    public void testName() throws SifTranslationException {
        EmployeePersonal info = new EmployeePersonal();
        openadk.library.common.Name original = new openadk.library.common.Name(NameType.BIRTH, "Smith", "John");
        info.setName(original);

        org.slc.sli.sif.domain.slientity.Name name = new org.slc.sli.sif.domain.slientity.Name();
        Mockito.when(mockNameConverter.convert(original)).thenReturn(name);

        List<StaffEntity> result = translator.translate(info, "");
        Assert.assertEquals(1, result.size());
        StaffEntity entity = result.get(0);
        Assert.assertEquals(name, entity.getName());
    }

    @Test
    public void testStaffIdentificationCode() throws SifTranslationException {
        EmployeePersonal info = new EmployeePersonal();
        HrOtherIdList original = new HrOtherIdList();
        info.setOtherIdList(original);

        List<StaffIdentificationCode> list = new ArrayList<StaffIdentificationCode>();
        Mockito.when(mockHrOtherIdListConverter.convert(original)).thenReturn(list);

        List<StaffEntity> result = translator.translate(info, "");
        Assert.assertEquals(1, result.size());
        StaffEntity entity = result.get(0);
        Assert.assertEquals(list, entity.getStaffIdentificationCode());
    }

    @Test
    public void testEmailList() throws SifTranslationException {
        EmailList emailList = new EmailList();
        EmployeePersonal info = new EmployeePersonal();
        info.setEmailList(emailList);

        List<ElectronicMail> emails = new ArrayList<ElectronicMail>();

        Mockito.when(mockEmailListConverter.convert(emailList)).thenReturn(emails);

        List<StaffEntity> result = translator.translate(info, "");
        Assert.assertEquals(1, result.size());
        StaffEntity entity = result.get(0);

        Mockito.verify(mockEmailListConverter).convert(emailList);
        Assert.assertEquals(emails, entity.getElectronicMail());
    }

    @Test
    public void testAddressList() throws SifTranslationException {
        AddressList addressList = new AddressList();
        EmployeePersonal info = new EmployeePersonal();
        info.setAddressList(addressList);

        List<Address> address = new ArrayList<Address>();

        Mockito.when(mockAddressConverter.convert(Mockito.eq(addressList))).thenReturn(address);

        List<StaffEntity> result = translator.translate(info, "");
        Assert.assertEquals(1, result.size());
        StaffEntity entity = result.get(0);

        Mockito.verify(mockAddressConverter).convert(Mockito.eq(addressList));
        Assert.assertEquals(address, entity.getAddress());

    }

    @Test
    public void testPhoneNumbers() throws SifTranslationException {
        PhoneNumberList phoneNumberList = new PhoneNumberList();
        EmployeePersonal info = new EmployeePersonal();
        info.setPhoneNumberList(phoneNumberList);

        List<PersonalTelephone> telephones = new ArrayList<PersonalTelephone>();

        Mockito.when(mockPhoneNumberListConverter.convertPersonalTelephone(phoneNumberList)).thenReturn(telephones);

        List<StaffEntity> result = translator.translate(info, "");
        Assert.assertEquals(1, result.size());
        StaffEntity entity = result.get(0);

        Mockito.verify(mockPhoneNumberListConverter).convertPersonalTelephone(phoneNumberList);
        Assert.assertEquals(telephones, entity.getTelephone());
    }
}
