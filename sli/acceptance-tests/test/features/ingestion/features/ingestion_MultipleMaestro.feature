@RALLY_US3203
Feature: Multiple Maestros watching same landing zone

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Post a zip file containing all data for Illinois Daybreak as a payload of the ingestion job: Clean Database
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "StoriedDataSet_IL_Daybreak.zip" file as the payload of the ingestion job
    And the following collections are empty in datastore:
        | collectionName              |
        | student                     |
        | studentSchoolAssociation    |
        | course                      |
        | educationOrganization       |
        | section                     |
        | studentSectionAssociation   |
        | staff                       |
        |staffEducationOrganizationAssociation|
        | teacherSchoolAssociation    |
        | teacherSectionAssociation   |
        | session                     |
        | assessment                  |
        | studentAssessment|
        | gradebookEntry              |
        | courseTranscript            |
        | studentGradebookEntry       |
        | parent                      |
        | studentParentAssociation    |
        | attendance                  |
        | program                     |
        | staffProgramAssociation     |
        | studentProgramAssociation   |
        | cohort                      |
        | staffCohortAssociation      |
        | studentCohortAssociation    |
        | studentCompetency           |
        | studentCompetencyObjective  |
        | learningStandard            |
        | learningObjective           |
        | disciplineIncident          |
        | disciplineAction            |
        | studentDisciplineIncidentAssociation|
        | grade                       |
        | gradingPeriod               |
        | calendarDate                |
        | reportCard                  |
        | courseOffering              |
        | studentAcademicRecord       |
  When zip file is scp to ingestion landing zone
  And "10" seconds have elapsed
  And an ingestion service "instance1" running with pid "13076" stops
  And a batch job log has been created

Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 78    |
        | course                      | 95    |
        | educationOrganization       | 5     |
        | section                     | 97    |
        | staff                       | 14    |
        | teacherSchoolAssociation    | 3     |
        | teacherSectionAssociation   | 11    |
        | session                     | 22    |
        | studentProgramAssociation   | 6     |
        | cohort                      | 3     |
        | staffCohortAssociation      | 3     |
        | learningObjective           | 135   |
        | disciplineAction            | 2     |
    And I should see "Processed 4148 records." in the resulting batch job file
    And I should not see an error log file created