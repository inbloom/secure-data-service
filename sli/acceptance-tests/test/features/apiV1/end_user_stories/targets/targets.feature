
Feature: Test resolution of targets link for different entities

Background: Logged in as a super-user and using the small data set
    Given I am logged in using "demo" "demo1234" to realm "SLI"
      And format "application/vnd.slc+json"

Scenario: Check targets resolution after reading an assessment by ID
      When I navigate to GET "/<ASSESSMENT URI>/<Writing Advanced Placement Test ID>"
      Then I should receive a return code of 200
          And I should receive a link named "getStudents" with URI "/<ASSESSMENT URI>/<Writing Advanced Placement Test ID>/<STUDENT ASSESSMENT ASSOC URI>/<STUDENT URI>"
      When I navigate to GET "/<ASSESSMENT URI>/<Writing Advanced Placement Test ID>/<STUDENT ASSESSMENT ASSOC URI>/<STUDENT URI>"
      Then I should receive a return code of 200
        And I should have a list of 5 "student" entities
        And I should have an entity with ID "<Alfonso ID>"
        And I should have an entity with ID "<Priscilla ID>"
        And I should have an entity with ID "<Alden ID>"
        And I should have an entity with ID "<Donna ID>"
        And I should have an entity with ID "<Rachel ID>"

Scenario: Check targets resolution after reading a teacher by ID
   When I navigate to GET "/<TEACHER URI>/<Quemby ID>"
   Then I should receive a return code of 200
      And I should receive a link named "getSections" with URI "/<TEACHER URI>/<Quemby ID>/<TEACHER SECTION ASSOC URI>/<SECTION URI>"
      And I should receive a link named "getSchools" with URI "/<TEACHER URI>/<Quemby ID>/<TEACHER SCHOOL ASSOC URI>/<SCHOOL URI>"
   When I navigate to GET "/<TEACHER URI>/<Quemby ID>/<TEACHER SECTION ASSOC URI>/<SECTION URI>"
      Then I should receive a return code of 200
        And I should have a list of 2 "section" entities
        And I should have an entity with ID "<Chemistry F11 ID>"
        And I should have an entity with ID "<Physics S08 ID>"
   When I navigate to GET "/<TEACHER URI>/<Quemby ID>/<TEACHER SCHOOL ASSOC URI>/<SCHOOL URI>"
      Then I should receive a return code of 200
        And I should have a list of 1 "school" entities
        And I should have an entity with ID "<Apple Alternative Elementary School ID>"

Scenario: Check targets resolution after reading a student by ID
   When I navigate to GET "/<STUDENT URI>/<Alfonso ID>"
   Then I should receive a return code of 200
      And I should receive a link named "getAssessments" with URI "/<STUDENT URI>/<Alfonso ID>/<STUDENT ASSESSMENT ASSOC URI>/<ASSESSMENT URI>"
      When I navigate to GET "/<STUDENT URI>/<Alfonso ID>/<STUDENT ASSESSMENT ASSOC URI>/<ASSESSMENT URI>"
      Then I should receive a return code of 200
        And I should have a list of 2 "assessment" entities
        And I should have an entity with ID "<Writing Advanced Placement Test ID>"
        And I should have an entity with ID "<Writing Achievement Assessment Test ID>"

Scenario: Check targets resolution after reading a school by ID
   When I navigate to GET "/<SCHOOL URI>/<Biology High School ID>"
   Then I should receive a return code of 200
      And I should receive a link named "getTeachers" with URI "/<SCHOOL URI>/<Biology High School ID>/<TEACHER SCHOOL ASSOC URI>/<TEACHER URI>"
      When I navigate to GET "/<SCHOOL URI>/<Biology High School ID>/<TEACHER SCHOOL ASSOC URI>/<TEACHER URI>"
      Then I should receive a return code of 200
        And I should have a list of 3 "teacher" entities
        And I should have an entity with ID "<Ms. Jones ID>"
        And I should have an entity with ID "<Mr. Smith ID>"
        And I should have an entity with ID "<Mrs. Solis ID>"