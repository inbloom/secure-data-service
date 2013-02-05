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

package org.slc.sli.dashboard.unit.manager;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.slc.sli.dashboard.client.APIClient;
import org.slc.sli.dashboard.entity.Config;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.manager.EntityManager;
import org.slc.sli.dashboard.manager.PopulationManager;
import org.slc.sli.dashboard.manager.impl.PopulationManagerImpl;
import org.slc.sli.dashboard.util.Constants;
import org.slc.sli.dashboard.util.StudentSummaryBuilder;

/**
 *
 * Tests for the population manager.
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(EntityManager.class)
public class PopulationManagerTest {

    private static final String SECTION_ID = "fc4de89d-534e-4ae7-ae3c-b4a536e1a4ac";
    private PopulationManagerImpl manager;
    private EntityManager mockEntity;
    private APIClient mockAPI;

    @Before
    public void setUp() throws Exception {
        manager = new PopulationManagerImpl();
        mockEntity = mock(EntityManager.class);
        manager.setEntityManager(mockEntity);
        mockAPI = mock(APIClient.class);
        manager.setApiClient(mockAPI);
    }

    @After
    public void tearDown() throws Exception {
        manager = null;
        mockEntity = null;
        mockAPI = null;
    }

    /*
     * Test that student summaries are being populated from programs, assessments, and students
     */
    @Test
    public void testGetStudentSummaries() throws Exception {

        String studentId = "0";
        String sectionId = "sectionId";
        String token = "token";
        String assessmentName = "Read2";
        List<String> studentIds = new ArrayList<String>();
        studentIds.add(studentId);
        List<String> programs = new ArrayList<String>();
        programs.add("ELL");

        EntityManager mockedEntityManager = PowerMockito.spy(new EntityManager());

        // Setup studentPrograms
        GenericEntity studentProgram = new GenericEntity();
        studentProgram.put("studentId", studentId);
        studentProgram.put("programs", programs);
        List<GenericEntity> studentPrograms = new ArrayList<GenericEntity>();
        studentPrograms.add(studentProgram);

        // Setup studentSummaries
        GenericEntity studentSummary = new GenericEntity();
        studentSummary.put("id", studentId);
        List<GenericEntity> students = new ArrayList<GenericEntity>();
        students.add(studentSummary);

        PowerMockito.doReturn(students).when(mockedEntityManager, "getStudents", token, sectionId);

        // setup attendance
        PowerMockito.doReturn(new ArrayList<GenericEntity>()).when(mockedEntityManager, "getAttendance", token,
                studentId, null, null);

        // setup session
        // GenericEntity baseSession = generateSession("2010-2011", "2010-12-31", "2011-01-31");
        PowerMockito.doReturn(new GenericEntity()).when(mockedEntityManager, "getSession", token, null);
        PowerMockito.doReturn(new ArrayList<GenericEntity>()).when(mockedEntityManager, "getSessionsByYear", token,
                null);

        // run it
        PopulationManager popMan = new PopulationManagerImpl();
        popMan.setEntityManager(mockedEntityManager);

        List<GenericEntity> studentSummaries = popMan.getStudentSummaries(token, studentIds, null, sectionId);
        assertTrue(studentSummaries.size() == 1);

        GenericEntity result = studentSummaries.get(0);
        assertTrue(result.get("id").equals(studentId));
    }

    private List<String> getStudentIds() {
        List<String> studentIds = new ArrayList<String>();
        studentIds.add("0");
        studentIds.add("1");
        return studentIds;
    }

    @Test
    public void testGetSessionDates() throws Exception {
        String sessionId = "1";
        GenericEntity baseSession = generateSession("2010-2011", "2010-12-31", "2011-01-31");
        when(mockEntity.getSession(null, sessionId)).thenReturn(baseSession);
        when(mockEntity.getSessionsByYear(null, "2010-2011")).thenReturn(Arrays.asList(baseSession));

        // See that we have the same beginning and end date
        List<String> dates = manager.getSessionDates(null, sessionId);
        assertTrue(dates.size() == 2);
        assertTrue(dates.get(0).compareTo("2010-12-31") == 0);
        assertTrue(dates.get(1).compareTo("2011-01-31") == 0);

        // See that we compare dates correctly
        GenericEntity lateSession = generateSession("2010-2011", "2011-02-1", "2011-03-14");
        when(mockEntity.getSessionsByYear(null, "2010-2011")).thenReturn(Arrays.asList(baseSession, lateSession));
        dates = manager.getSessionDates(null, sessionId);
        assertTrue(dates.size() == 2);
        assertTrue(dates.get(0).compareTo("2010-12-31") == 0);
        assertTrue(dates.get(1).compareTo("2011-03-14") == 0);

        // Try starting with the middle of a 3 semester setup.
        GenericEntity earlySession = generateSession("2010-2011", "2010-01-01", "2010-12-30");
        when(mockEntity.getSessionsByYear(null, "2010-2011")).thenReturn(
                Arrays.asList(baseSession, lateSession, earlySession));
        dates = manager.getSessionDates(null, sessionId);
        assertTrue(dates.size() == 2);
        assertTrue(dates.get(0).compareTo("2010-01-01") == 0);
        assertTrue(dates.get(1).compareTo("2011-03-14") == 0);

    }

    private GenericEntity generateSession(String schoolYear, String beginDate, String endDate) {
        GenericEntity startingSession = new GenericEntity();
        startingSession.put("schoolYear", schoolYear);
        startingSession.put("beginDate", beginDate);
        startingSession.put("endDate", endDate);
        return startingSession;
    }

    @Test
    public void testGetAttendanceForOneStudent() throws Exception {

        // start with a token, a student id, and a data config
        String studentId = "0";
        Config.Data config = new Config.Data();

        Gson gson = new Gson();
        List<Map> maps = null;
        // matt soller
        String enrollmentsJson = "[{id\":\"2012ap-d08c7b25-c484-11e1-a9ce-68a86d3c2f82\",\"educationalPlans\":[],\"studentId\":\"2012sh-cc10689b-c484-11e1-a9ce-68a86d3c2f82\",\"links\":[{\"rel\":\"self\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/studentSchoolAssociations/2012ap-d08c7b25-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"custom\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/studentSchoolAssociations/2012ap-d08c7b25-c484-11e1-a9ce-68a86d3c2f82/custom\"},{\"rel\":\"getStudent\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/students/2012sh-cc10689b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getSchool\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getEducationOrganization\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getStudents\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/studentSchoolAssociations/2012ap-d08c7b25-c484-11e1-a9ce-68a86d3c2f82/students\"},{\"rel\":\"getSchools\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/studentSchoolAssociations/2012ap-d08c7b25-c484-11e1-a9ce-68a86d3c2f82/schools\"}],\"schoolId\":\"2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\",\"entityType\":\"studentSchoolAssociation\",\"entryDate\":\"2011-09-01\",\"entryGradeLevel\":\"Eighth grade\",\"entryGradeLevelCode\":\"8\",\"school\":{\"educationOrgIdentificationCode\":[{\"identificationSystem\":\"School\",\"ID\":\"East Daybreak Junior High\"}],\"links\":[{\"rel\":\"self\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"custom\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82/custom\"},{\"rel\":\"getParentEducationOrganization\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012jg-d065df9a-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getCourses\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/courses?schoolId\u003d2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getStudentSchoolAssociations\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82/studentSchoolAssociations\"},{\"rel\":\"getStudents\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82/studentSchoolAssociations/students\"},{\"rel\":\"getSections\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/sections?schoolId\u003d2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getAttendances\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/attendances?schoolId\u003d2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getCohorts\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/cohorts?educationOrgId\u003d2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getDisciplineActionsAsResponsibleSchool\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/disciplineActions?responsibilitySchoolId\u003d2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getDisciplineActionsAsAssignedSchool\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/disciplineActions?assignmentSchoolId\u003d2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getSessions\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/sessions?schoolId\u003d2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getTeacherSchoolAssociations\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82/teacherSchoolAssociations\"},{\"rel\":\"getTeachers\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82/teacherSchoolAssociations/teachers\"},{\"rel\":\"getDisciplineIncidents\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/disciplineIncidents?schoolId\u003d2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"}],\"stateOrganizationId\":\"East Daybreak Junior High\",\"entityType\":\"school\",\"nameOfInstitution\":\"East Daybreak Junior High\",\"schoolCategories\":[\"Junior High School\"],\"id\":\"2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\",\"accountabilityRatings\":[],\"gradesOffered\":[\"Sixth grade\",\"Seventh grade\",\"Eighth grade\"],\"organizationCategories\":[\"School\"],\"address\":[{\"nameOfCounty\":\"Wake\",\"streetNumberName\":\"111 Ave B\",\"postalCode\":\"10112\",\"stateAbbreviation\":\"IL\",\"addressType\":\"Physical\",\"city\":\"Chicago\"}],\"parentEducationAgencyReference\":\"2012jg-d065df9a-c484-11e1-a9ce-68a86d3c2f82\",\"programReference\":[],\"telephone\":[{\"institutionTelephoneNumberType\":\"Main\",\"telephoneNumber\":\"(917)-555-3312\"}]}},{\"id\":\"2012oz-d08cc947-c484-11e1-a9ce-68a86d3c2f82\",\"educationalPlans\":[],\"entryType\":\"Next year school\",\"studentId\":\"2012sh-cc10689b-c484-11e1-a9ce-68a86d3c2f82\",\"exitWithdrawType\":\"End of school year\",\"links\":[{\"rel\":\"self\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/studentSchoolAssociations/2012oz-d08cc947-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"custom\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/studentSchoolAssociations/2012oz-d08cc947-c484-11e1-a9ce-68a86d3c2f82/custom\"},{\"rel\":\"getStudent\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/students/2012sh-cc10689b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getSchool\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getEducationOrganization\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getStudents\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/studentSchoolAssociations/2012oz-d08cc947-c484-11e1-a9ce-68a86d3c2f82/students\"},{\"rel\":\"getSchools\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/studentSchoolAssociations/2012oz-d08cc947-c484-11e1-a9ce-68a86d3c2f82/schools\"}],\"schoolId\":\"2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\",\"exitWithdrawDate\":\"2011-05-11\",\"entityType\":\"studentSchoolAssociation\",\"entryDate\":\"2010-09-01\",\"entryGradeLevel\":\"Seventh grade\",\"entryGradeLevelCode\":\"7\",\"school\":{\"educationOrgIdentificationCode\":[{\"identificationSystem\":\"School\",\"ID\":\"East Daybreak Junior High\"}],\"links\":[{\"rel\":\"self\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"custom\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82/custom\"},{\"rel\":\"getParentEducationOrganization\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012jg-d065df9a-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getCourses\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/courses?schoolId\u003d2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getStudentSchoolAssociations\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82/studentSchoolAssociations\"},{\"rel\":\"getStudents\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82/studentSchoolAssociations/students\"},{\"rel\":\"getSections\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/sections?schoolId\u003d2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getAttendances\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/attendances?schoolId\u003d2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getCohorts\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/cohorts?educationOrgId\u003d2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getDisciplineActionsAsResponsibleSchool\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/disciplineActions?responsibilitySchoolId\u003d2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getDisciplineActionsAsAssignedSchool\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/disciplineActions?assignmentSchoolId\u003d2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getSessions\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/sessions?schoolId\u003d2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getTeacherSchoolAssociations\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82/teacherSchoolAssociations\"},{\"rel\":\"getTeachers\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82/teacherSchoolAssociations/teachers\"},{\"rel\":\"getDisciplineIncidents\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/disciplineIncidents?schoolId\u003d2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"}],\"stateOrganizationId\":\"East Daybreak Junior High\",\"entityType\":\"school\",\"nameOfInstitution\":\"East Daybreak Junior High\",\"schoolCategories\":[\"Junior High School\"],\"id\":\"2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\",\"accountabilityRatings\":[],\"gradesOffered\":[\"Sixth grade\",\"Seventh grade\",\"Eighth grade\"],\"organizationCategories\":[\"School\"],\"address\":[{\"nameOfCounty\":\"Wake\",\"streetNumberName\":\"111 Ave B\",\"postalCode\":\"10112\",\"stateAbbreviation\":\"IL\",\"addressType\":\"Physical\",\"city\":\"Chicago\"}],\"parentEducationAgencyReference\":\"2012jg-d065df9a-c484-11e1-a9ce-68a86d3c2f82\",\"programReference\":[],\"telephone\":[{\"institutionTelephoneNumberType\":\"Main\",\"telephoneNumber\":\"(917)-555-3312\"}]}},{\"id\":\"2012qx-d08cf058-c484-11e1-a9ce-68a86d3c2f82\",\"educationalPlans\":[],\"entryType\":\"Transfer from a public school in the same local education agency\",\"studentId\":\"2012sh-cc10689b-c484-11e1-a9ce-68a86d3c2f82\",\"exitWithdrawType\":\"End of school year\",\"links\":[{\"rel\":\"self\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/studentSchoolAssociations/2012qx-d08cf058-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"custom\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/studentSchoolAssociations/2012qx-d08cf058-c484-11e1-a9ce-68a86d3c2f82/custom\"},{\"rel\":\"getStudent\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/students/2012sh-cc10689b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getSchool\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getEducationOrganization\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getStudents\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/studentSchoolAssociations/2012qx-d08cf058-c484-11e1-a9ce-68a86d3c2f82/students\"},{\"rel\":\"getSchools\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/studentSchoolAssociations/2012qx-d08cf058-c484-11e1-a9ce-68a86d3c2f82/schools\"}],\"schoolId\":\"2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\",\"exitWithdrawDate\":\"2010-05-11\",\"entityType\":\"studentSchoolAssociation\",\"entryDate\":\"2009-09-07\",\"entryGradeLevel\":\"Sixth grade\",\"entryGradeLevelCode\":\"6\",\"school\":{\"educationOrgIdentificationCode\":[{\"identificationSystem\":\"School\",\"ID\":\"East Daybreak Junior High\"}],\"links\":[{\"rel\":\"self\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"custom\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82/custom\"},{\"rel\":\"getParentEducationOrganization\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012jg-d065df9a-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getCourses\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/courses?schoolId\u003d2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getStudentSchoolAssociations\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82/studentSchoolAssociations\"},{\"rel\":\"getStudents\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82/studentSchoolAssociations/students\"},{\"rel\":\"getSections\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/sections?schoolId\u003d2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getAttendances\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/attendances?schoolId\u003d2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getCohorts\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/cohorts?educationOrgId\u003d2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getDisciplineActionsAsResponsibleSchool\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/disciplineActions?responsibilitySchoolId\u003d2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getDisciplineActionsAsAssignedSchool\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/disciplineActions?assignmentSchoolId\u003d2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getSessions\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/sessions?schoolId\u003d2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getTeacherSchoolAssociations\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82/teacherSchoolAssociations\"},{\"rel\":\"getTeachers\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82/teacherSchoolAssociations/teachers\"},{\"rel\":\"getDisciplineIncidents\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/disciplineIncidents?schoolId\u003d2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"}],\"stateOrganizationId\":\"East Daybreak Junior High\",\"entityType\":\"school\",\"nameOfInstitution\":\"East Daybreak Junior High\",\"schoolCategories\":[\"Junior High School\"],\"id\":\"2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\",\"accountabilityRatings\":[],\"gradesOffered\":[\"Sixth grade\",\"Seventh grade\",\"Eighth grade\"],\"organizationCategories\":[\"School\"],\"address\":[{\"nameOfCounty\":\"Wake\",\"streetNumberName\":\"111 Ave B\",\"postalCode\":\"10112\",\"stateAbbreviation\":\"IL\",\"addressType\":\"Physical\",\"city\":\"Chicago\"}],\"parentEducationAgencyReference\":\"2012jg-d065df9a-c484-11e1-a9ce-68a86d3c2f82\",\"programReference\":[],\"telephone\":[{\"institutionTelephoneNumberType\":\"Main\",\"telephoneNumber\":\"(917)-555-3312\"}]}},{\"id\":\"2012qy-d08d176a-c484-11e1-a9ce-68a86d3c2f82\",\"educationalPlans\":[],\"entryType\":\"Next year school\",\"studentId\":\"2012sh-cc10689b-c484-11e1-a9ce-68a86d3c2f82\",\"exitWithdrawType\":\"End of school year\",\"links\":[{\"rel\":\"self\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/studentSchoolAssociations/2012qy-d08d176a-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"custom\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/studentSchoolAssociations/2012qy-d08d176a-c484-11e1-a9ce-68a86d3c2f82/custom\"},{\"rel\":\"getStudent\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/students/2012sh-cc10689b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getSchool\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getEducationOrganization\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getStudents\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/studentSchoolAssociations/2012qy-d08d176a-c484-11e1-a9ce-68a86d3c2f82/students\"},{\"rel\":\"getSchools\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/studentSchoolAssociations/2012qy-d08d176a-c484-11e1-a9ce-68a86d3c2f82/schools\"}],\"schoolId\":\"2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\",\"exitWithdrawDate\":\"2009-05-11\",\"entityType\":\"studentSchoolAssociation\",\"entryDate\":\"2008-09-05\",\"entryGradeLevel\":\"Fifth grade\",\"entryGradeLevelCode\":\"5\"},{\"id\":\"2012cd-d08d3e7b-c484-11e1-a9ce-68a86d3c2f82\",\"educationalPlans\":[],\"entryType\":\"Next year school\",\"studentId\":\"2012sh-cc10689b-c484-11e1-a9ce-68a86d3c2f82\",\"exitWithdrawType\":\"End of school year\",\"links\":[{\"rel\":\"self\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/studentSchoolAssociations/2012cd-d08d3e7b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"custom\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/studentSchoolAssociations/2012cd-d08d3e7b-c484-11e1-a9ce-68a86d3c2f82/custom\"},{\"rel\":\"getStudent\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/students/2012sh-cc10689b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getSchool\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getEducationOrganization\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getStudents\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/studentSchoolAssociations/2012cd-d08d3e7b-c484-11e1-a9ce-68a86d3c2f82/students\"},{\"rel\":\"getSchools\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/studentSchoolAssociations/2012cd-d08d3e7b-c484-11e1-a9ce-68a86d3c2f82/schools\"}],\"schoolId\":\"2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\",\"exitWithdrawDate\":\"2008-05-10\",\"entityType\":\"studentSchoolAssociation\",\"entryDate\":\"2007-09-12\",\"entryGradeLevel\":\"Fourth grade\",\"entryGradeLevelCode\":\"4\"},{\"id\":\"2012zz-d08d8c9d-c484-11e1-a9ce-68a86d3c2f82\",\"educationalPlans\":[],\"entryType\":\"Transfer from a private, religiously-affiliated school in a different state\",\"studentId\":\"2012sh-cc10689b-c484-11e1-a9ce-68a86d3c2f82\",\"exitWithdrawType\":\"Student is in a different public school in the same local education agency\",\"links\":[{\"rel\":\"self\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/studentSchoolAssociations/2012zz-d08d8c9d-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"custom\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/studentSchoolAssociations/2012zz-d08d8c9d-c484-11e1-a9ce-68a86d3c2f82/custom\"},{\"rel\":\"getStudent\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/students/2012sh-cc10689b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getSchool\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getEducationOrganization\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getStudents\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/studentSchoolAssociations/2012zz-d08d8c9d-c484-11e1-a9ce-68a86d3c2f82/students\"},{\"rel\":\"getSchools\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/studentSchoolAssociations/2012zz-d08d8c9d-c484-11e1-a9ce-68a86d3c2f82/schools\"}],\"schoolId\":\"2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\",\"exitWithdrawDate\":\"2007-05-09\",\"entityType\":\"studentSchoolAssociation\",\"entryDate\":\"2006-09-11\",\"entryGradeLevel\":\"Third grade\",\"entryGradeLevelCode\":\"3\"}]";
        String attendanceJson = "[{\"id\":\"2012mt-d1ddd9ef-c484-11e1-a9ce-68a86d3c2f82\",\"studentId\":\"2012sh-cc10689b-c484-11e1-a9ce-68a86d3c2f82\",\"links\":[{\"rel\":\"self\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/attendances/2012mt-d1ddd9ef-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"custom\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/attendances/2012mt-d1ddd9ef-c484-11e1-a9ce-68a86d3c2f82/custom\"},{\"rel\":\"getStudent\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/students/2012sh-cc10689b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getSchool\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"},{\"rel\":\"getEducationOrganization\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\"}],\"schoolId\":\"2012fs-d068509b-c484-11e1-a9ce-68a86d3c2f82\",\"entityType\":\"attendance\",\"schoolYearAttendance\":[{\"schoolYear\":\"2010-2011\",\"attendanceEvent\":[]},{\"schoolYear\":\"2007-2008\",\"attendanceEvent\":[]},{\"schoolYear\":\"2008-2009\",\"attendanceEvent\":[]},{\"schoolYear\":\"2006-2007\",\"attendanceEvent\":[]},{\"schoolYear\":\"2011-2012\",\"attendanceEvent\":[{\"event\":\"In Attendance\",\"date\":\"2011-11-24\"},{\"event\":\"In Attendance\",\"date\":\"2011-10-14\"},{\"event\":\"In Attendance\",\"date\":\"2011-11-25\"},{\"event\":\"In Attendance\",\"date\":\"2011-09-16\"},{\"event\":\"In Attendance\",\"date\":\"2011-10-25\"},{\"event\":\"In Attendance\",\"date\":\"2011-10-18\"},{\"event\":\"In Attendance\",\"date\":\"2011-10-12\"},{\"event\":\"In Attendance\",\"date\":\"2011-10-24\"},{\"event\":\"In Attendance\",\"date\":\"2011-09-21\"},{\"event\":\"In Attendance\",\"date\":\"2011-10-10\"},{\"event\":\"In Attendance\",\"date\":\"2011-12-12\"},{\"event\":\"In Attendance\",\"date\":\"2011-11-18\"},{\"event\":\"In Attendance\",\"date\":\"2011-09-07\"},{\"event\":\"In Attendance\",\"date\":\"2011-12-15\"},{\"event\":\"In Attendance\",\"date\":\"2011-12-01\"},{\"event\":\"In Attendance\",\"date\":\"2011-09-29\"},{\"event\":\"In Attendance\",\"date\":\"2011-09-14\"},{\"event\":\"In Attendance\",\"date\":\"2011-09-26\"},{\"event\":\"In Attendance\",\"date\":\"2011-09-15\"},{\"event\":\"In Attendance\",\"date\":\"2011-10-11\"},{\"event\":\"In Attendance\",\"date\":\"2011-09-08\"},{\"event\":\"In Attendance\",\"date\":\"2011-11-03\"},{\"event\":\"In Attendance\",\"date\":\"2011-12-13\"},{\"event\":\"In Attendance\",\"date\":\"2011-11-21\"},{\"event\":\"In Attendance\",\"date\":\"2011-11-16\"},{\"event\":\"In Attendance\",\"date\":\"2011-10-21\"},{\"event\":\"In Attendance\",\"date\":\"2011-12-14\"},{\"event\":\"In Attendance\",\"date\":\"2011-11-08\"},{\"event\":\"In Attendance\",\"date\":\"2011-12-06\"},{\"reason\":\"Absent excused\",\"event\":\"Excused Absence\",\"date\":\"2011-10-27\"},{\"event\":\"In Attendance\",\"date\":\"2011-09-19\"},{\"event\":\"In Attendance\",\"date\":\"2011-10-05\"},{\"event\":\"In Attendance\",\"date\":\"2011-11-09\"},{\"event\":\"In Attendance\",\"date\":\"2011-11-07\"},{\"reason\":\"Absent excused\",\"event\":\"Excused Absence\",\"date\":\"2011-09-13\"},{\"event\":\"In Attendance\",\"date\":\"2011-11-04\"},{\"event\":\"In Attendance\",\"date\":\"2011-10-31\"},{\"event\":\"In Attendance\",\"date\":\"2011-09-20\"},{\"event\":\"In Attendance\",\"date\":\"2011-11-14\"},{\"event\":\"In Attendance\",\"date\":\"2011-11-30\"},{\"event\":\"In Attendance\",\"date\":\"2011-10-20\"},{\"event\":\"In Attendance\",\"date\":\"2011-11-10\"},{\"event\":\"In Attendance\",\"date\":\"2011-11-15\"},{\"reason\":\"Absent excused\",\"event\":\"Excused Absence\",\"date\":\"2011-09-09\"},{\"event\":\"In Attendance\",\"date\":\"2011-10-13\"},{\"event\":\"In Attendance\",\"date\":\"2011-09-22\"},{\"event\":\"In Attendance\",\"date\":\"2011-11-02\"},{\"event\":\"In Attendance\",\"date\":\"2011-11-29\"},{\"event\":\"In Attendance\",\"date\":\"2011-11-17\"},{\"event\":\"In Attendance\",\"date\":\"2011-11-28\"},{\"event\":\"In Attendance\",\"date\":\"2011-10-26\"},{\"event\":\"In Attendance\",\"date\":\"2011-09-28\"},{\"event\":\"In Attendance\",\"date\":\"2011-12-16\"},{\"event\":\"In Attendance\",\"date\":\"2011-10-07\"},{\"event\":\"In Attendance\",\"date\":\"2011-11-11\"},{\"event\":\"In Attendance\",\"date\":\"2011-11-01\"},{\"event\":\"In Attendance\",\"date\":\"2011-09-27\"},{\"event\":\"In Attendance\",\"date\":\"2011-10-06\"},{\"event\":\"In Attendance\",\"date\":\"2011-12-05\"},{\"event\":\"In Attendance\",\"date\":\"2011-11-22\"},{\"event\":\"In Attendance\",\"date\":\"2011-09-30\"},{\"event\":\"In Attendance\",\"date\":\"2011-09-12\"},{\"event\":\"In Attendance\",\"date\":\"2011-12-09\"},{\"event\":\"In Attendance\",\"date\":\"2011-12-07\"},{\"event\":\"In Attendance\",\"date\":\"2011-10-17\"},{\"event\":\"In Attendance\",\"date\":\"2011-12-02\"},{\"event\":\"In Attendance\",\"date\":\"2011-09-06\"},{\"event\":\"In Attendance\",\"date\":\"2011-10-04\"},{\"event\":\"In Attendance\",\"date\":\"2011-10-03\"},{\"reason\":\"Absent excused\",\"event\":\"Excused Absence\",\"date\":\"2011-09-23\"},{\"event\":\"In Attendance\",\"date\":\"2011-11-23\"},{\"event\":\"In Attendance\",\"date\":\"2011-10-28\"},{\"event\":\"In Attendance\",\"date\":\"2011-10-19\"},{\"event\":\"In Attendance\",\"date\":\"2011-12-08\"}]},{\"schoolYear\":\"2001-2002\",\"attendanceEvent\":[]}]}]";

        when(mockAPI.getEnrollmentForStudent(null, "0")).thenReturn(
                gson.fromJson(enrollmentsJson, ArrayList.class));
        when(mockEntity.getAttendance(null, "0", null, null)).thenReturn(
                gson.fromJson(attendanceJson, ArrayList.class));
        // set up mock attendance data
        /*
         * GenericEntity attend1 = new GenericEntity();
         * attend1.put(Constants.ATTR_ATTENDANCE_DATE, "2011-09-01");
         * attend1.put(Constants.ATTR_ATTENDANCE_EVENT_CATEGORY, "Tardy");
         * GenericEntity attend2 = new GenericEntity();
         * attend2.put(Constants.ATTR_ATTENDANCE_DATE, "2011-10-01");
         * attend2.put(Constants.ATTR_ATTENDANCE_EVENT_CATEGORY, "Excused Absence");
         *
         * List<GenericEntity> attendList = new ArrayList<GenericEntity>();
         * attendList.add(attend1);
         * attendList.add(attend2);
         *
         * when(mockEntity.getAttendance(null, "0", null, null)).thenReturn(attendList);
         */
        // make the call
        GenericEntity a = manager.getAttendance(null, studentId, config);

        Assert.assertNotNull(a);
        List<LinkedHashMap<String, Object>> attendances = a.getList("attendance");
        Assert.assertNotNull(attendances);
        Assert.assertEquals(2, attendances.size());
        Assert.assertEquals("2011-2012", attendances.get(0).get("term"));
        Assert.assertEquals("East Daybreak Junior High", attendances.get(0).get("schoolName"));
        Assert.assertEquals("8", attendances.get(0).get("gradeLevel"));
        Assert.assertEquals("95", attendances.get(0).get("present"));
        Assert.assertEquals(70, attendances.get(0).get("inAttendanceCount"));
        Assert.assertEquals(4, attendances.get(0).get("totalAbsencesCount"));
        Assert.assertEquals(0, attendances.get(0).get("absenceCount"));
        Assert.assertEquals(4, attendances.get(0).get("excusedAbsenceCount"));
        Assert.assertEquals(0, attendances.get(0).get("unexcusedAbsenceCount"));
        Assert.assertEquals(0, attendances.get(0).get("tardyCount"));
        Assert.assertEquals("2010-2011", attendances.get(1).get("term"));
        Assert.assertEquals("East Daybreak Junior High", attendances.get(1).get("schoolName"));
        Assert.assertEquals(0, attendances.get(1).get("tardyCount"));
        Assert.assertEquals(0, attendances.get(1).get("earlyDepartureCount"));

        // Assert.assertEquals(1, ((Integer) (((Map)
        // (a.getList("attendance").get(0))).get("totalCount"))).intValue());

    }

    @Test
    public void testApplyAssessmentFilters() throws Exception {

        // set up studentSummaries
        List<GenericEntity> studentSummaries = createSomeStudentSummaries();

        // set up config
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, String> assmtFilter = new HashMap<String, String>();
        assmtFilter.put("StateTest Reading", "MOST_RECENT_RESULT");
        params.put(Constants.CONFIG_ASSESSMENT_FILTER, assmtFilter);
        Config.Data config = new Config.Data("entity", "cacheKey", false, params);

        // make the call
        PopulationManagerImpl pm = new PopulationManagerImpl();
        pm.applyAssessmentFilters(studentSummaries, config);

        // check that two of the three assmts got filtered out
        GenericEntity student = studentSummaries.get(0);
        Map filteredAssmts = (Map) student.get("assessments");
        Map filteredAssmt = (Map) filteredAssmts.get("StateTest Reading");
        Assert.assertEquals(1, filteredAssmts.size());
        Assert.assertEquals("2011-05-01", filteredAssmt.get("administrationDate"));
    }

    @Test
    public void testEnhanceListOfStudents() {

        // set up studentSummaries
        List<GenericEntity> studentSummaries = createSomeStudentSummaries();

        // set up config
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, String> assmtFilter = new HashMap<String, String>();
        assmtFilter.put("StateTest Reading", "MOST_RECENT_RESULT");
        params.put(Constants.CONFIG_ASSESSMENT_FILTER, assmtFilter);
        Config.Data config = new Config.Data("entity", "cacheKey", false, params);

        // make the call
        PopulationManagerImpl pm = new PopulationManagerImpl();
        pm.applyAssessmentFilters(studentSummaries, config);
        // TODO - Pass "real" data, update createSomeStudentSummaries to use the same section ID
        // that you pass here.
        pm.enhanceListOfStudents(studentSummaries, SECTION_ID);

        // check for full name
        GenericEntity student = studentSummaries.get(0);
        Map name = (Map) student.get("name");
        Assert.assertEquals("John Doe", name.get("fullName"));

        // check for modified score attributes
        Map enhancedAssmts = (Map) student.get("assessments");
        Map enhancedAssmt = (Map) enhancedAssmts.get("StateTest Reading");
        Assert.assertEquals("50", enhancedAssmt.get("Scale score"));

        // check for attendance tallies
        Map attendances = (Map) student.get(Constants.ATTR_STUDENT_ATTENDANCES);
        Assert.assertEquals(1, attendances.get(Constants.ATTR_ABSENCE_COUNT));
        Assert.assertEquals(1, attendances.get(Constants.ATTR_TARDY_COUNT));
    }

    @Test
    public void testStudentComparator() {
        PopulationManagerImpl populationManager = new PopulationManagerImpl();
        List<GenericEntity> studentSummaries = new ArrayList<GenericEntity>();
        GenericEntity student = new GenericEntity();
        Map<String, Object> name = new HashMap<String, Object>();
        name.put("firstName", "J");
        name.put("lastSurname", "K");
        student.put("name", name);
        student.put("id", "dummyId");
        populationManager.addFullName(student);
        studentSummaries.add(student);

        name = new HashMap<String, Object>();
        student = new GenericEntity();
        name.put("firstName", "Z");
        name.put("lastSurname", "B");
        student.put("name", name);
        student.put("id", "dummyId3");
        populationManager.addFullName(student);
        studentSummaries.add(student);

        name = new HashMap<String, Object>();
        student = new GenericEntity();
        name.put("firstName", "A");
        name.put("lastSurname", "B");
        student.put("name", name);
        student.put("id", "dummyId1");
        populationManager.addFullName(student);
        studentSummaries.add(student);

        name = new HashMap<String, Object>();
        student = new GenericEntity();
        name.put("firstName", "A");
        name.put("lastSurname", "A");
        student.put("name", name);
        student.put("id", "dummyId5");
        populationManager.addFullName(student);
        studentSummaries.add(student);

        Assert.assertEquals("J K", studentSummaries.get(0)
                .getNode(Constants.ATTR_NAME + "." + Constants.ATTR_FULL_NAME));
        Collections.sort(studentSummaries, PopulationManagerImpl.STUDENT_COMPARATOR);
        Assert.assertEquals("A A", studentSummaries.get(0)
                .getNode(Constants.ATTR_NAME + "." + Constants.ATTR_FULL_NAME));
        Assert.assertEquals("A B", studentSummaries.get(1)
                .getNode(Constants.ATTR_NAME + "." + Constants.ATTR_FULL_NAME));
        Assert.assertEquals("J K", studentSummaries.get(2)
                .getNode(Constants.ATTR_NAME + "." + Constants.ATTR_FULL_NAME));
        Assert.assertEquals("Z B", studentSummaries.get(3)
                .getNode(Constants.ATTR_NAME + "." + Constants.ATTR_FULL_NAME));

    }

    private List<GenericEntity> createSomeStudentSummaries() {

        List<GenericEntity> studentSummaries = new ArrayList<GenericEntity>();
        GenericEntity student = new GenericEntity();
        Map<String, Object> name = new HashMap<String, Object>();
        name.put("firstName", "John");
        name.put("lastSurname", "Doe");
        student.put("name", name);
        student.put("id", "dummyId");
        studentSummaries.add(student);

        // create assmt data
        List<Map> assmtResults = new ArrayList<Map>();
        Map assmtResult1 = new HashMap();
        Map assmts1 = new HashMap();
        assmts1.put("assessmentFamilyHierarchyName",
                "StateTest.StateTest Reading for Grades 3-8.StateTest Reading for Grade 8");
        assmtResult1.put("assessments", assmts1);
        assmtResult1.put("administrationDate", "2011-05-01");
        List<Map> scores = new ArrayList<Map>();
        Map scaleScore = new HashMap();
        scaleScore.put("assessmentReportingMethod", "Scale score");
        scaleScore.put("result", "50");
        scores.add(scaleScore);
        assmtResult1.put("scoreResults", scores);
        assmtResults.add(assmtResult1);

        Map assmtResult2 = new HashMap();
        Map assmts2 = new HashMap();
        assmts2.put("assessmentFamilyHierarchyName",
                "StateTest.StateTest Reading for Grades 3-8.StateTest Reading for Grade 8");
        assmtResult2.put("assessments", assmts2);
        assmtResult2.put("administrationDate", "2011-03-01");
        assmtResults.add(assmtResult2);

        Map assmtResult3 = new HashMap();
        Map assmts3 = new HashMap();
        assmts3.put("assessmentFamilyHierarchyName", "Dummy Assessment Family");
        assmtResult3.put("assessments", assmts3);
        assmtResult3.put("administrationDate", "2011-02-01");
        assmtResults.add(assmtResult3);

        student.put(Constants.ATTR_STUDENT_ASSESSMENTS, assmtResults);

        // create attendance data
        Map<String, Object> attendanceBody = new HashMap<String, Object>();
        List<Map<String, Object>> attendances = new ArrayList<Map<String, Object>>();
        attendanceBody.put(Constants.ATTR_STUDENT_ATTENDANCES, attendances);
        student.put(Constants.ATTR_STUDENT_ATTENDANCES, attendanceBody);

        Map<String, Object> attendance1 = new HashMap<String, Object>();
        attendance1.put(Constants.ATTR_ATTENDANCE_EVENT_CATEGORY, "Unexcused Absence");
        attendances.add(attendance1);

        Map<String, Object> attendance2 = new HashMap<String, Object>();
        attendance2.put(Constants.ATTR_ATTENDANCE_EVENT_CATEGORY, "Tardy");
        attendances.add(attendance2);

        StudentSummaryBuilder.addFullDetailTranscripts(student);
        StudentSummaryBuilder.addRealGradeBookEntries(student);

        return studentSummaries;
    }

}
