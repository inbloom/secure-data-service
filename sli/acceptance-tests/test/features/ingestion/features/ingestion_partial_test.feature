@RALLY_US3696
Feature: Partial Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Partial ingestion of data
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "PartialIgestionDataSet_Tier0.zip" file as the payload of the ingestion job
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
        | graduationPlan              |
        | recordHash                  |
  When zip file is scp to ingestion landing zone
  And a batch job for file "PartialIgestionDataSet_Tier0.zip" is completed in database
  And a batch job log has been created
  Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 78    |
        | staff                       | 14    |
        | parent                      | 9     |
        | program                     | 2     |
        | learningStandard            | 36    |
        | calendarDate                | 556   |
        | graduationPlan              | 3     |
  And I should not see a warning log file created

  And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "PartialIgestionDataSet_Tier1.zip" file as the payload of the ingestion job
  When zip file is scp to ingestion landing zone
    And a batch job for file "PartialIgestionDataSet_Tier1.zip" is completed in database
    And a batch job log has been created
  Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 78    |
        | staff                       | 14    |
        | parent                      | 9     |
        | program                     | 2     |
        | learningStandard            | 36    |
        | calendarDate                | 556   |
        | graduationPlan              | 3     |
        | educationOrganization       | 5     |
        | gradingPeriod               | 17    |
        | learningObjective           | 63    |
        | staffProgramAssociation     | 3     |
        | studentParentAssociation    | 9     |
        | studentProgramAssociation   | 6     |
  And I should not see a warning log file created

  And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "PartialIgestionDataSet_Tier2.zip" file as the payload of the ingestion job
  When zip file is scp to ingestion landing zone
    And a batch job for file "PartialIgestionDataSet_Tier2.zip" is completed in database
    And a batch job log has been created
  Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 78    |
        | staff                       | 14    |
        | parent                      | 9     |
        | program                     | 2     |
        | learningStandard            | 36    |
        | calendarDate                | 556   |
        | graduationPlan              | 3     |
        | educationOrganization       | 5     |
        | gradingPeriod               | 17    |
        | learningObjective           | 63    |
        | staffProgramAssociation     | 3     |
        | studentParentAssociation    | 9     |
        | studentProgramAssociation   | 6     |
        | cohort                      | 3     |
        | course                      | 95    |
        | disciplineIncident          | 2     |
        | session                     | 22    |
        | staffEducationOrganizationAssociation | 10 |
        | studentSchoolAssociation    | 167   |
        | studentCompetencyObjective  | 4     |
  And I should not see a warning log file created

  And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "PartialIgestionDataSet_Tier3.zip" file as the payload of the ingestion job
  When zip file is scp to ingestion landing zone
    And a batch job for file "PartialIgestionDataSet_Tier3.zip" is completed in database
    And a batch job log has been created
  Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 78    |
        | staff                       | 14    |
        | parent                      | 9     |
        | program                     | 2     |
        | learningStandard            | 36    |
        | calendarDate                | 556   |
        | graduationPlan              | 3     |
        | educationOrganization       | 5     |
        | gradingPeriod               | 17    |
        | learningObjective           | 63    |
        | staffProgramAssociation     | 3     |
        | studentParentAssociation    | 9     |
        | studentProgramAssociation   | 6     |
        | cohort                      | 3     |
        | course                      | 95    |
        | disciplineIncident          | 2     |
        | session                     | 22    |
        | staffEducationOrganizationAssociation | 10 |
        | studentSchoolAssociation    | 167   |
        | studentCompetencyObjective  | 4     |
        | courseOffering              | 95    |
        | disciplineAction            | 2     |
        | staffCohortAssociation      | 3     |
        | studentCohortAssociation    | 6     |
  And I should not see a warning log file created

  And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "PartialIgestionDataSet_Tier4.zip" file as the payload of the ingestion job
  When zip file is scp to ingestion landing zone
    And a batch job for file "PartialIgestionDataSet_Tier4.zip" is completed in database
    And a batch job log has been created
    And I should not see a warning log file created

  And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "PartialIgestionDataSet_Tier5.zip" file as the payload of the ingestion job
  When zip file is scp to ingestion landing zone
    And a batch job for file "PartialIgestionDataSet_Tier5.zip" is completed in database
    And a batch job log has been created
  Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 78    |
        | staff                       | 14    |
        | parent                      | 9     |
        | program                     | 2     |
        | learningStandard            | 36    |
        | calendarDate                | 556   |
        | graduationPlan              | 3     |
        | educationOrganization       | 5     |
        | gradingPeriod               | 17    |
        | learningObjective           | 63    |
        | staffProgramAssociation     | 3     |
        | studentParentAssociation    | 9     |
        | studentProgramAssociation   | 6     |
        | cohort                      | 3     |
        | course                      | 95    |
        | disciplineIncident          | 2     |
        | session                     | 22    |
        | staffEducationOrganizationAssociation | 10 |
        | studentSchoolAssociation    | 167   |
        | studentCompetencyObjective  | 4     |
        | courseOffering              | 95    |
        | disciplineAction            | 2     |
        | staffCohortAssociation      | 3     |
        | studentCohortAssociation    | 6     |
        | section                     | 97    |
        | assessment                  | 3     |
        | assessmentFamily            | 2     |
        | assessmentPeriodDescriptor  | 2     |
        | attendance                  | 75    |
        | studentSectionAssociation   | 297   |
        | teacherSectionAssociation   | 11    |
  And I should not see a warning log file created

  And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "PartialIgestionDataSet_Tier6.zip" file as the payload of the ingestion job
  When zip file is scp to ingestion landing zone
    And a batch job for file "PartialIgestionDataSet_Tier6.zip" is completed in database
    And a batch job log has been created
    And I should not see a warning log file created

  And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "PartialIgestionDataSet_Tier7.zip" file as the payload of the ingestion job
  When zip file is scp to ingestion landing zone
    And a batch job for file "PartialIgestionDataSet_Tier7.zip" is completed in database
    And a batch job log has been created
  Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 78    |
        | staff                       | 14    |
        | parent                      | 9     |
        | program                     | 2     |
        | learningStandard            | 36    |
        | calendarDate                | 556   |
        | graduationPlan              | 3     |
        | educationOrganization       | 5     |
        | gradingPeriod               | 17    |
        | learningObjective           | 63    |
        | staffProgramAssociation     | 3     |
        | studentParentAssociation    | 9     |
        | studentProgramAssociation   | 6     |
        | cohort                      | 3     |
        | course                      | 95    |
        | disciplineIncident          | 2     |
        | session                     | 22    |
        | staffEducationOrganizationAssociation | 10 |
        | studentSchoolAssociation    | 167   |
        | studentCompetencyObjective  | 4     |
        | courseOffering              | 95    |
        | disciplineAction            | 2     |
        | staffCohortAssociation      | 3     |
        | studentCohortAssociation    | 6     |
        | section                     | 97    |
        | assessment                  | 3     |
        | attendance                  | 75    |
        | studentSectionAssociation   | 297   |
        | teacherSectionAssociation   | 11    |
        | reportCard                  | 2     |
        | studentAcademicRecord       | 116   |
        | studentCompetency           | 59    |
        | courseTranscript            | 196  |
  And I should not see a warning log file created

