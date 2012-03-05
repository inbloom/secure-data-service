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
	And "10" seconds have elapsed
Then I should see following map of entry counts in the corresponding collections:
	   | collectionName              | count |
	   | assessment                  | 1     |
	 And I check to find if record is in collection:
	   | collectionName              | expectedRecordCount | searchParameter                       | searchValue                               |
	   | assessment                  | 1                   | body.assessmentFamilyHierarchyName    | DIBELS Next.DIBELS Next Kindergarten      |

	And I should see "Processed 1 records." in the resulting batch job file


	