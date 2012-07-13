Feature: Test Java REST SDK

Background:  

Given I have an open web browser
Given the Java SDK test app  is deployed on test app server 
Given I navigate to the Sample App REST client
When I select "Illinois Daybreak School District 4529" and click go
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page

Scenario: IT Admin Add a students		
		When I put "STUDENTS"  with Name "Monique L Johnson" 
		And Sex as "Female" and  
		And BirthDate as "1995-01-01"
		And Address is "817 Oakridge Farm Lane"
		Then the student is added
		
		When I read all the students
		Then I should find student "Monique L Johnson" in the student list
		
		When I send test request "create" to SDK CRUD test url
		Then I should receive response "succeed"
		
Scenario: IT Admin updates students
		When I update "STUDENTS"  "Monique L Johnson" address  to "2817 Oakridge Farm Lane" 
		Then the street address is updated
		
		When I read "STUDENTS" with name "Monique L Johnson"
		Then her address is "2817 Oakridge Farm Lane" 
		
		When I send test request "update" to SDK CRUD test url
		Then I should receive response "succeed"
		
Scenario: IT Admin deletes a students		
		When I delete "STUDENTS" with name "Monique L Johnson"
		Then the student is deleted
		
		When I read collection "STUDENTS"
		Then I student "Monique L Johnson" is not in the student list
		
		When I send test request "delete" to SDK CRUD test url
		Then I should receive response "succeed"
			
Scenario: IT Admin sees list of male teachers with decending order by first name
		When I query for "teachers" with "sex" as "Male" and sort by "firstName" and sortOrder "descending"
		Then I should see a descending list off teachers where first teachers firstName is "Stephen"	
		
		When I send test request "query" to SDK CRUD test url
		Then I should receive response "succeed"
		
