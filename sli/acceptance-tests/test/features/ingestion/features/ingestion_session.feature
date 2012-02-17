Feature: Session Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
	And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "Session1.zip" file as the payload of the ingestion job
	And the following collections are empty in datastore:
	   | collectionName              |
	   | session                     |
When zip file is scp to ingestion landing zone
	And "5" seconds have elapsed
Then I should see following map of entry counts in the corresponding collections:
	   | collectionName              | count |
	   | session                     | 50    |
	 And I check to find if record is in collection:
	   | collectionName              | expectedRecordCount | searchParameter             | searchValue             |
	   | session                     | 1                   | body.sessionName            | Fall 2011 Able School   |
	   | session                     | 10                  | body.schoolYear             | 2011-2012               |
	   | session                     | 25                  | body.term                   | Fall Semester           |
	   | session                     | 5                   | body.beginDate              | 2011-09-06              |
	   | session                     | 5                   | body.endDate                | 2011-12-16              |
	   | session                     | 0                   | body.endDate                | 2011-12-23              |
	   | session                     | 5                   | body.totalInstructionalDays | 75                      |
	   | session                     | 0                   | body.totalInstructionalDays | 80                      |
	And I should see "Processed 50 records." in the resulting batch job file

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Populated Database
Given I post "Session2.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
	And "5" seconds have elapsed
Then I should see following map of entry counts in the corresponding collections:
	   | collectionName              | count |
	   | session                     | 50    |
	 And I check to find if record is in collection:
	   | collectionName              | expectedRecordCount | searchParameter             | searchValue             |
	   | session                     | 1                   | body.sessionName            | Fall 2011 Able School   |
	   | session                     | 10                  | body.schoolYear             | 2011-2012               |
	   | session                     | 25                  | body.term                   | Fall Semester           |
	   | session                     | 5                   | body.beginDate              | 2011-09-06              |
	   | session                     | 0                   | body.endDate                | 2011-12-16              |
	   | session                     | 5                   | body.endDate                | 2011-12-23              |
	   | session                     | 0                   | body.totalInstructionalDays | 75                      |
	   | session                     | 5                   | body.totalInstructionalDays | 80                      |
	And I should see "Processed 50 records." in the resulting batch job file
