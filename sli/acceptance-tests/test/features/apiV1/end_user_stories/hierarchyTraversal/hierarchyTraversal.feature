Feature: As an SLI application, I want to be able to traverse from education organizations down to low levels
    As a client application using SLI
    I want to be able to start at a very high level and be able to access relevant teachers
    I want to be able to start at a very high level and be able to access relevant students

Background:
    Given I am logged in using "demo" "demo1234" to realm "SLI"
    Given format "application/vnd.slc+json"

Scenario: Traverse from parent education organization through child education organization, school, session, course, section to a teacher
    When I navigate to GET "/v1/educationOrganizations/<'KANSAS STATE (EDORG)' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getEducationOrganizations" with URI "/v1/educationOrganizations?parentEducationAgencyReference=<'KANSAS STATE (EDORG)' ID>"
    When I navigate to GET "/v1/educationOrganizations?parentEducationAgencyReference=<'KANSAS STATE (EDORG)' ID>"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "getEducationOrganizations" with URI "/v1/educationOrganizations?parentEducationAgencyReference=<'SMALLVILLE DISTRICT (EDORG)' ID>"
    When I navigate to GET "/v1/educationOrganizations/<'SMALLVILLE DISTRICT (EDORG)' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getSchools" with URI "/v1/schools?parentEducationAgencyReference=<'SMALLVILLE DISTRICT (EDORG)' ID>"
    When I navigate to GET "/v1/schools?parentEducationAgencyReference=<'SMALLVILLE DISTRICT (EDORG)' ID>"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self" with URI "/v1/schools/<'DEREK SMALLS HIGH SCHOOL (SCHOOL)' ID>"
    When I navigate to GET "/v1/schools/<'DEREK SMALLS HIGH SCHOOL (SCHOOL)' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getSchoolSessionAssociations" with URI "/v1/schools/<'DEREK SMALLS HIGH SCHOOL (SCHOOL)' ID>/schoolSessionAssociations"
    When I navigate to GET "/v1/schoolSessionAssociations/<'SPRING 2011 @ DEREK SMALLS HIGH SCHOOL (SCHOOL-SESSION)' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "self" with URI "/v1/schoolSessionAssociations/<'SPRING 2011 @ DEREK SMALLS HIGH SCHOOL (SCHOOL-SESSION)' ID>"
    When I navigate to GET "/v1/schoolSessionAssociations/<'SPRING 2011 @ DEREK SMALLS HIGH SCHOOL (SCHOOL-SESSION)' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getSession" with URI "/v1/sessions/<'SPRING 2011 (SESSION)' ID>"
    When I navigate to GET "/v1/sessions/<'SPRING 2011 (SESSION)' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getSessionCourseAssociations" with URI "/v1/sessions/<'SPRING 2011 (SESSION)' ID>/sessionCourseAssociations"
    When I navigate to GET "/v1/sessions/<'SPRING 2011 (SESSION)' ID>/sessionCourseAssociations"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self" with URI "/v1/sessionCourseAssociations/<'MATH 1 DURING SPRING 2011 (SESSION-COURSE)' ID>"
    When I navigate to GET "/v1/sessionCourseAssociations/<'MATH 1 DURING SPRING 2011 (SESSION-COURSE)' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getCourse" with URI "/v1/courses/<'MATH 1 (COURSE)' ID>"
    When I navigate to GET "/v1/courses/<'MATH 1 (COURSE)' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getSections" with URI "/v1/sections?courseId=<'MATH 1 (COURSE)' ID>"
    When I navigate to GET "/v1/sections?courseId=<'MATH 1 (COURSE)' ID>"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self" with URI "/v1/sections/<'MATH 1 (SECTION)' ID>"
    When I navigate to GET "/v1/sections/<'MATH 1 (SECTION)' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getTeacherSectionAssociations" with URI "/v1/sections/<'MATH 1 (SECTION)' ID>/teacherSectionAssociations"
    When I navigate to GET "/v1/sections/<'MATH 1 (SECTION)' ID>/teacherSectionAssociations"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self" with URI "/v1/teacherSectionAssociations/<'TEACHER OF SECTION 1 (TEACHER-SECTION)' ID>"
    When I navigate to GET "/v1/teacherSectionAssociations/<'TEACHER OF SECTION 1 (TEACHER-SECTION)' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getTeacher" with URI "/v1/teachers/<'OTIS OBAMA (TEACHER)' ID>"
    When I navigate to GET "/v1/teachers/<'OTIS OBAMA (TEACHER)' ID>"
    Then I should receive a return code of 200
        And the "name" should be "Otis" "" "Obama"
        And "sex" should be "Male"
        And "highestLevelOfEducationCompleted" should be "Bachelors"

Scenario: Traverse from parent education organization through child education organization, school, session, course, section to a student
    When I navigate to GET "/v1/educationOrganizations/<'KANSAS STATE (EDORG)' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getEducationOrganizations" with URI "/v1/educationOrganizations?parentEducationAgencyReference=<'KANSAS STATE (EDORG)' ID>"
    When I navigate to GET "/v1/educationOrganizations?parentEducationAgencyReference=<'KANSAS STATE (EDORG)' ID>"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "getEducationOrganizations" with URI "/v1/educationOrganizations?parentEducationAgencyReference=<'SMALLVILLE DISTRICT (EDORG)' ID>"
    When I navigate to GET "/v1/educationOrganizations/<'SMALLVILLE DISTRICT (EDORG)' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getSchools" with URI "/v1/schools?parentEducationAgencyReference=<'SMALLVILLE DISTRICT (EDORG)' ID>"
    When I navigate to GET "/v1/schools?parentEducationAgencyReference=<'SMALLVILLE DISTRICT (EDORG)' ID>"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self" with URI "/v1/schools/<'DEREK SMALLS HIGH SCHOOL (SCHOOL)' ID>"
    When I navigate to GET "/v1/schools/<'DEREK SMALLS HIGH SCHOOL (SCHOOL)' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getSchoolSessionAssociations" with URI "/v1/schools/<'DEREK SMALLS HIGH SCHOOL (SCHOOL)' ID>/schoolSessionAssociations"
    When I navigate to GET "/v1/schoolSessionAssociations/<'SPRING 2011 @ DEREK SMALLS HIGH SCHOOL (SCHOOL-SESSION)' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "self" with URI "/v1/schoolSessionAssociations/<'SPRING 2011 @ DEREK SMALLS HIGH SCHOOL (SCHOOL-SESSION)' ID>"
    When I navigate to GET "/v1/schoolSessionAssociations/<'SPRING 2011 @ DEREK SMALLS HIGH SCHOOL (SCHOOL-SESSION)' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getSession" with URI "/v1/sessions/<'SPRING 2011 (SESSION)' ID>"
    When I navigate to GET "/v1/sessions/<'SPRING 2011 (SESSION)' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getSessionCourseAssociations" with URI "/v1/sessions/<'SPRING 2011 (SESSION)' ID>/sessionCourseAssociations"
    When I navigate to GET "/v1/sessions/<'SPRING 2011 (SESSION)' ID>/sessionCourseAssociations"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self" with URI "/v1/sessionCourseAssociations/<'MATH 1 DURING SPRING 2011 (SESSION-COURSE)' ID>"
    When I navigate to GET "/v1/sessionCourseAssociations/<'MATH 1 DURING SPRING 2011 (SESSION-COURSE)' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getCourse" with URI "/v1/courses/<'MATH 1 (COURSE)' ID>"
    When I navigate to GET "/v1/courses/<'MATH 1 (COURSE)' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getSections" with URI "/v1/sections?courseId=<'MATH 1 (COURSE)' ID>"
    When I navigate to GET "/v1/sections?courseId=<'MATH 1 (COURSE)' ID>"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self" with URI "/v1/sections/<'MATH 1 (SECTION)' ID>"
    When I navigate to GET "/v1/sections/<'MATH 1 (SECTION)' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getStudentSectionAssociations" with URI "/v1/sections/<'MATH 1 (SECTION)' ID>/studentSectionAssociations"
    When I navigate to GET "/v1/sections/<'MATH 1 (SECTION)' ID>/studentSectionAssociations"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self" with URI "/v1/studentSectionAssociations/<'SCOTTY PIERSON IN MATH 1 (STUDENT-SECTION)' ID>"
    When I navigate to GET "/v1/studentSectionAssociations/<'SCOTTY PIERSON IN MATH 1 (STUDENT-SECTION)' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getStudent" with URI "/v1/students/<'SCOTTY PIERSON (STUDENT)' ID>"
    When I navigate to GET "/v1/students/<'SCOTTY PIERSON (STUDENT)' ID>"
    Then I should receive a return code of 200
        And the "name" should be "Scotty" "" "Pierson"
        And "economicDisadvantaged" should be "true"
        And "schoolFoodServicesEligibility" should be "Free"
        And a "studentCharacteristics" "characteristic" should be "Homeless"
        And "limitedEnglishProficiency" should be "Limited"
        And a "disabilities" "disability" should be "Emotional Disturbance"
        And "section504Disabilities" should be "Motor Impairment"
        And "displacementStatus" should be "Hurricane Kevin"
        And a "programParticipations" should be "Extended Day/Child Care Services"

