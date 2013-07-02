Feature: As an bulk extract user, I want to be able to get the state public entities

Scenario: As an bulk extract user, I want to initialize my database with test data.
    Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "SEAFullDataset.zip" file as the payload of the ingestion job
    And all collections are empty
    When zip file is scp to ingestion landing zone
    And a batch job for file "SEAFullDataset.zip" is completed in database
    And a batch job log has been created
    Then I should see following map of entry counts in the corresponding collections:
      | collectionName                           |              count|
      | calendarDate                             |                556|
      | course                                   |                  6|
      | courseOffering                           |                  6|
      | educationOrganization                    |                  5|
      | gradingPeriod                            |                  6|
      | graduationPlan                           |                  5|
      | session                                  |                  4|