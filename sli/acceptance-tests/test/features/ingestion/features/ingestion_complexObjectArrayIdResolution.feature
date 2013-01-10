@wip
Feature: StudentTranscriptAssociation and CourseOffering Ingestion Test - CHANGES TO REFERENCE RESOLUTION MAKE THIS TEST UNNECESSARY

Background: I have a landing zone route configured
Given I am using local data store
	And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "complexObjectArrayIdResolutionTest.zip" file as the payload of the ingestion job
	And the following collections are empty in datastore:
	   | collectionName                |
	   | educationOrganization         |
	   | course                        |
	   | courseOffering                |
	   | gradingPeriod                 |
	   | session                       |
	   | studentAcademicRecord         |
	   | courseTranscript              |
When zip file is scp to ingestion landing zone
    And a batch job for file "complexObjectArrayIdResolutionTest.zip" is completed in database
	And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
	   | collectionName                | count |
	   | educationOrganization         | 3     |
	   | course                        | 6     |
	   | courseOffering                | 2     |
	   | gradingPeriod                 | 1     |
	   | session                       | 1     |
	   | studentAcademicRecord         | 1     |
	   | courseTranscript              | 3     |
	 And I check to find if record is in collection:
	   | collectionName                | expectedRecordCount | searchParameter                       | searchValue          | searchType           |
	   | courseTranscript              | 1                   | body.creditsEarned.credit             | 1                    | integer              |
	   | courseTranscript              | 1                   | body.creditsEarned.credit          | 2                    | integer              |
	   | courseTranscript              | 1                   | body.creditsEarned.credit          | 9                    | integer              |
	   | courseTranscript              | 0                   | body.creditsEarned.credit          | 3                    | integer              |
	   | courseTranscript              | 0                   | body.creditsEarned.credit          | 4                    | integer              |
	   | courseTranscript              | 0                   | body.creditsEarned.credit           | 5                    | integer              |
	And I should see "Processed 21 records." in the resulting batch job file

