Feature: Demo Data Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
	And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "PI3-SPRINT1-V2.zip" file as the payload of the ingestion job
	And the following collections are empty in datastore:
        | collectionName              |
        | student                     |
        | studentSchoolAssociation    |
        | course                      |
        | educationOrganization       |
        | school                      |
        | section                     |
        | studentSectionAssociation   |
        | teacher                     |
        | teacherSchoolAssociation    |
        | teacherSectionAssociation   |
When zip file is scp to ingestion landing zone
	And "30" seconds have elapsed
Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 52    |
        | studentSchoolAssociation    | 87    |
        | course                      | 11    |
        | educationOrganization       | 3     |
        | school                      | 5     |
        | section                     | 19    |
        | studentSectionAssociation   | 83    |
        | teacher                     | 3     |
        | teacherSchoolAssociation    | 5     |
        | teacherSectionAssociation   | 19    |
     And I check to find if record is in collection:
	   | collectionName              | expectedRecordCount | searchParameter          | searchValue          | searchType           |
	   | student                     | 1                   | metaData.externalId      | 530425896            | string               |
	   | student                     | 1                   | metaData.externalId      | 784204643            | string               |
	   | teacher                     | 1                   | metaData.externalId      | cgray                | string               |
	   | course                      | 1                   | metaData.externalId      | 1st Grade Homeroom   | string               |
	And I should see "Processed 170 records." in the resulting batch job file

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Populated Database
Given I post "PI3-SPRINT1-V2.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
	And "30" seconds have elapsed
Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 52    |
        | studentSchoolAssociation    | 87    |
        | course                      | 11    |
        | educationOrganization       | 3     |
        | school                      | 5     |
        | section                     | 19    |
        | studentSectionAssociation   | 83    |
        | teacher                     | 3     |
        | teacherSchoolAssociation    | 5     |
        | teacherSectionAssociation   | 19    |
     And I check to find if record is in collection:
	   | collectionName              | expectedRecordCount | searchParameter          | searchValue          | searchType           |
	   | student                     | 1                   | metaData.externalId      | 530425896            | string               |
	   | student                     | 1                   | metaData.externalId      | 784204643            | string               |
	   | teacher                     | 1                   | metaData.externalId      | cgray                | string               |
	   | course                      | 1                   | metaData.externalId      | 1st Grade Homeroom   | string               |
	And I should see "Processed 170 records." in the resulting batch job file
