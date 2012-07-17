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
     | studentAssessmentAssociation              |
     | studentCohortAssociation                  |
     | studentCompetency                         |
     | studentCompetencyObjective                |
     | studentDisciplineIncidentAssociation      |
     | studentParentAssociation                  |
     | studentProgramAssociation                 |
     | studentSchoolAssociation                  |
     | studentSectionAssociation                 |
     | studentSectionGradebookEntry              |
     | studentTranscriptAssociation              |
     | teacherSchoolAssociation                  |
     | teacherSectionAssociation                 |
When zip file is scp to ingestion landing zone
When "360" seconds have elapsed
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                              | count |
     | assessment                                  | 1 |
     | attendance                                  | 500 |
     | calendarDate                                | 6 |
     | cohort                                      | 20 |
     | competencyLevelDescriptor                   | 0 |
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
     | reportCard                                  | 500 |
     | school                                      | 0 |
     | schoolSessionAssociation                    | 0 |
     | section                                     | 300 |
     | sectionAssessmentAssociation                | 0 |
     | sectionSchoolAssociation                    | 0 |
     | session                                     | 10 |
     | sessionCourseAssociation                    | 0 |
     | staff                                       | 60 |
     | staffCohortAssociation                      | 20 |
     | staffEducationOrganizationAssociation       | 10 |
     | staffProgramAssociation                     | 13 |
     | student                                     | 500 |
     | studentAcademicRecord                       | 500 |
     | studentAssessmentAssociation                | 2500 |
     | studentCohortAssociation                    | 1500 |
     | studentCompetency                           | 1000 |
     | studentCompetencyObjective                  | 1 |
     | studentDisciplineIncidentAssociation        | 4875 |
     | studentParentAssociation                    | 763 |
     | studentProgramAssociation                   | 1000 |
     | studentSchoolAssociation                    | 500 |
     | studentSectionAssociation                   | 2500 |
     | studentSectionGradebookEntry                | 500 |
     | studentTranscriptAssociation                | 7500 |
     | teacherSchoolAssociation                    | 50 |
     | teacherSectionAssociation                   | 300 |

