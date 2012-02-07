Feature: Efficient usage of API to retrieve and display student/assement data in application


Background: 
	Given I am logged in using "demo" "demo1234"
	
Scenario: When using a ____ media type, I get student objects instead of student links
	Given format "application/vnd.slc.full+json"
	When I navigate to GET "/student-school-associations/<school GUID>/targets"
	Then I should receive a collection of student objects
		And I should not receive a collection of student links
		
Scenario: When using a ____ media type, I get association objects for the school instead of association links
	Given format "application/vnd.slc.full+json"
	When I navigate to GET "/student-school-associations/<school GUID>"
	Then I should receive a collection of student-school-association objects
		And I should not receive a collection of student-school-association links
	
Scenario: When using a ____ media type, I get school objects instead of school links
	Given format "application/vnd.slc.full+json"
	When I navigate to GET "/student-school-associations/<student GUID>/targets"
	Then I should receive a collection of student objects
		And I should not receive a collection of student links
		
Scenario: When using a ____ media type, I get association objects for the student instead of association links
	Given format "application/vnd.slc.full+json"
	When I navigate to GET "/student-school-associations/<student GUID>"
	Then I should receive a collection of student-school-association objects
		And I should not receive a collection of student-school-association links
	

	
	