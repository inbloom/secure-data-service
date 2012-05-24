@RALLY_US1889
Feature: DisciplineIncident Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "DisciplineIncident1.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | disciplineIncident          |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | disciplineIncident          | 2     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | disciplineIncident          | 1                   | body.incidentIdentifier     | Whack-a-mole            | string               |
     | disciplineIncident          | 1                   | body.incidentIdentifier     | Underwater cruise       | string               |
     | disciplineIncident          | 2                   | body.incidentLocation       | On School               | string               |
     | disciplineIncident          | 1                   | body.incidentDate           | 2011-02-01              | string               |
  And I should see "Processed 7 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "DisciplineIncident1.xml records considered: 2" in the resulting batch job file
  And I should see "DisciplineIncident1.xml records ingested successfully: 2" in the resulting batch job file
  And I should see "DisciplineIncident1.xml records failed: 0" in the resulting batch job file

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Populated Database
Given I post "DisciplineIncident2.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | disciplineIncident          | 4     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | disciplineIncident          | 1                   | body.incidentIdentifier     | Whack-a-mole            | string               |
     | disciplineIncident          | 1                   | body.incidentIdentifier     | Underwater cruise       | string               |
     | disciplineIncident          | 1                   | body.incidentIdentifier     | Bullying                | string               |
     | disciplineIncident          | 1                   | body.incidentIdentifier     | Hazing                  | string               |
     | disciplineIncident          | 2                   | body.incidentLocation       | On School               | string               |
     | disciplineIncident          | 2                   | body.incidentDate           | 2011-02-01              | string               |
     | disciplineIncident          | 2                   | body.weapons                | Non-Illegal Knife       | string               |
  And I should see "Processed 2 records." in the resulting batch job file
  And I should not see an error log file created
    And I should see "DisciplineIncident2.xml records considered: 2" in the resulting batch job file
  And I should see "DisciplineIncident2.xml records ingested successfully: 2" in the resulting batch job file
  And I should see "DisciplineIncident2.xml records failed: 0" in the resulting batch job file
