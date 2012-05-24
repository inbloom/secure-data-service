@RALLY_US2078
Feature: Student Parents Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing student parent interchange as a payload of the ingestion job: Clean Database
Given I post "Parents.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | student                     |
     | parent                      |
     | studentParentAssociation    |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student                     | 72    |
     | parent                      | 126   |
     | studentParentAssociation    | 126   |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter               | searchValue     |
     | parent                      | 1                   | body.parentUniqueStateId      | 0617477201      |
     | parent                      | 1                   | body.parentUniqueStateId      | 2843735625      |
     | parent                      | 1                   | body.parentUniqueStateId      | 8308453112      |
     | parent                      | 1                   | body.parentUniqueStateId      | 8775162757      |

  And I should see "All records processed successfully." in the resulting batch job file
  And I should see "Processed 324 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "InterchangeStudent.xml records considered: 72" in the resulting batch job file
  And I should see "InterchangeStudent.xml records ingested successfully: 72" in the resulting batch job file
  And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file
  And I should see "StudentParentData.xml records considered: 252" in the resulting batch job file
  And I should see "StudentParentData.xml records ingested successfully: 252" in the resulting batch job file
  And I should see "StudentParentData.xml records failed: 0" in the resulting batch job file

Scenario: Post a zip file containing student parent interchange as a payload of the ingestion job: Populated Database
Given I post "ParentsAppend.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student                     | 72    |
     | parent                      | 128   |
     | studentParentAssociation    | 128   |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter               | searchValue     |
     | parent                      | 1                   | body.parentUniqueStateId      | 3152281864      |
     | parent                      | 1                   | body.parentUniqueStateId      | 2521899635      |

  And I should see "All records processed successfully." in the resulting batch job file
  And I should see "Processed 4 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "StudentParentData.xml records considered: 4" in the resulting batch job file
  And I should see "StudentParentData.xml records ingested successfully: 4" in the resulting batch job file
  And I should see "StudentParentData.xml records failed: 0" in the resulting batch job file

Scenario: Post a zip file containing duplicate student parent interchange as a payload of the ingestion job: Populated Database
Given I post "ParentsDuplicate.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
  And I should see "All records processed successfully." in the resulting batch job file
  And I should see "Processed 4 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "StudentParentData.xml records considered: 4" in the resulting batch job file
  And I should see "StudentParentData.xml records ingested successfully: 4" in the resulting batch job file
  And I should see "StudentParentData.xml records failed: 0" in the resulting batch job file



Scenario: Post a zip file containing student parent interchange with non-existent student as a payload of the ingestion job: Populated Database
Given I post "ParentsNoStudent.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
  And I should see "Not all records were processed completely due to errors." in the resulting batch job file
  And I should see "Processed 1 records." in the resulting batch job file
  And I should see "StudentParentData.xml records considered: 1" in the resulting batch job file
  And I should see "StudentParentData.xml records ingested successfully: 0" in the resulting batch job file
  And I should see "StudentParentData.xml records failed: 1" in the resulting batch job file
