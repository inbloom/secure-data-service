@wip
Feature: Test Java SDK 

Background:  Given I am logged in using "demo" "demo1234" to realm "Daybreak Central High" 

Scenario: IT Admin Add a students		
		When I put "STUDENTS"  with Name "Monique L Johnson" 
		And Sex as "Female" and  
		And BirthDate as "1995-01-01"
		And Address is "817 Oakridge Farm Lane"
		Then the student is added
		
		When I read all the students
		Then I should find student "Monique L Johnson" in the student list
		
Scenario: IT Admin updates students
		When I update "STUDENTS"  "Monique L Johnson" address  to "2817 Oakridge Farm Lane" 
		Then the street address is updated
		
		When I read "STUDENTS" with name "Monique L Johnson"
		Then her address is "2817 Oakridge Farm Lane" 
		
Scenario: IT Admin deletes a students		
		When I delete "STUDENTS" with name "Monique L Johnson"
		Then the student is deleted
		
		When I read collection "STUDENTS"
		Then I student "Monique L Johnson" is not in the student list
			
Scenario: IT Admin sees decending ordered list of spanish speaking students in my class
		When I query for "STUDENTS" with "Languages" as "Spanish" and sort by "Descending"
		Then I should see a descending list of students where "Languages" is "Spanish"	