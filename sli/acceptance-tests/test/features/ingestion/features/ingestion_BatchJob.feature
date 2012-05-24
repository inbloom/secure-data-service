@RALLY_US2033
Feature: Batchjob Datamodel Data Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone

Scenario: Post a minimal zip file as a payload of the ingestion job: Clean Database
Given I post "BatchJob.zip" file as the payload of the ingestion job
    And the following collections are empty in batch job datastore:
        | collectionName              |
        | newBatchJob                 |
        | error                       |
    And the following collections are empty in datastore:
        | collectionName              |
        | securityEvent               |

When zip file is scp to ingestion landing zone
  And a batch job for file "BatchJob.zip" is completed in database

Then I should see following map of entry counts in the corresponding batch job db collections:
        | collectionName              | count |
        | newBatchJob                 | 1     |

 And I check to find if record is in batch job collection:
  | collectionName | expectedRecordCount | searchParameter                  | searchValue                          | searchType |
  | newBatchJob    | 1                   | totalFiles                       | 1                                    | integer    |
  | newBatchJob    | 1                   | status                           | CompletedSuccessfully                | string     |
  | newBatchJob    | 1                   | batchProperties.tenantId         | IL                                   | string     |
  # stages
  | newBatchJob    | 1                   | stages.0.chunks.0.stageName               | ZipFileProcessor                     | string     |
  | newBatchJob    | 1                   | stages.0.chunks.0.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.1.chunks.0.stageName               | ControlFilePreProcessor              | string     |
  | newBatchJob    | 1                   | stages.1.chunks.0.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.2.chunks.0.stageName               | ControlFileProcessor                 | string     |
  | newBatchJob    | 1                   | stages.2.chunks.0.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.3.chunks.0.stageName               | XmlFileProcessor                     | string     |
  | newBatchJob    | 1                   | stages.3.chunks.0.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.4.chunks.0.stageName               | EdFiProcessor                        | string     |
  | newBatchJob    | 1                   | stages.4.chunks.0.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.5.chunks.0.stageName               | TransformationProcessor              | string     |
  | newBatchJob    | 1                   | stages.5.chunks.0.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.6.chunks.0.stageName               | PersistenceProcessor                 | string     |
  | newBatchJob    | 1                   | stages.6.chunks.0.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.7.chunks.0.stageName               | JobReportingProcessor                | string     |
  | newBatchJob    | 1                   | stages.7.chunks.0.status                  | finished                             | string     |
  #resources
  | newBatchJob    | 1                   | resourceEntries.0.resourceId     | BatchJob.zip                         | string     |
  | newBatchJob    | 1                   | resourceEntries.0.resourceFormat | zip-file                             | string     |
  | newBatchJob    | 1                   | resourceEntries.1.resourceId     | controlfile.ctl                      | string     |
  | newBatchJob    | 1                   | resourceEntries.1.resourceFormat | control-file                         | string     |
  | newBatchJob    | 1                   | resourceEntries.2.resourceId     | InterchangeEducationOrganization.xml | string     |
  | newBatchJob    | 1                   | resourceEntries.2.resourceFormat | edfi-xml                             | string     |
  | newBatchJob    | 1                   | resourceEntries.2.resourceType   | EducationOrganization                | string     |
  | newBatchJob    | 1                   | resourceEntries.3.resourceFormat | neutralrecord                        | string     |
  | newBatchJob    | 1                   | resourceEntries.3.resourceType   | EducationOrganization                | string     |

   And I should see "Processed 1 records." in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records considered: 1" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records ingested successfully: 1" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records failed: 0" in the resulting batch job file
    And I should not see an error log file created

Scenario: Post a minimal zip file with purge set but not tenant as a payload of the ingestion job: Clean Database
Given I post "BatchJobPurge.zip" file as the payload of the ingestion job
    And the following collections are empty in batch job datastore:
        | collectionName              |
        | newBatchJob                 |
        | error                       |

When zip file is scp to ingestion landing zone
  And a batch job for file "BatchJobPurge.zip" is completed in database

Then I should see following map of entry counts in the corresponding batch job db collections:
        | collectionName              | count |
        | newBatchJob                 | 1     |
 
 And I check to find if record is in batch job collection:
  | collectionName | expectedRecordCount | searchParameter                  | searchValue                          | searchType |
  | newBatchJob    | 1                   | totalFiles                       | 1                                    | integer    |
  | newBatchJob    | 1                   | status                           | CompletedSuccessfully                | string     |
  # stages
  | newBatchJob    | 1                   | stages.0.chunks.0.stageName               | ZipFileProcessor                     | string     |
  | newBatchJob    | 1                   | stages.0.chunks.0.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.1.chunks.0.stageName               | ControlFilePreProcessor              | string     |
  | newBatchJob    | 1                   | stages.1.chunks.0.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.2.chunks.0.stageName               | ControlFileProcessor                 | string     |
  | newBatchJob    | 1                   | stages.2.chunks.0.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.3.chunks.0.stageName               | PurgeProcessor                       | string     |
  | newBatchJob    | 1                   | stages.3.chunks.0.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.4.chunks.0.stageName               | JobReportingProcessor                | string     |
  | newBatchJob    | 1                   | stages.4.chunks.0.status                  | finished                             | string     |
  #resources
  | newBatchJob    | 1                   | resourceEntries.0.resourceId     | BatchJobPurge.zip                    | string     |
  | newBatchJob    | 1                   | resourceEntries.0.recordCount    | 0                                    | integer    |
  | newBatchJob    | 1                   | resourceEntries.0.errorCount     | 0                                    | integer    |
  | newBatchJob    | 1                   | resourceEntries.1.resourceId     | controlfile.ctl                      | string     |
  | newBatchJob    | 1                   | resourceEntries.1.resourceFormat | control-file                         | string     |
  | newBatchJob    | 1                   | resourceEntries.2.resourceId     | InterchangeEducationOrganization.xml | string     |
  | newBatchJob    | 1                   | resourceEntries.2.resourceFormat | edfi-xml                             | string     |
  | newBatchJob    | 1                   | resourceEntries.2.resourceType   | EducationOrganization                | string     |

   And I should see "Processed 0 records." in the resulting batch job file
   And I should see "[configProperty] purge: true" in the resulting batch job file
   And I should not see an error log file created

Scenario: Post a zip file containing errors as a payload of the ingestion job: Clean Database
Given I post "BatchJobError.zip" file as the payload of the ingestion job
    And the following collections are empty in batch job datastore:
        | collectionName              |
        | newBatchJob                 |
        | error                       |

When zip file is scp to ingestion landing zone
  And a batch job for file "BatchJobError.zip" is completed in database

Then I should see following map of entry counts in the corresponding batch job db collections:
        | collectionName              | count |
        | newBatchJob                 | 1     |
        | error                       | 3     |

 And I check to find if record is in batch job collection:
  | collectionName | expectedRecordCount | searchParameter                  | searchValue                             | searchType |
  | newBatchJob    | 1                   | status                           | CompletedWithErrors                     | string     |
  | newBatchJob    | 1                   | totalFiles                       | 1                                       | integer    |
  # stages
  | newBatchJob    | 1                   | stages.0.chunks.0.stageName               | ZipFileProcessor                        |string      |
  | newBatchJob    | 1                   | stages.0.chunks.0.status                  | finished                                |string      |
  | newBatchJob    | 1                   | stages.1.chunks.0.stageName               | ControlFilePreProcessor                 |string      |
  | newBatchJob    | 1                   | stages.1.chunks.0.status                  | finished                                |string      |
  | newBatchJob    | 1                   | stages.2.chunks.0.stageName               | ControlFileProcessor                    |string      |
  | newBatchJob    | 1                   | stages.2.chunks.0.status                  | finished                                |string      |
  | newBatchJob    | 1                   | stages.3.chunks.0.stageName               | XmlFileProcessor                        |string      |
  | newBatchJob    | 1                   | stages.3.chunks.0.status                  | finished                                |string      |
  | newBatchJob    | 1                   | stages.4.chunks.0.stageName               | EdFiProcessor                           |string      |
  | newBatchJob    | 1                   | stages.4.chunks.0.status                  | finished                                |string      |
  | newBatchJob    | 1                   | stages.5.chunks.0.stageName               | JobReportingProcessor                   |string      |
  | newBatchJob    | 1                   | stages.5.chunks.0.status                  | finished                                |string      |
  #resources
  | newBatchJob    | 1                   | resourceEntries.0.resourceId     | BatchJobError.zip                       |string      |
  | newBatchJob    | 1                   | resourceEntries.0.recordCount    | 0                                       |integer     |
  | newBatchJob    | 1                   | resourceEntries.0.errorCount     | 0                                       |integer     |
  | newBatchJob    | 1                   | resourceEntries.1.resourceId     | controlfile.ctl                      | string     |
  | newBatchJob    | 1                   | resourceEntries.1.resourceFormat | control-file                         | string     |
  | newBatchJob    | 1                   | resourceEntries.2.resourceId     | InterchangeEducationOrganization.xml | string     |
  | newBatchJob    | 1                   | resourceEntries.2.resourceFormat | edfi-xml                             | string     |
  | newBatchJob    | 1                   | resourceEntries.2.resourceType   | EducationOrganization                | string     |
  | newBatchJob    | 1                   | resourceEntries.3.resourceFormat | neutralrecord                        | string     |
  | newBatchJob    | 1                   | resourceEntries.3.resourceType   | EducationOrganization                | string     |
 #errors
  | error          | 1                   | severity                         | WARNING                                 |string      |
  | error          | 2                   | severity                         | ERROR                                   |string      |

   And I should see "Processed 0 records." in the resulting batch job file
   And I should see "ERROR  Error resolving references in XML file InterchangeEducationOrganization.xml" in the resulting error log file

Scenario: Post two zip files then see the batch jobs in the database: Clean Database
Given I post "BatchJobLarge.zip" and "BatchJob.zip" files as the payload of two ingestion jobs
    And the following collections are empty in batch job datastore:
        | collectionName              |
        | newBatchJob                 |
        | error                       |

When zip files are scped to the ingestion landing zone
  And a batch job for file "BatchJob.zip" is completed in database
  And a batch job for file "BatchJobLarge.zip" is completed in database

Then I should see following map of entry counts in the corresponding batch job db collections:
        | collectionName              | count |
        | newBatchJob                 | 2     |

 And I check to find if record is in batch job collection:
  | collectionName | expectedRecordCount | searchParameter                | searchValue             | searchType |
  | newBatchJob    | 1                   | totalFiles                     | 1                       | integer    |
  | newBatchJob    | 2                   | status                         | CompletedSuccessfully   | string     |
  | newBatchJob    | 0                   | status                         | CompletedWithErrors     | string     | 
  | newBatchJob    | 1                   | resourceEntries.0.resourceId   | BatchJob.zip            | string     |
  | newBatchJob    | 1                   | resourceEntries.0.resourceId   | BatchJobLarge.zip       | string     |

