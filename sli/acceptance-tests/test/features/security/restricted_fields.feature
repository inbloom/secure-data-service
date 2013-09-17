@smoke @RALLY_US209 @RALLY_US210
Feature: As an SLI application I want to restrict user's access to restricted data
  That means the user should not be able to access/deduce any information about those data

  Background:
    Given format "application/json"

  Scenario: Linda Kim querying on the restricted fields
    Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
    When I make an API call to get "<'MARVIN MILLER'>"
    Then I should receive a return code of 200
    And I should see that "entityType" is "student" in the JSON response
    And I should see that "economicDisadvantaged" is nil in the JSON response
    And I should see that "schoolFoodServicesEligibility" is nil in the JSON response
    Given parameter "economicDisadvantaged" is "false"
    And I make an API call to get "<'MARVIN MILLER'>"
    Then I should receive a return code of 400
    Given parameter "schoolFoodServicesEligibility" is "Reduced price"
    When I make an API call to get "<'MARVIN MILLER'>"
    Then I should receive a return code of 400
    Given parameter "economicDisadvantaged" is "true"
    And I make an API call to get "<'MARVIN MILLER'>"
    Then I should receive a return code of 400

  Scenario: Rick Rogers querying on the restricted fields
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And parameter "economicDisadvantaged" is "true"
    And parameter "schoolFoodServicesEligibility" is "Free"
    When I make an API call to get "<'MARVIN MILLER'>"
    Then I should receive a return code of 200
    And I should see that "entityType" is "student" in the JSON response
    And I should see that "economicDisadvantaged" is "true" in the JSON response
    And I should see that "schoolFoodServicesEligibility" is "Free" in the JSON response

  Scenario: Rick Rogers querying on the restricted fields - incorrect values
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And parameter "economicDisadvantaged" is "false"
    When I make an API call to get "<'MARVIN MILLER'>"
    Then I should receive a return code of 404
    Given parameter "schoolFoodServicesEligibility" is "Reduced price"
    When I make an API call to get "<'MARVIN MILLER'>"
    Then I should receive a return code of 404

  Scenario: User with READ_RESTRICTED and not WRITE_RESTRICTED querying on the restricted fields
    Given I am logged in using "rrogerslimitedwrite" "rrogerslimitedwrite1234" to realm "IL"
    And parameter "economicDisadvantaged" is "true"
    And parameter "schoolFoodServicesEligibility" is "Free"
    When I make an API call to get "<'MARVIN MILLER'>"
    Then I should receive a return code of 200
    And I should see that "entityType" is "student" in the JSON response
    And I should see that "economicDisadvantaged" is "true" in the JSON response
    And I should see that "schoolFoodServicesEligibility" is "Free" in the JSON response

  Scenario: User with READ_RESTRICTED and not WRITE_RESTRICTED updating a restricted field
    Given I am logged in using "rrogerslimitedwrite" "rrogerslimitedwrite1234" to realm "IL"
    And parameter "economicDisadvantaged" is "true"
    And parameter "schoolFoodServicesEligibility" is "Free"
    When I make an API call to get "<'MARVIN MILLER'>"
    Then I should receive a return code of 200
    When I create request data out of the response
    And I set parameter "economicDisadvantaged" to "false"
    And I make an API call to update the student "<'MARVIN MILLER'>"
    Then I should receive a return code of 403

  Scenario: Charles Gray querying on the restricted fields
    Given I am logged in using "cgray" "cgray1234" to realm "IL"
    When I make an API call to get "teachers" "<'SHELIA TWEED'>"
    Then I should receive a return code of 200
    And I should see that "entityType" is "teacher" in the JSON response
    And I should see that "highlyQualifiedTeacher" is nil in the JSON response
    And I should see that "teacherUniqueStateId" is nil in the JSON response
    Given parameter "highlyQualifiedTeacher" is "true"
    When I make an API call to get "teachers" "<'SHELIA TWEED'>"
    Then I should receive a return code of 400
    Given parameter "teacherUniqueStateId" is "daybreak00001"
    When I make an API call to get "teachers" "<'SHELIA TWEED'>"
    Then I should receive a return code of 400

  Scenario: Charles Gray querying on the restricted fields for staff
    Given I am logged in using "cgray" "cgray1234" to realm "IL"
    When I make an API call to get "staff" "<'SHELIA TWEED'>"
    Then I should receive a return code of 200
    And I should see that "highlyQualifiedTeacher" is nil in the JSON response
    And I should see that "teacherUniqueStateId" is nil in the JSON response

  #The following scenario seems incorrect.. The return code should be 200 for all these cases.. DE2942
  Scenario: Charles Gray Admin querying on the restricted fields
    Given I am logged in using "cgrayadmin" "cgrayadmin1234" to realm "IL"
    When I make an API call to get "teachers" "<'SHELIA TWEED'>"
    Then I should receive a return code of 200
    And I should see that "entityType" is "teacher" in the JSON response
    And I should see that "highlyQualifiedTeacher" is "true" in the JSON response
    And I should see that "teacherUniqueStateId" is "daybreak00001" in the JSON response
    Given parameter "highlyQualifiedTeacher" is "true"
    When I make an API call to get "teachers" "<'SHELIA TWEED'>"
    Then I should receive a return code of 404
    Given parameter "teacherUniqueStateId" is "daybreak00001"
    When I make an API call to get "teachers" "<'SHELIA TWEED'>"
    Then I should receive a return code of 404

  Scenario: Charles Gray Admin querying on the restricted fields on staff
    Given I am logged in using "cgrayadmin" "cgrayadmin1234" to realm "IL"
    When I make an API call to get "staff" "<'SHELIA TWEED'>"
    Then I should receive a return code of 200
    And I should see that "highlyQualifiedTeacher" is "true" in the JSON response
    And I should see that "teacherUniqueStateId" is "daybreak00001" in the JSON response

  @sort_deny
  Scenario: Charles Gray cannot sort on restricted fields
    Given I am logged in using "cgray" "cgray1234" to realm "IL"
    And the sli securityEvent collection is empty
    And I navigate to GET "/v1/sections/47b5adbf-6fd0-4f07-ba5e-39612da2e234_id/studentSectionAssociations/students?sortBy=schoolFoodServicesEligibility"
    Then I should receive a return code of 403
    And I check to find if record is in sli db collection:
        | collectionName  | expectedRecordCount | searchParameter         | searchValue                                            | searchType |
        | securityEvent   | 1                   | body.tenantId           | Midgar                                                 | string     |
        | securityEvent   | 1                   | body.appId              | ke9Dgpo3uI                                             | string     |
        | securityEvent   | 1                   | body.className          | org.slc.sli.api.security.roles.RightAccessValidator    | string     |
        | securityEvent   | 1                   | body.userEdOrg          | IL-SUNSET                                              | string     |
        | securityEvent   | 1                   | body.targetEdOrgList    | Sunset Central High School                              | string     |
    And "1" security event with field "body.actionUri" matching "http.*/api/rest/v.*/sections/47b5adbf-6fd0-4f07-ba5e-39612da2e234_id/studentSectionAssociations/students" should be in the sli db
    And "1" security event matching "Access Denied:Cannot search on restricted field schoolFoodServicesEligibility" should be in the sli db
