@RALLY_US1107
Feature: Pre-loading of data for sandbox tenants - Ingestion component test

Scenario: Preload Small Sample Data Set
  Given I am using the tenant "Brian"
  And the tenant database "Brian" does not exist
  And the following collections are empty in datastore:
     | collectionName                            |
     | assessment                                |
     | assessmentFamily                          |
     | assessmentPeriodDescriptor                |
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
     | studentParentAssociation                  |
     | studentProgramAssociation                 |
     | studentSchoolAssociation                  |
     | studentSectionAssociation                 |
     | studentGradebookEntry                     |
     | courseTranscript                          |
     | teacher                                   |
     | teacherSchoolAssociation                  |
     | teacherSectionAssociation                 |
   And I create a tenant set to preload data set "small" for tenant "Brian"
   And a batch job has completed successfully in the database
   And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | assessment                               |                 19|
     | assessmentFamily                         |                 37|
     | assessmentPeriodDescriptor               |                  2|
     | attendance                               |                 75|
     | calendarDate                             |                556|
     | cohort                                   |                  3|
     | competencyLevelDescriptor                |                  0|
     | course                                   |                 95|
     | courseOffering                           |                 95|
     | courseSectionAssociation                 |                  0|
     | disciplineAction                         |                  2|
     | disciplineIncident                       |                  2|
     | educationOrganization                    |                  5|
     | educationOrganizationAssociation         |                  0|
     | educationOrganizationSchoolAssociation   |                  0|
     | grade                                    |                  4|
     | gradebookEntry                           |                 12|
     | gradingPeriod                            |                 17|
     | graduationPlan                           |                  0|
     | learningObjective                        |                198|
     | learningStandard                         |               1499|
     | parent                                   |                  9|
     | program                                  |                  2|
     | recordHash                               |               9479|
     | reportCard                               |                  2|
     | schoolSessionAssociation                 |                  0|
     | section                                  |                 97|
     | sectionSchoolAssociation                 |                  0|
     | session                                  |                 22|
     | sessionCourseAssociation                 |                  0|
     | staff                                    |                 14|
     | staffCohortAssociation                   |                  3|
     | staffEducationOrganizationAssociation    |                 13|
     | staffProgramAssociation                  |                  7|
     | student                                  |                 78|
     | studentAcademicRecord                    |                117|
     | studentAssessment                        |                203|
     | studentCohortAssociation                 |                  6|
     | studentCompetency                        |                 59|
     | studentCompetencyObjective               |                  4|
     | studentDisciplineIncidentAssociation     |                  4|
     | studentParentAssociation                 |                  9|
     | studentProgramAssociation                |                  6|
     | studentSchoolAssociation                 |                167|
     | studentSectionAssociation                |                297|
     | studentGradebookEntry                    |                315|
     | courseTranscript                         |                196|
     | teacherSchoolAssociation                 |                  3|
     | teacherSectionAssociation                |                 11|
    And I should not see an error log file created
	And I should not see a warning log file created


Scenario: Preload Medium Sample Data Set
   Given I am using the tenant "Sharon"
   And the tenant database "Sharon" does not exist
   Then I create a tenant set to preload data set "medium" for tenant "Sharon"
   And a batch job has completed successfully in the database
   And a batch job log has been created
   And I should not see an error log file created
   And I should not see a warning log file created
