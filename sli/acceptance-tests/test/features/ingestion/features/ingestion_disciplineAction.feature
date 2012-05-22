Feature: DisciplineAction Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "DisciplineAction1.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | student                     |
     | staff                       |
     | educationOrganization       |
     | disciplineIncident          |
     | disciplineAction            |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | educationOrganization       | 4     |
     | staff                       | 3     |
     | student                     | 72    |
     | disciplineIncident          | 2     |
     | disciplineAction            | 2     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | disciplineAction            | 1                   | body.disciplineDate         | 2011-03-04              | string               |
     | disciplineAction            | 1                   | body.disciplineDate         | 2011-04-04              | string               |
     | disciplineAction            | 2                   | body.disciplineActionLength | 74                      | integer              |
  And I should see "Processed 83 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "DisciplineIncident1.xml records considered: 2" in the resulting batch job file
  And I should see "DisciplineIncident1.xml records ingested successfully: 2" in the resulting batch job file
  And I should see "DisciplineIncident1.xml records failed: 0" in the resulting batch job file
  And I should see "DisciplineAction1.xml records considered: 2" in the resulting batch job file
  And I should see "DisciplineAction1.xml records ingested successfully: 2" in the resulting batch job file
  And I should see "DisciplineAction1.xml records failed: 0" in the resulting batch job file

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Populated Database
Given I post "DisciplineAction2.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | disciplineIncident          | 5     |
     | disciplineAction            | 5     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | disciplineAction            | 1                   | body.disciplineDate         | 2011-03-04              | string               |
     | disciplineAction            | 1                   | body.disciplineDate         | 2011-04-04              | string               |
     | disciplineAction            | 3                   | body.disciplineDate         | 2011-05-04              | string               |
     | disciplineAction            | 3                   | body.disciplineActionLength | 74                      | integer              |
  And I should see "Processed 6 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "DisciplineIncident2.xml records considered: 3" in the resulting batch job file
  And I should see "DisciplineIncident2.xml records ingested successfully: 3" in the resulting batch job file
  And I should see "DisciplineIncident2.xml records failed: 0" in the resulting batch job file
  And I should see "DisciplineAction2.xml records considered: 3" in the resulting batch job file
  And I should see "DisciplineAction2.xml records ingested successfully: 3" in the resulting batch job file
  And I should see "DisciplineAction2.xml records failed: 0" in the resulting batch job file
