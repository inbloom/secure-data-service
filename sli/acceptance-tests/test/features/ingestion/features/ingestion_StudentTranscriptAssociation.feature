Feature: StudentTranscriptAssociation Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
	And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "StudentTranscriptAssociation1.zip" file as the payload of the ingestion job
	And the following collections are empty in datastore:
	   | collectionName                |
	   | session                       |
	   | course                        |
	   | studentTranscriptAssociation  |
	   | studentAcademicRecord         |
When zip file is scp to ingestion landing zone
	And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
	   | collectionName                | count |
	   | session                       | 2     |
	   | course                        | 2     |
	   | studentTranscriptAssociation  | 5     |
	   | studentAcademicRecord         | 5     |
	 And I check to find if record is in collection:
	   | collectionName                | expectedRecordCount | searchParameter                       | searchValue             | searchType           |
	   | studentTranscriptAssociation  | 5                   | body.courseAttemptResult              | Pass                    | string               |
	   | studentTranscriptAssociation  | 1                   | body.finalNumericGradeEarned          | 90                      | integer              |
	   | studentTranscriptAssociation  | 1                   | body.finalNumericGradeEarned          | 87                      | integer              |
	   | studentTranscriptAssociation  | 0                   | body.finalNumericGradeEarned          | 82                      | integer              |
	   | studentTranscriptAssociation  | 2                   | body.finalLetterGradeEarned           | B                       | string               |
	   | studentTranscriptAssociation  | 5                   | body.gradeLevelWhenTaken              | Tenth grade             | string               |
       | studentAcademicRecord         | 5                   | body.cumulativeCreditsAttempted.credit| 5                       | integer              |

	And I should see "Processed 35 records." in the resulting batch job file
	And I should not see an error log file created


Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Populated Database
Given I post "StudentTranscriptAssociation2.zip" file as the payload of the ingestion job
And the following collections are empty in datastore:
	   | collectionName                |
	   | studentTranscriptAssociation  |
	   | studentAcademicRecord         |
When zip file is scp to ingestion landing zone
	And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
	   | collectionName                | count |
	   | studentTranscriptAssociation  | 6     |
	   | studentAcademicRecord         | 6     |
	 And I check to find if record is in collection:
	   | collectionName                | expectedRecordCount | searchParameter                       | searchValue             | searchType           |
	   | studentTranscriptAssociation  | 6                   | body.courseAttemptResult              | Pass                    | string               |
	   | studentTranscriptAssociation  | 1                   | body.finalNumericGradeEarned          | 90                      | integer              |
	   | studentTranscriptAssociation  | 1                   | body.finalNumericGradeEarned          | 87                      | integer              |
	   | studentTranscriptAssociation  | 1                   | body.finalNumericGradeEarned          | 82                      | integer              |
	   | studentTranscriptAssociation  | 3                   | body.finalLetterGradeEarned           | B                       | string               |
	   | studentTranscriptAssociation  | 6                   | body.gradeLevelWhenTaken              | Tenth grade             | string               |
       | studentAcademicRecord         | 6                   | body.cumulativeCreditsAttempted.credit| 5                       | integer              |
	   
	And I should see "Processed 12 records." in the resulting batch job file
	And I should not see an error log file created
