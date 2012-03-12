@wip
Feature: Security for Parent CRUD 
As a product owner, I want to validate that my parent entity is properly secured up to current SLI standards

Scenario: Showing parent list link in Student's available links
Given I am user "linda.kim" in IDP "SLI"
And I am assigned the Educator role in my IDP
And I teach the student "Marvin Miller"
When I make an API call to get the student "Marvin Miller"
Then I receive a JSON response
And I should see a link to get the list of its parents in the response labeled "parents"

Scenario: Authorized user tries to hit the parent list URL directly
Given I am user "linda.kim" in IDP "SLI"
And I am assigned the Educator role in my IDP
And I teach the student "Marvin Miller"
When I make an API call to get the student "Marvin Miller"'s list of parents
Then I should receive a list containing the student "Marvin Miller"'s parents

Scenario: Unauthorized authenticated user tries to hit the parent list URL directly
Given I am user "linda.kim" in IDP "SLI"
And I am assigned the Educator role in my IDP
And I do not teach the student "Delilah D. Sims"
When I make an API call to get the student "Delilah D. Sims"'s list of parents
Then I get a message that I am not authorized

Scenario: Authorized user accessing a specific parent-student association of a student
Given I am user "linda.kim" in IDP "SLI"
And I am assigned the Educator role in my IDP
And I teach the student "Marvin Miller"
When I make an API call to get the student-parent association "Marvin Miller to Mr. Miller"
Then I should receive a JSON object of the parent

Scenario: Authorized user accessing a specific parent entity directly
Given I am user "linda.kim" in IDP "SLI"
And I am assigned the Educator role in my IDP
And I teach the student "Marvin Miller"
When I make an API call to get the parent "Mr. Miller"
Then I should receive a JSON object of the parent

Scenario: Unauthorized user accessing a specific parent-student association of a student
Given I am user "linda.kim" in IDP "SLI"
And I am assigned the Educator role in my IDP
And I do not teach the student "Delilah D. Sims"
When I make an API call to get the student-parent association "Delilah D. Sims to Mrs. Sims"
Then I get a message that I am not authorized

Scenario: Unauthorized user accessing a specific parent entity directly
Given I am user "linda.kim" in IDP "SLI"
And I am assigned the Educator role in my IDP
And I do not teach the student "Delilah D. Sims"
When I make an API call to get the parent "Mrs. Sims"
Then I get a message that I am not authorized
