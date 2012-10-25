@RALLY_US4446
Feature: Subdoc Out of Order

  Background: I have a landing zone route configured
    Given I am using local data store

  Scenario: Post Small Sample Data Set
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "StudentSectionAssociation.zip" file as the payload of the ingestion job
    And the following collections are empty in datastore:
      | collectionName                        |
      | section                               |
      | gradebookEntry             |
    When zip file is scp to ingestion landing zone
    And I am willing to wait upto 60 seconds for ingestion to complete
    And a batch job log has been created
    Then I should see following map of entry counts in the corresponding collections:
      | collectionName              | count |
      | section                  | 19    |
      | gradebookEntry | 47   |