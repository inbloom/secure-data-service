@RALLY_US3033
Feature: Sample Data Set Ingestion Timimg

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Post Jefferson County onboarding XML templates
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "JeffCoOnboarding.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                            |
     | educationOrganization                     |
     | staff                                     |
     | staffEducationOrganizationAssociation     |
When zip file is scp to ingestion landing zone
  And a batch job for file "JeffCoOnboarding.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | educationOrganization                    |                  2|
     | staff                                    |                  1|
     | staffEducationOrganizationAssociation    |                  1|
    And I should see "Processed 4 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created


