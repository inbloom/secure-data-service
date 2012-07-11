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
     | studentAssessmentAssociation |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName               | count |
     | student                      | 350   |
     | studentAssessmentAssociation | 3500  |
When I run the aggregation job
   Then I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                                                         | searchValue |
     | student                     | 110                 | aggregations.assessments.Grade 7 2011 State Math.HighestEver.ScaleScore | 32.0          |
