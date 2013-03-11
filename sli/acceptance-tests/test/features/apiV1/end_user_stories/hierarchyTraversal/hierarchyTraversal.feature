
@RALLY_DE87
@RALLY_US209
@RALLY_US1244
Feature: As an SLI application, I want to be able to traverse from education organizations down to low levels
    As a client application using SLI
    I want to be able to start at a very high level and be able to access relevant teachers
    I want to be able to start at a very high level and be able to access relevant students

Background:
    Given format "application/vnd.slc+json"

Scenario: Traverse from parent education organization through child education organization, school, session, course, section to a teacher as Staff
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    When I navigate to GET "/v1/educationOrganizations/<'STATE EDUCATION ORGANIZATION' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getFeederEducationOrganizations"
    When I navigate to GET the link named "getFeederEducationOrganizations"
    Then I should receive a return code of 200
        And I should receive a link named "getFeederSchools"
    When I navigate to GET the link named "getFeederSchools" with "id" of "bd086bae-ee82-4cf2-baf9-221a9407ea07"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self"
    When I navigate to GET the link named "self" with "id" of "92d6d5a0-852c-45f4-907a-912752831772" 
    Then I should receive a return code of 200
       And I should receive a link named "getSessions"
    When I navigate to GET the link named "getSessions"
    Then I should receive a return code of 200
         And in an entity, I should receive a link named "self" 
    When I navigate to GET the link named "self"
    Then I should receive a return code of 200
        And I should receive a link named "getCourseOfferings"
    When I navigate to GET the link named "self" with "id" of "88ddb0c4-1787-4ed8-884e-96aa774e6d42"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self"
    When I navigate to GET the link named "self"
    Then I should receive a return code of 200
        And I should receive a link named "getCourses"
    When I navigate to GET the link named "getCourses"
    Then I should receive a return code of 200
        And I should receive a link named "getCourseOfferings"
    When I navigate to GET the link named "getCourseOfferings"
    Then I should receive a return code of 200
        And I should receive a collection link named "getSections"
    When I navigate to GET the link named "getSections"
    Then I should receive a return code of 200
        And I should receive zero entities

    When I navigate to GET "/v1/sections/<'SECTION' ID>/teacherSectionAssociations"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self"
    When I navigate to GET "/v1/teacherSectionAssociations/<'TEACHER-SECTION-ASSOCIATION' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getTeacher" with URI "/v1.2/teachers/<'TEACHER' ID>"
    When I navigate to GET "/v1/teachers/<'TEACHER' ID>"
    Then I should receive a return code of 200
        And the "name" should be "Charles" "" "Gray"
        And "sex" should be "Male"
        And "highestLevelOfEducationCompleted" should be "No Degree"

Scenario: Traverse from parent education organization through child education organization, school, session, course, section to a student as Staff
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    When I navigate to GET "/v1/educationOrganizations/<'STATE EDUCATION ORGANIZATION' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getFeederEducationOrganizations"
    When I navigate to GET the link named "getFeederEducationOrganizations"
    Then I should receive a return code of 200
        And I should receive a link named "getFeederSchools"
    When I navigate to GET the link named "getFeederSchools" with "id" of "bd086bae-ee82-4cf2-baf9-221a9407ea07"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self"
    When I navigate to GET the link named "self" with "id" of "92d6d5a0-852c-45f4-907a-912752831772"
    Then I should receive a return code of 200
         And I should receive a link named "getSessions"
    When I navigate to GET the link named "getSessions"
    Then I should receive a return code of 200
         And in an entity, I should receive a link named "self"
    When I navigate to GET the link named "self"
    Then I should receive a return code of 200
        And I should receive a link named "getCourseOfferings"
    When I navigate to GET the link named "getCourseOfferings"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self"
    When I navigate to GET the link named "self"
    Then I should receive a return code of 200
        And I should receive a link named "getCourse"
    When I navigate to GET the link named "getCourse"
    Then I should receive a return code of 200

        And I should receive a link named "getCourseOfferings"
    When I navigate to GET "/v1/courses/<'COURSE' ID>/courseOfferings"
    Then I should receive a return code of 200
        And I should receive a collection link named "getSections"
    When I navigate to GET "/v1/schools/92d6d5a0-852c-45f4-907a-912752831772/sections?courseOfferingId=88ddb0c4-1787-4ed8-884e-96aa774e6d42"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self"
    When I navigate to GET "/v1/sections/<'SECTION' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getStudentSectionAssociations"
    When I navigate to GET "/v1/sections/<'SECTION' ID>/studentSectionAssociations"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self"
    When I navigate to GET "/v1/studentSectionAssociations/<'STUDENT-SECTION' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getStudent"
    When I navigate to GET "/v1/students/<'STUDENT' ID>"
    Then I should receive a return code of 200
        And the "name" should be "Daniela" "" "Cusimana"
        And "economicDisadvantaged" should be "false"
        And "schoolFoodServicesEligibility" should be "Full price"
        And "limitedEnglishProficiency" should be "NotLimited"

Scenario: Traverse from parent education organization through child education organization, school, session, course, section to a teacher as Teacher
    Given I am logged in using "cgray" "cgray1234" to realm "IL"
    When I navigate to GET "/v1/educationOrganizations/<'STATE EDUCATION ORGANIZATION' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getFeederEducationOrganizations"
    When I navigate to GET the link named "getFeederEducationOrganizations"
    Then I should receive a return code of 200
        And I should receive a link named "getFeederSchools"
    When I navigate to GET the link named "getFeederSchools" with "id" of "bd086bae-ee82-4cf2-baf9-221a9407ea07"
    Then I should receive a return code of 200
        And in an entity "92d6d5a0-852c-45f4-907a-912752831772", I should receive a link named "self"
    When I navigate to GET the link named "self" with "id" of "92d6d5a0-852c-45f4-907a-912752831772" 
    Then I should receive a return code of 200
       And I should receive a link named "getSessions"
    When I navigate to GET the link named "getSessions"
    Then I should receive a return code of 200
         And in an entity "c549e272-9a7b-4c02-aff7-b105ed76c904", I should receive a link named "self"
    When I navigate to GET the link named "self"
    Then I should receive a return code of 200
  And in an entity "c549e272-9a7b-4c02-aff7-b105ed76c904", I should receive a link named "getCourseOfferings"
    When I navigate to GET the link named "getCourseOfferings"
    Then I should receive a return code of 200
        And in an entity "88ddb0c4-1787-4ed8-884e-96aa774e6d42", I should receive a link named "self"
    When I navigate to GET the link named "self"
    Then I should receive a return code of 200
        And I should receive a link named "getCourse"
    When I navigate to GET the link named "getCourse"
    Then I should receive a return code of 200
      And in an entity "5841cf31-16a6-4b4d-abe1-3909d86b4fc3", I should receive a link named "getCourseOfferings"
    When I navigate to GET the link named "getCourseOfferings"
    Then I should receive a return code of 200
        And I should receive a collection link named "getSections"
    When I navigate to GET the link named "getSections"
    Then I should receive a return code of 200

  And in an entity "15ab6363-5509-470c-8b59-4f289c224107_id", I should receive a link named "self"
  When I navigate to GET the link named "self"
  Then I should receive a return code of 200
  And I should receive a link named "getCourseOffering"
  When I navigate to GET the link named "getCourseOfferings"
  Then I should receive a return code of 200
  And in an entity, I should receive a link named "self"
  When I navigate to GET the link named "self"
  Then I should receive a return code of 200
  And I should receive a link named "getCourse"
  When I navigate to GET the link named "getCourse"
  Then I should receive a return code of 200
  And I should receive a link named "getCourseOfferings"
  When I navigate to GET the link named "getCourseOfferings"
  Then I should receive a return code of 200
  And I should receive a collection link named "getSections"
  When I navigate to GET the link named "getSections"
  Then I should receive a return code of 200
  And in an entity, I should receive a link named "self"
  When I navigate to GET the link named "self"
  Then I should receive a return code of 200
  And I should receive a link named "getTeacherSectionAssociations"

  When I navigate to GET "/v1/sections/<'SECTION' ID>/teacherSectionAssociations"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self"
    When I navigate to GET "/v1/teacherSectionAssociations/<'TEACHER-SECTION-ASSOCIATION' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getTeacher" with URI "/v1.2/teachers/<'TEACHER' ID>"
    When I navigate to GET "/v1/teachers/<'TEACHER' ID>"
    Then I should receive a return code of 200
        And the "name" should be "Charles" "" "Gray"
        And "sex" should be "Male"
        And "highestLevelOfEducationCompleted" should be "No Degree"

Scenario: Traverse from parent education organization through child education organization, school, session, course, section to a student as Teacher
    Given I am logged in using "cgray" "cgray1234" to realm "IL"
    When I navigate to GET "/v1/educationOrganizations/<'STATE EDUCATION ORGANIZATION' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getFeederEducationOrganizations"
    When I navigate to GET the link named "getFeederEducationOrganizations"
    Then I should receive a return code of 200
        And I should receive a link named "getFeederSchools"
    When I navigate to GET the link named "getFeederSchools" with "id" of "bd086bae-ee82-4cf2-baf9-221a9407ea07"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self"
    When I navigate to GET the link named "self" with "id" of "92d6d5a0-852c-45f4-907a-912752831772"
    Then I should receive a return code of 200
         And I should receive a link named "getSessions"
    When I navigate to GET the link named "getSessions"
    Then I should receive a return code of 200
         And in an entity "abcff7ae-1f01-46bc-8cc7-cf409819bbce", I should receive a link named "self"
    When I navigate to GET the link named "self"
    Then I should receive a return code of 200
        And I should receive a link named "getCourseOfferings"
    When I navigate to GET the link named "getCourseOfferings"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self"
    When I navigate to GET the link named "self"
    Then I should receive a return code of 200
        And I should receive a link named "getCourse"
    When I navigate to GET the link named "getCourse"
    Then I should receive a return code of 200

        And I should receive a link named "getCourseOfferings"
    When I navigate to GET "/v1/courses/<'COURSE' ID>/courseOfferings"
    Then I should receive a return code of 200
        And I should receive a collection link named "getSections"
    When I navigate to GET "/v1/sections?courseOfferingId=<'SESSION-COURSE-ASSOCIATION' ID>"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self"
    When I navigate to GET "/v1/sections/<'SECTION' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getStudentSectionAssociations"
    When I navigate to GET "/v1/sections/<'SECTION' ID>/studentSectionAssociations"
    Then I should receive a return code of 200
        And in an entity, I should receive a link named "self"
    When I navigate to GET "/v1/studentSectionAssociations/<'STUDENT-SECTION' ID>"
    Then I should receive a return code of 200
        And I should receive a link named "getStudent"
    When I navigate to GET "/v1/students/<'STUDENT' ID>"
    Then I should receive a return code of 200
        And the "name" should be "Daniela" "" "Cusimana"
        And "limitedEnglishProficiency" should be "NotLimited"

