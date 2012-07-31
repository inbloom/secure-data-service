package org.slc.sli.shtick.samples;

import org.junit.Assert;

import org.junit.Test;

import org.slc.sli.shtick.JsonLevel1Client;
import org.slc.sli.shtick.Level2Client;
import org.slc.sli.shtick.Level3Client;
import org.slc.sli.shtick.StandardLevel2Client;
import org.slc.sli.shtick.StandardLevel3Client;
import org.slc.sli.shtick.StatusCodeException;
import org.slc.sli.shtick.TestingConstants;
import org.slc.sli.shtick.pojo.Address;
import org.slc.sli.shtick.pojo.BirthData;
import org.slc.sli.shtick.pojo.Disability;
import org.slc.sli.shtick.pojo.DisabilityType;
import org.slc.sli.shtick.pojo.Name;
import org.slc.sli.shtick.pojo.SexType;
import org.slc.sli.shtick.pojo.StateAbbreviationType;
import org.slc.sli.shtick.pojo.Student;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/* Sample CRUD test using shtick for Student
* @author chung
*/
public class StudentCrudSampleTest {
    private final String BASE_URL = "http://local.slidev.org:8080/api/rest/v1";
    private static final Map<String, Object> EMPTY_QUERY_ARGS = Collections.emptyMap();

//    @Test
    public void testCrud() {
        final Level2Client inner = new StandardLevel2Client(BASE_URL, new JsonLevel1Client());
        final Level3Client client = new StandardLevel3Client(inner);

        try {
            // CREATE
            final String studentId = doPostStudents(client);
            Assert.assertNotNull(studentId);

            // READ
            Student student = doGetStudentsById(client, studentId);

            // UPDATE
            student.getName().setLastSurname("Girl");
            student.setSex(SexType.FEMALE);
            doPutStudentsById(client, studentId, student);

            // DELETE
            doDeleteStudentsById(client, studentId);
            try {
                doGetStudentsById(client, studentId);
            } catch (StatusCodeException e) {
                Assert.assertEquals(e.getStatusCode(), 404);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } catch (StatusCodeException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage() + " - " + e.getStatusCode());
        }
    }

    private String doPostStudents(Level3Client client) throws IOException, StatusCodeException {
        Student student = new Student();

        Name name = new Name();
        name.setFirstName("Testing");
        name.setMiddleName("Student");
        name.setLastSurname("Guy");
        student.setName(name);

        List<Address> addresses = new ArrayList<Address>();
        Address address = new Address();
        address.setStreetNumberName("1234 Testing St");
        address.setCity("City");
        address.setStateAbbreviation(StateAbbreviationType.NY);
        address.setPostalCode("12345");
        addresses.add(address);
        student.setAddress(addresses);

        BirthData birthData = new BirthData();
        birthData.setBirthDate("2000-01-02");
        student.setBirthData(birthData);

        List<Disability> disabilities = new ArrayList<Disability>();
        Disability disability = new Disability();
        disability.setDisability(DisabilityType.MENTAL_RETARDATION);
        disability.setDisabilityDiagnosis("Mental issues");
        disabilities.add(disability);
        student.setDisabilities(disabilities);

        student.setSex(SexType.MALE);

        return client.postStudents(TestingConstants.ROGERS_TOKEN, student);
    }

    private Student doGetStudentsById(Level3Client client, final String studentId) throws IOException, StatusCodeException {
        List<Student> students = client.getStudentsById(TestingConstants.ROGERS_TOKEN,
                new ArrayList<String>(){{ add(studentId); }}, EMPTY_QUERY_ARGS);
        Assert.assertEquals(students.size(), 1);
        Student student = students.get(0);

        SexType sex = student.getSex();
        Assert.assertEquals(sex, SexType.MALE);

        Name name = student.getName();
        Assert.assertEquals(name.getFirstName(), "Testing");
        Assert.assertEquals(name.getMiddleName(), "Student");
        Assert.assertEquals(name.getLastSurname(), "Guy");

        List<Address> addresses = student.getAddress();
        Assert.assertEquals(addresses.size(), 1);
        Address address = addresses.get(0);
        Assert.assertEquals(address.getStreetNumberName(), "1234 Testing St");
        Assert.assertEquals(address.getCity(), "City");
        Assert.assertEquals(address.getPostalCode(), 12345);
        Assert.assertEquals(address.getStateAbbreviation(), StateAbbreviationType.NY);

        BirthData birthData = student.getBirthData();
        Assert.assertEquals(birthData.getBirthDate(), "2000-01-02");

        List<Disability> disabilities = student.getDisabilities();
        Assert.assertEquals(disabilities.size(), 1);
        Disability disability = disabilities.get(0);
        Assert.assertEquals(disability.getDisability(), DisabilityType.MENTAL_RETARDATION);
        Assert.assertEquals(disability.getDisabilityDiagnosis(), "Mental issues");

        return student;
    }

    private void doPutStudentsById(Level3Client client, final String studentId, final Student student) throws IOException,
        StatusCodeException {
        client.putStudentsById(TestingConstants.ROGERS_TOKEN, studentId, student);

        List<Student> students = client.getStudentsById(TestingConstants.ROGERS_TOKEN,
                new ArrayList<String>() {{ add(studentId);}}, EMPTY_QUERY_ARGS);
        Assert.assertEquals(students.size(), 1);
        Student updatedStudent = students.get(0);

        Name name = updatedStudent.getName();
        Assert.assertEquals(name.getLastSurname(), "Girl");

        SexType sex = updatedStudent.getSex();
        Assert.assertEquals(sex, SexType.FEMALE);
    }

    private void doDeleteStudentsById(Level3Client client, String studentId) throws IOException, StatusCodeException {
        client.deleteStudentsById(TestingConstants.ROGERS_TOKEN, studentId);
    }
}
