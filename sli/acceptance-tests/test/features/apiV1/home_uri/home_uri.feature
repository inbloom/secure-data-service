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
    And I should receive a link named "self" with URI "/v1.5/teachers/<'linda.kim' ID>"
    And I should receive a link named "getTeacherSectionAssociations" with URI "/v1.5/teachers/<'linda.kim' ID>/teacherSectionAssociations"
    And I should receive a link named "getSections" with URI "/v1.5/teachers/<'linda.kim' ID>/teacherSectionAssociations/sections"
    And I should receive a link named "getTeacherSchoolAssociations" with URI "/v1.5/teachers/<'linda.kim' ID>/teacherSchoolAssociations"
    And I should receive a link named "getSchools" with URI "/v1.5/teachers/<'linda.kim' ID>/teacherSchoolAssociations/schools"

Scenario: Home URI returns valid links for user 'akopel'
  Given I am logged in using "akopel" "akopel1234" to realm "IL"
    And format "application/json"
  When I navigate to GET "/v1/home"
  Then I should receive a return code of 200
    And I should receive a link named "self" with URI "/v1.5/staff/<'amy kopel' ID>"
    And I should receive a link named "getStaffEducationOrgAssignmentAssociations" with URI "/v1.5/staff/<'amy kopel' ID>/staffEducationOrgAssignmentAssociations"
    And I should receive a link named "getEducationOrganizations" with URI "/v1.5/staff/<'amy kopel' ID>/staffEducationOrgAssignmentAssociations/educationOrganizations"

@RALLY_US5826
Scenario: Home URI logs a security event for an unauthenticated user
    When the sli securityEvent collection is empty
	And I GET the url "/v1/home" using a blank cookie
	Then I should receive a "401" response
	And I check to find if record is in sli db collection:
        | collectionName  | expectedRecordCount | searchParameter         | searchValue                                            | searchType |
        | securityEvent   | 1                   | body.appId              | UNKNOWN                                                | string     |
        | securityEvent   | 1                   | body.className          | org.slc.sli.api.resources.util.ResourceUtil            | string     |
        # user and targetEdOrgs are not known since no session was established
    And "1" security event matching "Access Denied: Login Required" should be in the sli db
    And "1" security event with field "body.actionUri" matching "http.*/api/rest/v.*/home" should be in the sli db

@RALLY_US5826
Scenario: Home URI logs a security event for user that has no entity in the db
  Given I am logged in using "fakerealmadmin" "fakerealmadmin1234" to realm "SLI"
    And format "application/json"
    And the sli securityEvent collection is empty
  When I navigate to GET "/v1/home"
	Then I should receive a "401" response
	And I check to find if record is in sli db collection:
        | collectionName  | expectedRecordCount | searchParameter         | searchValue                                            | searchType |
        | securityEvent   | 1                   | body.appId              | ke9Dgpo3uI                                             | string     |
        | securityEvent   | 1                   | body.className          | org.slc.sli.api.resources.v1.HomeResource              | string     |
        # user and targetEdOrgs are not known since no session was established
    And "1" security event matching "Access Denied: No entity mapping found for user" should be in the sli db
    And "1" security event with field "body.actionUri" matching "http.*/api/rest/v.*/home" should be in the sli db
