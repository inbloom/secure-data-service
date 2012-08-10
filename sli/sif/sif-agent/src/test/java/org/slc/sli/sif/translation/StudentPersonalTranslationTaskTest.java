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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import openadk.library.ADK;
import openadk.library.ADKException;
import openadk.library.common.Demographics;
import openadk.library.common.EmailList;
import openadk.library.common.GradeLevel;
import openadk.library.common.GradeLevelCode;
import openadk.library.common.LanguageCode;
import openadk.library.common.LanguageList;
import openadk.library.common.NameType;
import openadk.library.common.PhoneNumberList;
import openadk.library.common.RaceList;
import openadk.library.student.MostRecent;
import openadk.library.student.StudentAddressList;
import openadk.library.student.StudentPersonal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.sif.domain.converter.AddressListConverter;
import org.slc.sli.sif.domain.converter.DemographicsToBirthDataConverter;
import org.slc.sli.sif.domain.converter.EmailListConverter;
import org.slc.sli.sif.domain.converter.GradeLevelsConverter;
import org.slc.sli.sif.domain.converter.LanguageListConverter;
import org.slc.sli.sif.domain.converter.NameConverter;
import org.slc.sli.sif.domain.converter.OtherNamesConverter;
import org.slc.sli.sif.domain.converter.PhoneNumberListConverter;
import org.slc.sli.sif.domain.converter.RaceListConverter;
import org.slc.sli.sif.domain.slientity.Address;
import org.slc.sli.sif.domain.slientity.BirthData;
import org.slc.sli.sif.domain.slientity.ElectronicMail;
import org.slc.sli.sif.domain.slientity.InstitutionTelephone;
import org.slc.sli.sif.domain.slientity.StudentEntity;

/**
 * StudentPersonal to StudentEntity unit tests
 *
 * @author slee
 *
 */
public class StudentPersonalTranslationTaskTest {
       
    @InjectMocks
    private final StudentPersonalTranslationTask translator = new StudentPersonalTranslationTask();

    @Mock
    RaceListConverter mockRaceListConverter;

    @Mock
    DemographicsToBirthDataConverter mockBirthDataConverter;

    @Mock
    NameConverter mockNameConverter;

    @Mock
    OtherNamesConverter mockOtherNameConverter;

    @Mock
    LanguageListConverter mockLanguageListConverter;
    
    @Mock
    GradeLevelsConverter mockGradeLevelsConverter;

    @Mock
    EmailListConverter mockEmailListConverter;

    @Mock
    AddressListConverter mockAddressConverter;

    @Mock
    PhoneNumberListConverter mockPhoneNumberListConverter;

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
        List<StudentEntity> result = translator.translate(new StudentPersonal());
        Assert.assertNotNull("Result was null", result);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testRaceList() throws SifTranslationException {
        StudentPersonal info = new StudentPersonal();
        Demographics demographics = new Demographics();
        RaceList raceList = new RaceList();
        demographics.setRaceList(raceList);
        info.setDemographics(demographics);
        
        List<String> race = new ArrayList<String>();
        Mockito.when(mockRaceListConverter.convert(raceList)).thenReturn(race);

        List<StudentEntity> result = translator.translate(info);
        Assert.assertEquals(1, result.size());
        StudentEntity entity = result.get(0);
        Assert.assertEquals(race, entity.getRace());
    }
    
    @Test
    public void testBirthData() throws SifTranslationException {
        StudentPersonal info = new StudentPersonal();
        Demographics demographics = new Demographics();
        info.setDemographics(demographics);
        
        BirthData birthData = new BirthData();        
        Mockito.when(mockBirthDataConverter.convert(demographics)).thenReturn(birthData);

        List<StudentEntity> result = translator.translate(info);
        Assert.assertEquals(1, result.size());
        StudentEntity entity = result.get(0);
        Assert.assertEquals(birthData, entity.getBirthData());
    }
    
    @Test
    public void testName() throws SifTranslationException {
        StudentPersonal info = new StudentPersonal();
        openadk.library.common.Name original = new openadk.library.common.Name(NameType.BIRTH, "Smith", "John");
        info.setName(original);

        org.slc.sli.sif.domain.slientity.Name name = new org.slc.sli.sif.domain.slientity.Name();
        Mockito.when(mockNameConverter.convert(original)).thenReturn(name);

        List<StudentEntity> result = translator.translate(info);
        Assert.assertEquals(1, result.size());
        StudentEntity entity = result.get(0);
        Assert.assertEquals(name, entity.getName());
    }
    
    @Test
    public void testOtherNames() throws SifTranslationException {
        StudentPersonal info = new StudentPersonal();
        openadk.library.common.OtherNames original = new openadk.library.common.OtherNames();        
        info.setOtherNames(original);

        List<org.slc.sli.sif.domain.slientity.OtherName> otherNames = new ArrayList<org.slc.sli.sif.domain.slientity.OtherName>();
        Mockito.when(mockOtherNameConverter.convert(original)).thenReturn(otherNames);

        List<StudentEntity> result = translator.translate(info);
        Assert.assertEquals(1, result.size());
        StudentEntity entity = result.get(0);
        Assert.assertEquals(otherNames, entity.getOtherName());
    }
    
    @Test
    public void testLanguages() throws SifTranslationException {
        StudentPersonal info = new StudentPersonal();
        Demographics demographics = new Demographics();
        LanguageList languageList = new LanguageList();
        languageList.addLanguage(LanguageCode.ENGLISH);
        languageList.addLanguage(LanguageCode.CHINESE);
        demographics.setLanguageList(languageList);
        info.setDemographics(demographics);
        
        Mockito.when(mockLanguageListConverter.convert(Mockito.any(LanguageList.class))).thenReturn(Arrays.asList("English","Chinese"));

        List<StudentEntity> result = translator.translate(info);
        Assert.assertEquals(1, result.size());
        StudentEntity entity = result.get(0);
        List<String> list = entity.getLanguages();
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("language[0] is expected to be 'English'", "English", list.get(0));
        Assert.assertEquals("language[1] is expected to be 'Chinese'", "Chinese", list.get(1));
        
        list = entity.getHomeLanguages();
        Assert.assertNull("Home Languages was not null", list);
    }
    
    @Test
    public void testGradeLevel() throws SifTranslationException {
        StudentPersonal info = new StudentPersonal();
        GradeLevel gradeLevel = new GradeLevel(GradeLevelCode._10);
        MostRecent mostRecent = new MostRecent();
        mostRecent.setGradeLevel(gradeLevel);
        info.setMostRecent(mostRecent);
        Mockito.when(mockGradeLevelsConverter.convert(gradeLevel)).thenReturn("Tenth grade");

        List<StudentEntity> result = translator.translate(info);
        Assert.assertEquals(1, result.size());
        StudentEntity entity = result.get(0);
        Assert.assertEquals("entry grade level is expected to be 'Tenth grade'", "Tenth grade", entity.getGradeLevel());
    }

    @Test
    public void testEmailList() throws SifTranslationException {
        EmailList emailList = new EmailList();
        StudentPersonal info = new StudentPersonal();
        info.setEmailList(emailList);

        List<ElectronicMail> emails = new ArrayList<ElectronicMail>();

        Mockito.when(mockEmailListConverter.convert(emailList)).thenReturn(emails);

        List<StudentEntity> result = translator.translate(info);
        Assert.assertEquals(1, result.size());
        StudentEntity entity = result.get(0);

        Mockito.verify(mockEmailListConverter).convert(emailList);
        Assert.assertEquals(emails, entity.getElectronicMail());
    }

    @Test
    public void testAddressList() throws SifTranslationException {
        StudentAddressList addressList = new StudentAddressList();
        StudentPersonal info = new StudentPersonal();
        info.setAddressList(addressList);

        List<Address> address = new ArrayList<Address>();

        Mockito.when(mockAddressConverter.convert(Mockito.eq(addressList))).thenReturn(
               address);

        List<StudentEntity> result = translator.translate(info);
        Assert.assertEquals(1, result.size());
        StudentEntity entity = result.get(0);

        Mockito.verify(mockAddressConverter).convert(Mockito.eq(addressList));
        Assert.assertEquals(address, entity.getAddress());

    }

    @Test
    public void testPhoneNumbers() throws SifTranslationException {
        PhoneNumberList phoneNumberList = new PhoneNumberList();
        StudentPersonal info = new StudentPersonal();
        info.setPhoneNumberList(phoneNumberList);

        List<InstitutionTelephone> telephones = new ArrayList<InstitutionTelephone>();

        Mockito.when(mockPhoneNumberListConverter.convert(phoneNumberList)).thenReturn(telephones);

        List<StudentEntity> result = translator.translate(info);
        Assert.assertEquals(1, result.size());
        StudentEntity entity = result.get(0);

        Mockito.verify(mockPhoneNumberListConverter).convert(phoneNumberList);
        Assert.assertEquals(telephones, entity.getTelephone());
    }
}
