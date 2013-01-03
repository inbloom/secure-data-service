Feature: StudentSectionAssociation Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "StudentSectionAssociation1.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | StudentSectionAssociation                     |
When zip file is scp to ingestion landing zone
  And "10" seconds have elapsed
  And a batch job for file "StudentSectionAssociation1.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | StudentSectionAssociation   | 5     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             |
     | StudentSectionAssociation   | 1                   | body.StudentUniqueStateId   | 900000001               |
     | StudentSectionAssociation   | 1                   | body.StateOrganizationId    | ENG_8_1                 |
     | StudentSectionAssociation   | 1                   | body.UniqueSectionCode      | ENG_8_1                 |
     | StudentSectionAssociation   | 5                   | body.BeginDate              | 1967-08-13              |
     | StudentSectionAssociation   | 5                   | body.EndDate                | 1967-08-15              |
     | StudentSectionAssociation   | 5                   | body.HomeroomIndicator      | true                    |
     | StudentSectionAssociation   | 1                   | body.RepeatIdentifier       | Other                   |

  And I should see "Processed 5 records." in the resulting batch job file
  And I should not see an error log file created
  And I should not see a warning log file created

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Populated Database
Given I post "StudentSectionAssociation2.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And "10" seconds have elapsed
  And a batch job for file "StudentSectionAssociation2.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | StudentSectionAssociation   | 5     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             |
     | StudentSectionAssociation   | 1                   | body.StudentUniqueStateId   | 900000001               |
     | StudentSectionAssociation   | 1                   | body.StateOrganizationId    | ENG_8_1                 |
     | StudentSectionAssociation   | 1                   | body.UniqueSectionCode      | ENG_8_1                 |
     | StudentSectionAssociation   | 5                   | body.BeginDate              | 2005-08-13              |
     | StudentSectionAssociation   | 5                   | body.EndDate                | 2009-08-15              |
     | StudentSectionAssociation   | 5                   | body.HomeroomIndicator      | true                    |
     | StudentSectionAssociation   | 3                   | body.RepeatIdentifier       | Other                   |
  And I should see "Processed 5 records." in the resulting batch job file
  And I should not see an error log file created
  And I should not see a warning log file created
