@RALLY_US212
@RALLY_US308
@RALLY_US309
@RALLY_US2292
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
  And I should see "File student.xml: Empty file" in the resulting error log file
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
  And I should see "ERROR: There has been a data validation error when saving an entity" in the resulting error log file
  And I should see "     Error      ENUMERATION_MISMATCH" in the resulting error log file
  And I should see "     Entity     student" in the resulting error log file
  And I should see "     Instance   1" in the resulting error log file
  And I should see "     Field      sex" in the resulting error log file
  And I should see "     Value      Boy" in the resulting error log file
  And I should see "     Expected   ['Female', 'Male']" in the resulting error log file
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
  And I should see "An entity is missing one or more required natural key fields" in the resulting error log file
  And I should see "     Entity     student" in the resulting error log file
  And I should see "     Instance   1" in the resulting error log file
  And I should see "     Field      studentUniqueStateId" in the resulting error log file
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
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job for file "secondRecordHasIncorrectAttribute.zip" is completed in database
  And I should see "An entity is missing one or more required natural key fields" in the resulting error log file
  And I should see "     Entity     student" in the resulting error log file
  And I should see "     Instance   2" in the resulting error log file
  And I should see "     Field      studentUniqueStateId" in the resulting error log file
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
  And I should see "ERROR: There has been a data validation error when saving an entity" in the resulting error log file
  And I should see "     Error      REQUIRED_FIELD_MISSING" in the resulting error log file
  And I should see "     Entity     student" in the resulting error log file
  And I should see "     Instance   1" in the resulting error log file
  And I should see "     Field      firstName" in the resulting error log file
  And I should see "     Value      " in the resulting error log file
  And I should see "     Expected   [STRING]" in the resulting error log file
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
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job for file "firstRecordMissingAttribute.zip" is completed in database
  And I should see "ERROR: There has been a data validation error when saving an entity" in the resulting error log file
  And I should see "       Error      REQUIRED_FIELD_MISSING" in the resulting error log file
  And I should see "       Entity     student" in the resulting error log file
  And I should see "       Instance   1" in the resulting error log file
  And I should see "       Field      firstName" in the resulting error log file
  And I should see "       Value      " in the resulting error log file
  And I should see "       Expected   [STRING]" in the resulting error log file
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

Scenario: Post a zip file and then post it again and make sure the updated date changes but the created date stays the same
  Given I post "stringOrEnumContainsWhitespace.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
        | collectionName              |
        | student                     |
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job for file "stringOrEnumContainsWhitespace.zip" is completed in database
  And I find a(n) "student" record where "body.studentUniqueStateId" is equal to "100000000"
  And verify that "metaData.created" is equal to "metaData.updated"
  Given I am using preconfigured Ingestion Landing Zone
  And I post "stringOrEnumContainsWhitespace.zip" file as the payload of the ingestion job
  And the following collections are completely empty in batch job datastore
        | collectionName              |
        | recordHash                  |
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job log has been created
  And I find a(n) "student" record where "body.studentUniqueStateId" is equal to "100000000"
  And verify that "metaData.created" is unequal to "metaData.updated"

#Background: zip file contains a .txt and .rtf files, which should fail ingestion
Scenario: Post a minimal zip file as a payload of the ingestion job: No Valid Files Test
Given I post "NoValidFilesInCtlFile.zip" file as the payload of the ingestion job

When zip file is scp to ingestion landing zone
  And a batch job for file "NoValidFilesInCtlFile.zip" is completed in database
 Then I should see following map of entry counts in the corresponding batch job db collections:
     | collectionName              | count |
	| error                       | 1     |
    And I should see "INFO  Processed 0 records." in the resulting batch job file
    And I should see "ERROR  No valid files specified in control file." in the resulting error log file


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
Then I should see "File ./InterchangeEducationOrganization.xml: File name contains path." in the resulting error log file
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
Then I should see ".zip archive ZipContainsSubfolder.zip contains a directory." in the resulting error log file
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
Then I should see "File Students.xml: unknown or empty file format specified" in the resulting error log file
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
Then I should see "File MissingXmlFile.xml: Specified file is missing" in the resulting error log file
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
Then I should see "ERROR  File Session2.xml: Checksum validation failed. Possible file corruption." in the resulting error log file
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
  And I should see "ERROR  Invalid control file entry at line number" in the resulting error log file
  And I should see "Processed 0 records." in the resulting batch job file
