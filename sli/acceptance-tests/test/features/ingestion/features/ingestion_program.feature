@RALLY_US1876
Feature: Program Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "Program1.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | program                     |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | program                     | 3     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | program                     | 1                   | body.programId              | ACC-TEST-PROG-1         | string               |
     | program                     | 1                   | body.programId              | ACC-TEST-PROG-2         | string               |
     | program                     | 1                   | body.programId              | ACC-TEST-PROG-3         | string               |
     | program                     | 1                   | body.programType            | Regular Education       | string               |
     | program                     | 2                   | body.programType            | Remedial Education      | string               |
     | program                     | 2                   | body.programSponsor         | State Education Agency  | string               |
     | program                     | 1                   | body.programSponsor         | Local Education Agency  | string               |
  And I should see "Processed 3 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "Program1.xml records considered: 3" in the resulting batch job file
  And I should see "Program1.xml records ingested successfully: 3" in the resulting batch job file
  And I should see "Program1.xml records failed: 0" in the resulting batch job file

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Populated Database
Given I post "Program2.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | program                     | 4     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | program                     | 1                   | body.programId              | ACC-TEST-PROG-1         | string               |
     | program                     | 1                   | body.programId              | ACC-TEST-PROG-2         | string               |
     | program                     | 1                   | body.programId              | ACC-TEST-PROG-3         | string               |
     | program                     | 1                   | body.programId              | ACC-TEST-PROG-4         | string               |
     | program                     | 3                   | body.programType            | Regular Education       | string               |
     | program                     | 1                   | body.programType            | Remedial Education      | string               |
     | program                     | 1                   | body.programSponsor         | State Education Agency  | string               |
     | program                     | 3                   | body.programSponsor         | Local Education Agency  | string               |
  And I should see "Processed 4 records." in the resulting batch job file
  And I should not see an error log file created
    And I should see "Program2.xml records considered: 4" in the resulting batch job file
  And I should see "Program2.xml records ingested successfully: 4" in the resulting batch job file
  And I should see "Program2.xml records failed: 0" in the resulting batch job file
