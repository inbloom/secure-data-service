@RALLY_DE1995
Feature: Test resolution of targets link for different entities

  Background: Logged in as a teacher and using the small data set
    Given I am logged in using "linda.kim" "rbraverman1234" to realm "IL"
    And format "application/vnd.slc+json"

  Scenario: Check targets resolution after reading an assessment by ID
    When I navigate to GET "/<ASSESSMENT URI>/<SAT ID>"
    Then I should receive a return code of 200
    And I should receive a link named "getStudents" with URI "/<ASSESSMENT URI>/<SAT ID>/<STUDENT ASSESSMENT ASSOC URI>/<STUDENT URI>"
    When I navigate to GET "/<ASSESSMENT URI>/<SAT ID>/<STUDENT ASSESSMENT ASSOC URI>/<STUDENT URI>"
    Then I should receive a return code of 200
    And I should have a list of 2 "student" entities
    And I should have an entity with ID "<Marvin ID>"
    And I should have an entity with ID "<Matt Sollars ID>"

  Scenario: Check targets resolution after reading a student by ID
    When I navigate to GET "/<STUDENT URI>/<Marvin ID>"
    Then I should receive a return code of 200
    And I should receive a link named "getAssessments" with URI "/<STUDENT URI>/<Marvin ID>/<STUDENT ASSESSMENT ASSOC URI>/<ASSESSMENT URI>"
    When I navigate to GET "/<STUDENT URI>/<Marvin ID>/<STUDENT ASSESSMENT ASSOC URI>/<ASSESSMENT URI>"
    Then I should receive a return code of 200
    And I should have a list of 1 "assessment" entities
    And I should have an entity with ID "<SAT ID>"

