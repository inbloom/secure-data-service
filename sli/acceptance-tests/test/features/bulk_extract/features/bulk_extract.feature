Feature: A bulk extract is triggered

@smoke
Scenario: Trigger a bulk extract on ingested data
   Given I trigger a bulk extract
   When I retrieve the path to the extract file for the tenant "Midgar"
   And I verify that an extract tar file was created for the tenant "Midgar"
   And there is a metadata file in the extract
   And the extract contains a file for each of the following entities:
   |  entityType                            |
   |  assessment                            |
   |  assessmentFamily                      |
   |  assessmentPeriodDescriptor            |
   |  attendance                            |
   |  cohort                                |
   |  competencyLevelDescriptor             |
   |  course                                |
   |  courseOffering                        |
   |  courseTranscript                      |
   |  disciplineIncident                    |
   |  disciplineAction                      |
   |  educationOrganization                 |
   |  grade                                 |
   |  gradebookEntry                        |
   |  gradingPeriod                         |
   |  graduationPlan                        |
   |  learningObjective                     |
   |  learningStandard                      |
   |  parent                                |
   |  program                               |
   |  reportCard                            |
   |  school                                |
   |  section                               |
   |  session                               |
   |  staff                                 |
   |  staffCohortAssociation                |
   |  staffEducationOrganizationAssociation |
   |  staffProgramAssociation               |
   |  student                               |
   |  studentAcademicRecord                 |
   |  studentAssessment                     |
   |  studentCohortAssociation              |
   |  studentCompetency                     |
   |  studentCompetencyObjective            |
   |  studentDisciplineIncidentAssociation  |
   |  studentProgramAssociation             |
   |  studentGradebookEntry                 |
   |  studentSchoolAssociation              |
   |  studentSectionAssociation             |
   |  studentParentAssociation              |
   |  teacher                               |
   |  teacherSchoolAssociation              |
   |  teacherSectionAssociation             |
