@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

  Scenario: Safe Delete Grade Book Entry with cascade = false, force = false
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
    And I post "SafeGradebookEntryDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeGradebookEntryDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 0" in the resulting batch job file
    And I should see "records failed processing: 1" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeStudentGrades.xml"
    And I re-execute saved query "sc" to get "1" records
    And I re-execute saved query "yt" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |gradebookEntry                         |         0|
      |recordHash                             |         0|

  Scenario: Safe Delete Grade Book Entry Reference with cascade = false, force = false
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
    And I post "SafeGradebookEntryRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeGradebookEntryRefDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 0" in the resulting batch job file
    And I should see "records failed processing: 1" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeStudentGrades.xml"
    And I re-execute saved query "sc" to get "1" records
    And I re-execute saved query "yt" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |gradebookEntry                         |         0|
      |recordHash                             |         0|

  Scenario: Safe Delete Orphan Grade Book Entry with cascade = false, force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "sc"
      |field                                       |value                                                                                 |
      |gradebookEntry._id                          |6a90abcc1c5fc2f5945724fc063f604f6cdca7aa_idb3bfb205c55173b6240672627a2fa0fe11df528f_id|
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanGradebookEntryDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanGradebookEntryDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I re-execute saved query "sc" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |gradebookEntry                         |        -1|
      |recordHash                             |        -1|

  Scenario: Safe Delete Orphan Grade Book Entry Reference with cascade = false, force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "sc"
      |field                                       |value                                                                                 |
      |gradebookEntry._id                          |6a90abcc1c5fc2f5945724fc063f604f6cdca7aa_idb3bfb205c55173b6240672627a2fa0fe11df528f_id|
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanGradebookEntryRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanGradebookEntryRefDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I re-execute saved query "sc" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |gradebookEntry                         |        -1|
      |recordHash                             |        -1|


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
