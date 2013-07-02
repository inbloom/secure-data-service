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
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentCohort.xml"
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStaffAssociation.xml"
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentAssessment.xml"
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentEnrollment.xml"
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentDiscipline.xml"
	And I should not see an error log file created
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                            |    delta|
        |assessment                             |        0|
        |assessment<hollow>                     |        1|
        |assessmentFamily                       |       -1|
        |assessmentItem                         |       -1|
        |assessmentPeriodDescriptor             |       -1|
        |attendanceEvent                        |       -1|
        |calendarDate                           |       -1|
        |cohort                                 |       -1|
        |competencyLevelDescriptor              |       -1|
        |course                                 |       -1|
        |courseOffering                         |       -1|
        |courseTranscript                       |       -1|
        |custom_entities                        |       -2|
        |disciplineAction                       |       -1|
        |disciplineIncident                     |       -1|
        |educationOrganization                  |       -3|
        |grade                                  |       -1|
        |gradebookEntry                         |       -1|
        |gradingPeriod                          |       -1|
        |graduationPlan                         |       -1|
        |learningObjective                      |       -1|
        |learningStandard                       |       -1|
        |objectiveAssessment                    |       -1|
        |parent                                 |       -1|
        |program                                |       -1|
        |reportCard                             |       -1|
        |section                                |        0|
        |section<hollow>                        |        1|
        |session                                |       -1|
        |staff                                  |       -2|
        |staffEducationOrganizationAssociation  |       -1|
        |staffProgramAssociation                |       -1|
        |studentAcademicRecord                  |       -1|
        |studentAssessment                      |        0|
        |studentAssessment<hollow>              |        1|
        |studentAssessmentItem                  |       -1|
        |studentCohortAssociation               |       -1|
        |studentCompetency                      |       -1|
        |studentCompetencyObjective             |       -1|
        |studentDisciplineIncidentAssociation   |       -1|
        |studentGradebookEntry                  |       -1|
        |studentObjectiveAssessment             |       -1|
        |studentParentAssociation               |       -1|
        |studentProgramAssociation              |       -1|
        |studentSchoolAssociation               |       -1|
        |studentSectionAssociation              |       -1|
        |teacherSchoolAssociation               |       -1|
        |teacherSectionAssociation              |       -1|
        |recordHash                             |      -46|
        |student.schools                        |       -1|
        |student.section                        |       -1|