@wip
Feature: Security for Attendance CRUD 
As a product owner, I want to validate that my attendance entity is properly secured up to current SLI standards
	
Scenario: Showing attendance event list link in Student's available links
Given I am user "User" in IDP "realm"
And I am assigned the Educator role in my IDP
And I teach "student"
When I make an API call to get the student "student"
Then I receive a JSON response
And I should see a link to get the list of its attendance events in the response labeled "Get Attendance Events"

Scenario: Authorized user tries to hit the attendance events list URL directly
Given I am user "User" in IDP "realm"
And I am assigned the Educator role in my IDP
And I teach "student"
When I make an API call to get the student "student"'s attendance events list
Then I should receive a list containing the student "student"'s attendance events

Scenario: Unauthorized authenticated user tries to hit the attendance events list URL directly
Given I am user "User" in IDP "realm"
And I am assigned the Educator role in my IDP
And I do not teach "student"
When I make an API call to get the student "student"'s attendance events list
Then I get a message that I am not authorized

Scenario: Authorized user accessing a specific attendance event of a student
Given I am user "User" in IDP "realm"
And I am assigned the Educator role in my IDP
And I teach "student"
When I make an API call to get the specific attendance event "Blah"
Then I should receive a JSON object of the attendance event

Scenario: Unauthorized user accessing a specific attendance event of a student
Given I am user "User" in IDP "realm"
And I am assigned the Educator role in my IDP
And I do not teach "student"
When I make an API call to get the specific attendance event "Blah"
Then I get a message that I am not authorized
