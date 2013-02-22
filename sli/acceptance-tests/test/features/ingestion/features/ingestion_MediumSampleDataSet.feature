Feature: Sample Data Set Ingestion Timimg

Background: I have a landing zone route configured
Given I am using local data store
And I am using preconfigured Ingestion Landing Zone

Scenario: Post Medium Sample Data Set
Given I post "MediumSampleDataSet.zip" file as the payload of the ingestion job
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
     | teacherSchoolAssociation                  |
     | teacherSectionAssociation                 |
When zip file is scp to ingestion landing zone
  And a batch job for file "MediumSampleDataSet.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                              | count |
     | assessment                                  | 1 |
     | attendance                                  | 500 |
     | calendarDate                                | 20 |
     | cohort                                      | 20 |
     | competencyLevelDescriptor                   | 2 |
     | course                                      | 50 |
     | courseOffering                              | 100 |
     | courseSectionAssociation                    | 0 |
     | disciplineAction                            | 4875 |
     | disciplineIncident                          | 4875 |
     | educationOrganization                       | 7 |
     | educationOrganizationAssociation            | 0 |
     | educationOrganizationSchoolAssociation      | 0 |
     | grade                                       | 2500 |
     | gradebookEntry                              | 1 |
     | gradingPeriod                               | 40 |
     | graduationPlan                              | 5 |
     | learningObjective                           | 1 |
     | learningStandard                            | 3 |
     | parent                                      | 763 |
     | program                                     | 13 |
     | recordHash                                  | 40488 |
     | reportCard                                  | 500 |
     | school                                      | 0 |
     | schoolSessionAssociation                    | 0 |
     | section                                     | 300 |
     | sectionSchoolAssociation                    | 0 |
     | session                                     | 10 |
     | sessionCourseAssociation                    | 0 |
     | staff                                       | 60 |
     | staffCohortAssociation                      | 180 |
     | staffEducationOrganizationAssociation       | 60 |
     | staffProgramAssociation                     | 106 |
     | student                                     | 500 |
     | studentAcademicRecord                       | 500 |
     | studentAssessment                | 2500 |
     | studentCohortAssociation                    | 1500 |
     | studentCompetency                           | 1000 |
     | studentCompetencyObjective                  | 1 |
     | studentDisciplineIncidentAssociation        | 4875 |
     | studentParentAssociation                    | 763 |
     | studentProgramAssociation                   | 1000 |
     | studentSchoolAssociation                    | 500 |
     | studentSectionAssociation                   | 2500 |
     | studentGradebookEntry                       | 8 |
     | courseTranscript                            | 7500 |
     | teacherSchoolAssociation                    | 50 |
     | teacherSectionAssociation                   | 300 |
	And I should see "Processed 38489 records." in the resulting batch job file
	And I should not see an error log file created
	And I should not see a warning log file created

