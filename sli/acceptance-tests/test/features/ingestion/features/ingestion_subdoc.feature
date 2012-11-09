@RALLY_US4446
Feature: Subdoc Out of Order

  Background: I have a landing zone route configured
    Given I am using local data store

  Scenario: Check that ingesting gradebookEntry will also create section when section was not created.
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "SubDocOutOfOrder_grade.zip" file as the payload of the ingestion job
    And the following collections are empty in datastore:
      | collectionName             |
      | section                    |
      | gradebookEntry             |
    When zip file is scp to ingestion landing zone
    And I am willing to wait upto 60 seconds for ingestion to complete
    And a batch job log has been created
    Then I should see following map of entry counts in the corresponding collections:
      | collectionName  | count |
      | section         | 8     |
      | gradebookEntry  | 20    |
    Then I post "SubDocOutOfOrder_section.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And I am willing to wait upto 60 seconds for ingestion to complete
    And a batch job log has been created
    Then I should see following map of entry counts in the corresponding collections:
      | collectionName  | count |
      | section         | 17    |
      | gradebookEntry  | 20    |
