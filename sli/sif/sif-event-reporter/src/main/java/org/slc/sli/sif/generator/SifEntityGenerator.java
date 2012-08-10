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

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

import openadk.library.common.Address;
import openadk.library.common.AddressList;
import openadk.library.common.AddressType;
import openadk.library.common.CitizenshipStatus;
import openadk.library.common.CountryCode;
import openadk.library.common.Demographics;
import openadk.library.common.EmailList;
import openadk.library.common.EmailType;
import openadk.library.common.EntryTypeCode;
import openadk.library.common.ExitTypeCode;
import openadk.library.common.Gender;
import openadk.library.common.GradeLevelCode;
import openadk.library.common.GradeLevels;
import openadk.library.common.MembershipType;
import openadk.library.common.Name;
import openadk.library.common.NameType;
import openadk.library.common.NonResidentAttendRationale;
import openadk.library.common.OrganizationRelationshipType;
import openadk.library.common.PhoneNumberList;
import openadk.library.common.PhoneNumberType;
import openadk.library.common.PublicSchoolResidenceStatus;
import openadk.library.common.ResidencyStatus;
import openadk.library.common.StatePrCode;
import openadk.library.common.Street;
import openadk.library.common.StudentLEARelationship;
import openadk.library.common.YesNoUnknown;
import openadk.library.datamodel.SEAInfo;
import openadk.library.student.EducationAgencyTypeCode;
import openadk.library.student.FTPTStatus;
import openadk.library.student.LEAInfo;
import openadk.library.student.OperationalStatus;
import openadk.library.student.SchoolFocus;
import openadk.library.student.SchoolFocusList;
import openadk.library.student.SchoolFocusType;
import openadk.library.student.SchoolInfo;
import openadk.library.student.SchoolLevelType;
import openadk.library.student.StudentAddressList;
import openadk.library.student.StudentPersonal;
import openadk.library.student.StudentSchoolEnrollment;
import openadk.library.student.TimeFrame;

/**
 * Sif entity generator
 */
public class SifEntityGenerator {

    public static final String TEST_SCHOOLINFO_REFID = "D3E34B359D75101A8C3D00AA001A1652";
    public static final String TEST_LEAINFO_REFID = "73648462888624AA5294BC6380173276";
    public static final String TEST_SEAINFO_REFID = "73648462888624AA5294BC6380173276";
    public static final String TEST_STUDENTPERSONAL_REFID = "20120808934983498C3D00AA00495948";
    public static final String TEST_STUDENTSCHOOLENROLLMENT_REFID = "A8C3D3E34B359D75101D00AA001A1652";
    public static final String TEST_STUDENTLEARELATIONSHIP_REFID = "98C3D3224B35AA75101D00AA201B1652";

    public static SchoolInfo generateTestSchoolInfo() {
        SchoolInfo info = new SchoolInfo();
        info.setChanged(false);
        info.setRefId(TEST_SCHOOLINFO_REFID);
        info.setStateProvinceId("Daybreak West High");
        info.setNCESId("421575003045");
        info.setSchoolName("Daybreak West High");
        info.setLEAInfoRefId(TEST_LEAINFO_REFID);

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
        info.setChanged(false);
        info.setRefId(TEST_LEAINFO_REFID);
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
        info.setChanged(false);
        info.setRefId(TEST_SEAINFO_REFID);
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

    public static StudentSchoolEnrollment generateTestStudentSchoolEnrollment() {
        StudentSchoolEnrollment retVal = new StudentSchoolEnrollment();
        retVal.setChanged(false);
        retVal.setRefId(TEST_STUDENTSCHOOLENROLLMENT_REFID);
        retVal.setSchoolInfoRefId(TEST_SCHOOLINFO_REFID);
        retVal.setStudentPersonalRefId(TEST_STUDENTPERSONAL_REFID);
        retVal.setMembershipType(MembershipType.HOME);
        retVal.setTimeFrame(TimeFrame.CURRENT);
        retVal.setSchoolYear(2012);
        retVal.setExitType(ExitTypeCode._3502_NOT_ENROLLED_ELIGIBLE_TO);
        retVal.setExitDate(new GregorianCalendar(2012, 9, 17));

        retVal.setEntryDate(new GregorianCalendar(2012, 8, 16));
        retVal.setEntryType(EntryTypeCode._0619_1838);
        retVal.setGradeLevel(GradeLevelCode._10);
        retVal.setFTE(new BigDecimal(1.00));
        retVal.setFTPTStatus(FTPTStatus.FULLTIME);

        ResidencyStatus rs = new ResidencyStatus();
        rs.setCode(PublicSchoolResidenceStatus._0598_1653);
        retVal.setResidencyStatus(rs);
        retVal.setNonResidentAttendReason(NonResidentAttendRationale.VOCATIONAL);

        return retVal;
    }

    public static StudentLEARelationship generateTestStudentLeaRelationship() {
        StudentLEARelationship retVal = new StudentLEARelationship();
        retVal.setChanged(false);
        retVal.setRefId(TEST_STUDENTLEARELATIONSHIP_REFID);
        retVal.setStudentPersonalRefId(TEST_STUDENTPERSONAL_REFID);
        retVal.setLEAInfoRefId(TEST_LEAINFO_REFID);
        retVal.setSchoolYear(2012);

        retVal.setMembershipType(MembershipType.HOME);
        OrganizationRelationshipType ort = new OrganizationRelationshipType();
        ResidencyStatus rs = new ResidencyStatus();
        rs.setCode(PublicSchoolResidenceStatus.RESIDENT);
        ort.setResidencyStatus(rs);
        ort.setProvidingInstruction(true);
        ort.setProvidingServices(true);
        ort.setFinanciallyResponsible(true);
        retVal.setLEARelationshipType(ort);

        retVal.setEntryDate(new GregorianCalendar(2012, 8, 16));
        retVal.setEntryType(EntryTypeCode._0619_1838);
        retVal.setGradeLevel(GradeLevelCode._10);

        return retVal;
    }

    public static StudentPersonal generateTestStudentPersonal() {
        StudentPersonal studentPersonal = new StudentPersonal();
        studentPersonal.setChanged(false);
        studentPersonal.setRefId(TEST_STUDENTPERSONAL_REFID);
        studentPersonal.setStateProvinceId("IL-DAYBREAK-54321");
        Name name = new Name(NameType.NAME_OF_RECORD, "Smith", "Joe");
        name.setMiddleName("");
        name.setPreferredName("Joe");
        studentPersonal.setName(name);
        EmailList emailList = new EmailList();
        emailList.addEmail(EmailType.PRIMARY, "joe.student@anyschool.edu");
        studentPersonal.setEmailList(emailList);
        studentPersonal.setGraduationDate("1982");
        Demographics demographics = new Demographics();
        Calendar calendar = Calendar.getInstance();
        calendar.set(1981, 11, 20);
        demographics.setBirthDate(calendar);
        demographics.setCitizenshipStatus(CitizenshipStatus.USCITIZEN);
        demographics.setCountryOfBirth(CountryCode.US);
        demographics.setStateOfBirth(StatePrCode.AK);
        demographics.setGender(Gender.MALE);
        studentPersonal.setDemographics(demographics);
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
        StudentAddressList addressList = new StudentAddressList();
        addressList.add(address);
        studentPersonal.setAddressList(addressList);
        PhoneNumberList phoneNumberList = new PhoneNumberList();
        phoneNumberList.addPhoneNumber(PhoneNumberType.PRIMARY, "(312) 555-1234");
        studentPersonal.setPhoneNumberList(phoneNumberList);
        studentPersonal.setMigrant(YesNoUnknown.NO);

        return studentPersonal;
    }
}
