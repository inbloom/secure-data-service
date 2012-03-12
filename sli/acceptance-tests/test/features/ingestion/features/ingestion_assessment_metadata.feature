Feature: Transformed Assessment Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "assessmentMetaData.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | assessment                  |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | assessment                  | 3     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                              | searchValue                                      |
     | assessment                  | 3                   | body.assessmentFamilyHierarchyName           | DIBELS.DIBELS Next.DIBELS Next Kindergarten      |
     | assessment                  | 1                   | body.assessmentPeriodDescriptor.codeValue    | BOY                                              |
     | assessment                  | 1                   | body.assessmentPeriodDescriptor.codeValue    | MOY                                              |
     | assessment                  | 1                   | body.assessmentPeriodDescriptor.codeValue    | EOY                                              |

  And I should see "Processed 3 records." in the resulting batch job file
  And I should not see an error log file created

