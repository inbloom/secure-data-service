Feature: Ensure the token generator works for student and parent in CI.

  Background: None

  Scenario: As a student or parent, I want to use the token generator to create a session
    Given I use the token generator for realm "Illinois Daybreak Students" and user "student.m.sollars"
    And format "application/json"
    When I navigate to GET "/v1/home"
    Then I should receive a return code of 200
#    Given I use the token generator for realm "Illinois Daybreak Parents" and user "aaron.smith"
#    And format "application/json"
#    When I navigate to GET "/v1/home"
#    Then I should receive a return code of 200