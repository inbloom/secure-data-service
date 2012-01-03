Feature: In order to provide base information
	As a client application using SLI
	I want to know what links are available to a user based on their user type.
	This means all associations should be returned as links when accessing the HOME URI.

Background: Logged in as a super-user and using the small data set
	Given I am logged in using "demo" "demo1234"

Scenario: MOCK Home URI returns a valid response
	Given format "application/json"
		And mock student ID <mock ID>
	When I navigate to GET <home URI>
	Then I should receive a return code of 200
		And I should receive a link where rel is "self" and href ends with "/students/" and appropriate ID
		And I should receive a link where rel is "getStudentEnrollments" and href ends with "/student-school-associations/" and appropriate ID

Scenario: Previous functionality which is now shared logic continues to work as expected
	Given format "application/json"
		And mock student ID <mock ID>
	When I navigate to GET <student by ID>
	Then I should receive a return code of 200
		And I should receive a link where rel is "self" and href ends with "/students/" and appropriate ID
		And I should receive a link where rel is "getStudentEnrollments" and href ends with "/student-school-associations/" and appropriate ID

