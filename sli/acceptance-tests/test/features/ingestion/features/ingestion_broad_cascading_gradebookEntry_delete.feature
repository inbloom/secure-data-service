@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

@wip
Scenario: Delete Gradebook Entry with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	And I should see child entities of entityType "gradebookEntry" with id "e003fc1479112d3e953a0220a2d0ddd31077d6d9_idbb469f1ebc859c9a90383807b7a5aa027524e5f0_id" in the "Midgar" database	
    And I post "BroadGradebookEntryDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "BroadGradebookEntryDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I should not see "e003fc1479112d3e953a0220a2d0ddd31077d6d9_idbb469f1ebc859c9a90383807b7a5aa027524e5f0_id" in the "Midgar" database
	And I should not see any entity mandatorily referring to "e003fc1479112d3e953a0220a2d0ddd31077d6d9_idbb469f1ebc859c9a90383807b7a5aa027524e5f0_id" in the "Midgar" database
	And I should see entities optionally referring to "e003fc1479112d3e953a0220a2d0ddd31077d6d9_idbb469f1ebc859c9a90383807b7a5aa027524e5f0_id" be updated in the "Midgar" database


  Scenario: Delete Grade Book Entry with cascade = false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "sc"
      |field                                       |value                                                                                 |
      |gradebookEntry._id                          |60790710e1151aaf11318e0db7394126ed41e5ee_id61affa4eb6aa54e867cff2690a95046f7013abec_id|
    Then there exist "1" "studentGradebookEntry" records like below in "Midgar" tenant. And I save this query as "yt"
      |field                                       |value                                                                                 |
      |body.gradebookEntryId                       |60790710e1151aaf11318e0db7394126ed41e5ee_id61affa4eb6aa54e867cff2690a95046f7013abec_id|
    And I save the collection counts in "Midgar" tenant
    And I post "ForceGradebookEntryDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceGradebookEntryDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentGrades.xml"
    And I re-execute saved query "sc" to get "0" records
    And I re-execute saved query "yt" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |gradebookEntry                         |        -1|
      |recordHash                             |        -1|


  Scenario: Delete Grade Book Entry Reference with cascade = false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "sc"
      |field                                       |value                                                                                 |
      |gradebookEntry._id                          |60790710e1151aaf11318e0db7394126ed41e5ee_id61affa4eb6aa54e867cff2690a95046f7013abec_id|
    Then there exist "1" "studentGradebookEntry" records like below in "Midgar" tenant. And I save this query as "yt"
      |field                                       |value                                                                                 |
      |body.gradebookEntryId                       |60790710e1151aaf11318e0db7394126ed41e5ee_id61affa4eb6aa54e867cff2690a95046f7013abec_id|
    And I save the collection counts in "Midgar" tenant
    And I post "ForceGradebookEntryRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceGradebookEntryRefDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentGrades.xml"
    And I re-execute saved query "sc" to get "0" records
    And I re-execute saved query "yt" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |gradebookEntry                         |        -1|
      |recordHash                             |        -1|