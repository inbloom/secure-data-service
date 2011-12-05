Feature: <US63> In order to manage schools
    As a client application using SLI
    I want to have create, read, update, and delete functionality for a school.

Background: Logged in as a super-user and using the small data set
	Given the dummy data is loaded
	Given I am logged in using "demo" "demo1234"
	Given I have access to all schools

#### Happy Path 
Scenario: Create a new school JSON
    Given format "application/json"
       And the short name is "SCTS"
	  And the full name is "School Crud Test School"
	  And the website is "www.scts.edu"
    When I navigate to POST "/schools/" 
    Then I should receive a return code of 201
       And I should receive a ID for the newly created school

	  
Scenario: Read a school by id
    Given format "application/json"
    When I GET the newly created school by id
    Then I should receive a return code of 200
       And I should see the school "School Crud Test School"
       And I should see a website of "www.scts.edu"

Scenario: Update an existing school
    Given format "application/json"
      And the website is "www.scts.com"
    When I PUT/update the newly created school's website'
    Then I should receive a return code of 204
     When  I GET the newly created school by id
 	 And I should see a website of "www.scts.com"
 	 
Scenario: Delete an existing school
    Given format "application/json"
    When I DELETE the newly created school
    Then I should receive a return code of 204
     When I GET the newly created school by id
     Then I should receive a return code of 404
        
#### XML version
Scenario: Create a new school XML
    Given format "application/xml"
       And the short name is "SCTSX"
	  And the full name is "School Crud Test School XML"
	  And the website is "www.sctsx.edu"
    When I navigate to POST "/schools/" 
    Then I should receive a return code of 201
       And I should receive a ID for the newly created school
       
Scenario: Delete an existing school XML
    Given format "application/xml"
    When I DELETE the newly created school
    Then I should receive a return code of 204
     When I GET the newly created school by id
     Then I should receive a return code of 404
	  
### Error handling
Scenario: Attempt to read a non-existing school
    Given format "application/json"
    When I navigate to GET "/schools/1" 
    Then I should receive a return code of 404      

Scenario: Attempt to delete a non-existing school
    Given format "application/json"
    When I navigate to DELETE "/schools/1" 
    Then I should receive a return code of 404      

#bug below....creates new resource
Scenario: Update a non-existing school
    Given format "application/json"
    When I attempt to update a non-existing school "/schools/1"
    Then I should receive a return code of 404      
    
Scenario: Fail when asking for an unsupported format "text/plain"
    Given a known school exists
        And format "text/plain"
    When I navigate to GET to said school
    Then I should receive a return code of 406
    
Scenario: Fail if going to the wrong URI
	Given a known school exists
	    And format "application/json"
	When I navigate to GET to said school with "/school/"
     Then I should receive a return code of 404
    
