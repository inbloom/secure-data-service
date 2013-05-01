@RALLY_US5660

@wip
Feature: As an bulk extract user, I want to be able to get the state public entities

Scenario: As an bulk extract user, I want to be able to get the state public entities
  	Given the extraction zone is empty
    And the bulk extract files in the database are scrubbed
    And The bulk extract app has been approved for "Midgar-DAYBREAK" with client id "<clientId>"
    And The X509 cert "cert" has been installed in the trust store and aliased
    Then I trigger a bulk extract
    Then I should see "1" bulk extract SEA-public data file

Scenario: As an bulk extract user, I want to be able to get the state public entities
  	Given the extraction zone is empty
    And the bulk extract files in the database are scrubbed
    And The bulk extract app has been approved for "Midgar-DAYBREAK" with client id "<clientId>"
    And The X509 cert "cert" has been installed in the trust store and aliased
    Then I trigger a bulk extract
    Then I should see "4" bulk extract files

Scenario: Bulk extract should fail if there are more than 1 SEA in the tenant.
	Given I am using local data store
	And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "ExtendedSEA.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
  	And a batch job for file "ExtendStaffEdorgAssociation.zip" is completed in database
  	Given the extraction zone is empty
    And the bulk extract files in the database are scrubbed
    And The bulk extract app has been approved for "Midgar-DAYBREAK" with client id "<clientId>"
    And The X509 cert "cert" has been installed in the trust store and aliased
    Then I trigger a bulk extract
    Then I should see "0" bulk extract files
