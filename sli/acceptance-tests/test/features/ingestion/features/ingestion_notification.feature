Feature: Ingestion Notification

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Post Notify Data Set
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "EmailNotify.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                            |
     | educationOrganization                     |
When zip file is scp to ingestion landing zone
  And a batch job for file "EmailNotify.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | educationOrganization                    |                  1|
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created


