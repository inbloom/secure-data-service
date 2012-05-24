@RALLY_US1841
Feature: AP Assessment and StudentAssessment Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post an Assessment zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "apAssessmentMetadata.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | student                     |
     | assessment                  |
     | studentAssessmentAssociation|
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student                     | 78    |
     | studentAssessmentAssociation| 50    |
     | assessment                  | 2     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                                  | searchValue                                      |
     | assessment                  | 1                   | body.assessmentFamilyHierarchyName               | AP.AP Eng.AP-Eng-and-Literature                  |
     | assessment                  | 1                   | body.assessmentFamilyHierarchyName               | AP.AP Eng.AP-Lang-and-Literature                 |
     | studentAssessmentAssociation| 9                   | body.performanceLevelDescriptors.0.1.description | Extremely well qualified |
#     | studentAssessmentAssociation| 25                  | body.assessmentReference.assessmentIdentity.assessmentIdentificationCode.id | AP English Literature and Composition |
     | studentAssessmentAssociation| 1                   | metaData.externalId | {administrationDate=2011-05-01, studentId=900000000, assessmentId=AP English Literature and Composition} |

  And I should see "Processed 130 records." in the resulting batch job file
  And I should see "InterchangeStudent.xml records considered: 78" in the resulting batch job file
  And I should see "InterchangeStudent.xml records ingested successfully: 78" in the resulting batch job file
  And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata-AP-Eng.xml records considered: 2" in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata-AP-Eng.xml records ingested successfully: 2" in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata-AP-Eng.xml records failed: 0" in the resulting batch job file
  And I should see "InterchangeStudentAssessment-CgrayAP-English.xml records considered: 50" in the resulting batch job file
  And I should see "InterchangeStudentAssessment-CgrayAP-English.xml records ingested successfully: 50" in the resulting batch job file
  And I should see "InterchangeStudentAssessment-CgrayAP-English.xml records failed: 0" in the resulting batch job file
  And I should not see an error log file created
  
 
