Feature: Tenant database spin up

Background: I have a landing zone route configured
Given I am using local data store

Scenario: The tenant is locked while the database is spinning up
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the tenant database for "Midgar" does not exist
    And I post "tenant.zip" file as the payload of the ingestion job
When the tenant with tenantId "Midgar" is locked
    And zip file is scp to ingestion landing zone
    And a batch job for file "tenant.zip" is completed in database
    And I should not see a warning log file created
    And I should see "INFO  Not all records were processed completely due to errors." in the resulting batch job file
    And I should see "INFO  Processed 0 records." in the resulting batch job file
    And I should see "CORE_0001" in the resulting error log file
    And the tenantIsReady flag for the tenant "Midgar" is reset

Scenario: First ingestion for a new tenant
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the tenant database for "Midgar" does not exist
    And the tenantIsReady flag for the tenant "Midgar" is reset
    And I post "tenant.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job for file "tenant.zip" is completed in database
  And I should not see an error log file created
  And I should not see a warning log file created
Then I should see following map of indexes in the corresponding collections:
     | collectionName                             | index                       |
     | section                                    | body.schoolId               |
     | student                                    | body.studentUniqueStateId   |
     | teacherSchoolAssociation                   | body.schoolId               |
     | recordHash                                 | _id                         |
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
	 | recordHash                             |
