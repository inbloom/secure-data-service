@RALLY_US5756
Feature: Test SEOAA Datamodel

Background: I have a landing zone route configured
    Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file where 1 SEOAA is invalid and 1 SEOAA is valid
  Given I post "SEOAA.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
        | collectionName                        |
        | staffEducationOrganizationAssociation |
  When zip file is scp to ingestion landing zone
  And a batch job for file "SEOAA.zip" is completed in database
  And I should see "CORE_0006" in the resulting error log file
  And I should not see a warning log file created
  Then I should see following map of entry counts in the corresponding collections:
        | collectionName                        | count |
        | staffEducationOrganizationAssociation | 1     |
  When I check to find if record is in collection:
     | collectionName                        | expectedRecordCount | searchParameter                       | searchValue          |
     | staffEducationOrganizationAssociation | 1                   | body.staffClassification              | English Teacher      |