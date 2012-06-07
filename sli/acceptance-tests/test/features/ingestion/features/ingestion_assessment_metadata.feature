@RALLY_US1390 @RALLY_US632
Feature: Transformed Assessment Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "assessmentMetaData.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | assessment                  |
     | studentAssessmentAssociation|
     | learningStandard            |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | assessment                  | 5     |
     | studentAssessmentAssociation| 4     |
     | learningStandard            | 22    |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                                | searchValue                                      |  searchType |
     | assessment                  | 3                   | body.assessmentFamilyHierarchyName             | READ2.READ 2.0.READ 2.0 Kindergarten      | string |
     | assessment                  | 1                   | body.assessmentPeriodDescriptor.codeValue      | BOY                                              | string |
     | assessment                  | 1                   | body.assessmentPeriodDescriptor.codeValue      | MOY                                              | string |
     | assessment                  | 1                   | body.assessmentPeriodDescriptor.codeValue      | EOY                                              | string |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode    | SAT-Writing                                      | string |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode    | SAT-Math                                         | string |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode    | SAT-Critical Reading                             | string |
     | assessment                  | 1                   | body.objectiveAssessment.objectiveAssessments.identificationCode    | SAT-Math-Arithmetic         | string |
     | assessment                  | 1                   | body.objectiveAssessment.objectiveAssessments.identificationCode    | SAT-Math-Algebra            | string |
     | assessment                  | 1                   | body.objectiveAssessment.objectiveAssessments.identificationCode    | SAT-Math-Geometry           | string |
   
	| assessment                  | 1                   | body.objectiveAssessment.identificationCode    | ACT-English          | string |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode    | ACT-Reading          | string |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode    | ACT-Mathematics      | string |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode    | ACT-Science          | string |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode    | ACT-Writing          | string |
   
    | assessment                  | 1                   | body.objectiveAssessment.assessmentPerformanceLevel.performanceLevelDescriptor.codeValue      | act1                         | string |
    | assessment                  | 1                   | body.objectiveAssessment.assessmentPerformanceLevel.performanceLevelDescriptor.description    | American Literature I          | string |
   
   
     | studentAssessmentAssociation| 2                   | body.studentObjectiveAssessments.objectiveAssessment.identificationCode    | SAT-Writing          | string |
     | studentAssessmentAssociation| 2                   | body.studentObjectiveAssessments.objectiveAssessment.identificationCode    | SAT-Math             | string |
     | studentAssessmentAssociation| 2                   | body.studentObjectiveAssessments.objectiveAssessment.identificationCode    | SAT-Critical Reading | string |
     | studentAssessmentAssociation| 2                   | body.studentObjectiveAssessments.objectiveAssessment.identificationCode    | SAT-Math-Arithmetic  | string |
     | studentAssessmentAssociation| 2                   | body.studentObjectiveAssessments.objectiveAssessment.identificationCode    | SAT-Math-Algebra     | string |
     | studentAssessmentAssociation| 2                   | body.studentObjectiveAssessments.objectiveAssessment.identificationCode    | SAT-Math-Geometry    | string |
     | learningStandard            | 6                   | body.subjectArea                               | ELA                                              | string |
     | assessment                  | 1                   | body.assessmentItem.0.identificationCode       | AssessmentItem-1 | string |
     | assessment                  | 1                   | body.assessmentItem.0.itemCategory             | True-False       | string |
     | assessment                  | 1                   | body.assessmentItem.0.maxRawScore              | 5                | integer |
     | assessment                  | 1                   | body.assessmentItem.0.correctResponse          | False            | string |
     | assessment                  | 1                   | body.assessmentItem.1.identificationCode       | AssessmentItem-2 | string |
     | assessment                  | 1                   | body.assessmentItem.1.itemCategory             | True-False       | string |
     | assessment                  | 1                   | body.assessmentItem.1.maxRawScore              | 5                | integer |
     | assessment                  | 1                   | body.assessmentItem.1.correctResponse          | True             | string |
     | assessment                  | 1                   | body.assessmentItem.2.identificationCode       | AssessmentItem-3 | string |
     | assessment                  | 1                   | body.assessmentItem.2.itemCategory             | True-False       | string |
     | assessment                  | 1                   | body.assessmentItem.2.maxRawScore              | 5                | integer |
     | assessment                  | 1                   | body.assessmentItem.2.correctResponse          | True             | string |
     | assessment                  | 1                   | body.assessmentItem.3.identificationCode       | AssessmentItem-4 | string |
     | assessment                  | 1                   | body.assessmentItem.3.itemCategory             | True-False       | string |
     | assessment                  | 1                   | body.assessmentItem.3.maxRawScore              | 5                | integer |
     | assessment                  | 1                   | body.assessmentItem.3.correctResponse          | False            | string |
     # | studentAssessmentAssociation           | 1                   | body.studentAssessmentItem.0.AssessmentItem.identificationCode                                   | AssessmentItem-2               |
     # | studentAssessmentAssociation           | 1                   | body.studentAssessmentItem.0.StudentObjectiveAssessment.identificationCode                       | SOA_ACT-Math-Pre-Algebral_1    |
     # | studentAssessmentAssociation           | 1                   | body.studentAssessmentItem.1.AssessmentItem.identificationCode                                   | AssessmentItem-1               |
     # | studentAssessmentAssociation           | 1                   | body.studentAssessmentItem.1.StudentObjectiveAssessment.identificationCode                       | SOA_ACT-Math-Plane-Geometry_1  |
     # | studentAssessmentAssociation           | 1                   | body.studentAssessmentItem.2.AssessmentItem.identificationCode                                   | AssessmentItem-3               |
     # | studentAssessmentAssociation           | 1                   | body.studentAssessmentItem.2.StudentObjectiveAssessment.identificationCode                       | SOA_ACT-Math-Pre-Algebral_1    |
     | studentAssessmentAssociation           | 1                   | body.studentAssessmentItems.0.assessmentItem.identificationCode | AssessmentItem-4    | string |
     | studentAssessmentAssociation           | 1                   | body.studentAssessmentItems.0.assessmentResponse                | True                | string |
     | studentAssessmentAssociation           | 1                   | body.studentAssessmentItems.1.assessmentItem.identificationCode | AssessmentItem-3    | string |
     | studentAssessmentAssociation           | 1                   | body.studentAssessmentItems.1.assessmentResponse                | True                | string |
     

  And I should see "Processed 32 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "dibelsAssessmentMetadata.xml records considered: 3" in the resulting batch job file
  And I should see "dibelsAssessmentMetadata.xml records ingested successfully: 3" in the resulting batch job file
  And I should see "dibelsAssessmentMetadata.xml records failed: 0" in the resulting batch job file
  And I should see "satAssessmentMetadata.xml records considered: 1" in the resulting batch job file
  And I should see "satAssessmentMetadata.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "satAssessmentMetadata.xml records failed: 0" in the resulting batch job file
  And I should see "actAssessmentMetadata.xml records considered: 1" in the resulting batch job file
  And I should see "actAssessmentMetadata.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "actAssessmentMetadata.xml records failed: 0" in the resulting batch job file
  And I should see "InterchangeStudent.xml records considered: 1" in the resulting batch job file
  And I should see "InterchangeStudent.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file
  And I should see "InterchangeStudentAssessment.xml records considered: 3" in the resulting batch job file
  And I should see "InterchangeStudentAssessment.xml records ingested successfully: 3" in the resulting batch job file
  And I should see "InterchangeStudentAssessment.xml records failed: 0" in the resulting batch job file
  And I should see "actStudentAssessment.xml records considered: 1" in the resulting batch job file
  And I should see "actStudentAssessment.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "actStudentAssessment.xml records failed: 0" in the resulting batch job file
  And I should see "basicStandards.xml records considered: 6" in the resulting batch job file
  And I should see "basicStandards.xml records ingested successfully: 6" in the resulting batch job file
  And I should see "basicStandards.xml records failed: 0" in the resulting batch job file
  
