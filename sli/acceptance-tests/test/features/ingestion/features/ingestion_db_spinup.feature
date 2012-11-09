Feature: Tenant database spin up

Background: I have a landing zone route configured
Given I am using local data store


Scenario: First ingestion for a new tenant
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the tenant database for "Midgar" does not exist
    And I post "tenant.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job for file "tenant.zip" is completed in database
Then I should see following map of indexes in the corresponding collections:
     | collectionName                             | index                       |
     | section                                    | body.schoolId_1             |
     | student                                    | body.studentUniqueStateId_1 |
     | teacherSchoolAssociation                   | body.schoolId_1             |
    And the database is sharded for the following collections
     | collectionName	                      |
     | attendance                             |
     | cohort                                 |
     | courseSectionAssociation               |
     | disciplineAction                       |
     | disciplineIncident                     |
     | educationOrganizationAssociation       |
     | educationOrganizationSchoolAssociation |
     | grade                                  |
     | gradebookEntry                         |
     | graduationPlan                         |
     | parent                                 |
     | reportCard                             |
     | section                                |
     | staff                                  |
     | staffCohortAssociation                 |
     | staffEducationOrganizationAssociation  |
     | staffProgramAssociation                |
     | student                                |
     | studentAcademicRecord                  |
     | studentCohortAssociation               |
     | studentCompetency                      |
     | studentCompetencyObjective             |
     | studentDisciplineIncidentAssociation   |
     | studentGradebookEntry                  |
     | studentParentAssociation               |
     | studentProgramAssociation              |
     | studentSchoolAssociation               |
     | courseTranscript                       |
     | teacherSchoolAssociation               |
     | teacherSectionAssociation              |
