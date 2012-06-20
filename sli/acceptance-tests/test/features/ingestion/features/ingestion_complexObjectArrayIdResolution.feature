Feature: StudentTranscriptAssociation Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
	And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "complexObjectArrayIdResolutionTest.zip" file as the payload of the ingestion job
	And the following collections are empty in datastore:
	   | collectionName                |
	   | educationOrganization         |
	   | course                        |
	   | session                       |
	   | studentAcademicRecord         |
	   | studentTranscriptAssociation  |
When zip file is scp to ingestion landing zone
	And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
	   | collectionName                | count |
	   | educationOrganization         | 3     |
	   | course                        | 4     |
	   | session                       | 1     |
	   | studentAcademicRecord         | 1     |
	   | studentTranscriptAssociation  | 2     |
	 And I check to find if record is in collection:
	   | collectionName                | expectedRecordCount | searchParameter                       | searchValue          | searchType           |
	   | studentTranscriptAssociation  | 1                   | body.creditsEarned.credit             | 1                    | integer              |
	   | studentTranscriptAssociation  | 1                   | body.creditsEarned.credit          | 2                    | integer              |
	   | studentTranscriptAssociation  | 0                   | body.creditsEarned.credit          | 3                    | integer              |
	   | studentTranscriptAssociation  | 0                   | body.creditsEarned.credit          | 4                    | integer              |
	   | studentTranscriptAssociation  | 0                   | body.creditsEarned.credit           | 5                    | integer              |
	And I should see "Processed 16 records." in the resulting batch job file

