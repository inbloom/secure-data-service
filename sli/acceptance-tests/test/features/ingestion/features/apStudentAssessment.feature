@wip
Feature: AP StudentAssessment Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "apStudentAssessmentMetadata.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | studentAssessment           |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | studentAssessment           | 50    |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                                | searchValue                                      |
     | studentAssessment           | 5                   | body.performanceLevels.description             | Extremely well qualified |
     | studentAssessment           | 25                  | body.assessmentReference.assessmentIdentity.assessmentIdentificationCode.id | AP English Literature and Composition |

  And I should see "Processed 50 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "apStudentAssessmentMetadata.xml records considered: 50" in the resulting batch job file
  And I should see "apStudentAssessmentMetadata.xml records ingested successfully: 50" in the resulting batch job file
  And I should see "apStudentAssessmentMetadata.xml records failed: 0" in the resulting batch job file