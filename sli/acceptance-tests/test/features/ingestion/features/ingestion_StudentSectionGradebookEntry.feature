@RALLY_US149
Feature: StudentGradebookEntry Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
	And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "StudentGradebookEntry1.zip" file as the payload of the ingestion job
	And the following collections are empty in datastore:
	   | collectionName                |
	   | studentGradebookEntry         |
When zip file is scp to ingestion landing zone
    And a batch job for file "StudentGradebookEntry1.zip" is completed in database
	And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
	   | collectionName                | count |
	   | studentGradebookEntry         | 75    |
	 And I check to find if record is in collection:
	   | collectionName                | expectedRecordCount | searchParameter              | searchValue             | searchType           |
	   | studentGradebookEntry         | 25                  | body.dateFulfilled           | 2011-09-29              | string               |
	   | studentGradebookEntry         | 25                  | body.dateFulfilled           | 2011-10-13              | string               |
	   | studentGradebookEntry         | 25                  | body.dateFulfilled           | 2011-10-27              | string               |
	   | studentGradebookEntry         | 5                   | body.numericGradeEarned      | 86                      | integer              |
	   | studentGradebookEntry         | 4                   | body.numericGradeEarned      | 88                      | integer              |

	And I should see "Processed 109 records." in the resulting batch job file
	And I should not see an error log file created
	And I should not see a warning log file created

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Populated Database
Given I post "StudentGradebookEntry2.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
	And a batch job log has been created
	And a batch job for file "StudentGradebookEntry2.zip" is completed in database
Then I should see following map of entry counts in the corresponding collections:
	   | collectionName                | count |
	   | studentGradebookEntry         | 75    |
	 And I check to find if record is in collection:
	   | collectionName                | expectedRecordCount | searchParameter              | searchValue             | searchType           |
	   | studentGradebookEntry         | 25                  | body.dateFulfilled           | 2011-09-29              | string               |
	   | studentGradebookEntry         | 25                  | body.dateFulfilled           | 2011-10-13              | string               |
	   | studentGradebookEntry         | 25                  | body.dateFulfilled           | 2011-10-27              | string               |
	   | studentGradebookEntry         | 4                   | body.numericGradeEarned      | 86                      | integer              |
	   | studentGradebookEntry         | 5                   | body.numericGradeEarned      | 88                      | integer              |
	   
	And I should see "Processed 75 records." in the resulting batch job file
	And I should not see an error log file created
	And I should not see a warning log file created
