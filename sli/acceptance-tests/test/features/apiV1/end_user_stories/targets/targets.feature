@RALLY_US209
Feature: Test resolution of targets link for different entities

Background: Logged in as a super-user or teacher and using the small data set

Scenario Outline: Check targets resolution after reading an assessment by ID
  Given I am logged in using "<User>" "<Password>" to realm "IL"
  And format "application/vnd.slc+json"
      When I navigate to GET "/<ASSESSMENT URI>/<SAT ID>"
      Then I should receive a return code of 200
      And I should not receive a link named "getStudents" with URI "/<ASSESSMENT URI>/<SAT ID>/<STUDENT ASSESSMENT ASSOC URI>/<STUDENT URI>"
      When I navigate to GET "/<ASSESSMENT URI>/<SAT ID>/<STUDENT ASSESSMENT ASSOC URI>/<STUDENT URI>"
      Then I should receive a return code of 404
Examples:
  |User       |Password       |
  |rrogers    |rrogers1234    |
  |linda.kim  |linda.kim1234  |

Scenario Outline: Check targets resolution after reading a teacher by ID
  Given I am logged in using "<User>" "<Password>" to realm "IL"
  And format "application/vnd.slc+json"
   When I navigate to GET "/<TEACHER URI>/<Braverman ID>"
   Then I should receive a return code of 200
      And I should receive a link named "getSections" with URI "/<TEACHER URI>/<Braverman ID>/<TEACHER SECTION ASSOC URI>/<SECTION URI>"
      And I should receive a link named "getSchools" with URI "/<TEACHER URI>/<Braverman ID>/<TEACHER SCHOOL ASSOC URI>/<SCHOOL URI>"
   When I navigate to GET "/<TEACHER URI>/<Braverman ID>/<TEACHER SECTION ASSOC URI>/<SECTION URI>"
      Then I should receive a return code of 200
        And I should have a list of 1 "section" entities
        And I should have an entity with ID "<Homeroom ID>"
   When I navigate to GET "/<TEACHER URI>/<Braverman ID>/<TEACHER SCHOOL ASSOC URI>/<SCHOOL URI>"
      Then I should receive a return code of 200
        And I should have a list of 1 "school" entities
        And I should have an entity with ID "<South Daybreak Elementary ID>"
Examples:
  |User        |Password        |
  |rrogers     |rrogers1234     |
  |rbraverman  |rbraverman1234  |

Scenario Outline: Check targets resolution after reading a student by ID
  Given I am logged in using "<User>" "<Password>" to realm "IL"
  And format "application/vnd.slc+json"
   When I navigate to GET "/<STUDENT URI>/<Marvin ID>"
   Then I should receive a return code of 200
      And I should receive a link named "getAssessments" with URI "/<STUDENT URI>/<Marvin ID>/<STUDENT ASSESSMENT ASSOC URI>/<ASSESSMENT URI>"
      When I navigate to GET "/<STUDENT URI>/<Marvin ID>/<STUDENT ASSESSMENT ASSOC URI>/<ASSESSMENT URI>"
      Then I should receive a return code of 200
        And I should have a list of 1 "assessment" entities
        And I should have an entity with ID "<SAT ID>"
Examples:
  |User       |Password       |
  |rrogers    |rrogers1234    |
  |linda.kim  |linda.kim1234  |

Scenario Outline: Check targets resolution after reading a school by ID
  Given I am logged in using "<User>" "<Password>" to realm "IL"
  And format "application/vnd.slc+json"
   When I navigate to GET "/<SCHOOL URI>/<South Daybreak Elementary ID>"
   Then I should receive a return code of 200
      And I should receive a link named "getTeachers" with URI "/<SCHOOL URI>/<South Daybreak Elementary ID>/<TEACHER SCHOOL ASSOC URI>/<TEACHER URI>"
      When I navigate to GET "/<SCHOOL URI>/<South Daybreak Elementary ID>/<TEACHER SCHOOL ASSOC URI>/<TEACHER URI>"
      Then I should receive a return code of 200
        And I should have a list of 1 "teacher" entities
        And I should have an entity with ID "<Braverman ID>"
Examples:
  |User        |Password        |
  |rrogers     |rrogers1234     |
  |rbraverman  |rbraverman1234  |
