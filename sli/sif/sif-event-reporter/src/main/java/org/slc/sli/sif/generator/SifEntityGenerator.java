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

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

import openadk.library.common.Address;
import openadk.library.common.AddressList;
import openadk.library.common.AddressType;
import openadk.library.common.CitizenshipStatus;
import openadk.library.common.CountryCode;
import openadk.library.common.Demographics;
import openadk.library.common.ElectronicId;
import openadk.library.common.ElectronicIdList;
import openadk.library.common.ElectronicIdType;
import openadk.library.common.EmailList;
import openadk.library.common.EmailType;
import openadk.library.common.EntryTypeCode;
import openadk.library.common.ExitTypeCode;
import openadk.library.common.Gender;
import openadk.library.common.GradeLevel;
import openadk.library.common.GradeLevelCode;
import openadk.library.common.GradeLevels;
import openadk.library.common.InstructionalLevel;
import openadk.library.common.InstructionalLevelCode;
import openadk.library.common.JobFunction;
import openadk.library.common.JobFunctionCode;
import openadk.library.common.MembershipType;
import openadk.library.common.Name;
import openadk.library.common.NameType;
import openadk.library.common.NonResidentAttendRationale;
import openadk.library.common.OrganizationRelationshipType;
import openadk.library.common.OtherCode;
import openadk.library.common.OtherCodeList;
import openadk.library.common.OtherId;
import openadk.library.common.OtherIdList;
import openadk.library.common.OtherIdType;
import openadk.library.common.PhoneNumberList;
import openadk.library.common.PhoneNumberType;
import openadk.library.common.ProgramFundingSource;
import openadk.library.common.ProgramTypeCode;
import openadk.library.common.PublicSchoolResidenceStatus;
import openadk.library.common.ResidencyStatus;
import openadk.library.common.StatePrCode;
import openadk.library.common.Street;
import openadk.library.common.StudentLEARelationship;
import openadk.library.common.TeachingArea;
import openadk.library.common.YesNo;
import openadk.library.common.YesNoUnknown;
import openadk.library.datamodel.SEAInfo;
import openadk.library.hrfin.EmployeeAssignment;
import openadk.library.hrfin.EmployeePersonal;
import openadk.library.hrfin.EmploymentRecord;
import openadk.library.hrfin.FullTimeStatus;
import openadk.library.hrfin.HRProgramType;
import openadk.library.hrfin.HrOtherIdList;
import openadk.library.hrfin.JobClassification;
import openadk.library.hrfin.JobClassificationCode;
import openadk.library.student.EducationAgencyTypeCode;
import openadk.library.student.FTPTStatus;
import openadk.library.student.LEAInfo;
import openadk.library.student.OperationalStatus;
import openadk.library.student.SchoolFocus;
import openadk.library.student.SchoolFocusList;
import openadk.library.student.SchoolFocusType;
import openadk.library.student.SchoolInfo;
import openadk.library.student.SchoolLevelType;
import openadk.library.student.StaffAssignment;
import openadk.library.student.StaffPersonal;
import openadk.library.student.StudentAddressList;
import openadk.library.student.StudentPersonal;
import openadk.library.student.StudentSchoolEnrollment;
import openadk.library.student.TeachingAssignment;
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
    public static final String TEST_STAFFPERSONAL_REFID = "20120816934983498C3D00AA00495948";
    public static final String TEST_EMPLOYEEPERSONAL_REFID = "1652D3E34F419D75101A8C3D00AA001A";
    public static final String TEST_STAFFASSIGNMENT_REFID = "D3E34B359D75101A8C3D00AA001A1652";
    public static final String TEST_EMPLOYMENTRECORD_REFID = "CDF90651225DAC3859DEA3458BC39522";
    public static final String TEST_EMPLOYEEASSIGNMENT_REFID = "FE1078BA3261545A31905937B265CE01";

    public static SchoolInfo generateTestSchoolInfo() {
        SchoolInfo info = new SchoolInfo();
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

    public static StaffPersonal generateTestStaffPersonal() {
        StaffPersonal staffPersonal = new StaffPersonal();
        staffPersonal.setRefId(TEST_STAFFPERSONAL_REFID);
        staffPersonal.setEmployeePersonalRefId(TEST_EMPLOYEEPERSONAL_REFID);

        staffPersonal.setLocalId("946379881");
        staffPersonal.setStateProvinceId("C2345681");
        staffPersonal.setTitle("Principal");

        ElectronicId electronicId = new ElectronicId(ElectronicIdType.BARCODE, "206655");
        ElectronicIdList electronicIdList = new ElectronicIdList(electronicId);
        staffPersonal.setElectronicIdList(electronicIdList);

        OtherId otherId = new OtherId(OtherIdType.SOCIALSECURITY, "333333333");
        OtherIdList otherIdList = new OtherIdList(otherId);
        staffPersonal.setOtherIdList(otherIdList);

        Name name = new Name();
        name.setType(NameType.NAME_OF_RECORD);
        name.setPrefix("Mr.");
        name.setLastName("Woodall");
        name.setFirstName("Charles");
        name.setMiddleName("William");
        name.setPreferredName("Chuck");
        staffPersonal.setName(name);

        Demographics demographics = new Demographics();
        demographics.setGender(Gender.M);
        staffPersonal.setDemographics(demographics);

        Address address = new Address();
        address.setType(AddressType.MAILING);
        address.setCity("Chicago");
        address.setStateProvince(StatePrCode.IL);
        address.setCountry(CountryCode.US);
        address.setPostalCode("60660");

        Street street = new Street();
        street.setLine1("6799 33rd Ave.");
        street.setStreetNumber("6799");
        street.setStreetName("33rd");
        street.setStreetType("Ave.");
        address.setStreet(street);

        AddressList addressList = new AddressList(address);
        staffPersonal.setAddressList(addressList);

        PhoneNumberList phoneNumberList = new PhoneNumberList();
        phoneNumberList.addPhoneNumber(PhoneNumberType.PRIMARY, "(312) 555-1234");
        staffPersonal.setPhoneNumberList(phoneNumberList);

        EmailList emailList = new EmailList();
        emailList.addEmail(EmailType.PRIMARY, "chuckw@imginc.com");
        staffPersonal.setEmailList(emailList);

        return staffPersonal;
    }

    public static EmployeePersonal generateTestEmployeePersonal() {
        EmployeePersonal employeePersonal = new EmployeePersonal();
        employeePersonal.setRefId(TEST_EMPLOYEEPERSONAL_REFID);
        employeePersonal.setStateProvinceId("C2345681");

        OtherId otherId1 = new OtherId(OtherIdType.SOCIALSECURITY, "333333333");
        OtherId otherId2 = new OtherId(OtherIdType.OTHER, "3333");
        HrOtherIdList hrOtherIdList = new HrOtherIdList();
        hrOtherIdList.add(otherId1);
        hrOtherIdList.add(otherId2);
        employeePersonal.setOtherIdList(hrOtherIdList);

        Name name = new Name();
        name.setType(NameType.NAME_OF_RECORD);
        name.setLastName("Woodall");
        name.setFirstName("Charles");
        employeePersonal.setName(name);

        Demographics demographics = new Demographics();
        demographics.setGender(Gender.M);
        employeePersonal.setDemographics(demographics);

        Address address = new Address();
        address.setType(AddressType.MAILING);
        address.setCity("Chicago");
        address.setStateProvince(StatePrCode.IL);
        address.setCountry(CountryCode.US);
        address.setPostalCode("60660");

        Street street = new Street();
        street.setLine1("6799 33rd Ave.");
        address.setStreet(street);

        AddressList addressList = new AddressList(address);
        employeePersonal.setAddressList(addressList);

        PhoneNumberList phoneNumberList = new PhoneNumberList();
        phoneNumberList.addPhoneNumber(PhoneNumberType.PRIMARY, "(312) 555-9876");
        employeePersonal.setPhoneNumberList(phoneNumberList);

        EmailList emailList = new EmailList();
        emailList.addEmail(EmailType.PRIMARY, "chuckw@imginc.com");
        employeePersonal.setEmailList(emailList);

        return employeePersonal;
    }

    public static StaffAssignment generateTestStaffAssignment() {
        StaffAssignment staffAssignment = new StaffAssignment();
        staffAssignment.setRefId(TEST_STAFFASSIGNMENT_REFID);

        staffAssignment.setSchoolInfoRefId(TEST_SCHOOLINFO_REFID);
        staffAssignment.setStaffPersonalRefId(TEST_STAFFPERSONAL_REFID);
        staffAssignment.setEmployeePersonalRefId(TEST_EMPLOYEEPERSONAL_REFID);

        staffAssignment.setSchoolYear(new Integer(2013));
        staffAssignment.setDescription("Twelfth grade computer science teacher");
        staffAssignment.setPrimaryAssignment(YesNo.YES);

        staffAssignment.setJobStartDate(new GregorianCalendar(2010, 7, 1));
        staffAssignment.setJobEndDate(new GregorianCalendar(2013, 6, 31));

        staffAssignment.setJobFTE(new BigDecimal(1.00));
        staffAssignment.setJobFunction(new JobFunction(JobFunctionCode.INSTRUCTION));
        staffAssignment.setTeachingAssignment(new TeachingAssignment(TeachingArea.COMPUTER_SCIENCE));
        staffAssignment.setGradeLevels(new GradeLevels(new GradeLevel(GradeLevelCode._12)));
        staffAssignment.setItinerantTeacher(YesNo.NO);

        InstructionalLevel instructionalLevel = new InstructionalLevel();
        instructionalLevel.setCode(InstructionalLevelCode.COLLEGE_LEVEL);
        staffAssignment.setInstructionalLevel(instructionalLevel);

        return staffAssignment;
    }

    public static EmploymentRecord generateTestEmploymentRecord() {
        EmploymentRecord employmentRecord = new EmploymentRecord();
        employmentRecord.setRefId(TEST_EMPLOYMENTRECORD_REFID);

        employmentRecord.setSIF_RefId(TEST_STAFFPERSONAL_REFID);
        employmentRecord.setSIF_RefObject("StaffPersonal");

        employmentRecord.setLEAInfoRefId(TEST_LEAINFO_REFID);

        employmentRecord.setActive(true);
        employmentRecord.setFullTimeStatus(FullTimeStatus.FULLTIME);

        employmentRecord.setHireDate(new GregorianCalendar(2010, 7, 1));
        employmentRecord.setTerminationDate(new GregorianCalendar(2012, 6, 31));

        employmentRecord.setTotalYearsExperience(20);
        employmentRecord.setPositionTitle("Senior Staff");
        employmentRecord.setPositionNumber("10");

        employmentRecord.setSeniorityDate(new GregorianCalendar(2011, 1, 1));
        employmentRecord.setTenureDate(new GregorianCalendar(2011, 7, 1));

        return employmentRecord;
    }

    public static EmployeeAssignment generateTestEmployeeAssignment() {
        EmployeeAssignment employeeAssignment = new EmployeeAssignment();
        employeeAssignment.setRefId(TEST_EMPLOYEEASSIGNMENT_REFID);

        employeeAssignment.setEmployeePersonalRefId(TEST_EMPLOYEEPERSONAL_REFID);
        employeeAssignment.setDescription("Twelfth grade computer science teacher");
        employeeAssignment.setPrimaryAssignment(YesNo.YES);

        employeeAssignment.setJobStartDate(new GregorianCalendar(2010, 7, 1));
        employeeAssignment.setJobEndDate(new GregorianCalendar(2013, 6, 31));

        employeeAssignment.setJobFTE(new BigDecimal(1.00));

        JobClassification jobClassification = new JobClassification(JobClassificationCode.TEACHER);
        OtherCode jobClassificationOtherCode = new OtherCode();
        jobClassificationOtherCode.setValue("12345");
        jobClassification.setOtherCodeList(new OtherCodeList(jobClassificationOtherCode));
        employeeAssignment.setJobClassification(jobClassification);

        HRProgramType programType = new HRProgramType();
        programType.setCode(ProgramTypeCode.REGULAR_EDUCATION);
        OtherCode programTypeOtherCode = new OtherCode();
        programTypeOtherCode.setValue("67890");
        programType.setOtherCodeList(new OtherCodeList(programTypeOtherCode));
        employeeAssignment.setProgramType(programType);

        ProgramFundingSource programFundingSource = new ProgramFundingSource();
        programFundingSource.setCode("0617");
        OtherCode programFundingSourceOtherCode = new OtherCode();
        programFundingSourceOtherCode.setValue("54321");
        programFundingSource.setOtherCodeList(new OtherCodeList(programFundingSourceOtherCode));
        employeeAssignment.setFundingSource(programFundingSource);

        return employeeAssignment;
    }
}
