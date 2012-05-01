@wip
Feature: Test StudentAssessmentItem Mapping and StudentAssessments/StudentObjectiveAssessments and AssessmentItems 
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
     | studentAssessmentAssociation           | 1                   | body.studentAssessmentItem.0.AssessmentItem.identificationCode                                   | AssessmentItem-2               |
     | studentAssessmentAssociation           | 1                   | body.studentAssessmentItem.0.StudentObjectiveAssessment.identificationCode                       | SOA_ACT-Math-Pre-Algebral_1    |
     | studentAssessmentAssociation           | 1                   | body.studentAssessmentItem.1.AssessmentItem.identificationCode                                   | AssessmentItem-1               |
     | studentAssessmentAssociation           | 1                   | body.studentAssessmentItem.1.StudentObjectiveAssessment.identificationCode                       | SOA_ACT-Math-Plane-Geometry_1  |
     | studentAssessmentAssociation           | 1                   | body.studentAssessmentItem.2.AssessmentItem.identificationCode                                   | AssessmentItem-3               |
     | studentAssessmentAssociation           | 1                   | body.studentAssessmentItem.2.StudentObjectiveAssessment.identificationCode                       | SOA_ACT-Math-Pre-Algebral_1    |
     | studentAssessmentAssociation           | 1                   | body.studentAssessmentItem.3.AssessmentItem.identificationCode                                   | AssessmentItem-4               |
     | studentAssessmentAssociation           | 1                   | body.studentAssessmentItem.3.StudentAssessment.identificationCode                                | saa_1                          |
     | studentAssessmentAssociation           | 1                   | body.studentAssessmentItem.4.AssessmentItem.identificationCode                                   | AssessmentItem-3               |
     | studentAssessmentAssociation           | 1                   | body.studentAssessmentItem.4.StudentAssessment.identificationCode                                | saa_1                          |
      
  And I should see "Processed 8 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "StudentAssessmentItem_ACTAssessment_Mapping.xml records considered: 8" in the resulting batch job file
  And I should see "StudentAssessmentItem_ACTAssessment_Mapping.xml records ingested successfully: 8" in the resulting batch job file
  And I should see "StudentAssessmentItem_ACTAssessment_Mapping.xml records failed: 0" in the resulting batch job file