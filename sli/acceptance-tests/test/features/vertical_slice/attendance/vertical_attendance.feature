@wip
Feature: End-to-end Attendance Events
As a educational worker, I want to have an ingestion job import my attendance Ed-Fi interchange and then be able to see my student's attendance in the dashboard app.

Scenario: Ingest the attendance data

	Given I am using the data store "/../ingestion_data/"
	And I am using preconfigured Ingestion Landing Zone
	And I post "AttendanceDemoData.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
	And a batch job log has been created
	Then I should see "Processed 11856 records." in the resulting batch job file
	And the ingestion job should be successful

@wip	
Scenario: View the ingested data with the Dashboard

	Given I have an open web browser
	And the ingestion job was successful
	When soemthing
	Then Something