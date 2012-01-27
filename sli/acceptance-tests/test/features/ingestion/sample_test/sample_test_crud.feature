Feature: <US634> Sample Ingestion Gherkin Test

Background: I have a landing zone route configured
Given I am using local data store
	And I am using preconfigured Ingestion Landing Zone 
	And I connect to "sli" database

Scenario: Post a zip file of 100 students as a payload of the ingestion job
Given I post "validStudents100.zip" as the payload of the ingestion job
	And there are no students in DS
When zip file is scp to ingestion landing zone
	And "10" seconds have elapsed
Then I should see "100" entries in the student collection	
	And I should see "Processed 100 records." in the resulting batch job file

Scenario: Post a zip file of 1000 students as a payload of the ingestion job
Given I post "validStudents1000.zip" as the payload of the ingestion job
	And there are no students in DS
When zip file is scp to ingestion landing zone
	And "10" seconds have elapsed
Then I should see "1000" entries in the student collection	
	And I should see "Processed 1000 records." in the resulting batch job file
