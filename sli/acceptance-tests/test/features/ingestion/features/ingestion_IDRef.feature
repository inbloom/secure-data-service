Feature: Ingestion IDRef Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone


Scenario: Post a zip file containing studentAssessmentAssociation with ID References job: Clean Database
Given I post "ingestion_IDRef.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName               |
     | student                      |
     | assessment                   |
     | studentAssessmentAssociation |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName               | count   |
     | student                      | 78      |
     | assessment                   |  2      |
     | studentAssessmentAssociation |  8      |
  And I should see "Processed 88 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "InterchangeStudent.xml records considered: 78" in the resulting batch job file
  And I should see "InterchangeStudent.xml records ingested successfully: 78" in the resulting batch job file
  And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata-ISAT.xml records considered: 2" in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata-ISAT.xml records ingested successfully: 2" in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata-ISAT.xml records failed: 0" in the resulting batch job file
  And I should see "InterchangeStudentAssessmentWiIDRef.xml records considered: 8" in the resulting batch job file
  And I should see "InterchangeStudentAssessmentWiIDRef.xml records ingested successfully: 8" in the resulting batch job file
  And I should see "InterchangeStudentAssessmentWiIDRef.xml records failed: 0" in the resulting batch job file

Scenario: Post a zip file containing studentAssessmentAssociation without any ID Ref job: Clean Database
Given I post "ingestion_WoIDRef.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName               |
     | student                      |
     | assessment                   |
     | studentAssessmentAssociation |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count    |
     | student                      | 78      |
     | assessment                   |  2      |
     | studentAssessmentAssociation |  3      |
  And I should see "Processed 83 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "InterchangeStudent.xml records considered: 78" in the resulting batch job file
  And I should see "InterchangeStudent.xml records ingested successfully: 78" in the resulting batch job file
  And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata-ISAT.xml records considered: 2" in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata-ISAT.xml records ingested successfully: 2" in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata-ISAT.xml records failed: 0" in the resulting batch job file
  And I should see "InterchangeStudentAssessmentWoIDRef.xml records considered: 3" in the resulting batch job file
  And I should see "InterchangeStudentAssessmentWoIDRef.xml records ingested successfully: 3" in the resulting batch job file
  And I should see "InterchangeStudentAssessmentWoIDRef.xml records failed: 0" in the resulting batch job file

Scenario: Post a zip file containing a missing ID Ref, it should process other records job: Clean Database
Given I post "ingestion_MissingIDRef.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | student                     |
     | assessment                  |
     | studentAssessmentAssociation |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count    |
     | student                      | 78      |
     | assessment                   |  2      |
  And I should see "There is no ID/IDREF binding for IDREF 'ISAT-51'" in the resulting warning log file
  And I should see "Not all records were processed completely due to errors" in the resulting batch job file

