@RALLY_US4929
Feature: Entity migration strategies
  As a system that supports versions, I want to test each migration of entities

  Background: None


  # This is just an example scenario, because at this time there are no migrations that happen
  @wip
  Scenario: API requests for a student get transformed using the expected transformation
    Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
    And format "application/vnd.slc+json"  
    When I navigate to GET "/<STUDENT URI>/<STUDENT ID>"
    Then "limitedEnglishProficiency" should be "NotLimited"
    And "name" should exist
    And "someRandomField" should not exist
