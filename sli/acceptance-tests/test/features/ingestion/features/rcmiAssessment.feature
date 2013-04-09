@wip
Feature: RCMI Assessment Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "rcmiAssessmentMetadata.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | assessment                  |
When zip file is scp to ingestion landing zone
  And a batch job for file "rcmiAssessmentMetadata.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | assessment                  | 2     |
And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                                | searchValue                                                       |
     | assessment                  | 3                   | body.assessmentFamilyHierarchyName             | Reading.Reading Comprehension Measurement Instrument Grade 1      |
     | assessment                  | 1                   | body.assessmentPeriodDescriptor.codeValue      | RCMI-EOY-2011                                                     |
     | assessment                  | 1                   | body.assessmentPeriodDescriptor.codeValue      | RCMI-MOY-2011                                                     |

  And I should see "Processed 6 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "readingLevelAssessmentMetadata.xml records considered for processing: 6" in the resulting batch job file
  And I should see "readingLevelAssessmentMetadata.xml records ingested successfully: 6" in the resulting batch job file
  And I should see "readingLevelAssessmentMetadata.xml records failed processing: 0" in the resulting batch job file