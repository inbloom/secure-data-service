@RALLY_US3696
Feature: Partial Ingestion

Background: I have a landing zone route configured
Given I am using local data store

@wip
Scenario: Post StudentAssessment without required parent records in database

Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "StudentAssessment_Partial_Unhappy.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                            |
     | studentAssessment              |
When zip file is scp to ingestion landing zone
  And a batch job for file "StudentAssessment_Partial_Unhappy.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | studentAssessment             |                  0|
    And I should see "Not all records were processed completely due to errors." in the resulting batch job file
    And I should see "Processed 3 records." in the resulting batch job file

Scenario: Post StudentAssessment records with required parent records previously ingested

Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "StudentAssessmentFull.zip" file as the payload of the ingestion job
     And the following collections are empty in datastore:
     | collectionName                            |
     | assessment                                |
     | assessmentFamily                          |
     | student                                   |
     | studentAssessment                         |
     | recordHash                                |
When zip file is scp to ingestion landing zone
    And a batch job for file "StudentAssessmentFull.zip" is completed in database
    And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | assessment                               |                  1|
     | assessmentFamily                         |                  1|
     | student                                  |                  1|
    And I should see "Processed 5 records." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created

    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "StudentAssessment_Partial_Happy.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job for file "StudentAssessment_Partial_Happy.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | assessment                               |                  1|
     | student                                  |                  1|
     | studentAssessment                        |                  2|
    And I should see "Processed 5 records." in the resulting batch job file
    And I should not see a warning log file created

Scenario: Post Attendance without required parent records in database

Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "AttendancePartialUnHappy.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                            |
     | attendance                                |
     | recordHash                                |
When zip file is scp to ingestion landing zone
  And a batch job for file "AttendancePartialUnHappy.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | attendance                               |                  0|
    And I should see "Processed 2 records." in the resulting batch job file
    And I should see "StudentAttendanceEvents.xml records not considered for processing: 2" in the resulting batch job file


Scenario: Post Attendance records with required parent records previously ingested

Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "AttendanceFull.zip" file as the payload of the ingestion job
     And the following collections are empty in datastore:
     | collectionName                            |
     | attendance                                |
     | educationOrganization                     |
     | session                                   |
     | student                                   |
     | studentSchoolAssociation                  |
     |recordHash                                 |
When zip file is scp to ingestion landing zone
    And a batch job for file "AttendanceFull.zip" is completed in database
    And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | educationOrganization                    |                  3|
     | session                                  |                  1|
     | student                                  |                  1|
     | studentSchoolAssociation                 |                  1|
    And I should see "Processed 8 records." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created

    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "AttendancePartialHappy.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job for file "AttendancePartialHappy.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | attendance                               |                  1|
     | educationOrganization                    |                  3|
     | session                                  |                  1|
     | student                                  |                  1|
     | studentSchoolAssociation                 |                  1|
And I check to find if record is in collection:
    | collectionName              | expectedRecordCount | searchParameter                                  | searchValue    | searchType |
    | attendance                  | 1                   | body.attendanceEvent.event  | Tardy          | string     |
    | attendance                  | 1                   | body.attendanceEvent.reason | Dentist appointment | string     |
    | attendance                  | 1                   | body.attendanceEvent.date   | 2010-09-09          | string     |
    And I should see "Processed 2 records." in the resulting batch job file
    And I should not see a warning log file created

    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "AttendanceUpdateAndAppend.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job for file "AttendanceUpdateAndAppend.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | attendance                               |                  1|
    And I check to find if record is in collection:
    | collectionName              | expectedRecordCount | searchParameter                                  | searchValue         | searchType |
    | attendance                  | 1                   | body.attendanceEvent.event  | Tardy               | string     |
    | attendance                  | 1                   | body.attendanceEvent.reason | Missed school bus   | string     |
    | attendance                  | 1                   | body.attendanceEvent.reason | Dentist appointment | string     |
    | attendance                  | 1                   | body.attendanceEvent.event  | In Attendance       | string     |
    | attendance                  | 1                   | body.attendanceEvent.reason | On Time             | string     |
    | attendance                  | 1                   | body.attendanceEvent.date   | 2010-09-09          | string     |
And I should see "Processed 2 records." in the resulting batch job file
And I should not see a warning log file created


Scenario: Post partial Assessment dataset on an empty database
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "AssessmentPartial.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                            |
     | assessment                                |
     | assessmentFamily                          |
     | recordHash                                |

When zip file is scp to ingestion landing zone
  And a batch job for file "AssessmentPartial.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | assessment                               |                  2|
     | assessmentFamily                         |                  2|
    And I should see "Processed 4 records." in the resulting batch job file
    And I check to find if record is in collection:
       | collectionName              | expectedRecordCount | searchParameter                  | searchValue        | searchType         |
       | assessment                  | 2                   | body.assessmentPeriodDescriptor  | nil                | nil                |
    And I should not see a warning log file created

 Scenario: Post full assessment dataset on an empty database followed by partial assessment dataset
 Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "AssessmentFull.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                            |
     | assessment                                |
     | assessmentFamily                          |
     | assessmentPeriodDescriptor                |
     | recordHash                                |

When zip file is scp to ingestion landing zone
  And a batch job for file "AssessmentFull.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | assessment                               |                  2|
     | assessmentFamily                         |                  2|
     | assessmentPeriodDescriptor               |                  2|
    And I should see "Processed 6 records." in the resulting batch job file
    And I check to find if record is in collection:
       | collectionName              | expectedRecordCount | searchParameter                  | searchValue                | searchType           |
       | assessmentPeriodDescriptor  | 1                   | body.codeValue                   | READ2-BOY-2011             | string               |
       | assessmentPeriodDescriptor  | 1                   | body.codeValue                   | READ2-MOY-2011             | string               |
    And I should not see a warning log file created

    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "AssessmentPartial.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
    And a batch job for file "AssessmentPartial.zip" is completed in database
    And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | assessment                               |                  4|
    And I should see "Processed 4 records." in the resulting batch job file
    And I check to find if record is in collection:
       | collectionName              | expectedRecordCount | searchParameter                  | searchValue                | searchType           |
       | assessmentPeriodDescriptor  | 1                   | body.codeValue                   | READ2-BOY-2011             | string               |
       | assessmentPeriodDescriptor  | 1                   | body.codeValue                   | READ2-MOY-2011             | string               |
    And I should not see a warning log file created
       
