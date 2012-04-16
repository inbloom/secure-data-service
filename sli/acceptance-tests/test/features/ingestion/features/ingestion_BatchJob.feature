Feature: Batchjob Datamodel Data Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone
 
 @wip
Scenario: Post a minimal zip file as a payload of the ingestion job: Clean Database
Given I post "BatchJob.zip" file as the payload of the ingestion job
    And the following collections are empty in batch job datastore:
        | collectionName              |
        | newBatchJob                 |

When zip file is scp to ingestion landing zone
  And a batch job log has been created

Then I should see following map of entry counts in the corresponding batch job db collections:
        | collectionName              | count |
        | newBatchJob                 | 1     |

 And I check to find if record is in batch job collection:
  | collectionName | expectedRecordCount | searchParameter                  | searchValue                          | searchType |
  | newBatchJob    | 1                   | totalFiles                       | 1                                    | integer    |
  | newBatchJob    | 1                   | status                           | CompletedSuccessfully                | string     |
  | newBatchJob    | 1                   | batchProperties.tenantId         | IL                                   | string     |
  # stages
  | newBatchJob    | 1                   | stages.0.stageName               | ZipFileProcessing                    | string     |
  | newBatchJob    | 1                   | stages.0.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.1.stageName               | ControlFilePreprocessing             | string     |
  | newBatchJob    | 1                   | stages.1.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.2.stageName               | ControlFileProcessing                | string     |
  | newBatchJob    | 1                   | stages.2.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.3.stageName               | EdFiProcessing                       | string     |
  | newBatchJob    | 1                   | stages.3.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.3.metrics.0.resourceId    | InterchangeEducationOrganization.xml | string     |
  | newBatchJob    | 1                   | stages.3.metrics.0.recordCount   | 1                                    | integer    |
#  | newBatchJob    | 1                   | stages.3.metrics.0.errorCount    | 0                                    | integer    |
  | newBatchJob    | 1                   | stages.4.stageName               | TransformationProcessing             | string     |
  | newBatchJob    | 1                   | stages.4.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.5.stageName               | PersistenceProcessing                | string     |
  | newBatchJob    | 1                   | stages.5.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.5.metrics.0.resourceId    | InterchangeEducationOrganization.xml | string     |
#  | newBatchJob    | 1                   | stages.5.metrics.0.resourceId    | InterchangeEducationOrganization.xml | string     |
#  | newBatchJob    | 1                   | stages.5.metrics.0.recordCount   | 1                                    | ingeger     |
#  | newBatchJob    | 1                   | stages.5.metrics.0.errorCount    | 0                                    | integer     |
  | newBatchJob    | 1                   | stages.6.stageName               | JobReportingProcessing               | string     |
  | newBatchJob    | 1                   | stages.6.status                  | finished                             | string     |
  #resources
  | newBatchJob    | 1                   | resourceEntries.0.resourceId     | BatchJob.zip                         | string     |
  | newBatchJob    | 1                   | resourceEntries.0.recordCount    | 0                                    | integer    |
  | newBatchJob    | 1                   | resourceEntries.0.errorCount     | 0                                    | integer    |
  | newBatchJob    | 1                   | resourceEntries.1.resourceId     | InterchangeEducationOrganization.xml | string     |
  | newBatchJob    | 1                   | resourceEntries.1.resourceFormat | edfi-xml                             | string     |
  | newBatchJob    | 1                   | resourceEntries.1.resourceType   | EducationOrganization                | string     |
 #errors

   And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "InterchangeEducationOrganization.xml records considered: 1" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records ingested successfully: 1" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records failed: 0" in the resulting batch job file

@wip
Scenario: Post a zip file containing a purge attribute as a payload of the ingestion job: Clean Database
Given I post "BatchJobPurge.zip" file as the payload of the ingestion job
    And the following collections are empty in batch job datastore:
        | collectionName              |
        | newBatchJob                 |

When zip file is scp to ingestion landing zone
  And a batch job log has been created

Then I should see following map of entry counts in the corresponding batch job db collections:
        | collectionName              | count |
        | newBatchJob                 | 1     |

 And I check to find if record is in batch job collection:
  | collectionName | expectedRecordCount | searchParameter                  | searchValue                          | searchType |
  | newBatchJob    | 1                   | totalFiles                       | 1                                    | integer    |
  | newBatchJob    | 1                   | status                           | CompletedSuccessfully                | string     |
  | newBatchJob    | 1                   | batchProperties.purge            | true                                 | string     |
  # stages
  | newBatchJob    | 1                   | stages.0.stageName               | ZipFileProcessing                    | string     |
  | newBatchJob    | 1                   | stages.0.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.1.stageName               | ControlFilePreprocessing             | string     |
  | newBatchJob    | 1                   | stages.1.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.2.stageName               | ControlFileProcessing                | string     |
  | newBatchJob    | 1                   | stages.2.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.3.stageName               | PurgeProcessing                      | string     |
  | newBatchJob    | 1                   | stages.3.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.4.stageName               | JobReportingProcessing               | string     |
  | newBatchJob    | 1                   | stages.4.status                  | finished                             | string     |
  #resources
  | newBatchJob    | 1                   | resourceEntries.0.resourceId     | BatchJobPurge.zip                    | string     |
  | newBatchJob    | 1                   | resourceEntries.1.resourceId     | InterchangeEducationOrganization.xml | string     |
  | newBatchJob    | 1                   | resourceEntries.1.resourceFormat | edfi-xml                             | string     |
  | newBatchJob    | 1                   | resourceEntries.1.resourceType   | EducationOrganization                | string     |
 #errors

   And I should see "Processed 0 records." in the resulting batch job file
    And I should not see an error log file created


Scenario: Post a zip file containing configured interchanges and warnings as a payload of the ingestion job: Clean Database
Given I post "BatchJobLarge.zip" file as the payload of the ingestion job
    And the following collections are empty in batch job datastore:
        | collectionName              |
        | newBatchJob                 |
        | error                       |

When zip file is scp to ingestion landing zone
  And a batch job log has been created

Then I should see following map of entry counts in the corresponding batch job db collections:
        | collectionName              | count |
        | newBatchJob                 | 1     |
        | error                       | 1398  |

 And I check to find if record is in batch job collection:
  | collectionName | expectedRecordCount | searchParameter                  | searchValue                          |searchType  |
  | newBatchJob    | 1                   | totalFiles                       | 10                                   | integer    |
  | newBatchJob    | 1                   | status                           | CompletedSuccessfully                | string     |
  # stages
  | newBatchJob    | 1                   | stages.0.stageName               | ZipFileProcessing                    | string     |
  | newBatchJob    | 1                   | stages.0.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.1.stageName               | ControlFilePreprocessing             | string     |
  | newBatchJob    | 1                   | stages.1.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.2.stageName               | ControlFileProcessing                | string     |
  | newBatchJob    | 1                   | stages.2.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.3.stageName               | EdFiProcessing                       | string     |
  | newBatchJob    | 1                   | stages.3.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.3.metrics.0.resourceId    | InterchangeStudent.xml               | string     |
#  | newBatchJob    | 1                   | stages.3.metrics.0.recordCount   | 94                                   | string     |
  | newBatchJob    | 1                   | stages.4.stageName               | TransformationProcessing             | string     |
  | newBatchJob    | 1                   | stages.4.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.5.stageName               | PersistenceProcessing                | string     |
  | newBatchJob    | 1                   | stages.5.status                  | finished                             | string     |
  | newBatchJob    | 1                   | stages.5.metrics.9.resourceId    | InterchangeStudentParent.xml         | string     |
#  | newBatchJob    | 1                   | stages.5.metrics.9.recordCount   | 18                                   | string     |
#  | newBatchJob    | 1                   | stages.5.metrics.9.errorCount    | 0                                    | string     |
  | newBatchJob    | 1                   | stages.6.stageName               | JobReportingProcessing               | string     |
  | newBatchJob    | 1                   | stages.6.status                  | finished                             | string     |
  #resources
  | newBatchJob    | 1                   | resourceEntries.0.resourceId     | BatchJobLarge.zip                    | string     |
  | newBatchJob    | 1                   | resourceEntries.0.recordCount    | 0                                    | integer    |
  | newBatchJob    | 1                   | resourceEntries.0.errorCount     | 0                                    | integer    |
  | newBatchJob    | 1                   | resourceEntries.2.resourceId     | InterchangeEducationOrganization.xml | string     |
  | newBatchJob    | 1                   | resourceEntries.2.resourceFormat | edfi-xml                             | string     |
  | newBatchJob    | 1                   | resourceEntries.2.resourceType   | EducationOrganization                | string     |
#errors

   And I should see "Processed 983 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "InterchangeStudentGrade.xml records considered: 317" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records ingested successfully: 317" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records failed: 0" in the resulting batch job file

@wip
Scenario: Post two zip files then see the batch jobs in the database: Clean Database
Given I post "BatchJobLarge.zip" and "BatchJob.zip" files as the payload of two ingestion jobs
    And the following collections are empty in batch job datastore:
        | collectionName              |
        | newBatchJob                 |
        | error                       |

When zip files are scped to the ingestion landing zone
  And a batch job log has been created

Then I should see following map of entry counts in the corresponding batch job db collections:
        | collectionName              | count |
        | newBatchJob                 | 2     |

 And I check to find if record is in batch job collection:
  | collectionName | expectedRecordCount | searchParameter                | searchValue             |searchType     |
  | newBatchJob    | 1                   | totalFiles                     | 1                       |integer        |
  | newBatchJob    | 1                   | totalFiles                     | 10                      |integer        |
  | newBatchJob    | 1                   | status                         | CompletedSuccessfully   |string         |
  # Note there is a race here if the BatchJobLarge.zip job completes too quickly.
  | newBatchJob    | 1                   | status                         | Running                 |string         |
  | newBatchJob    | 1                   | resourceEntries.0.resourceId   | BatchJob.zip            |string         |
  | newBatchJob    | 1                   | resourceEntries.0.resourceId   | BatchJobLarge.zip       |string         |

 And I should not see an error log file created

