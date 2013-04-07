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
	And I should see "All records processed successfully." in the resulting batch job file
	And I should see "Processed 19 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                            |    delta|
        |assessment                             |       -1|
        |assessmentFamily                       |       -1|
        |attendance                             |       -1|
        |grade                                  |      +20|
        |student                                |       -1|
        |parent                                 |       -1|
        |yearlyTranscript                       |       -1|
        |calendarDate                           |       -1|
        |course                                 |       -1|
        |courseOffering                         |       -1|
        |cohort                                 |       -1|
        |session                                |       -1|
        |section                                |       -1|
        |school                                 |       -1|
        |educationOrganization                  |       -1|
        |gradingPeriod                          |       -1|
        |staffEducationOrganizationAssociation  |       -1|
        |studentAssessment                      |       -1|
        |studentSchoolAssociation               |       -1|
        |teacherSchoolAssociation               |       -1|
        |teacherSectionAssociation              |      +46|
        |reportCard                             |      +21|
        |studentAcademicRecord                  |      +21|
        |studentSectionAssociation              |      +20|
        |gradebookEntry                         |     +768|
        |studentProgramAssociation              |      +49|
        |studentParentAssociation               |      +15|
        |studentCohortAssociation               |      +20|
        |studentDisciplineIncidentAssociation   |      +20|
        |studentAssessmentItem                  |      +42|
        |assessmentItem                         |      +27|
        |objectiveAssessment                    |     +105|
        |studentObjectiveAssessment             |      +41|
        #|recordHash                            |      -20|