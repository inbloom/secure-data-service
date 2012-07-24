@RALLY_US3041
Feature: Test exposure of aggregates and derived values to the api

Background: 
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And format "application/json"

  Scenario Outline: As a teacher, I want to view the highest ever test score for my students 
    When I navigate to GET "/v1/students/<ENTITY_ID>"
    Then I should receive a return code of 200
    And I get a link to "getAggregates"
    When I navigate to that link 
    Then I see the highest ever test score is <SCORE>

                Examples:
                        | ENTITY_ID                                   | SCORE  |                        
                        | 2012aa-1b0d443a-d511-11e1-b0fd-0811960672a8 | "31.0" |
                        | 2012ac-1b1ea9be-d511-11e1-b0fd-0811960672a8 | "28.0" |
                        | 2012ae-1b124d5c-d511-11e1-b0fd-0811960672a8 | "24.0" |

  Scenario: As a teacher, I want to filter the aggregate values that are returned
    When I navigate to a student with many aggregates
    And I should receive a return code of 200
    And I get a link to "getAggregates"
    When I navigate to that link 
    When I see 9 aggregates
    When I navigate to that link and filter by "type=assessments"
    When I see 8 aggregates
    And each aggregate's type is "assessments"
    When I navigate to that link and filter by "type=assessments&name=ACT"
    When I see 4 aggregates
    And each aggregate's type is "assessments"
    And each aggregate's name is "ACT"
    When I navigate to that link and filter by "type=assessments&window=HighestEver"
    When I see 4 aggregates
    And each aggregate's type is "assessments"
    And each aggregate's window is "HighestEver"
    When I navigate to that link and filter by "type=assessments&method=ScaleScore"
    When I see 4 aggregates
    And each aggregate's type is "assessments"
    And each aggregate's method is "ScaleScore"
