@RALLY_US5660


Feature: As an bulk extract user, I want to be able to get the state public entities

Scenario: As an bulk extract user, I want to initialize my database with test data.
    Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "SEAPublicDataSet.zip" file as the payload of the ingestion job
    And all collections are empty
    When zip file is scp to ingestion landing zone
    And a batch job for file "SEAPublicDataSet.zip" is completed in database
    And a batch job log has been created
    When zip file is scp to ingestion landing zone
    And a batch job for file "SEAPublicDataSet.zip" is completed in database
    Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | course                                   |                  4|
     | courseOffering                           |                  4|
     | educationOrganization                    |                  4|
     | gradingPeriod                            |                  6|
     | graduationPlan                           |                  4|
     | session                                  |                  4|

Scenario: As an bulk extract user, I want to be able to get the state public entities
  	Given the extraction zone is empty
    And the bulk extract files in the database are scrubbed
    And The bulk extract app has been approved for "Midgar-DAYBREAK" with client id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    And The X509 cert "cert" has been installed in the trust store and aliased
    Then I remove the edorg with id "IL-Test" from the "Midgar" database
    Then I trigger a bulk extract
    Then I should see "1" bulk extract SEA-public data file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"

@wip
Scenario Outline: Extract should have all the valid data for the SEA
    When I retrieve the path to and decrypt the SEA public data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    And I verify that an extract tar file was created for the tenant "Midgar"
    And there is a metadata file in the extract
    And the extract contains a file for each of the following entities:
      |  entityType                            |
      |  course                                |
      |  courseOffering                        |
      |  educationOrganization                 |
      |  graduationPlan                        |
      |  school                                |
      |  session                               |
    Then the "<entity>" has the correct number of SEA public data records
    Then I verify that the "<entity>" reference an SEA only

    Examples:
    | entity                                 |
    |  course                                |
    |  courseOffering                        |
    |  educationOrganization                 |
    |  graduationPlan                        |
    |  school                                |
    |  session                               |

@wip
Scenario Outline: One of the entity doesn't reference the SEA
    Given the extraction zone is empty
    And the bulk extract files in the database are scrubbed
    And The bulk extract app has been approved for "Midgar-DAYBREAK" with client id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    And no <entity> references the SEA
    Then I trigger a bulk extract
    Then I should see "1" bulk extract SEA-public data file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    When I retrieve the path to and decrypt the SEA public data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    And I verify that an extract tar file was created for the tenant "Midgar"
    And there is a metadata file in the extract
    Then I verify that extract does not contain a file for <entity>

    Examples:
    | entity                                 |
    |  course                                |

  @wip
  Scenario Outline: None of the entities reference the SEA
    Given the extraction zone is empty
    And the bulk extract files in the database are scrubbed
    And The bulk extract app has been approved for "Midgar-DAYBREAK" with client id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    And no <entity> references the SEA
    Then I trigger a bulk extract
    Then I should see "0" bulk extract SEA-public data file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"

    Examples:
    | entity                                 |
    |  courseOffering                        |
    |  educationOrganization                 |
    |  graduationPlan                        |
    |  school                                |
    |  session                               |

  Scenario: Bulk extract should fail if there are more than 1 SEA in the tenant.
	Given I am using local data store
	And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "ExtendedSEA.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
  	And a batch job for file "ExtendedSEA.zip" is completed in database
    Then a batch job log has been created
  	Given the extraction zone is empty
    And the bulk extract files in the database are scrubbed
    And The bulk extract app has been approved for "Midgar-DAYBREAK" with client id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    And The X509 cert "cert" has been installed in the trust store and aliased
    Then I trigger a bulk extract
    Then I should see "0" bulk extract SEA-public data file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    Then I remove the edorg with id "IL-Test" from the "Midgar" database


@wip
 Scenario: No SEA is available for the tenant
   Given the extraction zone is empty
   And the bulk extract files in the database are scrubbed
   And The bulk extract app has been approved for "Midgar-DAYBREAK" with client id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
   And There is no SEA for the tenant "Midgar"
   Then I trigger a bulk extract
   Then I should see "0" bulk extract SEA-public data file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"

  Scenario: Clean up the SEA public data in the database
    Given all collections are empty
