@RALLY_US1129
Feature: Test exposure of aggregates and derived values to the api

Background: 
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And format "application/json"

  Scenario Outline: As a teacher, I want to view the highest ever test score for my students 
    When I navigate to GET "/students/<ENTITY_ID>"
    Then I should receive a return code of 200
    And I get a link to the aggregates
    And when I navigate to that link I see the highest ever test score is <SCORE>

                Examples:
                        | ENTITY_ID                                   | SCORE  |                        
                        | 2012aa-1b0d443a-d511-11e1-b0fd-0811960672a8 | "31.0" |
                        | 2012ac-1b1ea9be-d511-11e1-b0fd-0811960672a8 | "28.0" |
                        | 2012ae-1b124d5c-d511-11e1-b0fd-0811960672a8 | "24.0" |
