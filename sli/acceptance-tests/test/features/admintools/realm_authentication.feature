@no_ingestion_hooks
Feature: Multiple realms with the same IDP should be able to authenticate users.

  Background:
    Given I am a valid tenant-level realm administrator
      And I have an open browser
      And I am managing my realms
      And I add a new realm with the same IDP as an existing realm

  Scenario: A user logs into the realm with the same IDP endpoint as her realm
    Given I am a valid tenant-level IT administrator
      And I have an open browser
     When I attempt to log into the new realm
     Then I should not see an error message indicating "Invalid User Name or password"