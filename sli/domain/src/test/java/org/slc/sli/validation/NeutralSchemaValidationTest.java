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


package org.slc.sli.validation;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.Entity;
import org.slc.sli.validation.schema.NeutralSchemaValidator;

/**
 * Tests sample fixture data against Neutral schema.
 *
 * @author Dong Liu <dliu@wgen.net>
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class NeutralSchemaValidationTest {
    @Autowired
    private SchemaRepository schemaRepo;

    @Autowired
    @Qualifier("validationRepo")
    private DummyEntityRepository repo;

    @Before
    public void init() {
        repo.clean();
    }

    @Test
    public void testValidEducationOrganization() throws Exception {
        addDummyEntity("educationOrganization", "1d303c61-88d4-404a-ba13-d7c5cc324bc5");

        readAndValidateFixtureData("src/test/resources/educationOrganization_fixture_neutral.json",
                "educationOrganization");
    }

    @Test
    public void testValidStaff() throws Exception {
        readAndValidateFixtureData("src/test/resources/staff_fixture_neutral.json", "staff");
    }

    @Test
    public void testValidSession() throws Exception {
        this.addDummyEntity("educationOrganization", "eb3b8c35-f582-df23-e406-6947249a19f2");
        this.addDummyEntity("school", "eb3b8c35-f582-df23-e406-6947249a19f2", "educationOrganization");
        this.addDummyEntity("gradingPeriod", "12345678-1234-1234-1234-12345678012");
        readAndValidateFixtureData("src/test/resources/session_fixture_neutral.json", "session");
    }

    @Test
    public void testValidCourse() throws Exception {
        addDummyEntity("school", "d7859848-99e6-11e1-8920-68a86d548d3e", "educationOrganization");
        readAndValidateFixtureData("src/test/resources/course_fixture_neutral.json", "course");
    }

    @Test
    public void testValidSchool() throws Exception {
        readAndValidateFixtureData("src/test/resources/school_fixture_neutral.json", "school");
    }

    @Test
    public void testValidStudent() throws Exception {
        readAndValidateFixtureData("src/test/resources/student_fixture_neutral.json", "student");
    }

    @Test
    public void testValidDisciplineIncident() throws Exception {
        this.addDummyEntity("educationOrganization", "eb3b8c35-f582-df23-e406-6947249a19f2");
        this.addDummyEntity("school", "eb3b8c35-f582-df23-e406-6947249a19f2", "educationOrganization");

        readAndValidateFixtureData("src/test/resources/disciplineIncident_fixture_neutral.json", "disciplineIncident");
    }

    @Test
    public void testValidDisciplineAction() throws Exception {
        this.addDummyEntity("educationOrganization", "eb3b8c35-f582-df23-e406-6947249a19f2");
        this.addDummyEntity("educationOrganization", "2058ddfb-b5c6-70c4-3bee-b43e9e93307d");
        this.addDummyEntity("staff", "e24b24aa-2556-994b-d1ed-6e6f71d1be97");
        this.addDummyEntity("student", "714c1304-8a04-4e23-b043-4ad80eb60992");
        this.addDummyEntity("student", "7a86a6a7-1f80-4581-b037-4a9328b9b650");
        this.addDummyEntity("disciplineIncident", "0e26de79-22aa-5d67-9201-5113ad50a03b");
        this.addDummyEntity("school", "eb3b8c35-f582-df23-e406-6947249a19f2", "educationOrganization");
        this.addDummyEntity("school", "2058ddfb-b5c6-70c4-3bee-b43e9e93307d", "educationOrganization");

        readAndValidateFixtureData("src/test/resources/disciplineAction_fixture_neutral.json", "disciplineAction");
    }

    @Test
    public void testValidAssessment() throws Exception {
        readAndValidateFixtureData("src/test/resources/assessment_fixture_neutral.json", "assessment");
    }

    @Test
    public void testValidTeacher() throws Exception {
        readAndValidateFixtureData("src/test/resources/teacher_fixture_neutral.json", "teacher");
    }

    @Test
    public void testValidSection() throws Exception {
        addDummyEntity("school", "eb3b8c35-f582-df23-e406-6947249a19f2", "educationOrganization");
        addDummyEntity("session", "389b0caa-dcd2-4e84-93b7-daa4a6e9b18e");
        addDummyEntity("course", "53777181-3519-4111-9210-529350429899");
        addDummyEntity("courseOffering", "74b361b5-1180-4221-9de0-f57aeea9f351");
        addDummyEntity("program", "cb292c7d-3503-414a-92a2-dc76a1585d79");
        addDummyEntity("program", "e8d33606-d114-4ee4-878b-90ac7fc3df16");

        readAndValidateFixtureData("src/test/resources/section_fixture_neutral.json", "section");
    }

    @Test
    @ExpectedException(value = EntityValidationException.class)
    public void testInvalidSection() throws Exception {
        addDummyCollection("school");
        addDummyCollection("session");
        addDummyCollection("courseOffering");
        addDummyCollection("program");

        readAndValidateFixtureData("src/test/resources/section_fixture_neutral.json", "section");
    }

    @Test
    public void testValidStudentAssessment() throws Exception {
        addDummyEntity("student", "7afddec3-89ec-402c-8fe6-cced79ae3ef5");
        addDummyEntity("student", "1023bfc9-5cb8-4126-ae6a-4fefa74682c8");
        addDummyEntity("student", "034e6e7f-9da2-454a-b67c-b95bd9f36433");
        addDummyEntity("student", "bda1a4df-c155-4897-85c2-953926a3ebd8");
        addDummyEntity("student", "54c6546e-7998-4c6b-ad5c-b8d72496bf78");
        addDummyEntity("student", "714c1304-8a04-4e23-b043-4ad80eb60992");
        addDummyEntity("student", "e1af7127-743a-4437-ab15-5b0dacd1bde0");
        addDummyEntity("student", "61f13b73-92fa-4a86-aaab-84999c511148");
        addDummyEntity("student", "289c933b-ca69-448c-9afd-2c5879b7d221");
        addDummyEntity("student", "c7146300-5bb9-4cc6-8b95-9e401ce34a03");
        addDummyEntity("assessment", "6c572483-fe75-421c-9588-d82f1f5f3af5");
        addDummyEntity("assessment", "6a53f63e-deb8-443d-8138-fc5a7368239c");
        addDummyEntity("assessment", "7b2e6133-4224-4890-ac02-73962eb09645");
        addDummyEntity("assessment", "dd9165f2-65fe-4e27-a8ac-bec5f4b757f6");
        addDummyEntity("assessment", "a22532c4-6455-41da-b24d-4f93224f526d");
        addDummyEntity("assessment", "b5f684d4-9a12-40c3-a59e-0c0d1b971a1e");

        readAndValidateFixtureData("src/test/resources/student_assessment_fixture_neutral.json",
                "studentAssessment");
    }

    @Test
    @ExpectedException(value = EntityValidationException.class)
    public void testInvalidStudentAssessment() throws Exception {
        addDummyCollection("student");
        addDummyCollection("assessment");

        readAndValidateFixtureData("src/test/resources/student_assessment_fixture_neutral.json",
                "studentAssessment");
    }

    @Test
    public void testValidTeacherSectionAssociation() throws Exception {
        addDummyEntity("teacher", "eb424dcc-6cff-a69b-c1b3-2b1fc86b2c94", "staff");
        addDummyEntity("section", "4efb4262-bc49-f388-0000-0000c9355700");
        addDummyEntity("section", "58c9ef19-c172-4798-8e6e-c73e68ffb5a3");
        addDummyEntity("section", "5c4b1a9c-2fcd-4fa0-b21c-f867cf4e7431");

        readAndValidateFixtureData("src/test/resources/teacher_section_association_fixture_neutral.json",
                "teacherSectionAssociation");
    }

    @Test
    @ExpectedException(value = EntityValidationException.class)
    public void testInvalidTeacherSectionAssociation() throws Exception {
        addDummyCollection("teacher");
        addDummyCollection("section");

        readAndValidateFixtureData("src/test/resources/teacher_section_association_fixture_neutral.json",
                "teacherSectionAssociation");
    }

    @Test
    public void testValidCourseOffering() throws Exception {
        addDummyEntity("session", "389b0caa-dcd2-4e84-93b7-daa4a6e9b18e");
        addDummyEntity("course", "53777181-3519-4111-9210-529350429899");
        addDummyEntity("session", "31e8e04f-5b1a-4631-91b3-a5433a735d3b");
        addDummyEntity("course", "93d33f0b-0f2e-43a2-b944-7d182253a79a");
        addDummyEntity("course", "a7444741-8ba1-424e-b83f-df88c57f8b8c");
        addDummyEntity("educationOrganization", "eb3b8c35-f582-df23-e406-6947249a19f2");

        readAndValidateFixtureData("src/test/resources/course_offering_neutral.json", "courseOffering");
    }

    @Test
    @ExpectedException(value = EntityValidationException.class)
    public void testInvalidCourseOffering() throws Exception {
        addDummyCollection("session");
        addDummyCollection("course");
        addDummyCollection("educationOrganization");

        readAndValidateFixtureData("src/test/resources/course_offering_neutral.json", "courseOffering");
    }

    @Test
    public void testValidStudentSectionAssociation() throws Exception {
        addDummyEntity("section", "cb7a932f-2d44-800c-d574-cdb25a29fc76");
        addDummyEntity("student", "2899a720-4196-6112-9874-edde0e2541db");
        addDummyEntity("student", "9e6d1d73-a488-4311-877a-718b897a17c5");
        addDummyEntity("student", "54c6548e-1196-86ca-ad5c-b8d72496bf78");
        addDummyEntity("student", "a63ee073-cd6c-9a12-a124-fa6a1b4dfc7c");
        addDummyEntity("student", "51dbb0cd-4f25-2d58-b587-5fac7605e4b3");

        readAndValidateFixtureData("src/test/resources/student_section_association_fixture_neutral.json",
                "studentSectionAssociation");
    }

    @Test
    @ExpectedException(value = EntityValidationException.class)
    public void testInvalidStudentSectionAssociation() throws Exception {
        addDummyCollection("section");
        addDummyCollection("student");

        readAndValidateFixtureData("src/test/resources/student_section_association_fixture_neutral.json",
                "studentSectionAssociation");
    }

    @Test
    public void testValidStaffEducationOrganizationAssociation() throws Exception {
        this.addDummyEntity("staff", "269be4c9-a806-4051-a02d-15a7af3ffe3e");
        this.addDummyEntity("staff", "f0e41d87-92d4-4850-9262-ed2f2723159b");
        this.addDummyEntity("staff", "858bf25e-51b8-450a-ade6-adda0a570d9e");
        this.addDummyEntity("staff", "55015e96-56dd-4d13-a091-5cef847ca085");
        this.addDummyEntity("staff", "ad878c6d-4eaf-4a8a-8284-8fb6570cea64");
        this.addDummyEntity("staff", "0e26de79-222a-4d67-9201-5113ad50a03b");
        this.addDummyEntity("educationOrganization", "4f0c9368-8488-7b01-0000-000059f9ba56");
        this.addDummyEntity("educationOrganization", "2d7583b1-f8ec-45c9-a6da-acc4e6fde458");

        readAndValidateFixtureData("src/test/resources/staff_educationOrganization_association_fixture_neutral.json",
                "staffEducationOrganizationAssociation");
    }

    @Test
    @ExpectedException(value = EntityValidationException.class)
    public void testInvalidStaffEducationOrganizationAssociation() throws Exception {
        this.addDummyCollection("staff");
        this.addDummyCollection("educationOrganization");

        readAndValidateFixtureData("src/test/resources/staff_educationOrganization_association_fixture_neutral.json",
                "staffEducationOrganizationAssociation");
    }

    @Test
    public void testValidStudentSchoolAssociation() throws Exception {
        this.addDummyEntity("school", "eb3b8c35-f582-df23-e406-6947249a19f2", "educationOrganization");
        this.addDummyEntity("student", "714c1304-8a04-4e23-b043-4ad80eb60992");
        this.addDummyEntity("student", "7a86a6a7-1f80-4581-b037-4a9328b9b650");
        this.addDummyEntity("student", "e0e99028-6360-4247-ae48-d3bb3ecb606a");

        readAndValidateFixtureData("src/test/resources/student_school_association_fixture_neutral.json",
                "studentSchoolAssociation");
    }

    @Test
    @ExpectedException(value = EntityValidationException.class)
    public void testInvalidStudentSchoolAssociation() throws Exception {
        this.addDummyCollection("student");
        this.addDummyCollection("school");

        readAndValidateFixtureData("src/test/resources/student_school_association_fixture_neutral.json",
                "studentSchoolAssociation");
    }

    @Test
    public void testValidTeacherSchoolAssociation() throws Exception {
        this.addDummyEntity("school", "0f464187-30ff-4e61-a0dd-74f45e5c7a9d", "educationOrganization");
        this.addDummyEntity("teacher", "8e5b2d0e-959c-42ef-b3df-9b83cba85a33", "staff");
        this.addDummyEntity("teacher", "a249d5d9-f149-d348-9b10-b26d68e7cb9c", "staff");

        readAndValidateFixtureData("src/test/resources/teacher_school_association_fixture_neutral.json",
                "teacherSchoolAssociation");
    }

    @Test
    @ExpectedException(value = EntityValidationException.class)
    public void testInvalidTeacherSchoolAssociation() throws Exception {
        this.addDummyCollection("teacher");
        this.addDummyCollection("school");

        readAndValidateFixtureData("src/test/resources/teacher_school_association_fixture_neutral.json",
                "teacherSchoolAssociation");
    }

    @Test
    public void testValidLearningObjective() throws Exception {
        readAndValidateFixtureData("src/test/resources/learningObjective_fixture_neutral.json", "learningObjective");
    }

    @Test
    public void testValidLearningStandard() throws Exception {
        readAndValidateFixtureData("src/test/resources/learningStandard_fixture_neutral.json", "learningStandard");
    }

    @Test
    public void testValidStudentDisciplineIncidentAssociation() throws Exception {
        this.addDummyEntity("disciplineIncident", "0e26de79-226a-5d67-9201-5113ad50a03b");
        this.addDummyEntity("disciplineIncident", "0e26de79-22aa-5d67-9201-5113ad50a03b");
        this.addDummyEntity("student", "714c1304-8a04-4e23-b043-4ad80eb60992");
        this.addDummyEntity("student", "7a86a6a7-1f80-4581-b037-4a9328b9b650");
        this.addDummyEntity("student", "e0e99028-6360-4247-ae48-d3bb3ecb606a");
        this.addDummyEntity("educationOrganization", "eb3b8c35-f582-df23-e406-6947249a19f2");

        readAndValidateFixtureData("src/test/resources/student_disciplineIncident_association_fixture_neutral.json",
                "studentDisciplineIncidentAssociation");
    }

    @Test
    @ExpectedException(value = EntityValidationException.class)
    public void testInvalidStudentDisciplineIncidentAssociation() throws Exception {
        this.addDummyCollection("student");
        this.addDummyCollection("disciplineIncident");

        readAndValidateFixtureData("src/test/resources/student_disciplineIncident_association_fixture_neutral.json",
                "studentSchoolAssociation");
    }

    @Test
    public void testValidProgram() throws Exception {
        readAndValidateFixtureData("src/test/resources/program_fixture.json", "program");
    }

    @Test
    public void testValidStaffProgramAssociation() throws Exception {
        this.addDummyEntity("program", "cb292c7d-3503-414a-92a2-dc76a1585d79");
        this.addDummyEntity("program", "e8d33606-d114-4ee4-878b-90ac7fc3df16");
        this.addDummyEntity("staff", "f0e41d87-92d4-4850-9262-ed2f2723159b");
        this.addDummyEntity("staff", "858bf25e-51b8-450a-ade6-adda0a570d9e");
        this.addDummyEntity("staff", "55015e96-56dd-4d13-a091-5cef847ca085");
        this.addDummyEntity("staff", "ad878c6d-4eaf-4a8a-8284-8fb6570cea64");
        this.addDummyEntity("staff", "21e57d58-f775-4cc8-b759-d8d9d811b5b4");

        readAndValidateFixtureData("src/test/resources/staff_program_association_fixture.json",
                "staffProgramAssociation");
    }

    @Test
    public void testValidStudentProgramAssociation() throws Exception {
        this.addDummyEntity("program", "cb292c7d-3503-414a-92a2-dc76a1585d79");
        this.addDummyEntity("program", "e8d33606-d114-4ee4-878b-90ac7fc3df16");
        this.addDummyEntity("student", "714c1304-8a04-4e23-b043-4ad80eb60992");
        this.addDummyEntity("student", "e1af7127-743a-4437-ab15-5b0dacd1bde0");
        this.addDummyEntity("student", "e0e99028-6360-4247-ae48-d3bb3ecb606a");
        this.addDummyEntity("student", "7a86a6a7-1f80-4581-b037-4a9328b9b650");
        this.addDummyEntity("student", "61f13b73-92fa-4a86-aaab-84999c511148");
        this.addDummyEntity("student", "289c933b-ca69-448c-9afd-2c5879b7d221");
        this.addDummyEntity("student", "c7146300-5bb9-4cc6-8b95-9e401ce34a03");
        this.addDummyEntity("student", "4efb3a11-bc49-f388-0000-0000c93556fb");
        this.addDummyEntity("student", "4efb3a5e-bc49-f388-0000-0000c93556fc");
        this.addDummyEntity("educationOrganization", "2d7583b1-f8ec-45c9-a6da-acc4e6fde458");
        this.addDummyEntity("educationOrganization", "0a922b8a-7a3b-4320-8b34-0f7749b8b062");
        this.addDummyEntity("educationOrganization", "9f5cb095-8e99-49a9-b130-bedfa20639d2");

        readAndValidateFixtureData("src/test/resources/student_program_association_fixture.json",
                "studentProgramAssociation");
    }

    @Test
    @ExpectedException(value = EntityValidationException.class)
    public void testInvalidStaffProgramAssociation() throws Exception {
        this.addDummyCollection("staff");
        this.addDummyCollection("program");

        readAndValidateFixtureData("src/test/resources/staff_program_association_fixture.json",
                "staffProgramAssociation");
    }

    @Test
    @ExpectedException(value = EntityValidationException.class)
    public void testInvalidStudentProgramAssociation() throws Exception {
        this.addDummyCollection("student");
        this.addDummyCollection("program");
        this.addDummyCollection("educationOrganization");

        readAndValidateFixtureData("src/test/resources/student_program_association_fixture.json",
                "studentProgramAssociation");
    }

    @Test
    public void testValidCohort() throws Exception {
        this.addDummyEntity("educationOrganization", "2d7583b1-f8ec-45c9-a6da-acc4e6fde458");
        this.addDummyEntity("educationOrganization", "0a922b8a-7a3b-4320-8b34-0f7749b8b062");
        this.addDummyEntity("educationOrganization", "9f5cb095-8e99-49a9-b130-bedfa20639d2");
        this.addDummyEntity("program", "e8d33606-d114-4ee4-878b-90ac7fc3df16");
        this.addDummyEntity("program", "cb292c7d-3503-414a-92a2-dc76a1585d79");

        readAndValidateFixtureData("src/test/resources/cohort_fixture_neutral.json", "cohort");
    }

    @Test
    @ExpectedException(value = EntityValidationException.class)
    public void testInvalidCohort() throws Exception {
        this.addDummyCollection("educationalOrganization");
        this.addDummyCollection("program");

        readAndValidateFixtureData("src/test/resources/cohort_fixture_neutral.json", "cohort");
    }

    @Test
    public void testValidStudentCohortAssociation() throws Exception {
        this.addDummyEntity("student", "714c1304-8a04-4e23-b043-4ad80eb60992");
        this.addDummyEntity("student", "e1af7127-743a-4437-ab15-5b0dacd1bde0");
        this.addDummyEntity("student", "e0e99028-6360-4247-ae48-d3bb3ecb606a");
        this.addDummyEntity("student", "7a86a6a7-1f80-4581-b037-4a9328b9b650");
        this.addDummyEntity("student", "61f13b73-92fa-4a86-aaab-84999c511148");
        this.addDummyEntity("student", "289c933b-ca69-448c-9afd-2c5879b7d221");
        this.addDummyEntity("student", "c7146300-5bb9-4cc6-8b95-9e401ce34a03");
        this.addDummyEntity("student", "4efb3a11-bc49-f388-0000-0000c93556fb");
        this.addDummyEntity("student", "4efb3a5e-bc49-f388-0000-0000c93556fc");
        this.addDummyEntity("cohort", "7e9915ed-ea6f-4e6b-b8b0-aeae20a25826");
        this.addDummyEntity("cohort", "a50121a2-c566-401b-99a5-71eb5cab5f4f");
        this.addDummyEntity("cohort", "a6929135-4782-46f1-ab01-b4df2e6ad093");

        readAndValidateFixtureData("src/test/resources/student_cohort_association_fixture_neutral.json",
                "studentCohortAssociation");
    }

    @Test
    @ExpectedException(value = EntityValidationException.class)
    public void testInvalidStudentCohortAssociation() throws Exception {
        this.addDummyCollection("student");
        this.addDummyCollection("cohort");

        readAndValidateFixtureData("src/test/resources/student_cohort_association_fixture_neutral.json",
                "studentCohortAssociation");
    }

    @Test
    public void testValidStaffCohortAssociation() throws Exception {
        this.addDummyEntity("staff", "f0e41d87-92d4-4850-9262-ed2f2723159b");
        this.addDummyEntity("staff", "858bf25e-51b8-450a-ade6-adda0a570d9e");
        this.addDummyEntity("staff", "55015e96-56dd-4d13-a091-5cef847ca085");
        this.addDummyEntity("staff", "ad878c6d-4eaf-4a8a-8284-8fb6570cea64");
        this.addDummyEntity("staff", "21e57d58-f775-4cc8-b759-d8d9d811b5b4");
        this.addDummyEntity("cohort", "7e9915ed-ea6f-4e6b-b8b0-aeae20a25826");
        this.addDummyEntity("cohort", "a50121a2-c566-401b-99a5-71eb5cab5f4f");
        this.addDummyEntity("cohort", "a6929135-4782-46f1-ab01-b4df2e6ad093");

        readAndValidateFixtureData("src/test/resources/staff_cohort_association_fixture_neutral.json",
                "staffCohortAssociation");
    }

    @Test
    @ExpectedException(value = EntityValidationException.class)
    public void testInvalidStaffCohortAssociation() throws Exception {
        this.addDummyCollection("staff");
        this.addDummyCollection("cohort");

        readAndValidateFixtureData("src/test/resources/staff_cohort_association_fixture_neutral.json",
                "staffCohortAssociation");
    }

    @SuppressWarnings("unchecked")
    private void readAndValidateFixtureData(String fixtureFile, String collection) throws Exception {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fixtureFile));
            String school;
            while ((school = reader.readLine()) != null) {
                ObjectMapper oRead = new ObjectMapper();
                Map<String, Object> obj = oRead.readValue(school, Map.class);
                this.mapValidation((Map<String, Object>) obj.get("body"), collection);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private void mapValidation(Map<String, Object> obj, String schemaName) {
        NeutralSchemaValidator validator = new NeutralSchemaValidator();
        validator.setSchemaRegistry(schemaRepo);
        validator.setEntityRepository(repo);
    	SelfReferenceValidator selfReferenceValidator = Mockito.mock(SelfReferenceValidator.class);
    	Mockito.when(selfReferenceValidator.validate(Mockito.any(Entity.class), Mockito.any(List.class))).thenReturn(true);
    	validator.setSelfReferenceValidator(selfReferenceValidator);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(obj);
        when(e.getType()).thenReturn(schemaName);

        try {
            assertTrue(validator.validate(e));
        } catch (EntityValidationException ex) {
            for (ValidationError err : ex.getValidationErrors()) {
                System.err.println(err + "\t" + schemaName);
            }
            throw ex;
        }
    }

    private void addDummyEntity(String collection, String id) {
        this.addDummyEntity(collection, id, collection);
    }

    private void addDummyEntity(String type, String id, String collection) {
        repo.addEntity(collection, id, ValidationTestUtils.makeDummyEntity(type, id));
    }

    private void addDummyCollection(String collection) {
        repo.addCollection(collection);
    }
}
