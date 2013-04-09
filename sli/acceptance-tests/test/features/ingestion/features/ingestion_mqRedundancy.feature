@RALLY_US3197
Feature: Activemq Redundancy Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Post a zip file as a payload of the ingestion job: Clean Database
Given I am using preconfigured Ingestion Landing Zone for "IL-Daybreak"
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
        | assessmentFamily            |
        | assessmentPeriodDescriptor  |
        | studentAssessment           |
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
  And "30" seconds have elapsed
  And an activemq instance "instance1" running in "/opt/apache-activemq-5.6.0/bin" and on jmx port "1099" stops
  And a batch job for file "StoriedDataSet_IL_Daybreak.zip" is completed in database
  And a batch job log has been created

Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | assessment                  | 0     |
        | assessmentFamily            | 0     |
        | assessmentPeriodDescriptor  | 0     |
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
    And I restart the activemq instance "instance1" running on "/opt/apache-activemq-5.6.0"