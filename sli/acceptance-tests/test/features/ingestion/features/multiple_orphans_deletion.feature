@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
    Given I am using local data store

Scenario: Multiple Orphans
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    And I save the collection counts in "Midgar" tenant
    And I post "MultipleOrphansDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "MultipleOrphansDelete.zip" is completed in database
	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
    And I should see "Processed 21 records." in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records failed processing: 2" in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeEducationOrganization.xml"
    And I should see "CORE_0071" in the resulting error log file for "InterchangeEducationOrganization.xml"
    And the only errors I want to see in the resulting error log file for "InterchangeEducationOrganization.xml" are below
        | code    |
        | CORE_0066|
	| CORE_0071|
	And I should not see a warning log file created
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                            |    delta|
        |assessment                             |       -1|
        |assessmentFamily                       |       -1|
        |attendance                             |       -1|
        |attendanceEvent                        |       -1|
        |grade                                  |       -1|
        |student                                |       -1|
        |parent                                 |       -1|
        |yearlyTranscript                       |       -1|
        |calendarDate                           |       -1|
        |course                                 |       -1|
        |courseOffering                         |       -1|
        |cohort                                 |       -1|
        |session                                |       -1|
        |section                                |       -1|
        |educationOrganization                  |       -1|
        |gradingPeriod                          |       -1|
        |staffEducationOrganizationAssociation  |       -1|
        |studentAssessment                      |       -1|
        |studentSchoolAssociation               |       -1|
        |student.schools                        |       -1|
        |teacherSchoolAssociation               |       -1|
        |teacherSectionAssociation              |       -1|
        |recordHash                             |      -19|
        |custom_entities                        |       -3|

    #grade lives in yearly transcript so -1 for each expected, yearly transcript not record hashed
    #3 assessment types not record hashed
    # school has 3 associated custom entities
    # attendanceEvent entity deletion is now supported.
