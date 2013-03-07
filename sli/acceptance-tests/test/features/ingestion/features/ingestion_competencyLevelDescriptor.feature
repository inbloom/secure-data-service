@RALLY_US149
Feature: CompetencyLevelDescriptor Ingestion Test

  Background: I have a landing zone route configured
    Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone

  Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
    Given I post "CompetencyLevelDescriptor1.zip" file as the payload of the ingestion job
    And the following collections are empty in datastore:
      | collectionName              |
      | competencyLevelDescriptor   |
    When zip file is scp to ingestion landing zone
    And a batch job for file "CompetencyLevelDescriptor1.zip" is completed in database
    And a batch job log has been created
    Then I should see following map of entry counts in the corresponding collections:
      | collectionName              | count |
      | competencyLevelDescriptor   | 1     |
    And I check to find if record is in collection:
      | collectionName              | expectedRecordCount | searchParameter                | searchValue             | searchType           |
      | competencyLevelDescriptor   | 1                   | body.description               | Description1            | string               |
      | competencyLevelDescriptor   | 1                   | body.codeValue                 | CodeValue1              | string               |
      | competencyLevelDescriptor   | 1                   | body.performanceBaseConversion | Advanced                | string               |
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should not see an error log file created
    And I should see "InterchangeEducationOrganization.xml records considered for processing: 1" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records ingested successfully: 1" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records failed processing: 0" in the resulting batch job file

  Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Populated Database
    Given I post "CompetencyLevelDescriptor2.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "CompetencyLevelDescriptor2.zip" is completed in database
    And a batch job log has been created
    Then I should see following map of entry counts in the corresponding collections:
      | collectionName              | count |
      | competencyLevelDescriptor   | 2     |
    And I check to find if record is in collection:
      | collectionName              | expectedRecordCount | searchParameter                | searchValue             | searchType           |
      | competencyLevelDescriptor   | 1                   | body.description               | Description3            | string               |
      | competencyLevelDescriptor   | 0                   | body.description               | Description1            | string               |
      | competencyLevelDescriptor   | 1                   | body.codeValue                 | CodeValue1              | string               |
      | competencyLevelDescriptor   | 1                   | body.description               | Description2            | string               |
      | competencyLevelDescriptor   | 1                   | body.codeValue                 | CodeValue2              | string               |
      | competencyLevelDescriptor   | 2                   | body.performanceBaseConversion | Advanced                | string               |
    And I should see "Processed 2 records." in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should not see an error log file created
    And I should see "InterchangeEducationOrganization.xml records considered for processing: 2" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records ingested successfully: 2" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records failed processing: 0" in the resulting batch job file