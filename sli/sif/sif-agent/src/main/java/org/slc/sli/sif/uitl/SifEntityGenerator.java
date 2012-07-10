package org.slc.sli.sif.uitl;

import openadk.library.ADK;
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
import openadk.library.student.OperationalStatus;
import openadk.library.student.SchoolFocus;
import openadk.library.student.SchoolFocusList;
import openadk.library.student.SchoolFocusType;
import openadk.library.student.SchoolInfo;
import openadk.library.student.SchoolLevelType;


public class SifEntityGenerator
{
    public static SchoolInfo generateTestSchoolInfo() {
        SchoolInfo schoolInfo = new SchoolInfo();
        schoolInfo.setRefId(ADK.makeGUID());
        schoolInfo.setStateProvinceId("IL-DAYBREAK");
        schoolInfo.setSchoolName("Daybreak School District 4529");
        SchoolFocusList schoolFocusList = new SchoolFocusList();
        schoolFocusList.add(new SchoolFocus(SchoolFocusType.REGULAR));
        schoolInfo.setSchoolFocusList(schoolFocusList);
        schoolInfo.setSchoolURL("http://IL-DAYBREAK.edu");
        schoolInfo.setOperationalStatus(OperationalStatus.SCHOOL_CLOSED);
        schoolInfo.setSchoolType(SchoolLevelType._0031_2402_HIGH_SCHOOL);

        GradeLevels gradeLevels = new GradeLevels();
        gradeLevels.addGradeLevel(GradeLevelCode._09);
        gradeLevels.addGradeLevel(GradeLevelCode._10);
        gradeLevels.addGradeLevel(GradeLevelCode._11);
        gradeLevels.addGradeLevel(GradeLevelCode._12);
        schoolInfo.setGradeLevels(gradeLevels);

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
        street.setStreetName("IBM way");
        street.setStreetNumber("1");
        street.setStreetType("Plaza");
        street.setApartmentType("Suite");
        address.setStreet(street);
        address.setType(AddressType.MAILING);
        AddressList addressList = new AddressList();
        addressList.add(address);
        schoolInfo.setAddressList(addressList);

        PhoneNumberList phoneNumberList = new PhoneNumberList();
        phoneNumberList.addPhoneNumber(PhoneNumberType.PRIMARY, "(312) 555-1234");
        phoneNumberList.addPhoneNumber(PhoneNumberType.FAX, "(312) 555-2364");
        schoolInfo.setPhoneNumberList(phoneNumberList);
        return schoolInfo;
    }
}
