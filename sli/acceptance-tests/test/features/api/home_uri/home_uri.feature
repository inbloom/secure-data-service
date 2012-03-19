Feature: In order to provide base information
	As a client application using SLI
	I want to know what links are available to a user based on their user type.
	This means all associations should be returned as links when accessing the Home URI.

Scenario: Home URI returns valid links for user 'linda.kim'
  Given I am logged in using "linda.kim" "linda.kim1234"
    And I have access to all links
    And format "application/json"
  When I navigate to GET "/home"
  Then I should receive a return code of 200
    And I should receive a link named "self" with URI "/teachers/<'linda.kim' ID>"
    And I should receive a link named "getTeacherSectionAssociations" with URI "/teacher-section-associations/<'linda.kim' ID>"
    And I should receive a link named "getSections" with URI "/teacher-section-associations/<'linda.kim' ID>/targets"
    And I should receive a link named "getTeacherSchoolAssociations" with URI "/teacher-school-associations/<'linda.kim' ID>"
    And I should receive a link named "getSchools" with URI "/teacher-school-associations/<'linda.kim' ID>/targets"

Scenario: Home URI returns valid links for user 'demo'
  Given I am logged in using "demo" "demo1234"
    And I have access to all links
    And format "application/json"
  When I navigate to GET "/home"
  Then I should receive a return code of 200
    And I should receive a link named "self" with URI "/staff/<'demo' ID>"
    And I should receive a link named "getStaffEducationOrganizationAssociations" with URI "/staff-educationOrganization-associations/<'demo' ID>"
    And I should receive a link named "getEducationOrganizations" with URI "/staff-educationOrganization-associations/<'demo' ID>/targets"

@wip
Scenario: Home URI returns valid links for user 'aggregator'
  Given I am logged in using "aggregator" "aggregator1234"
    And I have access to all links
    And format "application/json"
  When I navigate to GET "/home"
  Then I should receive a return code of 200
    And I should receive a link named "self" with URI "/staff/<'aggregator' ID>"
    And I should receive a link named "getStaffEducationOrganizationAssociations" with URI "/staff-educationOrganization-associations/<'aggregator' ID>"
    And I should receive a link named "getEducationOrganizations" with URI "/staff-educationOrganization-associations/<'aggregator' ID>/targets"

@wip
Scenario: Home URI returns appropriate links for 'baduser'
  Given I am logged in using "baduser" "baduser1234"
    And I have access to all links
    And format "application/json"
  When I navigate to GET "/home"
  Then I should receive a return code of 200
    And I should receive a link named "self" with URI "/staff/<'baduser' ID>"
    And I should receive a link named "getStaffEducationOrganizationAssociations" with URI "/staff-educationOrganization-associations/<'baduser' ID>"
    And I should receive a link named "getEducationOrganizations" with URI "/staff-educationOrganization-associations/<'baduser' ID>/targets"
  When I navigate to GET "/staff-educationOrganization-associations/<'baduser' ID>"
  Then I should receive a return code of 403
  When I navigate to GET "/staff-educationOrganization-associations/<'baduser' ID>/targets"
  Then I should receive a return code of 403
