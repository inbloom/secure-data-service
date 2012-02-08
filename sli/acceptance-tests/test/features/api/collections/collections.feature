Feature: Efficient usage of API to retrieve and display student/assessment data in application


Background: 
	Given I am logged in using "demo" "demo1234"
	Given I have access to all students and schools

# @wip
Scenario: When using a application/vndslc.full+json media type, I get student objects instead of student links
	Given format "application/vnd.slc.full+json"
	When I navigate to GET "/student-school-associations/<'Apple Alternative Elementary School' ID>/targets"
	Then I should receive a collection of student objects
		And I should not receive a collection of student links.
		
# @wip
Scenario: When using a application/vndslc.full+json media type, I get association objects for the school instead of association links
	Given format "application/vnd.slc.full+json"
	When I navigate to GET "/student-school-associations/<'Apple Alternative Elementary School' ID>"
	Then I should receive a collection of student-school-association objects
		And I should not receive a collection of student-school-association links

# @wip
Scenario: When using a application/vndslc.full+json media type, I get school objects instead of school links
	Given format "application/vnd.slc.full+json"
	When I navigate to GET "/student-school-associations/<'Alfonso' ID>/targets"
	Then I should receive a collection of school objects
		And I should not receive a collection of school links

# @wip	
Scenario: When using a application/vndslc.full+json media type, I get association objects for the student instead of association links
	Given format "application/vnd.slc.full+json"
	When I navigate to GET "/student-school-associations/<'Alfonso' ID>"
	Then I should receive a collection of student-school-association objects
		And I should not receive a collection of student-school-association links
	

	
	