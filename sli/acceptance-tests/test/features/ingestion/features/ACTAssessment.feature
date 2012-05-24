@RALLY_US1945
Feature: ACT Assessment Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "actAssessmentMetadata.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | assessment                  |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | assessment                  | 1     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                                                     | searchValue                                      |
     | assessment                  | 1                   | body.assessmentFamilyHierarchyName                                  | ACT                                              |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode                         | ACT-English                                      |
     | assessment                  | 1                   | body.objectiveAssessment.objectiveAssessments.identificationCode    | ACT-English-Usage                                |
     | assessment                  | 1                   | body.objectiveAssessment.objectiveAssessments.identificationCode    | ACT-English-Rhetorical                           |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode                         | ACT-Mathematics                                  |
     | assessment                  | 1                   | body.objectiveAssessment.objectiveAssessments.identificationCode    | ACT-Math-Pre-Algebra                             |
     | assessment                  | 1                   | body.objectiveAssessment.objectiveAssessments.identificationCode    | ACT-Math-Algebra                                 |
     | assessment                  | 1                   | body.objectiveAssessment.objectiveAssessments.identificationCode    | ACT-Math-Plane-Geometry                          |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode                         | ACT-Reading                                      |
     | assessment                  | 1                   | body.objectiveAssessment.objectiveAssessments.identificationCode    | ACT-Reading-SocialStudies                        |
     | assessment                  | 1                   | body.objectiveAssessment.objectiveAssessments.identificationCode    | ACT-Reading-Arts                                 |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode                         | ACT-Science                                      |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode                         | ACT-Writing                                      |

  And I should see "Processed 1 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "InterchangeAssessmentMetadata-ACT.xml records considered: 1" in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata-ACT.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata-ACT.xml records failed: 0" in the resulting batch job file
  
  

Scenario: Post a zip file containing all student assessment interchanges as a payload of the ingestion job: Clean Database
Given I post "actStudentAssessment.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                         |
     | student                                |
     | assessment                             |
     | studentAssessmentAssociation           |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                 | count |
     | student                        | 78    |
     | assessment                     | 1     |
     | studentAssessmentAssociation   | 1     |
   And I check to find if record is in collection:
     | collectionName                         | expectedRecordCount | searchParameter                                                                                  | searchValue                  |
     | studentAssessmentAssociation           | 1                   | body.studentObjectiveAssessments.0.objectiveAssessment.identificationCode                        | ACT-English                  |
     | studentAssessmentAssociation           | 1                   | body.studentObjectiveAssessments.0.objectiveAssessment.objectiveAssessments.0.identificationCode | ACT-English-Usage            |
     | studentAssessmentAssociation           | 1                   | body.studentObjectiveAssessments.0.objectiveAssessment.objectiveAssessments.1.identificationCode | ACT-English-Rhetorical       |
     | studentAssessmentAssociation           | 1                   | body.studentObjectiveAssessments.0.scoreResults.0.result                                         | 18                           |
     | studentAssessmentAssociation           | 1                   | body.studentObjectiveAssessments.0.scoreResults.0.assessmentReportingMethod                      | Scale score                  |
     | studentAssessmentAssociation           | 1                   | body.studentObjectiveAssessments.1.objectiveAssessment.identificationCode                        | ACT-English-Usage            |
     | studentAssessmentAssociation           | 1                   | body.studentObjectiveAssessments.1.scoreResults.0.result                                         | 10                           |
     | studentAssessmentAssociation           | 1                   | body.studentObjectiveAssessments.1.scoreResults.0.assessmentReportingMethod                      | Scale score                  |
     | studentAssessmentAssociation           | 1                   | body.studentObjectiveAssessments.2.objectiveAssessment.identificationCode                        | ACT-English-Rhetorical       |
     | studentAssessmentAssociation           | 1                   | body.studentObjectiveAssessments.2.scoreResults.0.result                                         | 8                            |
     | studentAssessmentAssociation           | 1                   | body.studentObjectiveAssessments.2.scoreResults.0.assessmentReportingMethod                      | Scale score                  |
     
     
    
  And I should see "Processed 80 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "InterchangeStudentAssessment-Cgray-ACT.xml records considered: 1" in the resulting batch job file
  And I should see "InterchangeStudentAssessment-Cgray-ACT.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "InterchangeStudentAssessment-Cgray-ACT.xml records failed: 0" in the resulting batch job file
  
  
  
  
  
  
  
  
  
