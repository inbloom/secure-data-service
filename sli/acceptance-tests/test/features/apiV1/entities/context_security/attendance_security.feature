Feature: Security for Attendance CRUD
  As an API user with an educator role
  I want to only see attendance events to which I am allowed
  In order to ensure appropriate security of attendance information

  Background:
    Given I have a session as a middle school-level Educator

Scenario: An educator wants to have a link to get attendance events for a current student she teaches
When I make an API call to get the student "Marvin Miller"
Then I receive a JSON response
And I should see a link to get the list of its attendance events in the response labeled "attendances"

Scenario: Authorized user tries to hit the attendance events list URL directly
When I make an API call to get the student "Marvin Miller"'s attendance events list
Then I should receive a list containing the student "Marvin Miller"'s attendance events

Scenario: Unauthorized authenticated user tries to hit the attendance events list URL directly
When I make an API call to get the student "Delilah D. Sims"'s attendance events list
Then I get a message that I am not authorized

Scenario: Authorized user accessing a specific school year attendance events of a student
When I make an API call to get the specific attendance document "Marvin Miller Attendance events"
Then I should receive a JSON object of the attendance event

Scenario: Unauthorized user accessing a specific attendance event of a student
When I make an API call to get the specific attendance document "Delilah D. Sims Attendance events"
Then I get a message that I am not authorized
