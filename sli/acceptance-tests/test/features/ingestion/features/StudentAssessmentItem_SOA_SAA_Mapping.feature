Feature: Test StudentAssessmentItem Mapping and StudentAssessments/StudentObjectiveAssessments and AssessmentItems 

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all studentassessmentitems interchanges as a payload of the ingestion job: Clean Database
Given I post "StudentAssessmentItem_ACTAssessment_Mapping.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                         |
     | studentAssessmentAssociation           |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                 | count |
     | studentAssessmentAssociation   | 1     |
   And I check to find if record is in collection:
     | collectionName                         | expectedRecordCount | searchParameter                                                                                  | searchValue                    |
     #     | studentAssessmentAssociation           | 1                   | body.studentAssessmentItems.assessmentItem.identificationCode                                   | AssessmentItem-2               |
     #     | studentAssessmentAssociation           | 1                   | body.studentAssessmentItems.0.StudentObjectiveAssessment.identificationCode                       | SOA_ACT-Math-Pre-Algebral_1    |
     #| studentAssessmentAssociation           | 1                   | body.studentAssessmentItems.assessmentItem.identificationCode                                   | AssessmentItem-1               |
     #| studentAssessmentAssociation           | 1                   | body.studentAssessmentItems.1.StudentObjectiveAssessment.identificationCode                       | SOA_ACT-Math-Plane-Geometry_1  |
     #| studentAssessmentAssociation           | 1                   | body.studentAssessmentItems.assessmentItem.identificationCode                                   | AssessmentItem-3               |
     #| studentAssessmentAssociation           | 1                   | body.studentAssessmentItems.2.StudentObjectiveAssessment.identificationCode                       | SOA_ACT-Math-Pre-Algebral_1    |
     | studentAssessmentAssociation           | 1                   | body.studentAssessmentItems.assessmentItem.identificationCode                                   | AssessmentItem-4               |
     #| studentAssessmentAssociation           | 1                   | body.studentAssessmentItems.3.StudentAssessment.identificationCode                                | saa_1                          |
     | studentAssessmentAssociation           | 1                   | body.studentAssessmentItems.assessmentItem.identificationCode                                   | AssessmentItem-3               |
     #| studentAssessmentAssociation           | 1                   | body.studentAssessmentItems.4.StudentAssessment.identificationCode                                | saa_1                          |
      
  And I should see "Processed 110 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "StudentAssessmentItem_ACTAssessmentItem_Mapping.xml records considered: 1" in the resulting batch job file
  And I should see "StudentAssessmentItem_ACTAssessmentItem_Mapping.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "StudentAssessmentItem_ACTAssessmentItem_Mapping.xml records failed: 0" in the resulting batch job file
