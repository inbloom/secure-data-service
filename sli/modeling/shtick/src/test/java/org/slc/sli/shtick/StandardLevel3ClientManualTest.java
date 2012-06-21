package org.slc.sli.shtick;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.slc.sli.shtick.pojo.BirthData;
import org.slc.sli.shtick.pojo.Name;
import org.slc.sli.shtick.pojo.SexType;
import org.slc.sli.shtick.pojo.Student;

public class StandardLevel3ClientManualTest {

    private static final String BASE_URL = "http://local.slidev.org:8080/api/rest/v1";
    private static final Map<String, Object> EMPTY_QUERY_ARGS = Collections.emptyMap();

    private void doGetStudents(final Level2Client inner) {
        final Level3ClientManual client = new StandardLevel3ClientManual(BASE_URL, inner);
        try {
            final Map<String, Object> queryArgs = new HashMap<String, Object>();
            queryArgs.put("limit", 1000);
            final List<Student> students = client.getStudents(TestingConstants.ROGERS_TOKEN, queryArgs);
            assertNotNull(students);
            final Map<String, Student> studentMap = new HashMap<String, Student>();
            for (final Student student : students) {
                studentMap.put(student.getId(), student);
            }
            {
                final Student student = studentMap.get(TestingConstants.TEST_STUDENT_ID);
                assertNotNull(student);
                assertEquals(TestingConstants.TEST_STUDENT_ID, student.getId());
                assertEquals(SexType.MALE, student.getSex());
                final Name name = student.getName();
                assertEquals("Garry", name.getFirstName());
                assertEquals("Kinsel", name.getLastSurname());
                assertEquals(Boolean.FALSE, student.getEconomicDisadvantaged());
                assertEquals("100000005", student.getStudentUniqueStateId());
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final StatusCodeException e) {
            fail(e.getMessage());
        }
    }

    private void doGetStudentsById(final Level2Client inner) {
        final Level3ClientManual client = new StandardLevel3ClientManual(BASE_URL, inner);
        // One identifier.
        try {
            final List<String> studentIds = new LinkedList<String>();
            studentIds.add(TestingConstants.TEST_STUDENT_ID);
            final List<Student> students = client.getStudentsById(TestingConstants.ROGERS_TOKEN, studentIds,
                    EMPTY_QUERY_ARGS);

            assertNotNull(students);
            assertEquals(1, students.size());
            final Student student = students.get(0);
            assertNotNull(student);
            assertEquals(TestingConstants.TEST_STUDENT_ID, student.getId());
            assertEquals(SexType.MALE, student.getSex());
            assertEquals(Boolean.FALSE, student.getEconomicDisadvantaged());
            assertEquals("100000005", student.getStudentUniqueStateId());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final StatusCodeException e) {
            fail(e.getMessage());
        }
    }

    private void doGetStudentsWithBrokenToken(final Level1Client inner) {
        final Level2Client client = new StandardLevel2Client(BASE_URL, inner);
        try {
            final Map<String, Object> queryArgs = new HashMap<String, Object>();
            queryArgs.put("limit", 1000);
            final List<Entity> students = client.getStudents(TestingConstants.BROKEN_TOKEN, queryArgs);
            assertNotNull(students);
            final Map<String, Entity> studentMap = new HashMap<String, Entity>();
            for (final Entity student : students) {
                studentMap.put(student.getId(), student);
            }
            {
                final Entity student = studentMap.get(TestingConstants.TEST_STUDENT_ID);
                assertNotNull(student);
                assertEquals(TestingConstants.TEST_STUDENT_ID, student.getId());
                assertEquals("student", student.getType());
                final Map<String, Object> data = student.getData();
                assertNotNull(data);
                assertEquals(SexType.MALE, data.get("sex"));
                final Object name = data.get("name");
                assertTrue(name instanceof Map);
                @SuppressWarnings("unchecked")
                final Map<String, Object> nameMap = (Map<String, Object>) name;
                assertEquals("Garry", nameMap.get("firstName"));
                assertEquals("Kinsel", nameMap.get("lastSurname"));
                assertEquals(Boolean.FALSE, data.get("economicDisadvantaged"));
                assertEquals("100000005", data.get("studentUniqueStateId"));
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final StatusCodeException e) {
            fail(e.getMessage());
        }
    }

    @Before
    public void setup() {
    }

    // @Test
    public void testDeleteRequest() {

    }

    @Test
    public void testCrudJson() {
        try {
            final Level2Client inner = new StandardLevel2Client(BASE_URL, new JsonLevel1Client());
            final Level3ClientManual client = new StandardLevel3ClientManual(BASE_URL, inner);

            // POST
            final String studentId = doPostStudentUsingJson(client);

            // GET POSTED ENTITY
            final Student student = doGetStudentById(client, studentId);
            assertEquals("Jeff", student.getName().getFirstName());
            assertEquals("Stokes", student.getName().getLastSurname());
            assertEquals(studentId, student.getId());

            // PUT UPDATED ENTITY
            doPutStudent(client, student);
            // GET UPDATED ENTITY
            final Student updatedStudent = doGetStudentById(client, studentId);
            assertEquals("John", updatedStudent.getName().getFirstName());
            assertEquals(studentId, student.getId());

            // DELETE ENTITY
            doDeleteStudent(client, student);

            // GET DELETED ENTITY (SHOULD NOW FAIL)
            try {
                doGetStudentById(client, studentId);
            } catch (StatusCodeException e) {
                assertEquals(404, e.getStatusCode());
            }

        } catch (final IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } catch (final StatusCodeException e) {
            e.printStackTrace();
            fail("Status Code Returned : " + e.getStatusCode());
        }
    }

    private void doDeleteStudent(Level3ClientManual client, Student student) throws IOException,
            StatusCodeException {
        client.deleteStudent(TestingConstants.ROGERS_TOKEN, student);
    }

    private void doPutStudent(final Level3ClientManual client, final Student student) throws IOException,
            StatusCodeException {
        student.getName().setFirstName("John");
        client.putStudent(TestingConstants.ROGERS_TOKEN, student);
    }

    private Student doGetStudentById(final Level3ClientManual client, final String studentId) throws IOException,
            StatusCodeException {
        return client.getStudentsById(TestingConstants.ROGERS_TOKEN, Arrays.asList(studentId), EMPTY_QUERY_ARGS).get(0);
    }

    private String doPostStudentUsingJson(final Level3ClientManual client) throws IOException, StatusCodeException {
        Student student = new Student();

        Name name = new Name();
        name.setFirstName("Jeff");
        name.setMiddleName("Allen");
        name.setLastSurname("Stokes");
        student.setName(name);

        student.setStudentUniqueStateId("1234-STUDENT");
        student.setSex(SexType.MALE);

        BirthData birthData = new BirthData();
        birthData.setBirthDate("1988-12-01");
        student.setBirthData(birthData);

        student.setHispanicLatinoEthnicity(false);
        return client.postStudent(TestingConstants.ROGERS_TOKEN, student);
    }

    @Test
    public void testGetStudentsByIdUsingJson() {
        doGetStudentsById(new StandardLevel2Client(BASE_URL, new JsonLevel1Client()));
    }

    @Test
    @Ignore("Single entity deserialization returns N-entities where N=number of properties")
    public void testGetStudentsByIdUsingStAX() {
        doGetStudentsById(new StandardLevel2Client(BASE_URL, new StAXLevel1Client()));
    }

    @Test
    public void testGetStudentsUsingJson() {
        doGetStudents(new StandardLevel2Client(BASE_URL, new JsonLevel1Client()));
    }

    @Test
    public void testGetStudentsUsingStAX() {
        doGetStudents(new StandardLevel2Client(BASE_URL, new StAXLevel1Client()));
    }

    @Test
    @Ignore("Problem with invalid autorization token.")
    public void testGetStudentsWithBrokenTokenUsingJson() {
        doGetStudentsWithBrokenToken(new JsonLevel1Client());
    }

    @Test
    @Ignore("Problem with invalid autorization token.")
    public void testGetStudentsWithBrokenTokenUsingStAX() {
        doGetStudentsWithBrokenToken(new StAXLevel1Client());
    }
}
