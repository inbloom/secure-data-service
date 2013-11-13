@smoke @RALLY_US5860
Feature: Admin delegation CRUD

  Scenario: State administrator granting access to edOrg data
    And I am logged in using "iladmin_adminApp" "iladmin1234" to realm "SLI"
    Then I should revoke all app authorizations for district "IL-SUNSET"
    Then I should revoke all app authorizations for district "IL"
    Then I should revoke all app authorizations for district "IL-LONGWOOD"
    And I should grant all app authorizations for district "IL-SUNSET"
    And I should grant all app authorizations for district "IL-LONGWOOD"
    Then I should grant all app authorizations for district "IL"
    And I should receive a return code of 204
    And a security event "Application granted access to EdOrg data!" should be created for these targetEdOrgs
      | targetEdOrg                |
      | IL                         |


  Scenario Outline: Read the application Authorization data and confirm all fields are populated correctly
    And I am logged in using "iladmin" "iladmin1234" to realm "SLI"
    When I navigate to GET applicationAuthorization with "<APP_ID>"
    And There is a correct entry in applicationAuthorization edorg array for district "<EDORG>" for the application "<APP_ID>"
    And The value of "lastAuthorizingUser" should be "<LAST_AUTHORIZED_USER>"
    And The value of "lastAuthorizingRealmEdorg" should be "<LAST_AUTHORIZED_REALM_EDORG>"
  Examples:
    |APP_ID                               | LAST_AUTHORIZED_USER | LAST_AUTHORIZED_REALM_EDORG  | EDORG         |
    |78f71c9a-8e37-0f86-8560-7783379d96f7 | iladmin              | nil                          |  IL           |
    |78f71c9a-8e37-0f86-8560-7783379d96f7 | iladmin              | nil                          |  IL-SUNSET    |
    |78f71c9a-8e37-0f86-8560-7783379d96f7 | iladmin              | nil                          |  IL-LONGWOOD  |






