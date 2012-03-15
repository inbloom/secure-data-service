Feature: In order to provide base information
	As a client application using SLI
	I want to know what links are available to a user based on their user type.
	This means all associations should be returned as links when accessing the Home URI.

Scenario: Home URI returns valid links for user 'linda.kim'
  Given I am logged in using "linda.kim" "linda.kim1234"
    And I have access to all links
    And format "application/json"
  When I navigate to GET "/v1/home"
  Then I should receive a return code of 200
    And I should receive a link named "self" with URI "/v1/teachers/<'linda.kim' ID>"
    And I should receive a link named "getTeacherSectionAssociations" with URI "/v1/teachers/<'linda.kim' ID>/teacherSectionAssociations"
    And I should receive a link named "getSections" with URI "/v1/teachers/<'linda.kim' ID>/teacherSectionAssociations/sections"
    And I should receive a link named "getTeacherSchoolAssociations" with URI "/v1/teachers/<'linda.kim' ID>/teacherSchoolAssociations"
    And I should receive a link named "getSchools" with URI "/v1/teachers/<'linda.kim' ID>/teacherSchoolAssociations/schools"

Scenario: Home URI returns valid links for user 'demo'
  Given I am logged in using "demo" "demo1234"
    And I have access to all links
    And format "application/json"
  When I navigate to GET "/v1/home"
  Then I should receive a return code of 200
    And I should receive a link named "self" with URI "/v1/staff/<'demo' ID>"
    And I should receive a link named "getStaffEducationOrganizationAssociations" with URI "/v1/staff/<'demo' ID>/staffEducationOrganizationAssociations"
    And I should receive a link named "getEducationOrganizations" with URI "/v1/staff/<'demo' ID>/staffEducationOrganizationAssociations/educationOrganizations"

@wip
Scenario: Home URI returns valid links for user 'aggregator'
  Given I am logged in using "aggregator" "aggregator1234"
    And I have access to all links
    And format "application/json"
  When I navigate to GET "/v1/home"
  Then I should receive a return code of 200
    And I should receive a link named "self" with URI "/v1/staff/<'aggregator' ID>"
    And I should receive a link named "getStaffEducationOrganizationAssociations" with URI "/v1/staff/<'aggregator' ID>/staffEducationOrganizationAssociations"
    And I should receive a link named "getEducationOrganizations" with URI "/v1/staff/<'aggregator' ID>/staffEducationOrganizationAssociations/educationOrganizations"

@wip
Scenario: Home URI returns appropriate links for 'baduser'
  Given I am logged in using "baduser" "baduser1234"
    And I have access to all links
    And format "application/json"
  When I navigate to GET "/v1/home"
  Then I should receive a return code of 200
    And I should receive a link named "self" with URI "/v1/staff/<'baduser' ID>"
    And I should receive a link named "getStaffEducationOrganizationAssociations" with URI "/v1/staff/<'baduser' ID>/staffEducationOrganizationAssociations"
    And I should receive a link named "getEducationOrganizations" with URI "/v1/staff/<'baduser' ID>/staffEducationOrganizationAssociations/educationOrganizations"
  #When I navigate to GET "/v1/staff/<'baduser' ID>/staffEducationOrganizationAssociations"
  #Then I should receive a return code of 403
  #When I navigate to GET "/v1/staff/<'baduser' ID>/staffEducationOrganizationAssociations/educationOrganizations"
  #Then I should receive a return code of 403
