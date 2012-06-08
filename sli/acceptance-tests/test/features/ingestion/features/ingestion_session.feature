@RALLY_US934
@RALLY_US2285
Feature: Session Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "Session2.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | calendarDate                |
     | gradingPeriod               |
     | educationOrganization       |
     | session                     |
     | schoolSessionAssociation    |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count  |
     | calendarDate                | 5      |
     | educationOrganization       | 6      |
     | gradingPeriod               | 6      |
     | schoolSessionAssociation    | 10     |
     | session                     | 10     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                             | searchValue             | searchType           |
     | session                     | 1                   | body.sessionName                            | Fall 2011 Able School   | string               |
     | session                     | 1                   | body.schoolYear                             | 1997-1998               | string               |
     | session                     | 7                   | body.term                                   | Fall Semester           | string               |
     | session                     | 5                   | body.beginDate                              | 2011-09-06              | string               |
     | session                     | 0                   | body.endDate                                | 2011-12-16              | string               |
     | session                     | 5                   | body.endDate                                | 2011-12-23              | string               |
     | session                     | 5                   | body.totalInstructionalDays                 | 125                     | integer              |
     | session                     | 5                   | body.totalInstructionalDays                 | 80                      | integer              |
  And I should see "Processed 32 records." in the resulting batch job file
#  And I should not see an error log file created
  And I should see "Session2.xml records considered: 26" in the resulting batch job file
  And I should see "Session2.xml records ingested successfully: 26" in the resulting batch job file
  And I should see "Session2.xml records failed: 0" in the resulting batch job file
  And I should see "InterchangeEducationOrganization.xml records considered: 6" in the resulting batch job file 
  And I should see "InterchangeEducationOrganization.xml records ingested successfully: 6" in the resulting batch job file
  And I should see "InterchangeEducationOrganization.xml records failed: 0" in the resulting batch job file
  