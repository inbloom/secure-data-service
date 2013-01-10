@RALLY_US3041
Feature: Test exposure of aggregates and calculated values to the api

Background: 
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And format "application/json"

  Scenario Outline: As a teacher, I want to view the highest ever test score for my students 
    When I navigate to GET "/v1/students/<ENTITY_ID>"
    Then I should receive a return code of 200
    And I get a link to "getCalculatedValues"
    When I navigate to that link 
    Then I see the highest ever test score is <SCORE>
    When I navigate to GET "/v1/students/<ENTITY_ID>?includeCalculatedValues=true"
    Then I see the embedded highest ever test score is <SCORE>

                Examples:
                        | ENTITY_ID                                   | SCORE  |                        
                        | 2012aa-1b0d443a-d511-11e1-b0fd-0811960672a8_id | "31.0" |
                        | 2012ac-1b1ea9be-d511-11e1-b0fd-0811960672a8_id | "28.0" |
                        | 2012ae-1b124d5c-d511-11e1-b0fd-0811960672a8_id | "24.0" |

  Scenario: As a teacher, I want to filter the calculated values that are returned
    When I navigate to a student with many calculated values
    And I should receive a return code of 200
    And I get a link to "getCalculatedValues"
    When I navigate to that link 
    When I see 9 calculated values
    When I navigate to that link and filter by "type=assessments"
    When I see 8 calculated values
    And each calculated value's type is "assessments"
    When I navigate to that link and filter by "type=assessments&name=ACT"
    When I see 4 calculated values
    And each calculated value's type is "assessments"
    And each calculated value's name is "ACT"
    When I navigate to that link and filter by "type=assessments&window=HighestEver"
    When I see 4 calculated values
    And each calculated value's type is "assessments"
    And each calculated value's window is "HighestEver"
    When I navigate to that link and filter by "type=assessments&method=ScaleScore"
    When I see 4 calculated values
    And each calculated value's type is "assessments"
    And each calculated value's method is "ScaleScore"
