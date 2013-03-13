@RALLY_US209
Feature: In order to provide base information
	As a client application using SLI
	I want to know what links are available to a user based on their user type.
	This means all associations should be returned as links when accessing the Home URI.

Scenario: Home URI returns valid links for user 'lindakim'
  Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
    And format "application/json"
  When I navigate to GET "/v1/home"
  Then I should receive a return code of 200
    And I should receive a link named "self" with URI "/v1.2/teachers/<'linda.kim' ID>"
    And I should receive a link named "getTeacherSectionAssociations" with URI "/v1.2/teachers/<'linda.kim' ID>/teacherSectionAssociations"
    And I should receive a link named "getSections" with URI "/v1.2/teachers/<'linda.kim' ID>/teacherSectionAssociations/sections"
    And I should receive a link named "getTeacherSchoolAssociations" with URI "/v1.2/teachers/<'linda.kim' ID>/teacherSchoolAssociations"
    And I should receive a link named "getSchools" with URI "/v1.2/teachers/<'linda.kim' ID>/teacherSchoolAssociations/schools"

Scenario: Home URI returns valid links for user 'akopel'
  Given I am logged in using "akopel" "akopel1234" to realm "IL"
    And format "application/json"
  When I navigate to GET "/v1/home"
  Then I should receive a return code of 200
    And I should receive a link named "self" with URI "/v1.2/staff/<'amy kopel' ID>"
    And I should receive a link named "getStaffEducationOrgAssignmentAssociations" with URI "/v1.2/staff/<'amy kopel' ID>/staffEducationOrgAssignmentAssociations"
    And I should receive a link named "getEducationOrganizations" with URI "/v1.2/staff/<'amy kopel' ID>/staffEducationOrgAssignmentAssociations/educationOrganizations"