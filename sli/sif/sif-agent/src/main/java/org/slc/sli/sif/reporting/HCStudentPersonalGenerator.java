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

package org.slc.sli.sif.reporting;

import java.util.Calendar;
import java.util.Properties;

import openadk.library.ADK;
import openadk.library.Event;
import openadk.library.EventAction;
import openadk.library.common.Address;
import openadk.library.common.AddressType;
import openadk.library.common.CitizenshipStatus;
import openadk.library.common.CountryCode;
import openadk.library.common.Demographics;
import openadk.library.common.EmailList;
import openadk.library.common.EmailType;
import openadk.library.common.Name;
import openadk.library.common.NameType;
import openadk.library.common.PhoneNumberList;
import openadk.library.common.PhoneNumberType;
import openadk.library.common.StatePrCode;
import openadk.library.common.Street;
import openadk.library.student.StudentAddressList;
import openadk.library.student.StudentPersonal;

public class HCStudentPersonalGenerator implements EventGenerator {

    @Override
    public Event generateEvent(Properties props) {
        StudentPersonal student = generateTestStudent();
        Event event = new Event(student, EventAction.ADD);
        return event;
    }

    private StudentPersonal generateTestStudent() {
        StudentPersonal studentPersonal = new StudentPersonal();
        studentPersonal.setRefId(ADK.makeGUID());
        Name name = new Name(NameType.NAME_OF_RECORD, "Student", "Joe");
        name.setMiddleName("");
        name.setPreferredName("Joe");
        studentPersonal.setName(name);
        EmailList emailList = new EmailList();
        emailList.addEmail(EmailType.PRIMARY, "joe.student@anyschool.edu");
        studentPersonal.setGraduationDate("1982");
        Demographics demographics = new Demographics();
        Calendar calendar = Calendar.getInstance();
        calendar.set(1981, 12, 20);
        demographics.setBirthDate(calendar);
        demographics.setCitizenshipStatus(CitizenshipStatus.USCITIZEN);
        demographics.setCountryOfBirth(CountryCode.US);
        demographics.setStateOfBirth(StatePrCode.AK);
        studentPersonal.setDemographics(demographics);
        Address address = new Address();
        address.setCity("Salt Lake City");
        address.setStateProvince(StatePrCode.UT);
        address.setCountry(CountryCode.US);
        address.setPostalCode("84102");
        Street street = new Street();
        street.setLine1("1 IBM Plaza");
        street.setApartmentNumber("2000");
        street.setLine2("Suite 2000");
        street.setLine3("Salt Lake City, UT 84102");
        street.setStreetName("IBM");
        street.setStreetNumber("1");
        street.setStreetType("Plaza");
        street.setApartmentType("Suite");
        address.setStreet(street);
        address.setType(AddressType.MAILING);
        StudentAddressList addressList = new StudentAddressList();
        addressList.add(address);
        studentPersonal.setAddressList(addressList);
        PhoneNumberList phoneNumberList = new PhoneNumberList();
        phoneNumberList.addPhoneNumber(PhoneNumberType.PRIMARY, "(312) 555-1234");
        studentPersonal.setPhoneNumberList(phoneNumberList);

        return studentPersonal;
    }
}
