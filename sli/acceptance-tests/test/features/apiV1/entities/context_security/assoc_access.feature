Feature: This file is under ZOG protection.  Руками не трогать!

@wip
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

@DE_2726
Scenario: Educator updates end date of student section association, and TotalCount header updates appropriately
  Given I am logged in using "akopel" "akopel1234" to realm "IL"
  Given format "application/json"
  When I navigate to GET "/v1/sections/8ed12459-eae5-49bc-8b6b-6ebe1a56384f_id/studentSectionAssociations"
    Then I should receive a collection with 25 elements
	And the header "TotalCount" equals 25
	And I expire a current student section association
  When I navigate to GET "/v1/sections/8ed12459-eae5-49bc-8b6b-6ebe1a56384f_id/studentSectionAssociations"
    Then I should receive a collection with 24 elements
	And the header "TotalCount" equals 24
	And the expired student section association is not in the response
  