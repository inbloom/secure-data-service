Feature: Ingestion IDRef Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone


Scenario: Post a zip file containing studentAssessmentAssociation with ID References job: Clean Database
Given I post "ingestion_IDReferences.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName               |
     | course                       |
     | educationOrganization        |
     | gradebookEntry               |
     | learningObjective            |
     | learningStandard             |
     | schoolSessionAssociation     |
     | section                      |
     | session                      |
     | student                      |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName               | count   |
     | course                       |   5     |
     | educationOrganization        |   8     |
     | gradebookEntry               |  16     |
     | learningObjective            |   1     |
     | learningStandard             |   6     |
     | schoolSessionAssociation     |   5     |
     | section                      |   5     |
     | session                      |   5     |
     | student                      |  11     |
  And I should see "Processed 94 records." in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata.xml records ingested successfully: 11" in the resulting batch job file
  And I should see "InterchangeEducationOrgCalendar.xml records ingested successfully: 10" in the resulting batch job file
  And I should see "InterchangeEducationOrganization.xml records ingested successfully: 13" in the resulting batch job file
  And I should see "InterchangeMasterSchedule.xml records ingested successfully: 5" in the resulting batch job file
  And I should see "InterchangeStudentEnrollment.xml records ingested successfully: 0" in the resulting batch job file
  And I should see "InterchangeStudentGrade.xml records ingested successfully: 28" in the resulting batch job file
  And I should see "InterchangeStudentParent.xml records ingested successfully: 11" in the resulting batch job file
  And I should see "Reference not resolved: ref=<LS_101>. No matching id for this ref." in the resulting warning log file
  And I should see "Reference not resolved: ref=<LO_101>. No matching id for this ref." in the resulting warning log file
  And I should see "Reference not resolved: ref=<GP_101>. No matching id for this ref." in the resulting warning log file
  And I should see "Reference not resolved: ref=<CL_101>. No matching id for this ref." in the resulting warning log file
  And I should see "Reference not resolved: ref=<SSA_101>. No matching id for this ref." in the resulting warning log file
  And I should see "Reference not resolved: ref=<S_101>. No matching id for this ref." in the resulting warning log file
  And I should see "Not all records were processed completely due to errors" in the resulting batch job file

