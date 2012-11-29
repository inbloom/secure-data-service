@RALLY_US4816
Feature: Odin Data Set Ingestion Correctness and Fidelity

Background: I have a landing zone route configured
Given I am using odin data store

Scenario: Post Odin Sample Data Set
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And the tenant database for "Midgar" does not exist
  And I post "OdinSampleDataSet.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                            |
     | assessment                                |
     | attendance                                |
     | calendarDate                              |
     | cohort                                    |
     | competencyLevelDescriptor                 |
     | course                                    |
     | courseOffering                            |
     | courseSectionAssociation                  |
     | disciplineAction                          |
     | disciplineIncident                        |
     | educationOrganization                     |
     | educationOrganizationAssociation          |
     | educationOrganizationSchoolAssociation    |
     | grade                                     |
     | gradebookEntry                            |
     | gradingPeriod                             |
     | graduationPlan                            |
     | learningObjective                         |
     | learningStandard                          |
     | parent                                    |
     | program                                   |
     | reportCard                                |
     | school                                    |
     | schoolSessionAssociation                  |
     | section                                   |
     | sectionAssessmentAssociation              |
     | sectionSchoolAssociation                  |
     | session                                   |
     | sessionCourseAssociation                  |
     | staff                                     |
     | staffCohortAssociation                    |
     | staffEducationOrganizationAssociation     |
     | staffProgramAssociation                   |
     | student                                   |
     | studentAcademicRecord                     |
     | studentAssessment              |
     | studentCohortAssociation                  |
     | studentCompetency                         |
     | studentCompetencyObjective                |
     | studentDisciplineIncidentAssociation      |
     | studentParentAssociation                  |
     | studentProgramAssociation                 |
     | studentSchoolAssociation                  |
     | studentSectionAssociation                 |
     | studentGradebookEntry                     |
     | courseTranscript                          |
     | teacher                                   |
     | teacherSchoolAssociation                  |
     | teacherSectionAssociation                 |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | assessment                               |                  0|
     | attendance                               |                  0|
     | calendarDate                             |                594|
     | cohort                                   |                  0|
     | competencyLevelDescriptor                |                  0|
     | course                                   |                 34|
     | courseOffering                           |                102|
     | courseSectionAssociation                 |                  0|
     | disciplineAction                         |                  0|
     | disciplineIncident                       |                  0|
     | educationOrganization                    |                  6|
     | educationOrganizationAssociation         |                  0|
     | educationOrganizationSchoolAssociation   |                  0|
     | grade                                    |                  0|
     | gradebookEntry                           |                  0|
     | gradingPeriod                            |                  6|
     | graduationPlan                           |                  0|
     | learningObjective                        |                  0|
     | learningStandard                         |                  0|
     | parent                                   |                  0|
     | program                                  |                  0|
     | reportCard                               |                  0|
     | schoolSessionAssociation                 |                  0|
     | section                                  |                  0|
     | sectionAssessmentAssociation             |                  0|
     | sectionSchoolAssociation                 |                  0|
     | session                                  |                  6|
     | sessionCourseAssociation                 |                  0|
     | staff                                    |                  0|
     | staffCohortAssociation                   |                  0|
     | staffEducationOrganizationAssociation    |                  0|
     | staffProgramAssociation                  |                  0|
     | student                                  |                 10|
     | studentAcademicRecord                    |                  0|
     | studentAssessment                        |                  0|
     | studentCohortAssociation                 |                  0|
     | studentCompetency                        |                  0|
     | studentCompetencyObjective               |                  0|
     | studentDisciplineIncidentAssociation     |                  0|
     | studentParentAssociation                 |                  0|
     | studentProgramAssociation                |                  0|
     | studentSchoolAssociation                 |                 10|
     | studentSectionAssociation                |                  0|
     | studentGradebookEntry                    |                  0|
     | courseTranscript                         |                  0|
     | teacherSchoolAssociation                 |                  0|
     | teacherSectionAssociation                |                  0|
    And I should see "Processed 1355 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created


