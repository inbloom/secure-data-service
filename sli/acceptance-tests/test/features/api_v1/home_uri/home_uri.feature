Feature: In order to provide base information
	As a client application using SLI
	I want to know what links are available to a user based on their user type.
	This means all associations should be returned as links when accessing the Home URI.

Scenario: Home URI returns valid links for user 'educator'
  Given I am logged in using "educator" "educator1234"
    And I have access to all links
    And format "application/json"
  When I navigate to GET "/v1/home"
  Then I should receive a return code of 200
    And I should receive a link named "self" with URI "/teachers/<'educator' ID>"
    And I should receive a link named "getTeacherSectionAssociations" with URI "/v1/teachers/<'educator' ID>/teacher-section-associations"
    And I should receive a link named "getSections" with URI "/v1/teachers/<'educator' ID>/teacher-section-associations/sections"
    And I should receive a link named "getTeacherSchoolAssociations" with URI "/v1/teachers/<'educator' ID>/teacher-school-associations"
    And I should receive a link named "getSchools" with URI "/v1/teachers/<'educator' ID>/teacher-school-associations/schools"

Scenario: Home URI returns valid links for user 'aggregator'
  Given I am logged in using "aggregator" "aggregator1234"
    And I have access to all links
    And format "application/json"
  When I navigate to GET "/v1/home"
  Then I should receive a return code of 200
    And I should receive a link named "self" with URI "/staff/<'aggregator' ID>"
    And I should receive a link named "getEducationOrganizationsAssigned" with URI "/v1/staff/<'aggregator' ID>/staff-educationOrganization-associations"
    And I should receive a link named "getEducationOrganizations" with URI "/v1/staff/<'aggregator' ID>/staff-educationOrganization-associations/educationOrganizations"

Scenario: Home URI returns valid links for user 'administrator'
  Given I am logged in using "administrator" "administrator1234"
    And I have access to all links
    And format "application/json"
  When I navigate to GET "/v1/home"
  Then I should receive a return code of 200
    And I should receive a link named "self" with URI "/staff/<'administrator' ID>"
    And I should receive a link named "getEducationOrganizationsAssigned" with URI "/v1/staff/<'administrator' ID>/staff-educationOrganization-associations"
    And I should receive a link named "getEducationOrganizations" with URI "/v1/staff/<'administrator' ID>/staff-educationOrganization-associations/educationOrganizations"

Scenario: Home URI returns valid links for user 'leader'
  Given I am logged in using "leader" "leader1234"
    And I have access to all links
    And format "application/json"
  When I navigate to GET "/v1/home"
  Then I should receive a return code of 200
    And I should receive a link named "self" with URI "/staff/<'leader' ID>"
    And I should receive a link named "getEducationOrganizationsAssigned" with URI "/v1/staff/<'leader' ID>/staff-educationOrganization-associations"
    And I should receive a link named "getEducationOrganizations" with URI "/v1/staff/<'leader' ID>/staff-educationOrganization-associations/educationOrganizations"

Scenario: Home URI returns appropriate links for 'baduser'
  Given I am logged in using "baduser" "baduser1234"
    And I have access to all links
    And format "application/json"
  When I navigate to GET "/v1/home"
  Then I should receive a return code of 200
    And I should receive a link named "self" with URI "/staff/<'baduser' ID>"
    And I should receive a link named "getEducationOrganizationsAssigned" with URI "/v1/staff/<'baduser' ID>/staff-educationOrganization-associations"
    And I should receive a link named "getEducationOrganizations" with URI "/v1/staff/<'baduser' ID>/staff-educationOrganization-associations/educationOrganizations"
  #When I navigate to GET "/v1/staff/<'baduser' ID>/staff-educationOrganization-associations"
  #Then I should receive a return code of 403
  #When I navigate to GET "/v1/staff/<'baduser' ID>/staff-educationOrganization-associations/educationOrganizations"
  #Then I should receive a return code of 403
