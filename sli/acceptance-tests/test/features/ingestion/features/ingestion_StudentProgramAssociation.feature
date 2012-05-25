@RALLY_US1876
Feature: StudentProgramAssociation Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "StudentProgramAssociation1.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | program                     |
     | student                     |
     | educationOrganization       |
     | studentProgramAssociation   |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | program                     | 3     |
     | student                     | 78    |
     | educationOrganization       | 3     |
     | studentProgramAssociation   | 10    |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | studentProgramAssociation   | 3                   | body.beginDate              | 2011-01-01              | string               |
     | studentProgramAssociation   | 3                   | body.beginDate              | 2011-03-01              | string               |
     | studentProgramAssociation   | 4                   | body.beginDate              | 2011-05-01              | string               |
     | studentProgramAssociation   | 1                   | body.endDate                | 2011-12-31              | string               |
     | studentProgramAssociation   | 2                   | body.endDate                | 2012-02-15              | string               |
  And I should see "Processed 94 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "Program1.xml records considered: 6" in the resulting batch job file
  And I should see "Program1.xml records ingested successfully: 6" in the resulting batch job file
  And I should see "Program1.xml records failed: 0" in the resulting batch job file

  And I should see "Student1.xml records considered: 78" in the resulting batch job file
  And I should see "Student1.xml records ingested successfully: 78" in the resulting batch job file
  And I should see "Student1.xml records failed: 0" in the resulting batch job file

  And I should see "StudentProgramAssociation1.xml records considered: 10" in the resulting batch job file
  And I should see "StudentProgramAssociation1.xml records ingested successfully: 10" in the resulting batch job file
  And I should see "StudentProgramAssociation1.xml records failed: 0" in the resulting batch job file


Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Populated Database
Given I post "StudentProgramAssociation2.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | program                     | 3     |
     | student                     | 78    |
     | educationOrganization       | 3     |
     | studentProgramAssociation   | 13    |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | studentProgramAssociation   | 4                   | body.beginDate              | 2011-01-01              | string               |
     | studentProgramAssociation   | 4                   | body.beginDate              | 2011-03-01              | string               |
     | studentProgramAssociation   | 5                   | body.beginDate              | 2011-05-01              | string               |
     | studentProgramAssociation   | 1                   | body.endDate                | 2011-12-31              | string               |
     | studentProgramAssociation   | 3                   | body.endDate                | 2012-02-15              | string               |
     | studentProgramAssociation   | 6                   | body.endDate                | 2012-04-12              | string               |
  And I should see "Processed 97 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "Program2.xml records considered: 6" in the resulting batch job file
  And I should see "Program2.xml records ingested successfully: 6" in the resulting batch job file
  And I should see "Program2.xml records failed: 0" in the resulting batch job file
  And I should see "Student2.xml records considered: 78" in the resulting batch job file
  And I should see "Student2.xml records ingested successfully: 78" in the resulting batch job file
  And I should see "Student2.xml records failed: 0" in the resulting batch job file
  And I should see "StudentProgramAssociation2.xml records considered: 13" in the resulting batch job file
  And I should see "StudentProgramAssociation2.xml records ingested successfully: 13" in the resulting batch job file
  And I should see "StudentProgramAssociation2.xml records failed: 0" in the resulting batch job file
