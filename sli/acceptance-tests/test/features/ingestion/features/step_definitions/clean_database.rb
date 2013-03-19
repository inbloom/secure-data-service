############################################################
# Given
############################################################
Given /^all collections are empty$/ do
      steps %Q{
       Given the following collections are empty in datastore:
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

    }
end