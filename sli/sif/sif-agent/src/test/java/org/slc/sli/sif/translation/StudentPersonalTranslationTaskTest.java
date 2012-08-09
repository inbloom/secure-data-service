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
import java.util.List;

import openadk.library.ADK;
import openadk.library.ADKException;
import openadk.library.common.AddressList;
import openadk.library.common.EmailList;
import openadk.library.common.GradeLevel;
import openadk.library.common.GradeLevelCode;
import openadk.library.common.PhoneNumberList;
import openadk.library.student.LEAInfo;
import openadk.library.student.MostRecent;
import openadk.library.student.StudentAddressList;
import openadk.library.student.StudentPersonal;
import openadk.library.student.StudentSchoolEnrollment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.sif.domain.converter.AddressListConverter;
import org.slc.sli.sif.domain.converter.EmailListConverter;
import org.slc.sli.sif.domain.converter.GradeLevelsConverter;
import org.slc.sli.sif.domain.converter.PhoneNumberListConverter;
import org.slc.sli.sif.domain.slientity.Address;
import org.slc.sli.sif.domain.slientity.ElectronicMail;
import org.slc.sli.sif.domain.slientity.InstitutionTelephone;
import org.slc.sli.sif.domain.slientity.StudentEntity;
import org.slc.sli.sif.domain.slientity.StudentSchoolAssociationEntity;

/**
 * StudentPersonal to StudentEntity unit tests
 *
 * @author slee
 *
 */
public class StudentPersonalTranslationTaskTest
{
    @InjectMocks
    private final StudentPersonalTranslationTask translator = new StudentPersonalTranslationTask();

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
