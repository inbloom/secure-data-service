Feature: <US634> Sample Ingestion Gherkin Test

Background: I have a landing zone route configured
Given I am using local data store
	And I am using preconfigured Ingestion Landing Zone 
	And I connect to "sli" database

Scenario: Post a zip file as a payload of the ingestion job
Given I post "validXML.zip" as the payload of the ingestion job
	And there are no students in DS
When zip file is scp to ingestion landing zone
	And "10" seconds have elapsed
Then I should see "2" entries in the student collection	
	And I should see "Ingested 2 records into datastore." in the resulting batch job file


