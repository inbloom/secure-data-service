Feature: Education Organization Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
	And I am using preconfigured Ingestion Landing Zone
	And I connect to "sli" database

Scenario: Post a zip file containing a XML of 6 entities as a payload of the ingestion job
Given I post "educationOrganization_v1.zip" as the payload of the ingestion job
    And the payload contains entities of type "school"
	And there are none of this type of entity in the DS
When zip file is scp to ingestion landing zone
	And "10" seconds have elapsed
Then I should see "5" entries in the corresponding collection
	And I should see "Processed 5 records." in the resulting batch job file
