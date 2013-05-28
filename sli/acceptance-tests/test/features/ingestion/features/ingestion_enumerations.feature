@RALLY_US5711
Feature: Custom Enumeration Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Post a zip file containing custom enumeration data as a payload of the ingestion job: Clean Database
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "Enums.zip" file as the payload of the ingestion job
    And the following collections are empty in datastore:
        | collectionName                        |
        | recordHash                            |
        | assessment                            |
        | attendance                            |
        | student                               |
        | studentSchoolAssociation              |       
  When zip file is scp to ingestion landing zone
  And a batch job for file "Enums.zip" is completed in database

Then I should see following map of entry counts in the corresponding collections:
        | collectionName                           | count |
        | assessment                               | 1     |
        | attendance                               | 1     |
        | student                                  | 3     |
        | studentSchoolAssociation                 | 1     |        
    And I check to find if record is in collection:
       | collectionName              | expectedRecordCount | searchParameter                        | searchValue                | searchType           |
       | assessment                  | 1                   | body.academicSubject                   | Career Education           | string               |
       | attendance                  | 1                   | body.attendanceEvent.event             | In-School Suspension       | string               |
       | student                     | 1                   | studentParentAssociation.body.relation | Grandmother                | string               |
       | studentSchoolAssociation    | 1                   | body.exitWithdrawType                  | End of walk-in enrollment  | string               |  
    And I find a record in "student" with "body.studentUniqueStateId" equal to "100000000"
    And the field "body.birthData.stateOfBirthAbbreviation" with value "FM" is encrypted
    And the field "body.birthData.countryOfBirthCode" with value "SS" is encrypted
              

    And I should see "Processed 5 records." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created

