import junit.framework.TestCase;
import openadk.library.ADK;
import openadk.library.SIFDTD;
import openadk.library.SIFVersion;
import openadk.library.SIFWriter;
import openadk.library.common.*;
import openadk.library.student.StudentAddressList;
import openadk.library.student.StudentPersonal;
import org.junit.Assert;

import java.io.StringWriter;
import java.util.Calendar;


public class MessageTest extends TestCase {

    public void setUp() throws Exception {
        ADK.initialize(SIFVersion.LATEST, SIFDTD.SDO_ALL);
    }

    public StudentPersonal testStudentPersonal() {
        StudentPersonal studentPersonal = new StudentPersonal();
        OtherIdList otherIdList = new OtherIdList();
        otherIdList.addOtherId(OtherIdType.SIF1x_STATE_ASSIGNED_NUM, "P00001");
        otherIdList.addOtherId(OtherIdType.SIF1x_SSN, "123-45-6789");
        studentPersonal.setOtherIdList(otherIdList);
        Name name = new Name(NameType.BIRTH, "Student", "Joe");
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
        StudentAddressList addressList = new StudentAddressList();
        addressList.add(address);
        studentPersonal.setAddressList(addressList);
        PhoneNumberList phoneNumberList = new PhoneNumberList();
        phoneNumberList.addPhoneNumber(PhoneNumberType.PRIMARY, "(312) 555-1234");
        studentPersonal.setPhoneNumberList(phoneNumberList);
        return studentPersonal;
    }

    public void testMessageNamespace() {

        StudentPersonal studentPersonal = testStudentPersonal();

        StringWriter stringWriter = new StringWriter();
        SIFWriter sifWriter = new SIFWriter(stringWriter, true);
        sifWriter.write(studentPersonal);
        sifWriter.close();

        String xml = stringWriter.getBuffer().toString();
        Assert.assertTrue("SIF Message contains US namespace", xml.contains("xmlns=\"http://www.sifinfo.org/infrastructure/2.x\""));
    }

}
