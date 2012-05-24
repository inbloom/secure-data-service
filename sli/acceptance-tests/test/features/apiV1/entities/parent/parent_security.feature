@security
@RALLY_US209
Feature: Security for Parent CRUD
  As a product owner, I want to validate that my parent entity is properly secured up to current SLI standards

  Scenario: Showing parent list link in Student's available links
    Given I am user "linda.kim" in IDP "IL"
    And I am assigned the Educator role in my IDP
    And I teach the student "Marvin Miller"
    When I make an API call to get the student "Marvin Miller"
    Then I receive a JSON response
    And I should see a link "getParents" to get the list of parents
    And I should see a link "getStudentParentAssociations" to get the list of parent associations


  Scenario: Authorized user tries to hit the parent list URL directly
    Given I am user "linda.kim" in IDP "IL"
    And I am assigned the Educator role in my IDP
    And I teach the student "Marvin Miller"
    When I make an API call to get the student "Marvin Miller"'s list of parents
    Then I should receive a list containing the student "Marvin Miller"'s parents

  Scenario: Unauthorized authenticated user tries to hit the parent list URL directly
    Given I am user "linda.kim" in IDP "IL"
    And I am assigned the Educator role in my IDP
    And I do not teach the student "Delilah D. Sims"
    When I make an API call to get the student "Delilah D. Sims"'s list of parents
    Then I get a message that I am not authorized

  Scenario: Authorized user accessing a specific parent-student association of a student
    Given I am user "linda.kim" in IDP "IL"
    And I am assigned the Educator role in my IDP
    And I teach the student "Marvin Miller"
    When I make an API call to get the studentParentAssociation "Marvin Miller to Mr. Miller"
    Then I should receive a JSON object of the parent
    And in that object there should be a link to the parent "Mr. Miller"
    And in that object there should be a link to the student "Marvin Miller"

  Scenario: Authorized user accessing a specific parent entity directly
    Given I am user "linda.kim" in IDP "IL"
    And I am assigned the Educator role in my IDP
    And I teach the student "Marvin Miller"
    When I make an API call to get the parent "Mr. Miller"
    Then I should receive a JSON object of the parent

  Scenario: Unauthorized user accessing a specific parent-student association of a student
    Given I am user "linda.kim" in IDP "IL"
    And I am assigned the Educator role in my IDP
    And I do not teach the student "Delilah D. Sims"
    When I make an API call to get the studentParentAssociation "Delilah D. Sims to Mrs. Sims"
    Then I get a message that I am not authorized

  Scenario: Unauthorized user accessing a specific parent entity directly
    Given I am user "linda.kim" in IDP "IL"
    And I am assigned the Educator role in my IDP
    And I do not teach the student "Delilah D. Sims"
    When I make an API call to get the parent "Mrs. Sims"
    Then I get a message that I am not authorized
