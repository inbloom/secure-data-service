@RALLY_DE2632
@RALLY_DE2617

Feature: Security Event 

#  As an ingestion user, I want to be able to persist securityEvent in standard entity form retrievable from API

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Ingest and reingest with default mode
Given I am using preconfigured Ingestion Landing Zone
  And the landing zone is reinitialized
  And the following collections are empty in sli datastore:
    | collectionName              |
    | securityEvent               |
  And I post "TinyDataSet.zip" file as the payload of the ingestion job
  And zip file is scp to ingestion landing zone
  And a batch job for file "TinyDataSet.zip" is completed in database
    And I should not see a warning log file created
  And I should not see an error log file created
  Then I should see following map of entry counts in the corresponding sli db collections:
        | collectionName              | count |
        | securityEvent               | 11    |
  And I check to find if record is in sli db collection:
       | collectionName  | expectedRecordCount | searchParameter         | searchValue                                                                          | searchType |
       | securityEvent   | 11                  | body.appId              | Ingestion                                                                            | string     |
       | securityEvent   | 1                   | body.logMessage         | Ingestion process started.                                                           | string     |
       | securityEvent   | 1                   | body.logMessage         | [file] InterchangeEducationOrganization.xml (edfi-xml/EducationOrganization)         | string     |
	   | securityEvent   | 1                   | body.logMessage         | [file] InterchangeEducationOrganization.xml records considered for processing: 1     | string     |
	   | securityEvent   | 1                   | body.logMessage         | [file] InterchangeEducationOrganization.xml records ingested successfully: 1         | string     |
       | securityEvent   | 1                   | body.logMessage         | [file] InterchangeEducationOrganization.xml records deleted successfully: 0          | string     |
	   | securityEvent   | 1                   | body.logMessage         | [file] InterchangeEducationOrganization.xml records failed processing: 0             | string     |
	   | securityEvent   | 1                   | body.logMessage         | [file] InterchangeEducationOrganization.xml records not considered for processing: 0 | string     |
	   | securityEvent   | 1                   | body.logMessage         | [configProperty] tenantId: IL                                                        | string     |
	   | securityEvent   | 1                   | body.logMessage         | All records processed successfully.                                                  | string     |
	   | securityEvent   | 1                   | body.logMessage         | Processed 1 records.                                                                 | string     |
	   | securityEvent   | 11                  | body.userEdOrg          | Daybreak                                                                             | string     |
	   | securityEvent   | 11                  | body.targetEdOrgList    | Daybreak                                                                             | string     |
  And "1" security event matching "^jobId: TinyDataSet.zip" should be in the sli db