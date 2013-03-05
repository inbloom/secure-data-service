@RALLY_US4816
Feature: Odin Data Set Ingestion Correctness and Fidelity
  Background: I have a landing zone route configured
    Given I am using odin data store

  Scenario: Post Odin Sample Data Set
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the tenant database for "Midgar" does not exist
    And I post "Odin10k1SampleDataSet.zip" file as the payload of the ingestion job
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
    And a batch job for file "Odin10k1SampleDataSet.zip" is completed in database
    And a batch job log has been created
    Then I should see following map of entry counts in the corresponding collections:
      | collectionName                           |              count|
      | assessment                               |                 26|
      | assessmentFamily                         |                  0|
      | assessmentPeriodDescriptor               |                  0|
      | attendance                               |              10001|
      | calendarDate                             |                776|
      | cohort                                   |                 54|
      | competencyLevelDescriptor                |                  0|
      | course                                   |                 34|
      | courseOffering                           |                172|
      | courseSectionAssociation                 |                  0|
      | disciplineAction                         |                  0|
      | disciplineIncident                       |                  0|
      | educationOrganization                    |                 23|
      | educationOrganizationAssociation         |                  0|
      | educationOrganizationSchoolAssociation   |                  0|
      | grade                                    |                  0|
      | gradebookEntry                           |                  0|
      | gradingPeriod                            |                  4|
      | graduationPlan                           |                  3|
      | learningObjective                        |                  0|
      | learningStandard                         |                  0|
      | parent                                   |              20002|
      | program                                  |                309|
      | reportCard                               |                  0|
      | schoolSessionAssociation                 |                  0|
      | section                                  |               1122|
      | sectionAssessmentAssociation             |                  0|
      | sectionSchoolAssociation                 |                  0|
      | session                                  |                  4|
      | sessionCourseAssociation                 |                  0|
      | staff                                    |                786|
      | staffCohortAssociation                   |                 54|
      | staffEducationOrganizationAssociation    |                376|
      | staffProgramAssociation                  |               1091|
      | student                                  |              10001|
      | studentAcademicRecord                    |                  0|
      | studentAssessment                        |              60006|
      | studentCohortAssociation                 |               9023|
      | studentCompetency                        |                  0|
      | studentCompetencyObjective               |                  0|
      | studentDisciplineIncidentAssociation     |                  0|
      | studentParentAssociation                 |              20002|
      | studentProgramAssociation                |              30188|
      | studentSchoolAssociation                 |              10001|
      | studentSectionAssociation                |              26321|
      | studentGradebookEntry                    |                  0|
      | courseTranscript                         |                  0|
      | teacherSchoolAssociation                 |                608|
      | teacherSectionAssociation                |               1122|
    And I should see "Processed 202109 records." in the resulting batch job file
#    And I should not see an error log file created
#    And I should not see a warning log file created
