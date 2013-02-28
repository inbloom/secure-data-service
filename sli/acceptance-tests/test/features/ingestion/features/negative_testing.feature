@RALLY_US212
@RALLY_US308
@RALLY_US309
@RALLY_US2292
@RALLY_US4911
Feature: Negative Ingestion Testing

Background: I have a landing zone route configured
    Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone


Scenario: Post an empty zip file should fail
  Given I post "emptyFile.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
        | collectionName              |
        | student                     |
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job for file "emptyFile.zip" is completed in database
  And I should see "BASE_0015" in the resulting error log file
  And I should see "Processed 0 records." in the resulting batch job file
  And I should see "student.xml records considered: 0" in the resulting batch job file
  And I should see "student.xml records ingested successfully: 0" in the resulting batch job file
  And I should see "student.xml records failed: 0" in the resulting batch job file

Scenario: Post a zip file where the first record has an incorrect enum for an attribute value
  Given I post "valueTypeNotMatchAttributeType.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
        | collectionName              |
        | student                     |
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job for file "valueTypeNotMatchAttributeType.zip" is completed in database
  And I should see "CORE_0006" in the resulting error log file
  And I should see "Not all records were processed completely due to errors." in the resulting batch job file
  And I should see "student.xml records considered: 2" in the resulting batch job file
  And I should see "student.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "student.xml records failed: 1" in the resulting batch job file

Scenario: Post a zip file where the first record has a bad attribute should fail on that record and proceed
  Given I post "firstRecordHasIncorrectAttribute.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
        | collectionName              |
        | student                     |
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job for file "firstRecordHasIncorrectAttribute.zip" is completed in database
  And I should see "CORE_0010" in the resulting error log file
  And I should see "Not all records were processed completely due to errors." in the resulting batch job file
  And I should see "Processed 2 records." in the resulting batch job file
  And I should see "student.xml records considered: 2" in the resulting batch job file
  And I should see "student.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "student.xml records failed: 1" in the resulting batch job file

Scenario: Post a zip file where the second record has a bad attribute should fail and process previous records
  Given I post "secondRecordHasIncorrectAttribute.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
        | collectionName              |
        | student                     |
        | recordHash                  |
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job for file "secondRecordHasIncorrectAttribute.zip" is completed in database
  And I should see "CORE_0010" in the resulting error log file
  And I should see "Not all records were processed completely due to errors." in the resulting batch job file
  And I should see "Processed 2 records." in the resulting batch job file
  And I should see "student.xml records considered: 2" in the resulting batch job file
  And I should see "student.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "student.xml records failed: 1" in the resulting batch job file

@wip
Scenario: Post a zip file where the first record has an undefined attribute should fail on that record and proceed
  Given I post "firstRecordHasMoreAttributes.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
        | collectionName              |
        | student                     |
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job for file "firstRecordHasMoreAttributes.zip" is completed in database
  And I should see "CORE_0006" in the resulting error log file
  And I should see "Not all records were processed completely due to errors." in the resulting batch job file
  And I should see "Processed 2 records." in the resulting batch job file
  And I should see "student.xml records considered: 2" in the resulting batch job file
  And I should see "student.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "student.xml records failed: 1" in the resulting batch job file

Scenario: Post a zip file where the first record has a missing attribute should fail on that record and proceed
  Given I post "firstRecordMissingAttribute.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
        | collectionName              |
        | student                     |
        | recordHash                  |
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job for file "firstRecordMissingAttribute.zip" is completed in database
  And I should see "CORE_0006" in the resulting error log file
  And I should see "Not all records were processed completely due to errors." in the resulting batch job file
  And I should see "Processed 2 records." in the resulting batch job file
  And I should see "student.xml records considered: 2" in the resulting batch job file
  And I should see "student.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "student.xml records failed: 1" in the resulting batch job file

Scenario: Post a zip file where the the edfi input is malformed XML
  Given I post "malformedXML.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
        | collectionName              |
        | student                     |
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job for file "malformedXML.zip" is completed in database
  And I should see "Processed 0 records." in the resulting batch job file
  And I should see "Not all records were processed completely due to errors." in the resulting batch job file
  And I should see "student.xml records considered: 0" in the resulting batch job file
  And I should see "student.xml records ingested successfully: 0" in the resulting batch job file
  And I should see "student.xml records failed: 0" in the resulting batch job file

Scenario: Post a zip file where the the edfi input has no records
  Given I post "noRecord.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
        | collectionName              |
        | student                     |
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job for file "noRecord.zip" is completed in database
  And I should see "Processed 0 records." in the resulting batch job file
  And I should see "All records processed successfully." in the resulting batch job file
  And I should see "student.xml records considered: 0" in the resulting batch job file
  And I should see "student.xml records ingested successfully: 0" in the resulting batch job file
  And I should see "student.xml records failed: 0" in the resulting batch job file

#should ingest into Mongo with whitespace/returns trimmed from strings
Scenario: Post a zip file where the the edfi input has attributes/strings/enums with whitespace and returns
  Given I post "stringOrEnumContainsWhitespace.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
        | collectionName              |
        | student                     |
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job for file "stringOrEnumContainsWhitespace.zip" is completed in database
  And I find a(n) "student" record where "body.studentUniqueStateId" is equal to "100000000"
  And verify the following data in that document:
       | searchParameter                                                          | searchValue                           | searchType           |
       | body.studentUniqueStateId                                                | 100000000                             | string               |
       | body.schoolFoodServicesEligibility                                       | Reduced price                         | string               |
       | body.limitedEnglishProficiency                                           | NotLimited                            | string               |
  And I should see "Processed 1 records." in the resulting batch job file
  And I should see "All records processed successfully." in the resulting batch job file
  And I should see "student.xml records considered: 1" in the resulting batch job file
  And I should see "student.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "student.xml records failed: 0" in the resulting batch job file

#Background: zip file contains a .txt and .rtf files, which should fail ingestion
Scenario: Post a minimal zip file as a payload of the ingestion job: No Valid Files Test
Given I post "NoValidFilesInCtlFile.zip" file as the payload of the ingestion job

When zip file is scp to ingestion landing zone
  And a batch job for file "NoValidFilesInCtlFile.zip" is completed in database
 Then I should see following map of entry counts in the corresponding batch job db collections:
     | collectionName              | count |
	| error                       | 1     |
    And I should see "INFO  Processed 0 records." in the resulting batch job file
    And I should see "BASE_0002" in the resulting error log file


Scenario: Post a Zip File containing a control file with directory pathnames
  Given I post "DirPathnameInCtlFile.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
        | collectionName                          |
        | session                                 |
        | student                                 |
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job for file "DirPathnameInCtlFile.zip" is completed in database
Then I should see following map of entry counts in the corresponding collections:
        | collectionName                          | count     |
        | session                                 | 0         |
        | student                                 | 0         |
Then I should see "BASE_0004" in the resulting error log file
  And I should see "Processed 0 records." in the resulting batch job file


Scenario: Post a Zip File containing a control file and a subfolder with XML files
  Given I post "ZipContainsSubfolder.zip" zip file with folder as the payload of the ingestion job
  And the following collections are empty in datastore:
        | collectionName                          |
        | session                                 |
        | student                                 |
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job for file "ZipContainsSubfolder.zip" is completed in database
Then I should see following map of entry counts in the corresponding collections:
        | collectionName                          | count     |
        | session                                 | 0         |
        | student                                 | 0         |
Then I should see "BASE_0010" in the resulting error log file
  And I should see "Processed 0 records." in the resulting batch job file


Scenario: Post a Zip File containing a control file with invalid record type
  Given I post "InvalidRecordType.zip" zip file with folder as the payload of the ingestion job
  And the following collections are empty in datastore:
        | collectionName                          |
        | student                                 |
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job for file "InvalidRecordType.zip" is completed in database
Then I should see following map of entry counts in the corresponding collections:
        | collectionName                          | count     |
        | student                                 | 0         |
Then I should see "BASE_0005" in the resulting error log file
  And I should see "Processed 0 records." in the resulting batch job file

Scenario: Post a Zip File containing a control file with extra file item entry
  Given I post "ExtraCtlFileEntry.zip" zip file with folder as the payload of the ingestion job
  And the following collections are empty in datastore:
        | collectionName                          |
        | student                                 |
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job for file "ExtraCtlFileEntry.zip" is completed in database
Then I should see following map of entry counts in the corresponding collections:
        | collectionName                          | count     |
        | student                                 | 0         |
Then I should see "BASE_0001" in the resulting error log file
  And I should see "Processed 0 records." in the resulting batch job file

Scenario: Post a Zip File containing a control file with checksum error
  Given I post "ChecksumError.zip" zip file with folder as the payload of the ingestion job
  And the following collections are empty in datastore:
        | collectionName                          |
        | student                                 |
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job for file "ChecksumError.zip" is completed in database
Then I should see following map of entry counts in the corresponding collections:
        | collectionName                          | count     |
        | student                                 | 0         |
Then I should see "BASE_0006" in the resulting error log file
  And I should see "Processed 0 records." in the resulting batch job file

Scenario: Post a zip file with bad control file
  Given I post "BadCtlFile.zip" file as the payload of the ingestion job
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job for file "BadCtlFile.zip" is completed in database
  And I should see "Not all records were processed completely due to errors." in the resulting batch job file

Scenario: Post an zip file where the control file has extra properties
  Given I post "ControlFileHasExtraProperty.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
        | collectionName              |
        | session                     |
  When zip file is scp to ingestion landing zone
  And a batch job for file "ControlFileHasExtraProperty.zip" is completed in database
  And I should see "BASE_0016:" in the resulting error log file
  And I should see "CORE_0003:" in the resulting error log file
  And I should see "Processed 0 records." in the resulting batch job file

Scenario: Post a zip file containing error CalendarDate with ID References job: Clean Database
Given I post "Error_Report1.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName               |
     | calendarDate                 |
     | course                       |
     | educationOrganization        |
     | gradebookEntry               |
     | gradingPeriod                |
     | learningObjective            |
     | learningStandard             |
     | gradingPeriod                |
     | section                      |
     | session                      |
     | calendarDate                 |
     | student                      |
     | courseOffering               |
     | competencyLevelDescriptor    |
When zip file is scp to ingestion landing zone
  And a batch job for file "Error_Report1.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName               | count   |
     | session                      |  10     |
  And I should see "Processed 35 records." in the resulting batch job file
  And I should see "CORE_0009" in the resulting error log file for "InterchangeEducationOrgCalendar.xml"
  And I should see "CORE_0006" in the resulting error log file for "InterchangeEducationOrganization.xml"
  And I should see "SELF_REFERENCING_DATA" in the resulting error log file for "InterchangeEducationOrganization.xml"
  And I should see "parentEducationAgencyReference" in the resulting error log file for "InterchangeEducationOrganization.xml"
  And I should see "stateOrganizationId=IL-DAYBREAK" in the resulting error log file for "InterchangeEducationOrganization.xml"

Scenario: Post a zip file containing attendance but no session data: Clean Database
Given I post "Error_Report2.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName               |
     | calendarDate                 |
     | course                       |
     | educationOrganization        |
     | gradebookEntry               |
     | gradingPeriod                |
     | learningObjective            |
     | learningStandard             |
     | gradingPeriod                |
     | section                      |
     | session                      |
     | attendance                   |
     | calendarDate                 |
     | student                      |
     | courseOffering               |
     | competencyLevelDescriptor    |
When zip file is scp to ingestion landing zone
  And a batch job for file "Error_Report2.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName               | count   |
     | attendance                   |   0     |
  And I should see "Processed 7 records." in the resulting batch job file

Scenario: Post a zip file and then post it again and make sure the updated date changes but the created date stays the same
  Given I post "stringOrEnumContainsWhitespace.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
        | collectionName              |
        | student                     |
        | recordHash                  |
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job for file "stringOrEnumContainsWhitespace.zip" is completed in database
  And I find a(n) "student" record where "body.studentUniqueStateId" is equal to "100000000"
  And verify that "metaData.created" is equal to "metaData.updated"
  Given I am using preconfigured Ingestion Landing Zone
  And I post "stringOrEnumContainsWhitespace.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
        | collectionName              |
        | recordHash                  |
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job log has been created
  And I find a(n) "student" record where "body.studentUniqueStateId" is equal to "100000000"
  And verify that "metaData.created" is unequal to "metaData.updated"

Scenario: Post an unzipped ctl file and make sure it is not processed
  Given I post "UnzippedControlFile.ctl" unzipped file as the payload of the ingestion job
  And the following collections are empty in datastore:
        | collectionName              |
        | student                     |
        | recordHash                  |
  When ctl file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job for file "UnzippedControlFile.ctl" is completed in database
  And a batch job log has been created
  Then I should see following map of entry counts in the corresponding collections:
     | collectionName               | count   |
     | student                      |   0     |
     | recordHash                   |   0     |
  And I should see "Processed 0 records." in the resulting batch job file
  And I should see "CORE_0058" in the resulting error log file for "UnzippedControlFile.ctl"
