Feature: Ingestion IDRef Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing studentGradebookEntry with ID References job: Clean Database
Given I post "ingestion_IDReferences.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName               |
     | calendarDate                 |
     | course                       |
     | educationOrganization        |
     | gradebookEntry               |
     | gradingPeriod                |
     | learningObjective            |
     | learningStandard             |
     | schoolSessionAssociation     |
     | section                      |
     | session                      |
     | student                      |
     | studentSectionAssociation    |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName               | count   |
     | calendarDate                 |   9     |
     | course                       |   5     |
     | educationOrganization        |   8     |
     | gradebookEntry               |  16     |
     | gradingPeriod                |   9     |
     | learningObjective            |   4     |
     | learningStandard             |   6     |
     | schoolSessionAssociation     |   0     |
     | section                      |   9     |
     | session                      |  10     |
     | student                      |  11     |
     | studentSectionAssociation    |   2     |
  And I should see "Processed 112 records." in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata.xml records ingested successfully: 11" in the resulting batch job file
  And I should see "InterchangeEducationOrgCalendar.xml records ingested successfully: 28" in the resulting batch job file
  And I should see "InterchangeEducationOrganization.xml records ingested successfully: 13" in the resulting batch job file
  And I should see "InterchangeMasterSchedule.xml records ingested successfully: 9" in the resulting batch job file
  And I should see "InterchangeStudentEnrollment.xml records ingested successfully: 2" in the resulting batch job file
  And I should see "InterchangeStudentGrade.xml records ingested successfully: 28" in the resulting batch job file
  And I should see "InterchangeStudentParent.xml records ingested successfully: 11" in the resulting batch job file
  And I should see "Unable to resolve a reference with ref=[LS_101]: No matching element with id=[LS_101]" in the resulting warning log file for "InterchangeStudentGrade.xml"
  And I should see "Unable to resolve a reference with ref=[LO_101]: No matching element with id=[LO_101]" in the resulting warning log file for "InterchangeStudentGrade.xml"
  And I should see "Unable to resolve a reference with ref=[SSA_101]: No matching element with id=[SSA_101]" in the resulting warning log file for "InterchangeStudentGrade.xml"
  And I should see "Unable to resolve a reference with ref=[S_101]: No matching element with id=[S_101]" in the resulting warning log file for "InterchangeStudentGrade.xml"
  And I should see "Circular reference detected with ref=[LO_3]" in the resulting warning log file for "InterchangeAssessmentMetadata.xml"
  And I should see "Unable to resolve a reference with ref=[LS_1]: The reference refers to itself" in the resulting warning log file for "InterchangeAssessmentMetadata.xml"
  And I should see "Not all records were processed completely due to errors" in the resulting batch job file