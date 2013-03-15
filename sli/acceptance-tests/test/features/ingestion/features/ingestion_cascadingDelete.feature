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
     | assessment                               |                 2|
     | attendance                               |                 1|
     | calendarDate                             |                 1|
     | cohort                                   |                 2|
     | competencyLevelDescriptor                |                 0|
     | course                                   |                 1|
     | courseOffering                           |                 1|
     | courseSectionAssociation                 |                 0|
     | disciplineAction                         |                 1|
     | disciplineIncident                       |                 1|
     | educationOrganization                    |                 3|
     | educationOrganizationAssociation         |                 0|
     | educationOrganizationSchoolAssociation   |                 0|
     | grade                                    |                 1|
     | gradebookEntry                           |                 1|
     | gradingPeriod                            |                 1|
     | graduationPlan                           |                 0|
     | learningObjective                        |                 1|
     | learningStandard                         |                 1|
     | parent                                   |                 1|
     | program                                  |                 1|
     | recordHash                               |                40|
     | reportCard                               |                 1|
     | schoolSessionAssociation                 |                 0|
     | section                                  |                 4|
     | sectionAssessmentAssociation             |                 0|
     | sectionSchoolAssociation                 |                 0|
     | session                                  |                 1|
     | sessionCourseAssociation                 |                 0|
     | staff                                    |                 2|
     | staffCohortAssociation                   |                 1|
     | staffEducationOrganizationAssociation    |                 1|
     | staffProgramAssociation                  |                 1|
     | student                                  |                 1|
     | studentAcademicRecord                    |                 1|
     | studentAssessment                        |                 1|
     | studentCohortAssociation                 |                 1|
     | studentCompetency                        |                 1|
     | studentCompetencyObjective               |                 2|
     | studentDisciplineIncidentAssociation     |                 1|
     | studentObjectiveAssessment               |                 1|
     | studentParentAssociation                 |                 1|
     | studentProgramAssociation                |                 1|
     | studentSchoolAssociation                 |                 1|
     | studentSectionAssociation                |                 1|
     | studentGradebookEntry                    |                 1|
     | courseTranscript                         |                 1|
     | teacherSchoolAssociation                 |                 1|
     | teacherSectionAssociation                |                 1|
    And I should see "Processed 46 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created