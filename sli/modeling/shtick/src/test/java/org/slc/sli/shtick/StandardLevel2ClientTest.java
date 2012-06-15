package org.slc.sli.shtick;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class StandardLevel2ClientTest {

    private static final String BASE_URL = "http://local.slidev.org:8080/api/rest/v1";
    private static final Map<String, Object> EMPTY_QUERY_ARGS = Collections.emptyMap();

    private void doGetStudents(final Level1Client inner) {
        final Level2Client client = new StandardLevel2Client(BASE_URL, inner);
        try {
            final Map<String, Object> queryArgs = new HashMap<String, Object>();
            queryArgs.put("limit", 1000);
            final List<RestEntity> students = client.getStudents(TestingConstants.ROGERS_TOKEN, queryArgs);
            assertNotNull(students);
            final Map<String, RestEntity> studentMap = new HashMap<String, RestEntity>();
            for (final RestEntity student : students) {
                studentMap.put(student.getId(), student);
            }
            {
                final RestEntity student = studentMap.get(TestingConstants.TEST_STUDENT_ID);
                assertNotNull(student);
                assertEquals(TestingConstants.TEST_STUDENT_ID, student.getId());
                assertEquals("student", student.getType());
                final Map<String, Object> data = student.getData();
                assertNotNull(data);
                assertEquals("Male", data.get("sex"));
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
        } catch (final RestException e) {
            fail(e.getMessage());
        }
    }

    private void doGetStudentsById(final Level1Client inner) {
        final Level2Client client = new StandardLevel2Client(BASE_URL, inner);
        // One identifier.
        try {
            final List<RestEntity> students = client.getStudentsById(TestingConstants.ROGERS_TOKEN,
                    TestingConstants.TEST_STUDENT_ID, EMPTY_QUERY_ARGS);

            assertNotNull(students);
            assertEquals(1, students.size());
            final RestEntity student = students.get(0);
            assertNotNull(student);
            assertEquals(TestingConstants.TEST_STUDENT_ID, student.getId());
            assertEquals("student", student.getType());
            final Map<String, Object> data = student.getData();
            assertNotNull(data);
            assertEquals("Male", data.get("sex"));
            assertEquals(Boolean.FALSE, data.get("economicDisadvantaged"));
            assertEquals("100000005", data.get("studentUniqueStateId"));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final RestException e) {
            fail(e.getMessage());
        }
    }

    private void doGetStudentsWithBrokenToken(final Level1Client inner) {
        final Level2Client client = new StandardLevel2Client(BASE_URL, inner);
        try {
            final Map<String, Object> queryArgs = new HashMap<String, Object>();
            queryArgs.put("limit", 1000);
            final List<RestEntity> students = client.getStudents(TestingConstants.BROKEN_TOKEN, queryArgs);
            assertNotNull(students);
            final Map<String, RestEntity> studentMap = new HashMap<String, RestEntity>();
            for (final RestEntity student : students) {
                studentMap.put(student.getId(), student);
            }
            {
                final RestEntity student = studentMap.get(TestingConstants.TEST_STUDENT_ID);
                assertNotNull(student);
                assertEquals(TestingConstants.TEST_STUDENT_ID, student.getId());
                assertEquals("student", student.getType());
                final Map<String, Object> data = student.getData();
                assertNotNull(data);
                assertEquals("Male", data.get("sex"));
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
        } catch (final RestException e) {
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
    public void testGetStudentsByIdUsingJson() {
        doGetStudentsById(new JsonLevel1Client());
    }

    @Ignore("Problem with the plurality of XML documents.")
    public void testGetStudentsByIdUsingStAX() {
        doGetStudentsById(new StAXLevel1Client());
    }

    @Test
    public void testGetStudentsUsingJson() {
        doGetStudents(new JsonLevel1Client());
    }

    @Test
    public void testGetStudentsUsingStAX() {
        doGetStudents(new StAXLevel1Client());
    }

    @Ignore("Problem with invalid autorization token.")
    public void testGetStudentsWithBrokenTokenUsingJson() {
        doGetStudentsWithBrokenToken(new JsonLevel1Client());
    }

    @Ignore("Problem with invalid autorization token.")
    public void testGetStudentsWithBrokenTokenUsingStAX() {
        doGetStudentsWithBrokenToken(new StAXLevel1Client());
    }

    @Test
    public void testStudentSectionAssociations() {
        final Level1Client inner = new JsonLevel1Client();
        final Level2Client client = new StandardLevel2Client(BASE_URL, inner);
        try {
            final Map<String, Object> queryArgs = new HashMap<String, Object>();
            queryArgs.put("limit", 1000);
            final List<RestEntity> studentSchoolAssociations = client.getStudentSchoolAssociations(
                    TestingConstants.ROGERS_TOKEN, queryArgs);
            assertNotNull(studentSchoolAssociations);
            final Map<String, RestEntity> associationMap = new HashMap<String, RestEntity>();
            for (final RestEntity studentSchoolAssociation : studentSchoolAssociations) {
                @SuppressWarnings("unused")
                final Map<String, Object> data = studentSchoolAssociation.getData();
                associationMap.put(studentSchoolAssociation.getId(), studentSchoolAssociation);
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final RestException e) {
            fail(e.getMessage());
        }
    }
}
