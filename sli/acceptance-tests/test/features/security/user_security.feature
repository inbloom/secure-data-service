@smoke
Feature: Users who resolve incorrectly should not have access to anything except admin data

Scenario: An invalid user logs in and cannot see any real data
	Given I am logged in using "operator" "operator1234" to realm "SLI"
		And my role is SLI Administrator
	When I make an API call to access teachers
	Then I should not see any teacher data

Scenario: An invalid user logs in and can see admin data
	Given I am logged in using "operator" "operator1234" to realm "SLI"
		And my role is SLI Administrator
	When I make an API call to access applications
	Then I do not get a 403