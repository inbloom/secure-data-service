@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
    Given I am using local data store

Scenario: Multiple Orphans
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    And I save the collection counts in "Midgar" tenant
    And I post "MultipleForceDeletes.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "MultipleForceDeletes.zip" is completed in database
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeAssessmentMetadata.xml"
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentGrades.xml"
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeMasterSchedule.xml"
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeEducationOrganization.xml"
	And I should not see an error log file created
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                            |    delta|
        |assessment                             |       -1|
        |assessmentFamily                       |       -1|
        |assessmentItem                         |       -2|
        |assessmentPeriodDescriptor             |       -1|
        |attendanceEvent                        |       -1|
        |calendarDate                           |       -1|
        |competencyLevelDescriptor              |       -1|
        |course                                 |       -1|
        |courseOffering                         |       -1|
        |courseTranscript                       |       -1|
        |custom_entities                        |       -2|
        |educationOrganization                  |       -3|
        |grade                                  |       -1|
        |gradebookEntry                         |      -25|
        |gradingPeriod                          |       -1|
        |learningObjective                      |       -1|
        |learningStandard                       |       -1|
        |objectiveAssessment                    |       -4|
        |program                                |       -1|
        |reportCard                             |       -1|
        |section                                |       -1|
        |session                                |       -1|
        |studentAcademicRecord                  |       -1|
        |studentCompetency                      |       -1|
        |studentCompetencyObjective             |       -1|
        |studentSectionAssociation              |       -1|
        |teacherSectionAssociation              |       -2|
        |recordHash                             |      -20|