package org.slc.sli.shtick.samples;

import org.junit.Assert;

import org.junit.Ignore;
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

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *  Sample CRUD test using shtick for Student
 * @author chung
 *
**/
public class StudentCrudSampleTest {
    private final String BASE_URL = TestingConstants.BASE_URL;
    private static final Map<String, Object> EMPTY_QUERY_ARGS = Collections.emptyMap();

    private static final String UNIQUE_STATE_ID = "9998";

    @Test
    public void testCrud() throws IOException, StatusCodeException {
        final Level2Client inner = new StandardLevel2Client(BASE_URL, new JsonLevel1Client());
        final Level3Client client = new StandardLevel3Client(inner);
        String studentId = null;

        try {
            // CREATE
            studentId = doPostStudents(client);
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
                fail("Entity should be deleted");
            } catch (StatusCodeException e) {
                Assert.assertEquals(e.getStatusCode(), 404);
            }
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } catch (StatusCodeException e) {
            Assert.fail(e.getMessage() + " - " + e.getStatusCode());
        }
    }

    @Test
    public void testNaturalKeys() throws IOException, StatusCodeException {
        final Level2Client inner = new StandardLevel2Client(BASE_URL, new JsonLevel1Client());
        final Level3Client client = new StandardLevel3Client(inner);
        String studentId = null;

        try {
            studentId = doPostStudents(client);

            doPostStudents(client);
        } catch (StatusCodeException e) {
            assertEquals("Should match", 409, e.getStatusCode());
        } catch (IOException e) {
            fail(e.getMessage());
        } finally {
            if (studentId != null) {
                doDeleteStudentsById(client, studentId);
            }
        }
    }

    private String doPostStudents(Level3Client client) throws IOException, StatusCodeException {
        Student student = new Student();

        Name name = new Name();
        name.setFirstName("Testing");
        name.setMiddleName("Student");
        name.setLastSurname("Guy");
        student.setName(name);

        student.setStudentUniqueStateId(UNIQUE_STATE_ID);

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
        Assert.assertEquals("Testing", name.getFirstName());
        Assert.assertEquals("Student", name.getMiddleName());
        Assert.assertEquals("Guy", name.getLastSurname());

        Assert.assertEquals(student.getStudentUniqueStateId(), UNIQUE_STATE_ID);

        List<Address> addresses = student.getAddress();
        Assert.assertEquals(addresses.size(), 1);
        Address address = addresses.get(0);
        Assert.assertEquals("1234 Testing St", address.getStreetNumberName());
        Assert.assertEquals("City", address.getCity());
        Assert.assertEquals("12345", address.getPostalCode());
        Assert.assertEquals(StateAbbreviationType.NY, address.getStateAbbreviation());

        BirthData birthData = student.getBirthData();
        Assert.assertEquals(birthData.getBirthDate(), "2000-01-02");

        List<Disability> disabilities = student.getDisabilities();
        Assert.assertEquals(1, disabilities.size());
        Disability disability = disabilities.get(0);
        Assert.assertEquals(DisabilityType.MENTAL_RETARDATION, disability.getDisability());
        Assert.assertEquals("Mental issues", disability.getDisabilityDiagnosis());

        return student;
    }

    private void doPutStudentsById(Level3Client client, final String studentId, final Student student) throws IOException,
        StatusCodeException {
        client.putStudentsById(TestingConstants.ROGERS_TOKEN, studentId, student);

        List<Student> students = client.getStudentsById(TestingConstants.ROGERS_TOKEN,
                new ArrayList<String>() {{ add(studentId);}}, EMPTY_QUERY_ARGS);
        Assert.assertEquals(1, students.size());
        Student updatedStudent = students.get(0);

        Name name = updatedStudent.getName();
        Assert.assertEquals("Girl", name.getLastSurname());

        SexType sex = updatedStudent.getSex();
        Assert.assertEquals(SexType.FEMALE, sex);
    }

    private void doDeleteStudentsById(Level3Client client, String studentId) throws IOException, StatusCodeException {
        client.deleteStudentsById(TestingConstants.ROGERS_TOKEN, studentId);
    }
}
