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

package org.slc.sli.shtick;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slc.sli.shtick.pojo.Address;
import org.slc.sli.shtick.pojo.BirthData;
import org.slc.sli.shtick.pojo.Home;
import org.slc.sli.shtick.pojo.LanguageItemType;
import org.slc.sli.shtick.pojo.Links;
import org.slc.sli.shtick.pojo.Name;
import org.slc.sli.shtick.pojo.SexType;
import org.slc.sli.shtick.pojo.StateAbbreviationType;
import org.slc.sli.shtick.pojo.Student;

public class StandardLevel3ClientManualTest {

    private static final String BASE_URL = TestingConstants.BASE_URL;
    private static final Map<String, Object> EMPTY_QUERY_ARGS = Collections.emptyMap();


    @Before
    public void setup() {
    }

    @Test
    public void testCrudJson() {
        try {
            final Level2Client inner = new StandardLevel2Client(BASE_URL, new JsonLevel1Client());
            final Level3Client client = new StandardLevel3Client(inner);

            // POST
            final String studentId = doPostStudentUsingJson(client);

            // GET POSTED ENTITY
            final Student student = doGetStudentById(client, studentId);
            assertEquals("Jeff", student.getName().getFirstName());
            assertEquals("Stokes", student.getName().getLastSurname());
            assertEquals(studentId, student.getId());

            assertEquals(2, student.getAddress().size());

            assertEquals(2, student.getLanguages().size());
            assertTrue(student.getLanguages().contains(LanguageItemType.ENGLISH));
            assertTrue(student.getLanguages().contains(LanguageItemType.APACHE));

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

    @Test
    public void testCustom() throws IOException, StatusCodeException {
        final Level3Client client = new StandardLevel3Client(BASE_URL);
        String studentId = doPostStudentUsingJson(client);
        doPostCustomForStudentById(studentId, client);

        Map<String, Object> custom = client.getCustomForStudentsById(TestingConstants.ROGERS_TOKEN,
                Arrays.asList(studentId), new HashMap<String, Object>());

        doDeleteStudent(client, doGetStudentById(client, studentId));
        assertEquals("testCustom1V", custom.get("testCustom1K"));
        assertEquals("testCustom2V", custom.get("testCustom2K"));
        assertTrue(custom.get("testCustom3K") instanceof Map);
    }

    @Test
    public void testHome() throws IOException, StatusCodeException {
        final Level3Client client = new StandardLevel3Client(BASE_URL);
        final Home home = client.getHome(TestingConstants.ROGERS_TOKEN, EMPTY_QUERY_ARGS);
        List<Links> homeLinks = home.getLinks();
        assertTrue(!homeLinks.isEmpty());

        Map<String, Links> linksMap = new HashMap<String, Links>();
        for (Links link : home.getLinks()) {
           linksMap.put(link.getRel(), link);
        }
        assertTrue(linksMap.containsKey("self"));
        Links selfLink = linksMap.get("self");
        assertTrue(selfLink.getHref().contains("staff"));
    }

    @Test
    public void testValidationError() throws IOException, StatusCodeException {
        final Level3Client client = new StandardLevel3Client(BASE_URL);
        final Student student = new Student();

        // try to post an empty student
        try {
            client.postStudents(TestingConstants.ROGERS_TOKEN, student);
        } catch (final StatusCodeException sce) {
            assertEquals(400, sce.getStatusCode());
            assertTrue(sce.getMessage().contains("ValidationError"));
        }
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
    @Ignore("Problem with invalid authorization token.")
    public void testGetStudentsWithBrokenTokenUsingJson() {
        doGetStudentsWithBrokenToken(new JsonLevel1Client());
    }

    @Test
    @Ignore("Problem with invalid authorization token.")
    public void testGetStudentsWithBrokenTokenUsingStAX() {
        doGetStudentsWithBrokenToken(new StAXLevel1Client());
    }

    private void doGetStudents(final Level2Client inner) {
        final Level3Client client = new StandardLevel3Client(inner);
        try {
            final Map<String, Object> queryArgs = new HashMap<String, Object>();
            queryArgs.put("limit", 1000);
            final List<Student> students = client.getStudents(TestingConstants.KIM_TOKEN, queryArgs);
            assertNotNull(students);
            final Map<String, Student> studentMap = new HashMap<String, Student>();
            for (final Student student : students) {
                studentMap.put(student.getId(), student);
            }
            {
                final Student student = studentMap.get(TestingConstants.TEST_STUDENT_KIM_ID);
                assertNotNull(student);
                assertEquals(TestingConstants.TEST_STUDENT_KIM_ID, student.getId());
                assertEquals(SexType.MALE, student.getSex());
                final Name name = student.getName();
                assertEquals("Preston", name.getFirstName());
                assertEquals("Muchow", name.getLastSurname());
                assertEquals("800000019", student.getStudentUniqueStateId());
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final StatusCodeException e) {
            fail(e.getMessage());
        }
    }

    private void doGetStudentsById(final Level2Client inner) {
        final Level3Client client = new StandardLevel3Client(inner);
        // One identifier.
        try {
            final List<String> studentIds = new LinkedList<String>();
            studentIds.add(TestingConstants.TEST_STUDENT_KIM_ID);
            final List<Student> students = client.getStudentsById(TestingConstants.KIM_TOKEN, studentIds,
                    EMPTY_QUERY_ARGS);

            assertNotNull(students);
            assertEquals(1, students.size());
            final Student student = students.get(0);
            assertNotNull(student);
            assertEquals(TestingConstants.TEST_STUDENT_KIM_ID, student.getId());
            assertEquals(SexType.MALE, student.getSex());
            assertEquals("800000019", student.getStudentUniqueStateId());

            @SuppressWarnings("unused")
            List<Address> studentAddresses = student.getAddress();
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
                final Entity student = studentMap.get(TestingConstants.TEST_STUDENT_KIM_ID);
                assertNotNull(student);
                assertEquals(TestingConstants.TEST_STUDENT_KIM_ID, student.getId());
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
    private void doPostCustomForStudentById(String studentId, Level3Client client) throws IOException,
            StatusCodeException {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("testCustom1K", "testCustom1V");
        data.put("testCustom2K", "testCustom2V");

        Map<String, Object> embedded = new HashMap<String, Object>();
        embedded.put("embedded1K", "embedded1V");
        data.put("testCustom3K", embedded);

        Entity customEntity = new Entity("Custom", data);

        client.postCustomForStudentsById(TestingConstants.ROGERS_TOKEN, studentId, customEntity.getData());
    }

    private void doDeleteStudent(Level3Client client, Student student) throws IOException, StatusCodeException {
        client.deleteStudentsById(TestingConstants.ROGERS_TOKEN, student.getId());
    }

    private void doPutStudent(final Level3Client client, final Student student) throws IOException,
            StatusCodeException {
        student.getName().setFirstName("John");
        client.putStudentsById(TestingConstants.ROGERS_TOKEN, student.getId(), student);
    }

    private Student doGetStudentById(final Level3Client client, final String studentId) throws IOException,
            StatusCodeException {
        return client.getStudentsById(TestingConstants.ROGERS_TOKEN, Arrays.asList(studentId), EMPTY_QUERY_ARGS).get(0);
    }

    private String doPostStudentUsingJson(final Level3Client client) throws IOException, StatusCodeException {
        final Student student = new Student();

        final Name name = new Name();
        name.setFirstName("Jeff");
        name.setMiddleName("Allen");
        name.setLastSurname("Stokes");
        student.setName(name);

        List<Address> addressList = new ArrayList<Address>();
        final Address address1 = new Address();
        address1.setStreetNumberName("1234 My Street");
        address1.setCity("New York");
        address1.setPostalCode("11111");
        address1.setStateAbbreviation(StateAbbreviationType.NY);

        final Address address2 = new Address();
        address2.setStreetNumberName("5555 My Street");
        address2.setCity("San Fran");
        address2.setPostalCode("22222");
        address2.setStateAbbreviation(StateAbbreviationType.CA);

        addressList.add(address1);
        addressList.add(address2);

        student.setAddress(addressList);

        final List<LanguageItemType> languageList = new ArrayList<LanguageItemType>();
        languageList.add(LanguageItemType.ENGLISH);
        languageList.add(LanguageItemType.APACHE);

        student.setLanguages(languageList);

        student.setStudentUniqueStateId("1234-STUDENT");
        student.setSex(SexType.MALE);

        final BirthData birthData = new BirthData();
        birthData.setBirthDate("1988-12-01");
        student.setBirthData(birthData);

        student.setHispanicLatinoEthnicity(false);
        return client.postStudents(TestingConstants.ROGERS_TOKEN, student);
    }
}
