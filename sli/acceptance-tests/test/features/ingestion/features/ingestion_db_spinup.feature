Feature: Tenant database spin up

Background: I have a landing zone route configured
Given I am using local data store


Scenario: First ingestion for a new tenant
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the tenant database does not exist
    And I post "tenant.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  	And a batch job log has been created
Then I should see following map of indexes in the corresponding collections:
     | collectionName                             | index                                                                |
     | section                                    | body.schoolId_1                                                      |
     | student                                    | body.studentUniqueStateId_1                                          |
     | teacherSchoolAssociation                   | body.schoolId_1                                                      |
    And the database is sharded for the following collections
     | collectionName	|
     | student 			|
     | assessment		|