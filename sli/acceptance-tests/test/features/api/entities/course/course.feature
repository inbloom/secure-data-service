Feature: As an SLI application, I want to be able to manage courses of instruction
  This means I want to be able to perform CRUD on courses

Background: Logged in as a super-user and using the small data set
  Given I am logged in using "demo" "demo1234"
  Given I have access to all courses

Scenario: Create a new course
  Given format "application/json"
    And "courseTitle" is "Chinese 1"
    And "numberOfParts" is 1
    And "courseCode.identificationSystem" is "Bob's ID System"
    And "courseCode.assigningOrganizationCode" is "Bob's Code Generator"
    And "courseCode.id" is "C1"
    And "courseLevel" is "Basic"
    And "courseLevelCharacteristics" is "Ap"
    And a "gradesOffered" is "Ninth_grade"
    And a "gradesOffered" is "Tenth_grade"
    And "subjectArea" is "Foreign Language and Literature"
    And "courseDescription" is "Intro to Chinese"
    And "dateCourseAdopted" is "2001-01-01"
    And "highSchoolCourseRequirement" is "<false>"
    And "courseGpaApplicability" is "Normal"
    And "courseDefinedBy" is "NAACP"
    And "minimumAvailableCredit" is 1
    And "maximumAvailableCredit" is 1
    And "careerPathway" is "Hospitality and Tourism"
  When I navigate to POST "/courses"
  Then I should receive a return code of 201
    And I should receive an ID for the newly created course
  When I navigate to GET "/courses/<newly created ID>"
  Then I should receive a return code of 200
    And "courseTitle" should be "Chinese 1"
    And "numberOfParts" should be 1
    And "courseCode.identificationSystem" should be "Bob's ID System"
    And "courseCode.assigningOrganizationCode" should be "Bob's Code Generator"
    And "courseCode.id" should be "C1"
    And "courseLevel" should be "Basic"
    And "courseLevelCharacteristics" should be "Ap"
    And a "gradesOffered" should be "Ninth_grade"
    And a "gradesOffered" should be "Tenth_grade"
    And "subjectArea" should be "Foreign Language and Literature"
    And "courseDescription" should be "Intro to Chinese"
    And "dateCourseAdopted" should be "2001-01-01"
    And "highSchoolCourseRequirement" should be "<false>"
    And "courseGpaApplicability" should be "Normal"
    And "courseDefinedBy" should be "NAACP"
    And "minimumAvailableCredit" should be 1
    And "maximumAvailableCredit" should be 1
    And "careerPathway" should be "Hospitality and Tourism"

Scenario: Read a course by ID
  Given format "application/json"
  When I navigate to GET "/courses/<'Spanish 1 Course' ID>"
  Then I should receive a return code of 200
    And I should receive a link named "self" with URI "/courses/<'Spanish 1 Course' ID>"
    And "courseTitle" should be "Spanish 1"
    And "numberOfParts" should be 1
    And "courseCode.identificationSystem" should be "Sid's ID System"
    And "courseCode.assigningOrganizationCode" should be "Sid's Code Generator"
    And "courseCode.id" should be "SP1"
    And "courseLevel" should be "Basic"
    And "courseLevelCharacteristics" should be "Ap"
    And a "gradesOffered" should be "Ninth_grade"
    And a "gradesOffered" should be "Tenth_grade"
    And a "gradesOffered" should be "Eleventh_grade"
    And "subjectArea" should be "Foreign Language and Literature"
    And "courseDescription" should be "Intro to Spanish"
    And "dateCourseAdopted" should be "2000-01-01"
    And "highSchoolCourseRequirement" should be "<false>"
    And "courseGpaApplicability" should be "Normal"
    And "courseDefinedBy" should be "NAAHP"
    And "minimumAvailableCredit" should be 1
    And "maximumAvailableCredit" should be 1
    And "careerPathway" should be "Hospitality and Tourism"

Scenario: Update an existing course
  Given format "application/json"
  When I navigate to GET "/courses/<'Russian 1 Course' ID>"
  Then I should receive a return code of 200
  When I set "maximumAvailableCredit" to 2
    And I navigate to PUT "/courses/<'Russian 1 Course' ID>"
  Then I should receive a return code of 204
  When I navigate to GET "/courses/<'Russian 1 Course' ID>"
  Then I should receive a link named "self" with URI "/courses/<'Russian 1 Course' ID>"
    And "maximumAvailableCredit" should be 2

Scenario: Delete an existing course
  Given format "application/json"
  When I navigate to DELETE "/courses/<'Russian 1 Course' ID>"
  Then I should receive a return code of 204
  When I navigate to GET "/courses/<'Russian 1 Course' ID>"
  Then I should receive a return code of 404
  
Scenario: Attempt to read a non-existing course
  Given format "application/json"
  When I navigate to GET "/courses/<non-existent ID>"
  Then I should receive a return code of 404

Scenario: Attempt to update a non-existing course
  Given format "application/json"
  When I navigate to GET "/courses/<non-existent ID>"
  Then I should receive a return code of 404

Scenario: Attempt to delete a non-existing course
  Given format "application/json"
  When I navigate to GET "/courses/<non-existent ID>"
  Then I should receive a return code of 404