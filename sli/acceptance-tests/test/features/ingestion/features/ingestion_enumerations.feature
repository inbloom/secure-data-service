@RALLY_US5711
@RALLY_US5776
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
        | assessmentItem                        |
        | attendance                            |
        | disciplineIncident                    |
        | educationOrganization                 |
        | grade                                 |
        | section                               |
        | section                               |
        | session                               |
        | student                               |        
        | studentAssessment                     |
        | studentSchoolAssociation              |      
  When zip file is scp to ingestion landing zone
  And a batch job for file "Enums.zip" is completed in database

Then I should see following map of entry counts in the corresponding collections:
        | collectionName                           | count |
        | assessment                               | 1     |
        | assessmentItem                           | 1     |
        | attendance                               | 1     |
        | disciplineIncident                       | 1     |
        | educationOrganization                    | 1     |
        | grade                                    | 1     |
        | section                                  | 1     |
        | session                                  | 1     |
        | student                                  | 3     |
        | studentAssessment                        | 1     |
        | studentSchoolAssociation                 | 1     |        
    And I check to find if record is in collection:
       | collectionName              | expectedRecordCount | searchParameter                        | searchValue                | searchType           |
       | assessment                  | 1                   | body.academicSubject                   | Career Education           | string               |
       | assessmentItem              | 1                   | body.itemCategory                      | Other                      | string               |
       | attendance                  | 1                   | body.attendanceEvent.event             | In-School Suspension       | string               |
       | disciplineIncident          | 1                   | body.incidentLocation                  | Other                      | string               |
       | disciplineIncident          | 1                   | body.secondaryBehaviors.behaviorCategory| Other                     | string               |
       | educationOrganization       | 1                   | body.schoolCategories                  | Other                      | string               |
       | grade                       | 1                   | body.gradeType                         | Other                      | string               |
       | section                     | 1                   | body.educationalEnvironment            | Other                      | string               |
       | session                     | 1                   | body.term                              | Term 4                     | string               |
       | student                     | 1                   | studentParentAssociation.body.relation | Grandmother                | string               |
       | studentAssessment           | 1                   | body.administrationEnvironment         | Other                      | string               |
       | studentAssessment           | 1                   | body.linguisticAccommodations          | Other                      | string               |
       | studentAssessment           | 1                   | studentAssessmentItem.body.assessmentItemResult|Did not meet standard| string               |
       | studentSchoolAssociation    | 1                   | body.exitWithdrawType                  | End of walk-in enrollment  | string               |  
    And I find a record in "student" with "body.studentUniqueStateId" equal to "100000000"
    And the field "body.birthData.stateOfBirthAbbreviation" with value "FM" is encrypted
    And the field "body.birthData.countryOfBirthCode" with value "SS" is encrypted
              

    And I should see "Processed 13 records." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created

