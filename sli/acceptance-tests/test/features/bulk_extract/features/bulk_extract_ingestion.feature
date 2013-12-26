Feature: Ingest odin data for use with bulk extract

Scenario: Generate and ingest data set for bulk extract
  Given I am using the odin working directory
    And I am using odin data store 

    When I generate the bulk extract data set in the generated directory
    And I zip generated data under filename OdinSampleDataSet.zip to the new OdinSampleDataSet directory
    And I copy generated data to the new OdinSampleDataSet directory
    Then I should see generated file <File>
| File  |
|ControlFile.ctl|
|InterchangeAssessmentMetadata.xml|
|InterchangeAttendance.xml|
|InterchangeEducationOrgCalendar.xml|
|InterchangeEducationOrganization.xml|
|InterchangeMasterSchedule.xml|
|InterchangeStaffAssociation.xml|
|InterchangeStudentAssessment.xml|
|InterchangeStudentCohort.xml|
|InterchangeStudentDiscipline.xml|
|InterchangeStudentEnrollment.xml|
|InterchangeStudentGrades.xml|
|InterchangeStudentParent.xml|
|InterchangeStudentProgram.xml|
|OdinSampleDataSet.zip|
|manifest.json|


Scenario: Post Odin Sample Data Set
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I am using odin data store 
  And I post "OdinSampleDataSet.zip" file as the payload of the ingestion job
  And the "Midgar" tenant db is empty
When zip file is scp to ingestion landing zone
  And a batch job for file "OdinSampleDataSet.zip" is completed in database
  And a batch job log has been created
  
Then I should see following map of entry counts in the corresponding collections:
  | collectionName                           |              count|
  | student                                  |                260|
  And I should not see an error log file created
  And I should not see a warning log file created
