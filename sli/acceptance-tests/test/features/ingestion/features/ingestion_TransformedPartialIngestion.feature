@RALLY_US3696
Feature: Partial Ingestion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Post StudentAssessment without required parent records in database

Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "StudentAssessment_Partial_Unhappy.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                            |
     | studentAssessmentAssociation              |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | studentAssessmentAssociation             |                  0|
    And I should see "Not all records were processed completely due to errors." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file

Scenario: Post StudentAssessment records with required parent records previously ingested

Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "StudentAssessmentFull.zip" file as the payload of the ingestion job
     And the following collections are empty in datastore:
     | collectionName                            |
     | assessment                                |
     | student                                   |
     | studentAssessmentAssociation              |
When zip file is scp to ingestion landing zone
    And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | assessment                               |                  1|
     | student                                  |                  1|
    And I should see "Processed 2 records." in the resulting batch job file
    And I should not see an error log file created

    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "StudentAssessment_Partial_Happy.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | assessment                               |                  1|
     | student                                  |                  1|
     | studentAssessmentAssociation             |                  1|
    And I should see "Processed 1 records." in the resulting batch job file

Scenario: Post Attendance without required parent records in database

Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "AttendancePartialUnHappy.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                            |
     | attendance                                |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | attendance                               |                  0|
    And I should see "Processed 0 records." in the resulting batch job file


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
When zip file is scp to ingestion landing zone
    And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | educationOrganization                    |                  3|
     | session                                  |                  1|
     | student                                  |                  1|
     | studentSchoolAssociation                 |                  1|
    And I should see "Processed 7 records." in the resulting batch job file
    And I should not see an error log file created

    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "AttendancePartialHappy.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
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
    | attendance                  | 1                   | body.schoolYearAttendance.attendanceEvent.event  | Tardy          | string     |
    | attendance                  | 1                   | body.schoolYearAttendance.attendanceEvent.reason | Dentist appointment | string     |
    | attendance                  | 1                   | body.schoolYearAttendance.attendanceEvent.date   | 2010-09-09          | string     |
    And I should see "Processed 1 records." in the resulting batch job file

    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "AttendanceUpdateAndAppend.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | attendance                               |                  1|
    And I check to find if record is in collection:
    | collectionName              | expectedRecordCount | searchParameter                                  | searchValue         | searchType |
    | attendance                  | 1                   | body.schoolYearAttendance.attendanceEvent.event  | Tardy               | string     |
    | attendance                  | 1                   | body.schoolYearAttendance.attendanceEvent.reason | Missed school bus   | string     |
    | attendance                  | 0                   | body.schoolYearAttendance.attendanceEvent.reason | Dentist appointment | string     |
    | attendance                  | 1                   | body.schoolYearAttendance.attendanceEvent.event  | In Attendance       | string     |
    | attendance                  | 1                   | body.schoolYearAttendance.attendanceEvent.reason | On Time             | string     |
    | attendance                  | 1                   | body.schoolYearAttendance.attendanceEvent.date   | 2010-09-09          | string     |
And I should see "Processed 1 records." in the resulting batch job file


Scenario: Post partial Assessment dataset on an empty database
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "AssessmentPartial.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                            |
     | assessment                                |

When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | assessment                               |                  2|
    And I should see "Processed 2 records." in the resulting batch job file
    And I check to find if record is in collection:
       | collectionName              | expectedRecordCount | searchParameter                  | searchValue        | searchType         |
       | assessment                  | 2                   | body.assessmentPeriodDescriptor  | nil                | nil                |

 Scenario: Post full assessment dataset on an empty database followed by partial assessment dataset
 Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "AssessmentFull.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                            |
     | assessment                                |

When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | assessment                               |                  2|
    And I should see "Processed 2 records." in the resulting batch job file
    And I check to find if record is in collection:
       | collectionName              | expectedRecordCount | searchParameter                  | searchValue                | searchType           |
       | assessment                  | 1                   | body.assessmentPeriodDescriptor.codeValue  | READ2-BOY-2011                       | string               |
       | assessment                  | 1                   | body.assessmentPeriodDescriptor.codeValue  | READ2-MOY-2011                       | string               |

    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "AssessmentPartial.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
    And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | assessment                               |                  4|
    And I should see "Processed 2 records." in the resulting batch job file
    And I check to find if record is in collection:
       | collectionName              | expectedRecordCount | searchParameter                  | searchValue                | searchType           |
       | assessment                  | 2                   | body.assessmentPeriodDescriptor.codeValue  | READ2-BOY-2011                       | string               |
       | assessment                  | 2                   | body.assessmentPeriodDescriptor.codeValue  | READ2-MOY-2011                       | string               |
