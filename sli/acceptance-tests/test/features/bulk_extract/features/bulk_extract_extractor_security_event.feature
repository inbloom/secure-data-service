@RALLY_US5725

Feature: Security events are logged when bulk extract is run

  Background: Reset bulk extract files
    Given I clean the bulk extract file system and database
    And I am using local data store
    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"

  Scenario: Trigger a bulk extract on an empty database
    Given all collections are empty
    And the following collections are empty in sli datastore:
      | collectionName              |
      | securityEvent               |
    And I trigger a bulk extract
    Then I should see following map of entry counts in the corresponding sli db collections:
      | collectionName              | count |
      | securityEvent               | 6    |
    And I check to find if record is in sli db collection:
      | collectionName  | expectedRecordCount | searchParameter         | searchValue                                                                  | searchType      |
      | securityEvent   | 1                   | body.logMessage         | Beginning bulk extract execution                                             | string          |
      | securityEvent   | 1                   | body.logMessage         | Unable to trigger SEA public data extract                                    | string          |
      | securityEvent   | 1                   | body.logMessage         | No SEA is available for the tenant                                           | string          |

  Scenario: Trigger a full extract and check security event
    And I post "StoriedDataSet_IL_Daybreak.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "StoriedDataSet_IL_Daybreak.zip" is completed in database
    Then I should not see an error log file created
    And I should not see a warning log file created
    And all LEAs in "Midgar" are authorized for "SDK Sample"
    And all LEAs in "Midgar" are authorized for "Paved Z00"

    Given the following collections are empty in sli datastore:
      | collectionName              |
      | securityEvent               |
    And I trigger a bulk extract
    Then I should see following map of entry counts in the corresponding sli db collections:
      | collectionName              | count |
      | securityEvent               | 51    |

    And I check to find if record is in sli db collection:
      | collectionName  | expectedRecordCount | searchParameter         | searchValue                                                                  | searchType      |
      | securityEvent   | 1                   | body.logMessage         | Beginning bulk extract execution                                             | string          |
      #Public Data for Tenant/SEA Extract
      | securityEvent   | 1                   | body.logMessage         | Beginning SEA public data extract                                            | string          |
      | securityEvent   | 1                   | body.logMessage         | Completed SEA public data extract                                            | string          |
      | securityEvent   | 3                   | body.logMessage         | Generating archive for app 22c2a28d-7327-4444-8ff9-caad4b1c7aa3              | string          |
      | securityEvent   | 3                   | body.logMessage         | Generating archive for app 19cca28d-7357-4044-8df9-caad4b1c8ee4              | string          |
      | securityEvent   | 1                   | body.logMessage         | Extracting educationOrganization                                             | string          |
      | securityEvent   | 1                   | body.logMessage         | Extracting course                                                            | string          |
      | securityEvent   | 1                   | body.logMessage         | Extracting courseOffering                                                    | string          |
      | securityEvent   | 1                   | body.logMessage         | Extracting session                                                           | string          |
      | securityEvent   | 1                   | body.logMessage         | Extracting graduationPlan                                                    | string          |
      | securityEvent   | 1                   | body.logMessage         | Extracting calendarDate                                                      | string          |
      | securityEvent   | 1                   | body.logMessage         | Extracting gradingPeriod                                                     | string          |
      #LEA Extract
      | securityEvent   | 1                   | body.logMessage         | Finished LEA level bulk extract                                              | string          |
      | securityEvent   | 1                   | body.logMessage         | Beginning LEA level bulk extract                                             | string          |
      | securityEvent   | 2                   | body.logMessage         | Extracting attendance for LEA extract                                        | string          |
      | securityEvent   | 2                   | body.logMessage         | Extracting parent for LEA extract                                            | string          |
      | securityEvent   | 2                   | body.logMessage         | Extracting staff for LEA extract                                             | string          |
      | securityEvent   | 2                   | body.logMessage         | Extracting staffCohortAssociation for LEA extract                            | string          |
      | securityEvent   | 2                   | body.logMessage         | Extracting staffEducationOrganizationAssociation for LEA extract             | string          |
      | securityEvent   | 2                   | body.logMessage         | Extracting staffProgramAssociation for LEA extract                           | string          |
      | securityEvent   | 2                   | body.logMessage         | Extracting student for LEA extract                                           | string          |
      | securityEvent   | 2                   | body.logMessage         | Extracting studentAssessment for LEA extract                                 | string          |
      | securityEvent   | 2                   | body.logMessage         | Extracting studentSchoolAssociation for LEA extract                          | string          |
      | securityEvent   | 2                   | body.logMessage         | Extracting teacherSchoolAssociation for LEA extract                          | string          |
      | securityEvent   | 2                   | body.logMessage         | Extracting yearlyTranscript for LEA extract                                  | string          |


  Scenario: Trigger a delta extract and check security events
    Given I post "new_edorg_in_daybreak.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "new_edorg_in_daybreak.zip" is completed in database
    Then I should not see an error log file created
    And I should not see a warning log file created

    Given the following collections are empty in sli datastore:
      | collectionName              |
      | securityEvent               |
    And I trigger a delta extract
    Then I should see following map of entry counts in the corresponding sli db collections:
      | collectionName             | count |
      | securityEvent              | 5     |
    And I check to find if record is in sli db collection:
      | collectionName  | expectedRecordCount | searchParameter         | searchValue                                                                  | searchType      |
      | securityEvent   | 1                   | body.logMessage         | Beginning bulk extract execution                                             | string          |
      | securityEvent   | 2                   | body.className          | org.slc.sli.bulk.extract.extractor.DeltaExtractor                            | string          |
      | securityEvent   | 1                   | body.logMessage         | Generating archive for app 19cca28d-7357-4044-8df9-caad4b1c8ee4              | string          |
      | securityEvent   | 1                   | body.logMessage         | Generating archive for app 22c2a28d-7327-4444-8ff9-caad4b1c7aa3              | string          |


  Scenario: Trigger a bulk extract on a tenant that doesn't have any authorized bulk extract apps
    Given I am using preconfigured Ingestion Landing Zone for "Hyrule-NYC"
    And all collections are empty
    And the tenant "Hyrule" does not have any bulk extract apps for any of its education organizations
    And I post "StoriedDataSet_NY.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "StoriedDataSet_NY.zip" is completed in database
    Then I should not see an error log file created
    And I should not see a warning log file created

    Given the following collections are empty in sli datastore:
      | collectionName              |
      | securityEvent               |
    And I trigger an extract for tenant "Hyrule"
    Then I should see following map of entry counts in the corresponding sli db collections:
      | collectionName              | count |
      | securityEvent               | 6     |
    And I check to find if record is in sli db collection:
      | collectionName  | expectedRecordCount | searchParameter         | searchValue                                                                  | searchType      |
      | securityEvent   | 6                   | body.appId              | BulkExtract                                                                  | string          |
      | securityEvent   | 4                   | body.tenantId           | Hyrule                                                                       | string          |
      | securityEvent   | 1                   | body.logMessage         | Beginning bulk extract execution                                             | string          |
      | securityEvent   | 1                   | body.logMessage         | No authorized application to extract data                                    | string          |
      | securityEvent   | 0                   | body.logMessage         | No authorized application to extract data Hyrule                             | string          |

  Scenario: Trigger a bulk extract on a tenant that doesn't exit
    Given the following collections are empty in sli datastore:
      | collectionName              |
      | securityEvent               |
    And I use an invalid tenant to trigger a bulk extract
  #Add a count to the following step once we know how many security events will be logged
    Then I should see following map of entry counts in the corresponding sli db collections:
      | collectionName              | count |
      | securityEvent               |   2   |
  #Add counts, and more log messages as we figure them out
    And I check to find if record is in sli db collection:
      | collectionName  | expectedRecordCount | searchParameter         | searchValue                                                                  | searchType      |
      | securityEvent   |         1           | body.logMessage         | A bulk extract is not being initiated for the tenant NoTenantForYou because the tenant has not been onboarded | string          |
      | securityEvent   | 1                   | body.logMessage         | Beginning bulk extract execution                            | string          |
