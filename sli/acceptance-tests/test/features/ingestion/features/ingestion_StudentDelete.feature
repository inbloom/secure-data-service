@RALLY_US3201
Feature: Student ('Simple') Delete Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

@Initial Ingestion of Data Set
Scenario: Post a zip file containing student parent interchange as a payload of the ingestion job: Clean Database
Given I post "StudentDelete1.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | student                     |
     | parent                      |
     | studentParentAssociation    |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student                     | 2     |
     | parent                      | 1     |
     | studentParentAssociation    | 1     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter               | searchValue     |
     | student                     | 1                   | body.studentUniqueStateId     | 530425896       |
     | student                     | 1                   | body.studentUniqueStateId     | 814202099       |
     | parent                      | 1                   | body.parentUniqueStateId      | 5344776141      |

  And I should see "All records processed successfully." in the resulting batch job file
  And I should see "Processed 4 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "InterchangeStudent.xml records considered: 2" in the resulting batch job file
  And I should see "InterchangeStudent.xml records ingested successfully: 2" in the resulting batch job file
  And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file
  And I should see "StudentParentData.xml records considered: 2" in the resulting batch job file
  And I should see "StudentParentData.xml records ingested successfully: 2" in the resulting batch job file
  And I should see "StudentParentData.xml records failed: 0" in the resulting batch job file

@Delete unreferenced student
Scenario: Post a zip file containing student parent interchange as a payload of the ingestion job: Populated Database
Given I post "StudentDelete2.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student                     | 1     |
     | parent                      | 1     |
     | studentParentAssociation    | 1     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter               | searchValue     |
     | student                     | 1                   | body.studentUniqueStateId     | 530425896       |
     | student                     | 0                   | body.studentUniqueStateId     | 814202099       |
     | parent                      | 1                   | body.parentUniqueStateId      | 5344776141      |

  And I should see "All records processed successfully." in the resulting batch job file
  And I should see "Processed 1 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "InterchangeStudent.xml records considered: 1" in the resulting batch job file
  And I should see "InterchangeStudent.xml records deleted successfully: 1" in the resulting batch job file
  And I should see "InterchangeStudent.xml records attempted to be deleted, but not present: 0" in the resulting batch job file
  And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file

@Delete non-existent student
Scenario: Post a zip file containing student parent interchange as a payload of the ingestion job: Populated Database
Given I post "StudentDelete2.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student                     | 1     |
     | parent                      | 1     |
     | studentParentAssociation    | 1     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter               | searchValue     |
     | student                     | 1                   | body.studentUniqueStateId     | 530425896       |
     | parent                      | 1                   | body.parentUniqueStateId      | 5344776141      |

  And I should see "All records processed successfully." in the resulting batch job file
  And I should see "Processed 1 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "InterchangeStudent.xml records considered: 1" in the resulting batch job file
  And I should see "InterchangeStudent.xml records deleted successfully: 0" in the resulting batch job file
  And I should see "InterchangeStudent.xml records attempted to be deleted, but not present: 1" in the resulting batch job file
  And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file

@Delete referenced student
Scenario: Post a zip file containing student parent interchange as a payload of the ingestion job: Populated Database
Given I post "StudentDelete3.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student                     | 1     |
     | parent                      | 1     |
     | studentParentAssociation    | 1     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter               | searchValue     |
     | student                     | 1                   | body.studentUniqueStateId     | 530425896       |
     | parent                      | 1                   | body.parentUniqueStateId      | 5344776141      |

  And I should see "All records processed successfully." in the resulting batch job file
  And I should see "Processed 1 records." in the resulting batch job file
  And I should see "InterchangeStudent.xml records considered: 1" in the resulting batch job file
  And I should see "InterchangeStudent.xml records deleted successfully: 0" in the resulting batch job file
  And I should see "InterchangeStudent.xml records attempted to be deleted, but not present: 0" in the resulting batch job file
  And I should see "InterchangeStudent.xml records failed: 1" in the resulting batch job file

