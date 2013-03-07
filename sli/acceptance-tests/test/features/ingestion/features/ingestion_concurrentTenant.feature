Feature: Concurrent tenant ingestion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Concurrent job processing
Given I am using preconfigured Ingestion Landing Zone for "Hyrule-NYC"
    And the following collections are empty in datastore:
        | collectionName              |
        | assessment                  |
        | assessmentFamily            |
        | assessmentPeriodDescriptor  |
        | attendance                  |
        | calendarDate                |
        | cohort                      |
        | course                      |
        | courseOffering              |
        | disciplineAction            |
        | disciplineIncident          |
        | educationOrganization       |
        | grade                       |
        | gradebookEntry              |
        | gradingPeriod               |
        | learningObjective           |
        | learningStandard            |
        | parent                      |
        | program                     |
        | reportCard                  |
        | section                     |
        | session                     |
        | staff                       |
        | staffCohortAssociation      |
        | staffProgramAssociation     |
        | student                     |
        | studentAcademicRecord       |
        | studentAssessment           |
        | studentCohortAssociation    |
        | studentCompetency           |
        | studentCompetencyObjective  |
        | studentDisciplineIncidentAssociation|
        | studentGradebookEntry       |
        | studentParentAssociation    |
        | studentProgramAssociation   |
        | studentSchoolAssociation    |
        | studentSectionAssociation   |
        | courseTranscript            |
        | teacherSchoolAssociation    |
        | teacherSectionAssociation   |
        |staffEducationOrganizationAssociation|
        | recordHash                  |
    And I post "StoriedDataSet_IL_Daybreak.zip" and "StoriedDataSet_NY.zip" files as the payload of two ingestion jobs
When zip files are scped to the ingestion landing zone
  And a batch job for file "StoriedDataSet_IL_Daybreak.zip" is completed in database
  And a batch job for file "StoriedDataSet_NY.zip" is completed in database
  Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | assessment                  | 19    |
        | assessmentFamily            | 37    |
        | assessmentPeriodDescriptor  | 2     |
        | attendance                  | 75    |
        | calendarDate                | 1112  |
        | cohort                      | 3     |
        | competencyLevelDescriptor   | 6     |
        | course                      | 103   |
        | courseOffering              | 103   |
        | disciplineAction            | 3     |
        | disciplineIncident          | 4     |
        | educationOrganization       | 13    |
        | grade                       | 4     |
        | gradebookEntry              | 12    |
        | gradingPeriod               | 23    |
        | graduationPlan              | 4     |
        | learningObjective           | 198   |
        | learningStandard            | 1499  |
        | parent                      | 9     |
        | program                     | 2     |
        | reportCard                  | 2     |
        | section                     | 113   |
        | session                     | 26    |
        | staff                       | 51    |
        | staffCohortAssociation      | 3     |
        | staffEducationOrganizationAssociation| 50 |
        | staffProgramAssociation     | 7     |
        | student                     | 78    |
        | studentAcademicRecord       | 117   |
        | studentAssessment           | 203   |
        | studentCohortAssociation    | 6     |
        | studentCompetency           | 60    |
        | studentCompetencyObjective  | 4     |
        | studentDisciplineIncidentAssociation| 8|
        | studentGradebookEntry       | 315   |
        | studentParentAssociation    | 9     |
        | studentProgramAssociation   | 6     |
        | studentSchoolAssociation    | 175   |
        | studentSectionAssociation   | 305   |
        | courseTranscript            | 196   |
        | teacherSchoolAssociation    | 19    |
        | teacherSectionAssociation   | 27    |
   Then I should not see an error log file created
   And I should not see a warning log file created

