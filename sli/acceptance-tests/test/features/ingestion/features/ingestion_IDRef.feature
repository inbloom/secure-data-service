Feature: Ingestion IDRef Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone


Scenario: Post a zip file containing studentGradebookEntry with resolvable/unresolvable ID References job: Clean Database
Given I post "ingestion_IDRef.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName               |
     | studentGradebookEntry        |
     | gradebookEntry               |
     | studentSectionAssociation    |
     | student                      |
     | section                      |
     | learningObjective            |
     | learningStandard             |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName               | count   |
     | studentGradebookEntry        |  2      |
     | gradebookEntry               |  2      |
     | studentSectionAssociation    |  2      |
     | student                      |  2      |
     | section                      |  2      |
     | learningObjective            |  2      |
     | learningStandard             |  2      |
  And I should see "Processed 88 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "InterchangeStudentGradebook.xml records considered: 78" in the resulting batch job file
  And I should see "InterchangeStudentGradebook.xml records ingested successfully: 78" in the resulting batch job file
  And I should see "InterchangeStudentGradebook.xml records failed: 0" in the resulting batch job file
  And I should see "InterchangeStudentEnrollment.xml records considered: 8" in the resulting batch job file
  And I should see "InterchangeStudentEnrollment.xml records ingested successfully: 8" in the resulting batch job file
  And I should see "InterchangeStudentEnrollment.xml records failed: 0" in the resulting batch job file
  And I should see "InterchangeStudentParent.xml records considered: 8" in the resulting batch job file
  And I should see "InterchangeStudentParent.xml records ingested successfully: 8" in the resulting batch job file
  And I should see "InterchangeStudentParent.xml records failed: 0" in the resulting batch job file
  And I should see "InterchangeMasterSchedule.xml records considered: 8" in the resulting batch job file
  And I should see "InterchangeMasterSchedule.xml records ingested successfully: 8" in the resulting batch job file
  And I should see "InterchangeMasterSchedule.xml records failed: 0" in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata.xml records considered: 2" in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata.xml records ingested successfully: 2" in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata.xml records failed: 0" in the resulting batch job file
  And I should see "There is no ID/IDREF binding for IDREF 'ISAT-51'" in the resulting warning log file
  And I should see "Not all records were processed completely due to errors" in the resulting batch job file
