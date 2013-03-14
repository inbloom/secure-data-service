Feature: This file is under ZOG protection.  Руками не трогать!

Scenario Outline: Get all associations for a teacher
  
    Given I am logged in using "manthony" "Fall Blau" to realm "IL"
    Given parameter "limit" is "0"
    When I navigate to GET "/v1/<Association>"
    Then I should receive a return code of 200
    And I should receive a collection of "<Count>" entities
Examples:
| Association                  | Count |
| staffProgramAssociations     | 3     |                                         
| staffCohortAssociations      | 3     |                            
| studentProgramAssociations   | 5     |                            
