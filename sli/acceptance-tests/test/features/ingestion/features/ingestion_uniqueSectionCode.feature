@RALLY_DE1823
Feature: Sample Data Set UniqueSectionCode Length

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Post UniqueSectionCode test XML templates
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "UniqueSectionCodeTest.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                            |
     | assessment                                |
     | attendance                                |
     | teacherSectionAssociation                 |
     | studentSectionAssociation                 |
     | gradebookEntry                            |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                            |              count|
     | assessment                                |                  1|
     | attendance                                |                  1|
     | teacherSectionAssociation                 |                  1|
     | studentSectionAssociation                 |                  1|
     | gradebookEntry                            |                  1|
    And I should not see an error log file created
    And I should not see a warning log file created