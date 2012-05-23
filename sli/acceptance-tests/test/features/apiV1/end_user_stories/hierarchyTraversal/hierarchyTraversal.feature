@RALLY_US1244
Feature: As an SLI application, I want to be able to traverse from education organizations down to low levels
    As a client application using SLI
    I want to be able to start at a very high level and be able to access relevant teachers
    I want to be able to start at a very high level and be able to access relevant students

Background:
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    Given format "application/vnd.slc+json"

Scenario: Traverse from parent education organization through child education organization, school, session, course, section to a teacher
    When I navigate to GET "/v1/educationOrganizations/<'STATE EDUCATION ORGANIZATION' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getEducationOrganizations" with URI "/v1/educationOrganizations?parentEducationAgencyReference=<'STATE EDUCATION ORGANIZATION' ID>"
    When I navigate to GET "/v1/educationOrganizations?parentEducationAgencyReference=<'STATE EDUCATION ORGANIZATION' ID>"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "getEducationOrganizations" with URI "/v1/educationOrganizations?parentEducationAgencyReference=<'LOCAL EDUCATION ORGANIZATION' ID>"
    When I navigate to GET "/v1/educationOrganizations/<'LOCAL EDUCATION ORGANIZATION' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getSchools" with URI "/v1/schools?parentEducationAgencyReference=<'LOCAL EDUCATION ORGANIZATION' ID>"
    When I navigate to GET "/v1/schools?parentEducationAgencyReference=<'LOCAL EDUCATION ORGANIZATION' ID>"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self" with URI "/v1/schools/<'SCHOOL' ID>"
    When I navigate to GET "/v1/schools/<'SCHOOL' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getSchoolSessionAssociations" with URI "/v1/schools/<'SCHOOL' ID>/schoolSessionAssociations"
    When I navigate to GET "/v1/schools/<'SCHOOL' ID>/schoolSessionAssociations"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self" with URI "/v1/schoolSessionAssociations/<'SCHOOL-SESSION-ASSOCIATION' ID>"
    When I navigate to GET "/v1/schoolSessionAssociations/<'SCHOOL-SESSION-ASSOCIATION' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getSession" with URI "/v1/sessions/<'SESSION' ID>"
    When I navigate to GET "/v1/sessions/<'SESSION' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getCourseOfferings" with URI "/v1/sessions/<'SESSION' ID>/courseOfferings"
    When I navigate to GET "/v1/sessions/<'SESSION' ID>/courseOfferings"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self" with URI "/v1/courseOfferings/<'SESSION-COURSE-ASSOCIATION' ID>"
    When I navigate to GET "/v1/courseOfferings/<'SESSION-COURSE-ASSOCIATION' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getCourse" with URI "/v1/courses/<'COURSE' ID>"
    When I navigate to GET "/v1/courses/<'COURSE' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getSections" with URI "/v1/sections?courseId=<'COURSE' ID>"
    When I navigate to GET "/v1/sections?courseId=<'COURSE' ID>"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self" with URI "/v1/sections/<'SECTION' ID>"
    When I navigate to GET "/v1/sections/<'SECTION' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getTeacherSectionAssociations" with URI "/v1/sections/<'SECTION' ID>/teacherSectionAssociations"
    When I navigate to GET "/v1/sections/<'SECTION' ID>/teacherSectionAssociations"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self" with URI "/v1/teacherSectionAssociations/<'TEACHER-SECTION-ASSOCIATION' ID>"
    When I navigate to GET "/v1/teacherSectionAssociations/<'TEACHER-SECTION-ASSOCIATION' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getTeacher" with URI "/v1/teachers/<'TEACHER' ID>"
    When I navigate to GET "/v1/teachers/<'TEACHER' ID>"
    Then I should receive a return code of 200
        And the "name" should be "Charles" "" "Gray"
        And "sex" should be "Male"
        And "highestLevelOfEducationCompleted" should be "No Degree"

Scenario: Traverse from parent education organization through child education organization, school, session, course, section to a student
    When I navigate to GET "/v1/educationOrganizations/<'STATE EDUCATION ORGANIZATION' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getEducationOrganizations" with URI "/v1/educationOrganizations?parentEducationAgencyReference=<'STATE EDUCATION ORGANIZATION' ID>"
    When I navigate to GET "/v1/educationOrganizations?parentEducationAgencyReference=<'STATE EDUCATION ORGANIZATION' ID>"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "getEducationOrganizations" with URI "/v1/educationOrganizations?parentEducationAgencyReference=<'LOCAL EDUCATION ORGANIZATION' ID>"
    When I navigate to GET "/v1/educationOrganizations/<'LOCAL EDUCATION ORGANIZATION' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getSchools" with URI "/v1/schools?parentEducationAgencyReference=<'LOCAL EDUCATION ORGANIZATION' ID>"
    When I navigate to GET "/v1/schools?parentEducationAgencyReference=<'LOCAL EDUCATION ORGANIZATION' ID>"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self" with URI "/v1/schools/<'SCHOOL' ID>"
    When I navigate to GET "/v1/schools/<'SCHOOL' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getSchoolSessionAssociations" with URI "/v1/schools/<'SCHOOL' ID>/schoolSessionAssociations"
    When I navigate to GET "/v1/schools/<'SCHOOL' ID>/schoolSessionAssociations"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self" with URI "/v1/schoolSessionAssociations/<'SCHOOL-SESSION-ASSOCIATION' ID>"
    When I navigate to GET "/v1/schoolSessionAssociations/<'SCHOOL-SESSION-ASSOCIATION' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getSession" with URI "/v1/sessions/<'SESSION' ID>"
    When I navigate to GET "/v1/sessions/<'SESSION' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getCourseOfferings" with URI "/v1/sessions/<'SESSION' ID>/courseOfferings"
    When I navigate to GET "/v1/sessions/<'SESSION' ID>/courseOfferings"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self" with URI "/v1/courseOfferings/<'SESSION-COURSE-ASSOCIATION' ID>"
    When I navigate to GET "/v1/courseOfferings/<'SESSION-COURSE-ASSOCIATION' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getCourse" with URI "/v1/courses/<'COURSE' ID>"
    When I navigate to GET "/v1/courses/<'COURSE' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getSections" with URI "/v1/sections?courseId=<'COURSE' ID>"
    When I navigate to GET "/v1/sections?courseId=<'COURSE' ID>"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self" with URI "/v1/sections/<'SECTION' ID>"
    When I navigate to GET "/v1/sections/<'SECTION' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getStudentSectionAssociations" with URI "/v1/sections/<'SECTION' ID>/studentSectionAssociations"
    When I navigate to GET "/v1/sections/<'SECTION' ID>/studentSectionAssociations"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self" with URI "/v1/studentSectionAssociations/<'STUDENT-SECTION' ID>"
    When I navigate to GET "/v1/studentSectionAssociations/<'STUDENT-SECTION' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getStudent" with URI "/v1/students/<'STUDENT' ID>"
    When I navigate to GET "/v1/students/<'STUDENT' ID>"
    Then I should receive a return code of 200
        And the "name" should be "Daniela" "" "Cusimana"
        And "economicDisadvantaged" should be "false"
        And "schoolFoodServicesEligibility" should be "Full price"
        And "limitedEnglishProficiency" should be "NotLimited"

