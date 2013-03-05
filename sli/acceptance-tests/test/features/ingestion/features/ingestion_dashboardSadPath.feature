Feature: Dashboard Sad Path Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store

 @IL-Daybreak
Scenario: Post a zip file containing all data for Illinois Daybreak as a payload of the ingestion job: Clean Database
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "DashboardSadPath_IL_Daybreak.zip" file as the payload of the ingestion job
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
        | studentAssessment           |
        | assessmentFamily            |
        | assessmentPeriodDescriptor  |
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
        | studentAcademicRecord       |
When zip file is scp to ingestion landing zone
  And a batch job for file "DashboardSadPath_IL_Daybreak.zip" is completed in database
  And a batch job log has been created
  And I should see "Processed 759 records." in the resulting batch job file

@IL-Sunset
Scenario: Post a zip file containing bad data for Illinois Sunset as a payload of the ingestion job: Append Database
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Sunset"
  And I post "DashboardSadPath_IL_Sunset.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job for file "DashboardSadPath_IL_Sunset.zip" is completed in database
  And a batch job log has been created

@NY-NYC  
Scenario: Post a zip file containing bad data for New York as a payload of the ingestion job: Append Database
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
        | studentAssessment|
        | studentCohortAssociation    |
        | studentCompetency           |
        | studentCompetencyObjective  |
        | studentDisciplineIncidentAssociation|
        | studentGradebookEntry       |
        | studentParentAssociation    |
        | studentProgramAssociation   |
        | studentSchoolAssociation    |
        | studentSectionAssociation   |
        | studentTranscriptAssociation|
        | teacherSchoolAssociation    |
        | teacherSectionAssociation   |
        |staffEducationOrganizationAssociation|
  And I post "DashboardSadPath_NY.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job for file "DashboardSadPath_NY.zip" is completed in database
  And a batch job log has been created
