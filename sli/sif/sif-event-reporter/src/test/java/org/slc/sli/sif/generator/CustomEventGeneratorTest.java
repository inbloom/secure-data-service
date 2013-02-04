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

package org.slc.sli.sif.generator;

import java.net.URL;
import java.util.Calendar;

import junit.framework.Assert;

import openadk.library.ADK;
import openadk.library.ADKException;
import openadk.library.Event;
import openadk.library.EventAction;
import openadk.library.common.Address;
import openadk.library.common.AddressType;
import openadk.library.common.CitizenshipStatus;
import openadk.library.common.ContactInfo;
import openadk.library.common.CountryCode;
import openadk.library.common.Demographics;
import openadk.library.common.Email;
import openadk.library.common.EmailList;
import openadk.library.common.EmailType;
import openadk.library.common.GradeLevel;
import openadk.library.common.GradeLevelCode;
import openadk.library.common.GradeLevels;
import openadk.library.common.IdentificationInfo;
import openadk.library.common.IdentificationInfoList;
import openadk.library.common.Name;
import openadk.library.common.NameType;
import openadk.library.common.PhoneNumber;
import openadk.library.common.PhoneNumberList;
import openadk.library.common.PhoneNumberType;
import openadk.library.common.StatePrCode;
import openadk.library.common.Street;
import openadk.library.student.EducationAgencyTypeCode;
import openadk.library.student.LEAContact;
import openadk.library.student.LEAContactList;
import openadk.library.student.LEAInfo;
import openadk.library.student.OperationalStatus;
import openadk.library.student.StudentAddressList;
import openadk.library.student.StudentPersonal;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for CustomEventGenerator
 *
 * @author vmcglaughlin
 *
 */
public class CustomEventGeneratorTest {

    @Before
    public void setup() throws ADKException {
        ADK.initialize();
    }

    @Test
    public void testGenerateStudentPersonalDefaultEvent() throws ADKException {
        String messageFile = getPathForFile("/element_xml/StudentPersonal.xml");
        Event studentPersonalEvent = CustomEventGenerator.generateEvent(messageFile, EventAction.ADD);

        EventAction eventAction = studentPersonalEvent.getAction();
        Assert.assertEquals(EventAction.ADD, eventAction);

        checkStudentPersonal(studentPersonalEvent);
    }

    @Test
    public void testGenerateStudentPersonalAddEvent() throws ADKException {
        runStudentPersonalTests(EventAction.ADD);
    }

    @Test
    public void testGenerateStudentPersonalChangeEvent() throws ADKException {
        runStudentPersonalTests(EventAction.CHANGE);
    }

    @Test
    public void testGenerateStudentPersonalDeleteEvent() throws ADKException {
        runStudentPersonalTests(EventAction.DELETE);
    }

    private void runStudentPersonalTests(EventAction action) throws ADKException {
        String messageFile = getPathForFile("/element_xml/StudentPersonal.xml");
        Event studentPersonalEvent = CustomEventGenerator.generateEvent(messageFile, action);

        EventAction eventAction = studentPersonalEvent.getAction();
        Assert.assertEquals(action, eventAction);

        checkStudentPersonal(studentPersonalEvent);
    }

    private void checkStudentPersonal(Event studentPersonalEvent) throws ADKException {
        StudentPersonal studentPersonal = (StudentPersonal) studentPersonalEvent.getData().readDataObject();
        Assert.assertEquals("2044A2B5639A45EF8273A6593B67EFEA", studentPersonal.getRefId());
        Assert.assertEquals("1982", studentPersonal.getGraduationDate().getValue());

        Name name = studentPersonal.getName();
        Assert.assertEquals(NameType.NAME_OF_RECORD.getValue(), name.getType());
        Assert.assertEquals("Quest", name.getLastName());
        Assert.assertEquals("Jonathan", name.getFirstName());
        Assert.assertNull(name.getMiddleName());
        Assert.assertEquals("Jonny", name.getPreferredName());

        Demographics demographics = studentPersonal.getDemographics();
        Assert.assertEquals(CitizenshipStatus.USCITIZEN.getValue(), demographics.getCitizenshipStatus());
        Assert.assertEquals(CountryCode.US.getValue(), demographics.getCountryOfBirth());
        Assert.assertEquals(StatePrCode.AK.getValue(), demographics.getStateOfBirth());

        Calendar birthDate = demographics.getBirthDate();
        Assert.assertEquals(1982, birthDate.get(Calendar.YEAR));
        Assert.assertEquals(0, birthDate.get(Calendar.MONTH));
        Assert.assertEquals(20, birthDate.get(Calendar.DATE));

        StudentAddressList addressList = studentPersonal.getAddressList();
        Assert.assertEquals(1, addressList.size());

        Address address = addressList.get(0);
        Assert.assertEquals("Salt Lake City", address.getCity());
        Assert.assertEquals(StatePrCode.UT.getValue(), address.getStateProvince());
        Assert.assertEquals(CountryCode.US.getValue(), address.getCountry());
        Assert.assertEquals("84102", address.getPostalCode());
        Assert.assertEquals(AddressType.MAILING.getValue(), address.getType());

        Street street = address.getStreet();
        Assert.assertEquals("1 IBM Plaza", street.getLine1());
        Assert.assertEquals("2000", street.getApartmentNumber());
        Assert.assertEquals("Suite 2000", street.getLine2());
        Assert.assertEquals("Salt Lake City, UT 84102", street.getLine3());
        Assert.assertEquals("IBM", street.getStreetName());
        Assert.assertEquals("1", street.getStreetNumber());
        Assert.assertEquals("Plaza", street.getStreetType());
        Assert.assertEquals("Suite", street.getApartmentType());

        PhoneNumberList phoneNumberList = studentPersonal.getPhoneNumberList();
        Assert.assertEquals(1, phoneNumberList.size());

        PhoneNumber phoneNumber = phoneNumberList.get(0);
        Assert.assertEquals(PhoneNumberType.PRIMARY.getValue(), phoneNumber.getType());
        Assert.assertEquals("(312) 555-1234", phoneNumber.getNumber());
    }

    @Test
    public void testGenerateLeaInfoDefaultEvent() throws ADKException {
        String messageFile = getPathForFile("/element_xml/LEAInfo.xml");
        Event leaInfoEvent = CustomEventGenerator.generateEvent(messageFile, EventAction.ADD);

        EventAction eventAction = leaInfoEvent.getAction();
        Assert.assertEquals(EventAction.ADD, eventAction);

        checkLeaInfo(leaInfoEvent);
    }

    @Test
    public void testGenerateLeaInfoAddEvent() throws ADKException {
        runLeaInfoTests(EventAction.ADD);
    }

    @Test
    public void testGenerateLeaInfoChangeEvent() throws ADKException {
        runLeaInfoTests(EventAction.CHANGE);
    }

    @Test
    public void testGenerateLeaInfoDeleteEvent() throws ADKException {
        runLeaInfoTests(EventAction.DELETE);
    }

    private void runLeaInfoTests(EventAction action) throws ADKException {
        String messageFile = getPathForFile("/element_xml/LEAInfo.xml");
        Event leaInfoEvent = CustomEventGenerator.generateEvent(messageFile, action);

        EventAction eventAction = leaInfoEvent.getAction();
        Assert.assertEquals(action, eventAction);

        checkLeaInfo(leaInfoEvent);
    }

    private void checkLeaInfo(Event leaInfoEvent) throws ADKException {
        LEAInfo leaInfo = (LEAInfo) leaInfoEvent.getData().readDataObject();
        Assert.assertEquals("D3E34B359D75101A8C3D00AA001A1652", leaInfo.getRefId());
        Assert.assertEquals("946379881", leaInfo.getLocalId());
        Assert.assertEquals("C2345681", leaInfo.getStateProvinceId());
        Assert.assertEquals("4215750", leaInfo.getNCESId());
        Assert.assertEquals("Happy Meadow School District", leaInfo.getLEAName());
        Assert.assertEquals("http://www.happymeadowsd.edu", leaInfo.getLEAURL());
        Assert.assertEquals(OperationalStatus.SCHOOL_OPEN.getValue(), leaInfo.getOperationalStatus());
        Assert.assertEquals(EducationAgencyTypeCode._1151_3486_REGULAR_SCHOOL_DISTRICT.getValue(), leaInfo.getEducationAgencyType().getCode());
        Assert.assertEquals("3", leaInfo.getCongressionalDistrict());

        LEAContactList leaContactList = leaInfo.getLEAContactList();
        Assert.assertEquals(1, leaContactList.size());

        LEAContact leaContact = leaContactList.get(0);
        Assert.assertEquals("Yes", leaContact.getPublishInDirectory());

        ContactInfo contactInfo = leaContact.getContactInfo();
        Assert.assertEquals("Superintendent", contactInfo.getPositionTitle());

        Name name = contactInfo.getName();
        Assert.assertEquals(NameType.NAME_OF_RECORD.getValue(), name.getType());
        Assert.assertEquals("Geisel", name.getLastName());
        Assert.assertEquals("Theodore", name.getFirstName());
        Assert.assertEquals("Theodore Geisel", name.getFullName());

        EmailList emailList = contactInfo.getEmailList();
        Assert.assertEquals(1, emailList.size());

        Email email = emailList.get(0);
        Assert.assertEquals("drseuss@happymeadowsd.edu", email.getValue());
        Assert.assertEquals(EmailType.PRIMARY.getValue(), email.getType());

        PhoneNumberList phoneNumberList = leaInfo.getPhoneNumberList();
        Assert.assertEquals(1, phoneNumberList.size());

        PhoneNumber phoneNumber = phoneNumberList.get(0);
        Assert.assertEquals(PhoneNumberType.PRIMARY.getValue(), phoneNumber.getType());
        Assert.assertEquals("(312) 555-1234", phoneNumber.getNumber());

        IdentificationInfoList identificationInfoList = leaInfo.getIdentificationInfoList();
        Assert.assertEquals(1, identificationInfoList.size());

        IdentificationInfo identificationInfo = identificationInfoList.get(0);
        Assert.assertEquals("9999", identificationInfo.getCode());
        Assert.assertEquals("2134A", identificationInfo.getValue());

        GradeLevels gradeLevels = leaInfo.getGradeLevels();
        Assert.assertEquals(4,  gradeLevels.size());

        GradeLevel gradeLevel1 = gradeLevels.get(0);
        Assert.assertEquals(GradeLevelCode._09.getValue(), gradeLevel1.getCode());

        GradeLevel gradeLevel2 = gradeLevels.get(1);
        Assert.assertEquals(GradeLevelCode._10.getValue(), gradeLevel2.getCode());

        GradeLevel gradeLevel3 = gradeLevels.get(2);
        Assert.assertEquals(GradeLevelCode._11.getValue(), gradeLevel3.getCode());

        GradeLevel gradeLevel4 = gradeLevels.get(3);
        Assert.assertEquals(GradeLevelCode._12.getValue(), gradeLevel4.getCode());
    }

    private String getPathForFile(String filename) {
        URL url = getClass().getResource(filename);
        return url.getPath();
    }

}
