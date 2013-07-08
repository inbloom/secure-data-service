@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

  Background: I have a landing zone route configured
  Given I am using local data store

  @wip
  Scenario: Delete Program with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported

    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "DR1"
      |field                                                  |value                                                |
      |_id                                                    |908404e876dd56458385667fa383509035cd4312_id          |
    Then there exist "2" "attendance" records like below in "Midgar" tenant. And I save this query as "DR2"
      |field                                                  |value                                                |
      |body.studentId                                         |908404e876dd56458385667fa383509035cd4312_id          |
    Then there exist "1" "cohort" records like below in "Midgar" tenant. And I save this query as "DR3"
      |field                                                  |value                                                |
      |studentCohortAssociation.body.studentId                |908404e876dd56458385667fa383509035cd4312_id          |
    Then there exist "7" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "DR4"
      |field                                                  |value                                                |
      |body.studentId                                         |908404e876dd56458385667fa383509035cd4312_id          |
    Then there exist "8" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "DR5"
      |field                                                  |value                                                |
      |body.studentId                                         |908404e876dd56458385667fa383509035cd4312_id          |
    Then there exist "3" "program" records like below in "Midgar" tenant. And I save this query as "DR6"
      |field                                                  |value                                                |
      |studentProgramAssociation.body.studentId               |908404e876dd56458385667fa383509035cd4312_id          |
    Then there exist "6" "section" records like below in "Midgar" tenant. And I save this query as "DR7"
      |field                                                  |value                                                |
      |studentSectionAssociation.body.studentId               |908404e876dd56458385667fa383509035cd4312_id          |
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "DR8"
      |field                                                  |value                                                |
      |studentDisciplineIncidentAssociation.body.studentId    |908404e876dd56458385667fa383509035cd4312_id          |
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "DR9"
      |field                                                  |value                                                |
      |studentParentAssociation.body.studentId                |908404e876dd56458385667fa383509035cd4312_id          |
    Then there exist "4" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "DR10"
      |field                                                  |value                                                |
      |body.studentId                                         |908404e876dd56458385667fa383509035cd4312_id          |
    Then there exist "139" "studentGradebookEntry" records like below in "Midgar" tenant. And I save this query as "DR11"
      |field                                                  |value                                                |
      |body.studentId                                         |908404e876dd56458385667fa383509035cd4312_id          |
    Then there exist "2" "studentSchoolAssociation" records like below in "Midgar" tenant. And I save this query as "DR12"
      |field                                                  |value                                                |
      |body.studentId                                         |908404e876dd56458385667fa383509035cd4312_id          |
    Then there exist "2" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "DR13"
      |field                                                  |value                                                |
      |body.studentId                                         |908404e876dd56458385667fa383509035cd4312_id          |
    Then there exist "2" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "DR14"
      |field                                                  |value                                                |
      |grade.body.studentId                                   |908404e876dd56458385667fa383509035cd4312_id          |
    Then there exist "2" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "DR15"
      |field                                                  |value                                                |
      |reportCard.body.studentId                              |908404e876dd56458385667fa383509035cd4312_id          |
    Then there exist "2" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "DR16"
      |field                                                  |value                                                |
      |studentAcademicRecord.body.studentId                   |908404e876dd56458385667fa383509035cd4312_id          |

    And I post "BroadStudentDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "BroadStudentDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created

    And I re-execute saved query "DR1" to get "0" records
    And I re-execute saved query "DR2" to get "0" records
    And I re-execute saved query "DR3" to get "0" records
    And I re-execute saved query "DR4" to get "0" records
    And I re-execute saved query "DR5" to get "0" records
    And I re-execute saved query "DR6" to get "0" records
    And I re-execute saved query "DR7" to get "0" records
    And I re-execute saved query "DR8" to get "0" records
    And I re-execute saved query "DR9" to get "0" records
    And I re-execute saved query "DR10" to get "0" records
    And I re-execute saved query "DR11" to get "0" records
    And I re-execute saved query "DR12" to get "0" records
    And I re-execute saved query "DR13" to get "0" records
    And I re-execute saved query "DR14" to get "0" records
    And I re-execute saved query "DR15" to get "0" records
    And I re-execute saved query "DR16" to get "0" records

    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "BroadSetOfTypes.zip" file as the payload of the ingestion job
    Then a batch job for file "BroadSetOfTypes.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    When zip file is scp to ingestion landing zone
    And I should not see an error log file created
    And I should not see a warning log file created

    And I re-execute saved query "DR1" to get "1" records
    And I re-execute saved query "DR2" to get "2" records
    And I re-execute saved query "DR3" to get "1" records
    And I re-execute saved query "DR4" to get "7" records
    And I re-execute saved query "DR5" to get "8" records
    And I re-execute saved query "DR6" to get "3" records
    And I re-execute saved query "DR7" to get "6" records
    And I re-execute saved query "DR8" to get "1" records
    And I re-execute saved query "DR9" to get "1" records
    And I re-execute saved query "DR10" to get "4" records
    And I re-execute saved query "DR11" to get "139" records
    And I re-execute saved query "DR12" to get "2" records
    And I re-execute saved query "DR13" to get "2" records
    And I re-execute saved query "DR14" to get "2" records
    And I re-execute saved query "DR15" to get "2" records
    And I re-execute saved query "DR16" to get "2" records
