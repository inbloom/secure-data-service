@RALLY_2875
Feature: Aggregate highest ever assessments

Background: I have a landing zone route configured
Given I am using the general data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "aggregationData.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName               |
     | assessment                   |
     | student                      |
     | studentSchoolAssociation     |
     | studentSectionAssociation     |
     | studentAssessmentAssociation |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName               | count |
     | student                      | 350   |
     | studentSchoolAssociation     | 350   |
     | studentSectionAssociation    | 350   |
     | studentAssessmentAssociation | 3500  |
When I run the highest ever aggregation job
   Then I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                                                         | searchValue |
     | student                     | 110                 | aggregations.assessments.Grade 7 2011 State Math.HighestEver.ScaleScore | 32.0        |
   And for the student with "body.studentUniqueStateId" set to "0", "aggregations.assessments.Grade 7 2011 State Math.HighestEver.ScaleScore" is "32.0"
   And for the student with "body.studentUniqueStateId" set to "1", "aggregations.assessments.Grade 7 2011 State Math.HighestEver.ScaleScore" is "27.0"
   And for the student with "body.studentUniqueStateId" set to "2", "aggregations.assessments.Grade 7 2011 State Math.HighestEver.ScaleScore" is "30.0"
   #When I run the proficiency aggregation job
   #Then for the school with "body.educationOrgIdentificationCode.ID" set to "East Daybreak Junior High", "aggregations.proficiency.Grade 7 2011 State Math.E" is "296"
   #Then for the school with "body.educationOrgIdentificationCode.ID" set to "East Daybreak Junior High", "aggregations.proficiency.Grade 7 2011 State Math.S" is "52"
   #Then for the school with "body.educationOrgIdentificationCode.ID" set to "East Daybreak Junior High", "aggregations.proficiency.Grade 7 2011 State Math.B" is "2"
