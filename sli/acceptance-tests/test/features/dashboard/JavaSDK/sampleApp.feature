Feature: List Students in SampleApp

As a SEA/LEA user, I want to be able to see list of students.

Background:
    Given I have an open web browser
    Given the sampleApp is deployed on sampleApp server
	
Scenario: Teacher sees list of students
		When I navigate to the sampleApp home page
		Then I should be redirected to the Realm page
		When I select "Sunset School District 4526" and click go
		When I login as "cgray" "cgray1234"
		When I go to List of Students
		Then the page should include a table with header "Student"
		And I should see student "Swanson, Alec" in the student list
		
@wip
Scenario: Teacher sees list of students from canonical ds
		When I navigate to the sampleApp home page
		Then I should be redirected to the Realm page
		When I select "Daybreak Central High" and click go
		When I login as "cgray" "cgray1234"
		When I go to List of Students
		Then the page should include a table with header "Student"
		And I should see student "Dominic, R Berdan" in the student list
				
@wip	
Scenario: IT Admin updates students
		When I navigate to the sampleApp home page
		Then I should be redirected to the Realm page
		When I select "Daybreak Central High" and click go
		When I login as "demo" "demo1234"
		When I go to List of Students
		Then the page should include a table with header "Student"
		And I should see student "Dominic, R Berdan" in the student list
		When I click on student "Dominic, R Berdan" I am directed to a form which displays student address in a text box
		Then I update Address.StreetNumberName to "2817 Oakridge Farm Lane" and click "OK"
		
		When I go to List of Students
		Then I should see student "Dominic, R Berdan" in the student list
		When I click on student "Dominic, R Berdan" I am directed to a form which displays student address in a text box
		And I see Address.StreetNumberName as "2817 Oakridge Farm Lane"
		
@wip
Scenario: IT Admin deletes a students		
		When I navigate to the sampleApp home page
		Then I should be redirected to the Realm page
		When I select "Daybreak Central High" and click go
		When I login as "demo" "demo1234"
		When I go to List of Students
		Then the page should include a table with header "Student"
		And I should see student "Dominic, R Berdan" in the student list
		When I click on student "Dominic, R Berdan" I am directed to a form that has a button Delete Student 
		Then I click on Delete Student 
		
		When I go to List of Students
		Then I should not see student "Dominic, R Berdan" in the student list

@wip		
Scenario: IT Admin Add a students		
		When I navigate to the sampleApp home page
		Then I should be redirected to the Realm page
		When I select "Daybreak Central High" and click go
		When I login as "demo" "demo1234"
		When I go to List of Students
		Then the page should include a button Add Student
		
		When I click on Add Student
		Then I get a form where I can add Student "Monique L Johnson" with Sex as "Female" and  BirthDate as "1995-01-01"
		And I click OK
		
		When I go to List of Students
		Then I should see student "Monique L Johnson" in the student list
		
@wip		
Scenario: Teacher sees decending ordered list of spanish speaking students in my class
		When I navigate to the sampleApp home page
		Then I should be redirected to the Realm page
		When I select "Daybreak Central High" and click go
		When I login as "cgray" "cgray1234"
		When I select checkbox "Ordered List of Students"
		And I select checkbox as "Spanish"
		And I click OK
		Then the page should include a table with header "Student"
		And I should see a descending list of students where "Languages" is "Spanish"	
