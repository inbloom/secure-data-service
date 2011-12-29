Feature: <US63> In order to manage schools
    As a client application using SLI
    I want to have create, read, update, and delete functionality for a school.

Background: Logged in as a super-user and using the small data set
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
    When I navigate to GET a school "Apple Alternative Elementary School"
    Then I should receive a return code of 200
       And I should see the school "Apple Alternative Elementary School"
       And I should see a phone number of "(785) 667-6006"

Scenario: Update an existing school
    Given format "application/json"
      And the full name is "Yellow Middle and High School"
    When I navigate to PUT a school "Yellow Middle and High School"
    Then I should receive a return code of 204
     When  I navigate to GET a school "Yellow Middle and High School"
 	 And I should see the school "Yellow Middle and High School"
 	 
Scenario: Delete an existing school
    Given format "application/json"
    When I navigate to DELETE a school "Delete Me Middle School"
    Then I should receive a return code of 204
     When I navigate to GET a school "Delete Me Middle School"
     Then I should receive a return code of 404
        
#### XML version
@wip
Scenario: Create a new school XML
    Given format "application/xml"
       And the short name is "SCTSX"
	  And the full name is "School Crud Test School XML"
	  And the website is "www.sctsx.edu"
    When I navigate to POST "/schools/" 
    Then I should receive a return code of 201
       And I should receive a ID for the newly created school
       
@wip
Scenario: Delete an existing school XML
    Given format "application/xml"
    When I DELETE the newly created school
    Then I should receive a return code of 204
     When I GET the newly created school by id
     Then I should receive a return code of 404
     
Scenario: School resource provides a link to it's student associations
	Given format "application/json"
	When I navigate to GET a school "Apple Alternative Elementary School"
	Then I should receive a return code of 200
	   And I should receive a link where rel is "self" and href ends with "/schools/eb3b8c35-f582-df23-e406-6947249a19f2"
	   And I should receive a link where rel is "getSchoolEnrollments" and href ends with "/student-school-associations/eb3b8c35-f582-df23-e406-6947249a19f2"
	  
### Error handling
Scenario: Attempt to read a non-existing school
    Given format "application/json"
    When I navigate to GET a school "that doesn't exist" 
    Then I should receive a return code of 404      

Scenario: Attempt to delete a non-existing school
    Given format "application/json"
    When I navigate to DELETE a school "that doesn't exist" 
    Then I should receive a return code of 404      

Scenario: Update a non-existing school
    Given format "application/json"
    When I attempt to update a school "that doesn't exist"
    Then I should receive a return code of 404      
    
Scenario: Fail when asking for an unsupported format "text/plain"
    Given format "text/plain"
    When I navigate to GET a school "Apple Alternative Elementary School"
    Then I should receive a return code of 406
    
Scenario: Fail if going to the wrong URI
	Given format "application/json"
	When I navigate to GET a school "using a wrong URI"
     Then I should receive a return code of 404

Scenario: Attempt to read the base resource with no GUID
	Given format "application/json"
	When I navigate to GET a school "with no GUID"
	Then I should receive a return code of 405
