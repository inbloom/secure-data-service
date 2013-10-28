@smoke @RALLY_US134 @RALLY_US5156
Feature: Admin delegation CRUD

#US5464
  Scenario: State administrator revoking access to edOrg data
    And I am logged in using "iladmin" "iladmin1234" to realm "SLI"
    Then I should revoke all app authorizations for district "IL"
    And I should receive a return code of 204
    And a security event "EdOrg data access has been revoked!" should be created for these targetEdOrgs
      | targetEdOrg                |
      | IL                         |


#US5464
  Scenario: State administrator granting access to edOrg data
    And I am logged in using "iladmin" "iladmin1234" to realm "SLI"
    Then I should grant all app authorizations for district "IL"
    And I should receive a return code of 204
    And a security event "Application granted access to EdOrg data!" should be created for these targetEdOrgs
      | targetEdOrg                |
      | IL                         |


#US5464
  Scenario: LEA administrator revoking access to edOrg data
    And I am logged in using "sunsetadmin" "sunsetadmin1234" to realm "SLI"
    Then I should revoke all app authorizations for district "IL-SUNSET"
    And I should receive a return code of 204
    And a security event "EdOrg data access has been revoked!" should be created for these targetEdOrgs ONLY
      | targetEdOrg                |
      | IL-SUNSET                  |


#US5464
  Scenario: LEA administrator granting access to edOrg data
    And I am logged in using "sunsetadmin" "sunsetadmin1234" to realm "SLI"
    Then I should grant all app authorizations for district "IL-SUNSET"
    And I should receive a return code of 204
    And a security event "Application granted access to EdOrg data!" should be created for these targetEdOrgs ONLY
      | targetEdOrg                |
      | IL-SUNSET                  |
