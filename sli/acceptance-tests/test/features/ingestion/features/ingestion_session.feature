Feature: Session Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
	And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "Session.zip" file as the payload of the ingestion job
	And the following collections are empty in datastore:
        | collectionName              |
        | session                     |
When zip file is scp to ingestion landing zone
	And "10" seconds have elapsed
Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | session                     | 6     |
     And I check to find if record is in collection:
	   | collectionName              | expectedRecordCount | searchParameter          | searchValue          |
	   | session                     | 1                   | body.sessionName         | "Fall 2011"          |
	   | session                     | 1                   | body.sessionName         | "Spring 2012"        |
	   | session                     | 1                   | body.sessionName         | "Fall 2012"          |
	   | session                     | 1                   | body.sessionName         | "Spring 2013"        |
	   | session                     | 1                   | body.sessionName         | "Fall 2013"          |
	   | session                     | 1                   | body.sessionName         | "Spring 2014"        |
	And I should see "Processed 6 records." in the resulting batch job file

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Populated Database
Given I post "Session.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
	And "10" seconds have elapsed
Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | session                     | 6     |
     And I check to find if record is in collection:
	   | collectionName              | expectedRecordCount | searchParameter          | searchValue          |
	   | session                     | 1                   | body.sessionName         | "Fall 2011"          |
	   | session                     | 1                   | body.sessionName         | "Spring 2012"        |
	   | session                     | 1                   | body.sessionName         | "Fall 2012"          |
	   | session                     | 1                   | body.sessionName         | "Spring 2013"        |
	   | session                     | 1                   | body.sessionName         | "Fall 2013"          |
	   | session                     | 1                   | body.sessionName         | "Spring 2014"        |
	And I should see "Processed 6 records." in the resulting batch job file
