Feature: Session Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "ingestion_IDRef.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | student                     |
     | assessment                  |
     | studentAssessmentAssociation|
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student	                   |  78   |
     | assessment                  |  2    |
     | studentAssessmentAssociation|  3    |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | studentAssessmentAssociation| 3                   | body.administrationDate     | 2011-05-01   | string               |
     | studentAssessmentAssociation| 3                   | body.administrationLanguage| English           | string               |
  # ENCRYPTED FIELDS
  And I should see "Processed 88 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "InterchangeStudentAssessmentWoIDRef.xml records considered: 3" in the resulting batch job file
  And I should see "InterchangeStudentAssessmentWoIDRef.xml records ingested successfully: 3" in the resulting batch job file
  And I should see "InterchangeStudentAssessmentWoIDRef.xml records failed: 0" in the resulting batch job file

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "ingestion_MissingIDRef.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | student                     |
     | assessment                  |
     | studentAssessmentAssociation|
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student	                   |  78   |
     | assessment                  |  2    |
     | studentAssessmentAssociation|  0    |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | studentAssessmentAssociation| 0                   | body.administrationDate     | 2011-05-01   | string               |
     | studentAssessmentAssociation| 0                   | body.administrationLanguage| English           | string               |
  # ENCRYPTED FIELDS
  And I should see "Processed 88 records." in the resulting batch job file
  And I should see "Errors found for input file InterchangeStudentAssessmentMissingIDRef.xml
  And I should see "InterchangeStudentAssessmentWoIDRef.xml records ingested successfully: 0" in the resulting batch job file
  And I should see "InterchangeStudentAssessmentWoIDRef.xml records failed: 3" in the resulting batch job file
