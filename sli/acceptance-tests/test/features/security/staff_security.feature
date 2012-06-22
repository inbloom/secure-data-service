
Feature: As a staff I want to demonstrate that all other staff I am associated with has secured data

Scenario: Teacher accesses another teacher in same school
	Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
		And my role is Educator
	When I make an API call to access teachers
	Then I get a response
	And the response does not includes the protected fields


Scenario: Leader accesses teacher in same school and sees restricted fields
	Given I am logged in using "sbantu" "sbantu1234" to realm "IL"
		And my role is Leader
	When I make an API call to access teachers
	Then I get a response
	And the response includes the protected fields


Scenario: Teacher accesses Leader in same school
	Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
		And my role is Educator
	When I make an API call to access staff
	Then I get a response
	And the response does not includes the protected fields

Scenario: Teacher accesses themselves and sees their restricted fields
	Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
		And my role is Educator
	When I make an API call to access myself
	Then I get a response
	Then I should see my restricted information
