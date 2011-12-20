Feature: <US565> In order to manage sections
    As a client application using SLI
    I want to have create, read, update, and delete functionality for a section.

Background: Logged in as a super-user and using the small data set
  	Given the SLI_SMALL dataset is loaded
    Given I am logged in using "demo" "demo1234"
    Given I have access to all students

#### Happy Path 
Scenario: Create a new section JSON
    Given format "application/json"
      	And the unique section code is "567"
      	And the sequence of course is "1"
       	And the educational environment is "2"
       	And the medium of instruction is "3"
       	And the population served is "4"
       	And the available credit is "0.5"
	When I navigate to POST "/sections/" 
    Then I should receive a return code of 201
       	And I should receive a ID for the newly created section

Scenario: Read a section by id JSON
    Given format "application/json"
    When I navigate to GET section "123"
    Then I should receive a return code of 200
    	And I should see the sequence of course is 7
     	And I should see the educational environment is "2"
     	And I should see the medium of instruction is "6"
     	And I should see the population served is "12"
     	And I should see the available credit is "2"

Scenario: Update an existing section JSON
    Given format "application/json"
    And the sequence of course is 2
    When I navigate to PUT section "123"
    Then I should receive a return code of 204
    When I navigate to GET section "123"
     And I should see the sequence of course is 2
        
Scenario: Delete an existing section JSON
    Given format "application/json"
    When I navigate to DELETE section "123"
    Then I should receive a return code of 204
    When I navigate to GET section "123"
    Then I should receive a return code of 404
    
    
#### XML version
@wip
Scenario: Create a new section XML
     Given format "application/xml"
        And the unique section code is "567"
        And the sequence of course is 1
        And the educational environment is "2"
        And the medium of instruction is "3"
        And the population served is "4"
        And the available credit is "0.5"
    When I navigate to POST "/sections/" 
    Then I should receive a return code of 201
        And I should receive a ID for the newly created section
@wip
Scenario: Read a section by id XML
    Given format "application/xml"
    When I navigate to GET section "123"
    Then I should receive a return code of 200
        And I should see the sequence of course is 7
        And I should see the educational environment is "2"
        And I should see the medium of instruction is "6"
        And I should see the population served is "12"
        And I should see the available credit is "2"
@wip
Scenario: Update an existing section XML
    Given format "application/xml"
    And the sequence of course is 2
    When I navigate to PUT section "123"
    Then I should receive a return code of 204
    When I navigate to GET section "123"
        And I should see the sequence of course is 2
@wip    
Scenario: Delete an existing section XML
    Given format "application/xml"
    When I navigate to DELETE section "123"
    Then I should receive a return code of 204
    When  I navigate to GET section "123"
    Then I should receive a return code of 404
    
### Error handling
Scenario: Attempt to read a non-existing section
    Given format "application/json"
    When I navigate to GET section "Invalid"
    Then I should receive a return code of 404      

Scenario: Attempt to delete a non-existing section
    Given format "application/json"
    When I navigate to DELETE section "Invalid"
    Then I should receive a return code of 404      

Scenario: Update a non-existing section
    Given format "application/json"
    When I attempt to update a non-existing section "Invalid"
    Then I should receive a return code of 404      
    
Scenario: Fail when asking for an unsupported format "text/plain"
    Given format "text/plain"
    When I navigate to GET section "123"
    Then I should receive a return code of 406
    
Scenario: Fail if going to the wrong URI
    Given format "application/json"
    When I navigate to GET section "WrongURI"
    Then I should receive a return code of 404
    
 Scenario: Attempt to read the base section resource with no GUID
    Given format "application/json"
    When I navigate to GET section "NoGUID"
    Then I should receive a return code of 405