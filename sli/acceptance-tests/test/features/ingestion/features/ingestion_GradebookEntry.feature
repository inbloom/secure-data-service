@RALLY_US149
Feature: Gradebook Entry Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
	And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "GradebookEntry1.zip" file as the payload of the ingestion job
	And the following collections are empty in datastore:
	   | collectionName                |
	   | gradebookEntry                |
When zip file is scp to ingestion landing zone
	And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
	   | collectionName                | count |
	   | gradebookEntry                | 6     |
	 And I check to find if record is in collection:
	   | collectionName                | expectedRecordCount | searchParameter              | searchValue             | searchType           |
	   | gradebookEntry                | 1                   | body.dateAssigned            | 2011-09-15              | string               |
	   | gradebookEntry                | 2                   | body.dateAssigned            | 2011-09-29              | string               |
	   | gradebookEntry                | 2                   | body.dateAssigned            | 2011-10-13              | string               |
	   | gradebookEntry                | 1                   | body.dateAssigned            | 2011-10-27              | string               |
	   | gradebookEntry                | 6                   | body.gradebookEntryType      | Unit test               | string               |

	And I should see "Processed 18 records." in the resulting batch job file
	And I should not see an error log file created

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Populated Database
Given I post "GradebookEntry2.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
	And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
	   | collectionName                | count |
	   | gradebookEntry                | 6     |
	 And I check to find if record is in collection:
	   | collectionName                | expectedRecordCount | searchParameter              | searchValue             | searchType           |
	   | gradebookEntry                | 1                   | body.dateAssigned            | 2011-09-15              | string               |
	   | gradebookEntry                | 2                   | body.dateAssigned            | 2011-09-29              | string               |
	   | gradebookEntry                | 3                   | body.dateAssigned            | 2011-10-13              | string               |
	   | gradebookEntry                | 0                   | body.dateAssigned            | 2011-10-27              | string               |
	   | gradebookEntry                | 6                   | body.gradebookEntryType      | Unit test               | string               |
	   
	And I should see "Processed 6 records." in the resulting batch job file
	And I should not see an error log file created
