@RALLY_US3033
Feature: Sample Data Set Ingestion Timimg

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Post Small Sample Data Set
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "safe_cascading_deletion.zip" file as the payload of the ingestion job
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
     | recordHash                                |
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
     | studentAssessment                         |
     | studentCohortAssociation                  |
     | studentCompetency                         |
     | studentCompetencyObjective                |
     | studentDisciplineIncidentAssociation      |
     | studentObjectiveAssessment                |
     | studentParentAssociation                  |
     | studentProgramAssociation                 |
     | studentSchoolAssociation                  |
     | studentSectionAssociation                 |
     | studentGradebookEntry                     |
     | courseTranscript                          |
     | teacher                                   |
     | teacherSchoolAssociation                  |
     | teacherSectionAssociation                 |
     | yearlyTranscript                          |
When zip file is scp to ingestion landing zone
  And a batch job for file "safe_cascading_deletion.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | assessment                               |                 0|
     | attendance                               |                 0|
     | calendarDate                             |                 1|
     | cohort                                   |                 0|
     | competencyLevelDescriptor                |                 0|
     | course                                   |                 1|
     | courseOffering                           |                 1|
     | courseSectionAssociation                 |                 0|
     | disciplineAction                         |                 0|
     | disciplineIncident                       |                 0|
     | educationOrganization                    |                 3|
     | educationOrganizationAssociation         |                 0|
     | educationOrganizationSchoolAssociation   |                 0|
     | grade                                    |                 0|
     | gradebookEntry                           |                 0|
     | gradingPeriod                            |                 1|
     | graduationPlan                           |                 0|
     | learningObjective                        |                 0|
     | learningStandard                         |                 0|
     | parent                                   |                 1|
     | program                                  |                 1|
     | recordHash                               |                22|
     | reportCard                               |                 0|
     | schoolSessionAssociation                 |                 0|
     | section                                  |                 3|
     | sectionAssessmentAssociation             |                 0|
     | sectionSchoolAssociation                 |                 0|
     | session                                  |                 1|
     | sessionCourseAssociation                 |                 0|
     | staff                                    |                 2|
     | staffCohortAssociation                   |                 0|
     | staffEducationOrganizationAssociation    |                 1|
     | staffProgramAssociation                  |                 1|
     | student                                  |                 1|
     | studentAcademicRecord                    |                 0|
     | studentAssessment                        |                 0|
     | studentCohortAssociation                 |                 0|
     | studentCompetency                        |                 0|
     | studentCompetencyObjective               |                 0|
     | studentDisciplineIncidentAssociation     |                 0|
     | studentObjectiveAssessment               |                 0|
     | studentParentAssociation                 |                 1|
     | studentProgramAssociation                |                 1|
     | studentSchoolAssociation                 |                 1|
     | studentSectionAssociation                |                 1|
     | studentGradebookEntry                    |                 0|
     | courseTranscript                         |                 0|
     | teacherSchoolAssociation                 |                 1|
     | teacherSectionAssociation                |                 1|
    And I should see "Processed 22 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created