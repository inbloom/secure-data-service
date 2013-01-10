@RALLY_1341
 
Feature: Concurrent job On Same Tenant Error Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "DailyAttendance.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | student                     |
     | studentSchoolAssociation    |
     | educationOrganization       |
     | school                      |
     | session                     |
     | attendance                  |
When zip file is scp to ingestion landing zone
And "1" seconds have elapsed

Then I post "concurrentJobErrorTest.ctl" control file for concurent processing
And "3" seconds have elapsed

And I should see "Another job is currently running for this tenant" in the resulting error log file for "concurrentJobErrorTest.ctl"
And a batch job for file "DailyAttendance.zip" is completed in database
And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student                     | 94    |
     | studentSchoolAssociation    | 123   |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                                 | searchValue   |
    | attendance                  | 38                  | body.schoolYearAttendance.schoolYear            | 2011-2012     |
    | attendance                  | 33                  | body.schoolYearAttendance.attendanceEvent.event | In Attendance |

  And I should see "Processed 281 records." in the resulting batch job file


Scenario: Post two zip files to the same landing zone and see that the second is locked out: Clean Database
Given I post "BatchJobLarge.zip" and "BatchJob.zip" files as the payload of two ingestion jobs
    And the following collections are empty in batch job datastore:
        | collectionName              |
        | newBatchJob                 |
        | error                       |
        | tenantJobLock               |

When zip files are scped to the ingestion landing zone
  And a batch job for file "BatchJob.zip" is completed in database
  And a batch job for file "BatchJobLarge.zip" is completed in database

Then I should see following map of entry counts in the corresponding batch job db collections:
        | collectionName              | count |
        | newBatchJob                 | 2     |

 And I check to find if record is in batch job collection:
  | collectionName | expectedRecordCount | searchParameter                | searchValue             | searchType |
  | newBatchJob    | 1                   | totalFiles                     | 1                       | integer    |
  | newBatchJob    | 1                   | status                         | CompletedSuccessfully   | string     |
  | newBatchJob    | 1                   | status                         | CompletedWithErrors     | string     |
  | newBatchJob    | 1                   | resourceEntries.0.resourceId   | BatchJob.zip            | string     |
  | newBatchJob    | 1                   | resourceEntries.0.resourceId   | BatchJobLarge.zip       | string     |

 And I should see "ERROR  Another job is currently running for this tenant.  Please retry this ingestion once it has finished." in the resulting error log file

