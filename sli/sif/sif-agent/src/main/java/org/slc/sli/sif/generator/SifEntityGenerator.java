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

package org.slc.sli.sif.generator;

import openadk.library.common.Address;
import openadk.library.common.AddressList;
import openadk.library.common.AddressType;
import openadk.library.common.CountryCode;
import openadk.library.common.GradeLevelCode;
import openadk.library.common.GradeLevels;
import openadk.library.common.PhoneNumberList;
import openadk.library.common.PhoneNumberType;
import openadk.library.common.StatePrCode;
import openadk.library.common.Street;
import openadk.library.student.EducationAgencyTypeCode;
import openadk.library.student.OperationalStatus;
import openadk.library.student.SchoolFocus;
import openadk.library.student.SchoolFocusList;
import openadk.library.student.SchoolFocusType;
import openadk.library.student.SchoolInfo;
import openadk.library.student.SchoolLevelType;
import openadk.library.student.LEAInfo;
import openadk.library.datamodel.SEAInfo;


public class SifEntityGenerator
{
    public static SchoolInfo generateTestSchoolInfo() {
        SchoolInfo info = new SchoolInfo();
        info.setRefId("D3E34B359D75101A8C3D00AA001A1652");
        info.setStateProvinceId("Daybreak West High");
        info.setNCESId("421575003045");
        info.setSchoolName("Daybreak West High");
        info.setLEAInfoRefId("73648462888624AA5294BC6380173276");
        
        SchoolFocusList schoolFocusList = new SchoolFocusList();
        schoolFocusList.add(new SchoolFocus(SchoolFocusType.REGULAR));
        info.setSchoolFocusList(schoolFocusList);
        info.setSchoolURL("http://IL-DAYBREAK.edu");
        info.setOperationalStatus(OperationalStatus.SCHOOL_CLOSED);
        info.setSchoolType(SchoolLevelType._0031_2402_HIGH_SCHOOL);

        GradeLevels gradeLevels = new GradeLevels();
        gradeLevels.addGradeLevel(GradeLevelCode._09);
        gradeLevels.addGradeLevel(GradeLevelCode._10);
        gradeLevels.addGradeLevel(GradeLevelCode._11);
        gradeLevels.addGradeLevel(GradeLevelCode._12);
        info.setGradeLevels(gradeLevels);

        Address address = new Address();
        address.setCity("Salt Lake City");
        address.setStateProvince(StatePrCode.IL);
        address.setCountry(CountryCode.US);
        address.setPostalCode("84102");
        Street street = new Street();
        street.setLine1("1 IBM Plaza");
        street.setApartmentNumber("2000");
        street.setLine2("Suite 2000");
        street.setLine3("Salt Lake City, IL 84102");
        street.setStreetName("IBM way");
        street.setStreetNumber("1");
        street.setStreetType("Plaza");
        street.setApartmentType("Suite");
        address.setStreet(street);
        address.setType(AddressType.MAILING);
        AddressList addressList = new AddressList();
        addressList.add(address);
        info.setAddressList(addressList);

        PhoneNumberList phoneNumberList = new PhoneNumberList();
        phoneNumberList.addPhoneNumber(PhoneNumberType.PRIMARY, "(312) 555-1234");
        phoneNumberList.addPhoneNumber(PhoneNumberType.FAX, "(312) 555-2364");
        info.setPhoneNumberList(phoneNumberList);
        return info;
    }
    
    public static LEAInfo generateTestLEAInfo() {
        LEAInfo info = new LEAInfo();
        info.setRefId("73648462888624AA5294BC6380173276");
        info.setStateProvinceId("IL-DAYBREAK");
        info.setNCESId("4215750");
        info.setLEAName("Daybreak School District 4530");
        
        info.setLEAURL("http://IL-DAYBREAK.edu");
        info.setEducationAgencyType(EducationAgencyTypeCode.REG_DISTRICT);
        info.setOperationalStatus(OperationalStatus.SCHOOL_CLOSED);

        GradeLevels gradeLevels = new GradeLevels();
        gradeLevels.addGradeLevel(GradeLevelCode._09);
        gradeLevels.addGradeLevel(GradeLevelCode._10);
        gradeLevels.addGradeLevel(GradeLevelCode._11);
        gradeLevels.addGradeLevel(GradeLevelCode._12);
        info.setGradeLevels(gradeLevels);

        Address address = new Address();
        address.setCity("Salt Lake City");
        address.setStateProvince(StatePrCode.IL);
        address.setCountry(CountryCode.US);
        address.setPostalCode("84102");
        Street street = new Street();
        street.setLine1("1 IBM Plaza");
        street.setApartmentNumber("2000");
        street.setLine2("Suite 2000");
        street.setLine3("Salt Lake City, IL 84102");
        street.setStreetName("IBM way");
        street.setStreetNumber("1");
        street.setStreetType("Plaza");
        street.setApartmentType("Suite");
        address.setStreet(street);
        address.setType(AddressType.MAILING);
        AddressList addressList = new AddressList();
        addressList.add(address);
        info.setAddressList(addressList);

        PhoneNumberList phoneNumberList = new PhoneNumberList();
        phoneNumberList.addPhoneNumber(PhoneNumberType.PRIMARY, "(312) 555-1234");
        phoneNumberList.addPhoneNumber(PhoneNumberType.FAX, "(312) 555-2364");
        info.setPhoneNumberList(phoneNumberList);
        return info;
    }

    public static SEAInfo generateTestSEAInfo() {
        SEAInfo info = new SEAInfo();
        info.setRefId("D3E34B359D75101A8C3D00AA001A1652");
        info.setSEAName("Illinois State Board of Education");        
        info.setSEAURL("http://IL.edu");

        Address address = new Address();
        address.setCity("Salt Lake City");
        address.setStateProvince(StatePrCode.IL);
        address.setCountry(CountryCode.US);
        address.setPostalCode("84102");
        Street street = new Street();
        street.setLine1("1 IBM Plaza");
        street.setApartmentNumber("2000");
        street.setLine2("Suite 2000");
        street.setLine3("Salt Lake City, IL 84102");
        street.setStreetName("IBM way");
        street.setStreetNumber("1");
        street.setStreetType("Plaza");
        street.setApartmentType("Suite");
        address.setStreet(street);
        address.setType(AddressType.MAILING);
        AddressList addressList = new AddressList();
        addressList.add(address);
        info.setAddressList(addressList);

        PhoneNumberList phoneNumberList = new PhoneNumberList();
        phoneNumberList.addPhoneNumber(PhoneNumberType.PRIMARY, "(312) 555-1234");
        phoneNumberList.addPhoneNumber(PhoneNumberType.FAX, "(312) 555-2364");
        info.setPhoneNumberList(phoneNumberList);
        return info;
    }
}

