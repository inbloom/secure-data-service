Feature:
  As an API user
  In order to find various entities
  I want to be able to search

  Background: Use JSON format
    Given I am logged in as a local-level IT Administrator
      And I want to use format "application/json"

Scenario Outline: An API user searches for assessments
    When I navigate to GET "/v1.5/<Entity URI>"
    Then I should receive a return code of 200
    And I should receive a collection with <COUNT> elements  
    

    Examples:
    | Entity URI | COUNT|
    |search/assessments?q=2003|1|
    |search/assessments?q=%222003%22|1|
    |search/assessments?q=209999|0|
    |search/assessments?q=%22209999%22|0|
    |search/assessments?q=Writing%202011|7|
    |search/assessments?q=%22Writing%202011%22|0|
    |search/assessments?q=Writing&q=2011|3|
    |search/assessments?q=%22BadTest111%22|0|
    |search/assessments?q=BadTest111|0|

Scenario Outline: check that elastic search functions as expected for students, staff, teachers and educationOrganizations
    Given I am logged in as a local-level IT Administrator
    When I navigate to GET "/v1.5/<Entity URI>"
    Then I should receive a return code of 200
    And I should receive a collection with <COUNT> elements  
    

    Examples:
    | Entity URI | COUNT|
    |search/students?q=mat|1|
    |search/students?q=rud|5|
    |search/students?q=waffles|0|
    |search/staff?q=mar|2|
    |search/staff?q=stev|1|
    |search/staff?q=waffles|0|
    |search/teachers?q=b|1|
    |search/teachers?q=waffles|0|
    |search/educationOrganizations?q=LEA|20|
    |search/educationOrganizations?q=wat|1|
    |search/educationOrganizations?q=waffles|0|
    |search/staff,teachers?q=b|2|
     
  Scenario Outline: Result of elastic search should contain correct unicode encoding
    Given I am logged in using "akopel" "akopel1234" to realm "IL"
    And parameter "limit" is "0"
    When I navigate to GET "/v1.1/<Entity URI>"
    Then I should receive a return code of 200
    And  I should have entity with "<ENTITY ID>" and "<DESC>"
  Examples:
    | Entity URI | ENTITY ID | DESC |
    |search?q=cri%20tri%20geo|dd9165f2-653e-6f27-a82c-bfc5f4c757bc|Use congruence and similarity criteria for triangles to solve problems and to prove relationships in geometric figures.★|
