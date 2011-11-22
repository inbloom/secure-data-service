Feature: <US63> In order to manage schools
    As a client application using SLI
    I want to have create, read, update, and delete functionality for a school.

#Background probably eliminates for most of the Given clauses in the scenarios - revisit
Background: Logged in as a super-user and using the small data set
	Given the SLI_SMALL dataset is loaded
	Given I am logged in using "jimi" "jimi"
	Given I have access to all schools

#### Happy Path 
Scenario: Create a new school
    When I navigate to POST "/schools/" 
        And format "application/json"
        And the short name is "FMWE"
		And the full name is "Farmington Woods Elementary School"
    Then I should receive a return code of 201
        And GET "/schools/" should return a code of 200
		And contains "Farmington Woods Elementary School"
                
Scenario: Read a school by id
    When I navigate to GET "/schools/152901001" 
        And format "application/json"
    Then I should receive a return code of 200
        And I should see the school "Apple Alternative Elementary School""
          

Scenario: Delete an existing school
    When I navigate to DELETE "/schools/152901001" 
        And format "application/json"
    Then I should receive a return code of 200
        And GET "/schools/152901001" should return a code of 404


Scenario: Update an existing school
	Given the address for "Apple Alternative Elementary School" is "123 Main Street"
    When I navigate to POST "/schools/152901001" 
        And format "application/json"
        And address is "56 Old Elm Road"
    Then I should receive a return code of 200
        And  GET "/schools/152901001" should be "Apple Alternative Elementary School"
 		And have and address of "56 Old Elm Road"
        
#### XML version
Scenario: Create a new school
    When I navigate to POST "/schools/" 
    	And format "application/xml"
        And the short name is "FMWE"
		And the full name is "Farmington Woods Elementary School"
    Then I should receive a return code of 201
        And GET "/schools/" should return a code of 200
		And contains "Farmington Woods Elementary School"
		

### Error handling
Scenario: Attempt to read a non-existing school
    When I navigate to GET "/schools/1" 
        And format "application/json"
    Then I should receive a return code of 404      

Scenario: Attempt to delete a non-existing school
    When I navigate to DELETE "/schools/1" 
        And format "application/json"
    Then I should receive a return code of 404      

Scenario: Update a non-existing school
    When I navigate to POST "/schools/1" 
        And format "application/json"
    Then I should receive a return code of 404      
    
Scenario: Fail when asking for an unsupported format "text/plain"
		And school 152901001 exists
    When I navigate to GET "/schools/152901001" 
        And format "text/plain"
    Then I should receive a return code of 406
    
Scenario: Fail if going to the wrong URI
		And school 152901001 exists
    When I navigate to GET "/schools/152901001" 
        And format "application/json"
    Then I should receive a return code of 404
    
